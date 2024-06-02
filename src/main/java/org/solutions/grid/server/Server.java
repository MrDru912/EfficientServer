package org.solutions.grid.server;

import org.solutions.grid.datastructures.Graph;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {
    private static int port;
    private static boolean running = true;
    /**
     * Read-Write Lock for the shared data(graph and grid).
     */
    private final static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * Service for utilizing virtual threads.
     */
    private static ExecutorService executor;
    /**
     * Locations graph utilizing hash grid for faster detection of equivalent locations.
     */
    private static Graph graph;

    public static void main(String[] args) throws InterruptedException {
        /* Loading configuration data. */
        ConfigLoader config = new ConfigLoader("src/main/resources/config.properties");
        port = config.getIntProperty("server.port");
        int expectedLocationsNumber = config.getIntProperty("server.expected_locations_number");
        graph = new Graph(expectedLocationsNumber);
        executor = Executors.newVirtualThreadPerTaskExecutor();

        /* Starting up the server. */
        startServer();
    }

    public static void startServer(){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection accepted: " + clientSocket);
                Runnable task = new RequestHandler(clientSocket, graph, lock);
                executor.submit(task);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
