# Manchester Metrolink Journey Planner

A route planner for the Manchester Metrolink tram network, written in Java with no external
dependencies. Given a start and end station, it computes the fastest route across the network
using Dijkstra's shortest-path algorithm — accounting for the time cost of changing lines.

## Overview

The Metrolink network is modelled as an undirected weighted graph: stations are vertices, and
the tram connections between them are edges weighted by average journey time in minutes.

The planner:

- loads the network from a CSV file of station-to-station connections
- builds an adjacency-list graph of 99 stations and 145 connections across 7 lines
- runs Dijkstra's algorithm to find the lowest-total-time route between any two stations
- adds a **2-minute penalty whenever the route changes from one line to another**, so a route
  that stays on a single line can beat a nominally quicker one that requires a change

That last point is the part worth highlighting. A plain shortest-path search treats a change of
line as free, which produces routes that look fast on paper but are slower in practice. Tracking
which line was used to arrive at each station — and charging for switching — produces noticeably
more realistic journeys.

## How it works

**Graph construction.** Each row of the CSV is one connection. Journey time is assumed to be the
same in both directions, so every row inserts two directed edges (`A→B` and `B→A`), making the
graph effectively undirected. Each station holds its own adjacency list of outgoing edges, and
duplicate connections are rejected on insert.

**Shortest path.** Dijkstra's algorithm, using a `PriorityQueue` ordered by cumulative journey
time. The search keeps three parallel arrays: the best known time to each station, a visited set
of stations whose shortest time is already final, and a predecessor array used to rebuild the
route once the destination is reached. Each edge also carries its line colour, which is compared
against the colour used to reach the current station to decide whether the change penalty applies.

**Route reconstruction.** Walking the predecessor array backwards from the destination and
reversing the result gives the ordered list of stations to travel through.

## The network data

`Metrolink_times_linecolour.csv` holds one connection per row:

```
From ,To ,Line ,Time (mins)
Bury,Radcliffe,yellow,6
Brooklands,Sale,purple,1.5
Sale,Dane Road,purple,1
```

So the second row reads as: it takes 6 minutes to travel between Bury and Radcliffe on the yellow
line, in either direction.

The included dataset covers 145 connections across seven lines:

| Line | Connections |
|------------|----|
| pink | 32 |
| lightblue | 27 |
| green | 24 |
| darkblue | 24 |
| purple | 16 |
| yellow | 14 |
| red | 8 |

Times are averages rather than timetabled departures, so the planner optimises typical journey
duration, not a specific departure. The loader trims surrounding whitespace, so the padded column
headers in the file are handled without special-casing.

## Requirements

A Java Development Kit, version 17 or newer. Developed and tested against OpenJDK 21. There are no
libraries, build files or package managers to install.

## Running it

Compile the two source files:

```bash
javac Graph.java Driver.java
```

Then run the planner:

```bash
java Driver
```

Run it from the project root — the data file is loaded by relative path, so the CSV needs to be in
the working directory.

Station names must match the dataset, including punctuation: `Besses o'th' Barn`,
`Deansgate/Castlefield`, `St. Peter's Square`.

## Example

```
Welcome to the Metrolink Journey Planner!
Enter a start station
Didsbury Village
Enter an end station
Victoria
Would you like the shortest route between the stations? Yes/No
yes
Shortest path from Didsbury Village to Victoria ( 29.0 mins):
Didsbury Village -> West Didsbury -> Burton Road -> Withington -> St Werburgh's Road -> Chorlton
-> Firswood -> Trafford Bar -> Cornbrook -> Deansgate/Castlefield -> St. Peter's Square
-> Exchange Square -> Victoria
```

## Project structure

| File | Purpose |
|------|---------|
| `Graph.java` | The engine — graph structure, CSV loading, Dijkstra's algorithm, path reconstruction |
| `Driver.java` | Terminal interface: prompts for stations and prints the resulting route |
| `Metrolink_times_linecolour.csv` | Network data — station connections, line colours and journey times |

## Design notes

Dijkstra's algorithm runs in O(E log V) time here, dominated by priority-queue operations. Station
lookup by name is currently a linear scan over the station array, which is irrelevant at this
network's size but would be the first thing to change for a substantially larger network.

The algorithm is kept independent of the dataset: the planner reads whatever network the CSV
describes, so swapping in different stations, lines or journey times requires no code changes.

## Possible improvements

- Report the line colours and change points in the output, not just the station sequence
- Alternative optimisation criteria: fewest changes, fewest stops
- Multi-stop journeys with intermediate waypoints
- Step-free and accessibility-aware routing
- Real timetable data in place of average times, enabling departure-specific planning
- A graphical or web front end in place of the terminal prompts
