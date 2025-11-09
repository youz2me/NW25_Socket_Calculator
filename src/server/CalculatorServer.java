package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalculatorServer {

    //region Properties

    private static final int DEFAULT_PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;

    private final int port;
    private final ExecutorService threadPool;
    private volatile boolean running = false;

    //endregion

    //region Initialization

    public CalculatorServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        CalculatorServer server = new CalculatorServer(port);
        server.start();
    }

    //endregion

    //region Server Control

    public void start() {
        running = true;
        System.out.println("Calculator Server starting on port " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Waiting for clients...");

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        running = false;
        threadPool.shutdown();
        System.out.println("Server shutdown.");
    }

    //endregion
}
