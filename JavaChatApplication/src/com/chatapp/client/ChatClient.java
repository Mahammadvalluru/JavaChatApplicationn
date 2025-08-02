package com.chatapp.client;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * ChatClient - Main client class for the chat application
 * Connects to the server and provides console-based user interface
 * Implements multi-threading for concurrent message sending and receiving
 */
public class ChatClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;
    
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;
    private volatile boolean isConnected = false;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    
    public ChatClient(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.isConnected = true;
    }
    
    /**
     * Main method to start the chat client
     */
    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        
        // Parse command line arguments
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port " + DEFAULT_PORT);
            }
        }
        
        try {
            ChatClient client = new ChatClient(host, port);
            client.start();
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            System.err.println("Make sure the server is running on " + host + ":" + port);
            System.exit(1);
        }
    }
    
    /**
     * Starts the client and begins message handling
     */
    public void start() {
        System.out.println("Connected to chat server!");
        System.out.println("Type '/help' for available commands.");
        System.out.println("Type '/quit' to disconnect.");
        System.out.println("----------------------------------------");
        
        // Start message receiver thread
        executor.submit(this::receiveMessages);
        
        // Start message sender thread
        executor.submit(this::sendMessages);
        
        // Wait for completion
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            disconnect();
        }
    }
    
    /**
     * Receives messages from the server
     */
    private void receiveMessages() {
        try {
            String message;
            while (isConnected && (message = reader.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            if (isConnected) {
                System.err.println("Error receiving message: " + e.getMessage());
            }
        } finally {
            isConnected = false;
        }
    }
    
    /**
     * Sends messages to the server
     */
    private void sendMessages() {
        try {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String message;
            
            while (isConnected && (message = consoleReader.readLine()) != null) {
                message = message.trim();
                
                if (message.isEmpty()) {
                    continue;
                }
                
                // Handle local commands
                if (message.startsWith("/")) {
                    handleLocalCommand(message);
                } else {
                    // Send message to server
                    writer.println(message);
                }
                
                // Check for quit command
                if (message.equalsIgnoreCase("/quit") || message.equalsIgnoreCase("/exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            if (isConnected) {
                System.err.println("Error reading from console: " + e.getMessage());
            }
        } finally {
            isConnected = false;
        }
    }
    
    /**
     * Handles local commands that don't need to be sent to server
     */
    private void handleLocalCommand(String command) {
        String[] parts = command.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        
        switch (cmd) {
            case "/clear":
                clearConsole();
                break;
                
            case "/status":
                showStatus();
                break;
                
            case "/help":
                showLocalHelp();
                break;
                
            default:
                // Send command to server
                writer.println(command);
                break;
        }
    }
    
    /**
     * Clears the console (platform-dependent)
     */
    private void clearConsole() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            // Fallback: print multiple newlines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    /**
     * Shows connection status
     */
    private void showStatus() {
        System.out.println("=== Connection Status ===");
        System.out.println("Connected: " + isConnected);
        System.out.println("Socket: " + (socket != null ? socket.getRemoteSocketAddress() : "Not connected"));
        System.out.println("Username: " + (username != null ? username : "Not set"));
        System.out.println("========================");
    }
    
    /**
     * Shows local help commands
     */
    private void showLocalHelp() {
        System.out.println("=== Local Commands ===");
        System.out.println("/clear - Clear the console");
        System.out.println("/status - Show connection status");
        System.out.println("/help - Show this help message");
        System.out.println("======================");
        System.out.println("=== Server Commands ===");
        System.out.println("/help - Show server commands");
        System.out.println("/users - Show online users");
        System.out.println("/whisper <user> <msg> - Send private message");
        System.out.println("/me <action> - Perform an action");
        System.out.println("/quit or /exit - Disconnect");
        System.out.println("=======================");
    }
    
    /**
     * Disconnects from the server
     */
    public void disconnect() {
        isConnected = false;
        
        try {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
        
        System.out.println("Disconnected from server.");
    }
    
    /**
     * Checks if the client is connected
     */
    public boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }
    
    /**
     * Gets the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username
     */
    public void setUsername(String username) {
        this.username = username;
    }
} 