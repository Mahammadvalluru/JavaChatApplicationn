package com.chatapp.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * ChatServer - Main server class for the real-time chat application
 * Handles multiple concurrent client connections using multi-threading
 * Implements TCP/IP networking with message broadcasting capabilities
 */
public class ChatServer {
    private static final int DEFAULT_PORT = 8080;
    private static final int MAX_CLIENTS = 100;
    
    private ServerSocket serverSocket;
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private final ExecutorService threadPool;
    private volatile boolean isRunning = false;
    
    public ChatServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        System.out.println("Chat Server started on port " + port);
        System.out.println("Waiting for client connections...");
    }
    
    /**
     * Main method to start the chat server
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port " + DEFAULT_PORT);
            }
        }
        
        try {
            ChatServer server = new ChatServer(port);
            server.start();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Starts the server and begins accepting client connections
     */
    public void start() {
        isRunning = true;
        
        // Start server monitoring thread
        Thread monitorThread = new Thread(this::monitorServer);
        monitorThread.setDaemon(true);
        monitorThread.start();
        
        try {
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                threadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            if (isRunning) {
                System.err.println("Error accepting client connection: " + e.getMessage());
            }
        } finally {
            shutdown();
        }
    }
    
    /**
     * Broadcasts a message to all connected clients
     */
    public void broadcastMessage(String message, ClientHandler sender) {
        String formattedMessage = formatMessage(message, sender);
        System.out.println(formattedMessage);
        
        // Remove disconnected clients and broadcast to active ones
        clients.removeIf(client -> !client.isConnected());
        
        for (ClientHandler client : clients) {
            if (client != sender && client.isConnected()) {
                client.sendMessage(formattedMessage);
            }
        }
    }
    
    /**
     * Sends a system message to all clients
     */
    public void broadcastSystemMessage(String message) {
        String systemMessage = "[SYSTEM] " + message;
        System.out.println(systemMessage);
        
        for (ClientHandler client : clients) {
            if (client.isConnected()) {
                client.sendMessage(systemMessage);
            }
        }
    }
    
    /**
     * Removes a client from the active clients list
     */
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        broadcastSystemMessage("User '" + client.getUsername() + "' has left the chat.");
        System.out.println("Client disconnected: " + client.getUsername());
    }
    
    /**
     * Formats a message with timestamp and username
     */
    private String formatMessage(String message, ClientHandler sender) {
        return String.format("[%s] %s: %s", 
            new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()),
            sender.getUsername(), 
            message);
    }
    
    /**
     * Monitors server status and provides statistics
     */
    private void monitorServer() {
        while (isRunning) {
            try {
                Thread.sleep(30000); // Check every 30 seconds
                System.out.println("Server Status - Active clients: " + clients.size());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * Gracefully shuts down the server
     */
    public void shutdown() {
        isRunning = false;
        
        // Close all client connections
        for (ClientHandler client : clients) {
            client.disconnect();
        }
        clients.clear();
        
        // Shutdown thread pool
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
        
        // Close server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        
        System.out.println("Server shutdown complete.");
    }
    
    /**
     * Gets the number of active clients
     */
    public int getActiveClientCount() {
        return clients.size();
    }
} 