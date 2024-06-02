package org.solutions.grid.server;

import com.sun.jdi.request.InvalidRequestStateException;
import cz.cvut.fel.esw.server.proto.*;
import org.solutions.grid.algorithms.Dijkstra;
import org.solutions.grid.datastructures.Graph;
import org.solutions.grid.datastructures.LocationRecord;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static cz.cvut.fel.esw.server.proto.Request.MsgCase.*;

/**
 * Handler thread of the requests coming from client.
 */
public class RequestHandler implements Runnable {
    private final Socket clientSocket;
    /**
     * Stream for receiving requests from client.
     */
    private final InputStream in;
    /**
     * Stream for sending responses to client.
     */
    private final DataOutputStream out;
    private final Graph graph;
    private final ReentrantReadWriteLock lock;
    /**
     * Map of new coming walk requests from particular client.
     */
    private static final Map<Socket, List<Walk>> clientWalks = new ConcurrentHashMap<>();


    public RequestHandler(Socket clientSocket, Graph graph, ReentrantReadWriteLock lock)
            throws IOException {
        this.clientSocket = clientSocket;
        this.in = clientSocket.getInputStream();
        this.out = new DataOutputStream(clientSocket.getOutputStream());
        this.graph = graph;
        this.lock = lock;
    }

    @Override
    public void run() {
        try (DataInputStream din = new DataInputStream(in)) {
            while (!clientSocket.isClosed()) {
                int messageSize = din.readInt();
                byte[] data = new byte[messageSize];
                din.readFully(data);
                Request request = Request.parseFrom(data);
                /* Handling requests in form of protobuf messages from clients. */
                switch (request.getMsgCase()) {
                    case WALK:
                        handleWalk(request.getWalk());
                        break;
                    case RESET:
                        handleReset();
                        break;
                    case ONETOONE:
                        handleOneToOne(request.getOneToOne());
                        break;
                    case ONETOALL:
                        handleOneToAll(request.getOneToAll());
                        break;
                    default:
                        throw new InvalidRequestStateException("Unknown request was received from client.");
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected");
        }
    }

    /**
     * Handling oneToAll request from the client.
     * @param oneToAll request of finding sum of all shortest paths in the graph
     *                 from the given location
     * @throws IOException
     */
    private void handleOneToAll(OneToAll oneToAll) throws IOException {
        /* Adding relevant walks to the graph after synchronisation point. */
        lock.writeLock().lock();
        try {
            addNewWalks();
        }
        catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            lock.writeLock().unlock();
        }

        /* Finding the shortest paths sum in graph with relevant walks. */
        lock.readLock().lock();
        try {
            LocationRecord origin = new LocationRecord(oneToAll.getOrigin());
            LocationRecord existingOrigin = graph.getGrid().locationIsInGrid(origin);
            long shortestPathsSum = Dijkstra.findShortestPathOneToAll(graph, existingOrigin);
            Response response = Response.newBuilder()
                    .setStatus(Response.Status.OK)
                    .setTotalLength(shortestPathsSum)
                    .build();
            System.out.println("OneToMany request: " + shortestPathsSum);
            sendResponseTCP(response, out);
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Handling oneToOne request from the client.
     * @param oneToOne request of finding the shortest path from given origin to destination location.
     * @throws IOException
     */
    private void handleOneToOne(OneToOne oneToOne) throws IOException {
        /* Adding relevant walks to the graph after synchronisation point. */
        lock.writeLock().lock();
        try {
            addNewWalks();
        }
        catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        finally {
            lock.writeLock().unlock();
        }

        /* Finding the shortest paths sum in graph with relevant walks. */
        lock.readLock().lock();
        try {
            LocationRecord origin = new LocationRecord(oneToOne.getOrigin());
            LocationRecord destination = new LocationRecord(oneToOne.getDestination());
            LocationRecord existingOrigin = graph.getGrid().locationIsInGrid(origin);
            LocationRecord existingDestination = graph.getGrid().locationIsInGrid(destination);
            long shortestPath = Dijkstra.findShortestPathOneToOne(graph, existingOrigin, existingDestination);

            Response response = Response.newBuilder()
                    .setStatus(Response.Status.OK)
                    .setShortestPathLength(shortestPath)
                    .build();
            System.out.println("OneToOne request: " + shortestPath);
            sendResponseTCP(response, out);
        }
        catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Clearing whole graph and grid.
     * @throws IOException
     */
    private void handleReset() throws IOException {
        lock.writeLock().lock();
        try {
            graph.reset();
            Response response = Response.newBuilder()
                    .setStatus(Response.Status.OK)
                    .build();
            sendResponseTCP(response, out);
        }
        catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Collecting relevant walks for the next synchronization point.
     * @param walk Sequence of locations and sequence of edges which connect them.
     * @throws IOException
     */
    private void handleWalk(Walk walk) throws IOException {
        clientWalks.computeIfAbsent(this.clientSocket, k -> new ArrayList<>()).add(walk);
        Response response = Response.newBuilder()
                .setStatus(Response.Status.OK)
                .build();
        sendResponseTCP(response, out);
    }

    /**
     * Adding collected relevant walks into graph and grid.
     */
    public void addNewWalks() {
        List<Walk> walks = clientWalks.remove(this.clientSocket);
        if (walks == null) return;
        graph.addNewWalks(walks);
    }

    /**
     * Sending response to request to the client.
     * @param response protobuf response message.
     * @param out client socket data stream
     * @throws IOException
     */
    public void sendResponseTCP(Response response, DataOutputStream out) throws IOException {
        int messageSize = response.getSerializedSize();
        out.writeInt(messageSize);
        response.writeTo(out);
        out.flush();
    }
}
