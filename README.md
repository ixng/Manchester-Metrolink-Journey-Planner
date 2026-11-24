# Manchester Metrolink Journey Planner

A route planner for the Manchester Metrolink tram network. Give it two stations and it works out the
fastest way between them, which lines to travel on, and where you have to change.

I made this because the interesting part of a journey planner is not really the shortest path, it is
what you decide counts as "shortest". The obvious version treats the network as a graph of travel
times and returns the quickest sequence of stations, which is a textbook Dijkstra's and takes an
afternoon. But that version thinks changing from one line to another is free, so it will happily
route you through three changes to save a minute, which nobody would actually do. So this one
charges 2 minutes every time the route switches line, which means the planner has to keep track of
which line it arrived on, not just where it is. That one extra piece of state is most of what makes
this project more than a graph exercise.

It is plain Java with no dependencies, no build tool and no package manager, and runs in the
terminal.

---

## Contents

- [Status](#status)
- [Requirements](#requirements)
- [Setup](#setup)
- [Usage](#usage)
- [Design decisions](#design-decisions)
- [Project layout](#project-layout)

---

## Status



| STAGE | State |
|---|---|
| 1. Read the network in from the data file | done |
| 2. Terminal interface for a start and end station | done |
| 3. Dijkstra's shortest path on journey time | done |
| 4. Penalty for changing line | done |
| 5. Report the lines travelled and count the changes | done |



---

## Requirements

- A Java Development Kit, version 17 or later. Developed and tested on OpenJDK 21.

No libraries, no build files, nothing to install. Everything used is standard library: `java.util`
for the collections and the priority queue, `java.io` for reading the data file.

The network data is included in the repository as `Metrolink_times_linecolour.csv`, so there is no
API key and nothing to configure before running it.

---

## Setup

```bash
git clone https://github.com/ixng/Summer-project.git
cd Summer-project
javac Graph.java Driver.java
```

That is the whole setup. `javac` writes `.class` files next to the source, and they are gitignored.

---

## Usage

### Plan a journey

```bash
java Driver
```

It asks for a start station, an end station, and whether you want the route. Station names have to
match the data file including punctuation, so `Besses o'th' Barn`, `Deansgate/Castlefield`,
`St. Peter's Square`. Capitalisation does not matter, typing `bury` finds `Bury`, and the output
prints the spelling from the data file rather than echoing back what was typed.

```
Welcome to the Metrolink Journey Planner!
Enter a start station
Altrincham
Enter an end station
Eccles
Would you like the shortest route between the stations? Yes/No
yes

Altrincham  ->  Eccles
Journey time: 43.5 mins   |   Changes: 1

  purple     Altrincham  ->  Cornbrook   (9 stops)
             via Navigation Road, Timperley, Brooklands, Sale, Dane Road, Stretford, Old Trafford, Trafford Bar
  lightblue  Cornbrook  ->  Eccles   (11 stops)
             via Pomona, Exchange Quay, Salford Quays, Anchorage, Harbour City, Media CityUK, Broadway, Langworthy, Weaste, Ladywell

Full route: Altrincham -> Navigation Road -> Timperley -> Brooklands -> Sale -> Dane Road -> Stretford -> Old Trafford -> Trafford Bar -> Cornbrook -> Pomona -> Exchange Quay -> Salford Quays -> Anchorage -> Harbour City -> Media CityUK -> Broadway -> Langworthy -> Weaste -> Ladywell -> Eccles
```

A journey needing no change says so instead of printing an empty change count. `Bury` to
`Piccadilly Station` comes out as 38.0 mins, 0 changes, yellow the whole way.

### Use a different network

```bash
java Driver path/to/another-network.csv
```

With no argument it reads `Metrolink_times_linecolour.csv` from the working directory. Any file in
the same four-column format works, so the planner is not tied to this particular network.

---

## Design decisions

The decisions I had to make while building this, as bullet points:

**Changing line costs time, and the search has to know which line it is on -** A plain Dijkstra's
treats every edge the same, so a route that switches line three times to save a minute looks better
than one that stays put. Charging 2 minutes per change fixes the output, but it means the cost of an
edge now depends on how you arrived at the station rather than just the edge itself. So the search
records the line colour used to reach every station alongside the predecessor, and compares it
against each outgoing edge before adding the penalty. That array is also what makes the per-line
output possible, so one piece of state pays for itself twice.

**The penalty is a named constant, not a 2 scattered through the search -** It lives in
`CHANGE_PENALTY_MINUTES`. A bare `2` in the middle of the distance arithmetic reads like it has
something to do with the graph, when actually it is a modelling choice that someone might want to
argue with or tune.

**Stations are looked up through a name index, not searched for -** The first version walked the
station array comparing names, which is O(n) per lookup, and since loading the network does two
lookups per row that made loading O(n²). Now a `HashMap` keyed on the lower-cased name holds the
index, so lookup is O(1), loading is linear, and case-insensitive input comes free rather than
needing a second pass. The station list also grows instead of being a fixed 1000-slot array, which
was an arbitrary cap that would have crashed on a bigger network.

**Journey times are stored with their decimals and only rounded when printed -** The data has times
like 2.5 and 3.5, and distances are summed as doubles the whole way through. The rounding to one
decimal place happens in the output formatting and nowhere else, because a rounded total cannot be
summed into anything else later without the error compounding.

**Graph edges and queue entries are separate types -** They started as one class, which is how the
priority queue ended up holding objects whose `time` field meant "total journey so far" while the
identical field on the adjacency lists meant "time for this one hop". It worked but it was the most
confusing part of the code to read back. An `Edge` now carries a fixed hop time and a `QueueEntry`
carries a cumulative one, and the queue entry is the only one that is `Comparable`, since ordering
is the only reason it exists.

**The route is grouped into legs instead of printed as a list of stations -** A flat list of
stations tells you nothing about where to get off. Walking the predecessors back gives the stations
in order, and then consecutive stations sharing a line colour get grouped into a leg, so each leg
can print the line, the stops along it and where to change. The number of changes is just the number
of legs minus one, which falls out of the grouping for free.

**An invalid station stops the run instead of carrying on -** It used to print "Invalid start
Station" and then keep going, which meant the invalid name reached the search and threw an
`IllegalArgumentException` at the user. A wrong station name is a typo, not a crash, so it reports
which one it did not recognise and exits.

**Only the first connection between two stations is kept -** This is the decision behind the
problem in the verification section below. Several lines run over the
same stretch of track through the city centre, so the same physical connection appears in the data
once per line that uses it, with that line's own time. The loader rejects a connection between two
stations it already has, so whichever line happens to appear first in the file wins and the rest are
dropped. Of 145 rows in the data, 45 are discarded this way.

**The algorithm does not know anything about Metrolink -** No station names, line colours or times
are in the code. Everything comes from the data file, which is why passing a different CSV works
without touching the source.

---

## Project layout

**The program**, in the order the data moves through it:

| | |
|---|---|
| `Metrolink_times_linecolour.csv` | the network: station pairs, line colours and journey times |
| `Graph.java` | the whole engine: loading the file, the graph, Dijkstra's, route reconstruction and leg grouping |
| `Driver.java` | the terminal interface: prompts for stations, prints the route |

Inside `Graph.java`, the pieces are:

| | |
|---|---|
| `loadFromFile` | reads the CSV and builds the graph |
| `addStation`, `addLine` | the graph's two insert operations |
| `indexOf`, `canonicalName` | name lookup through the index |
| `shortestLength` | Dijkstra's, including the change penalty |
| `findShortestPath`, `indexPath` | walking the predecessors back into a route |
| `legsFor`, `printShortestPath` | grouping the route into legs and printing it |

If you are reading the code, start with `shortestLength` for the search and the penalty, then
`legsFor` for how the route becomes the per-line output.
