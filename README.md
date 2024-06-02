# EfficientServer

https://gitlab.fel.cvut.cz/mamaeand/efficientserver.git

## About repo
This repo is for a semestral project on ESW course which is called Efficient Server. The goal was to implement efficient TCP server in a way that it can handle multiple clients providing geographical data of paths between different location (Walk requests) and also compute distances based on already procesed data (OneToOne and OneToAll requests). For more details: https://esw.pages.fel.cvut.cz/labs/efficient-servers/. 

## Implementaion
* This project is a java implementation of such a server.
* I used jdk21 by GraalVM.
* For the build I used nix shell and maven.
* To handle mutiple connections with clients I used java 21 virtual threads. For synchronization purposes I used ReentrantReadWriteLock.
* From Walk requests server builds a graph represented by adjacency list. Locations were indexed to store them efficiently.
* To respond to OneToOne and OneToAll (requiring computing distances between locations) requests I used Dijkstra's algorithm using priority queue for efficientcy. I aslo tried to benefit from A-star algorithm using different heuristics but I did not detect significant performance boost from them. 
* Also I used hash-grid data structure for effective searching for duplicit location in the graph. At first I was trying to utilize K2-tree for this purpose but it turned out to be significantly less efficient than grid approach for this particular problem.
* Comunication between clients and server was implemented using protobuf messages.
* There is a configuration file to set up the server port and meta data in: src/main/resources/config.properties
* Other details can be found in javadoc.

## Download the project

```
git clone project
cd \efficientserver
```

## Dependencies
* Project requires following dependencies:
* GraalVM compiler for jdk21
* protobuf
* maven
* pkg-config

## Build

To download dependencies and build the project I used nix shell and maven. Navigate to the root of the project and build it using the following commands:

```
nix-shell -p jdk
mvn compile
mvn exec:java
```
