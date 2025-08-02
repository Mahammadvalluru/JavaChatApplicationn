package com.chatapp.server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ClientHandler - Manages individual client connections
 * Handles message reading, writing, and connection management
 * Implements Runnable for multi-threading support
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ChatServer server;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;
    private final AtomicBoolean connected = new AtomicBoolean(true);
    
    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.server = server;
    }
    
    @Override
    public void run() {
        try {
            // Initialize input/output streams
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            
            // Handle client authentication/username setup
            handleAuthentication();
            
            // Notify other clients about new user
            server.broadcastSystemMessage("User '" + username + "' has joined the chat.");
            
            // Main message handling loop
            handleMessages();
            
        } catch (IOException e) {
            System.err.println("Error handling client " + getClientInfo() + ": " + e.getMessage());
        } finally {
            cleanup();
        }
    }
    
    /**
     * Handles client authentication and username setup
     */
    private void handleAuthentication() throws IOException {
        // Send welcome message
        sendMessage("Welcome to the Chat Server!");
        sendMessage("Please enter your username: ");
        
        // Read username
        username = reader.readLine();
        if (username == null || username.trim().isEmpty()) {
            username = "Anonymous_" + clientSocket.getPort();
        }
        
        // Send confirmation
        sendMessage("Hello " + username + "! You are now connected to the chat.");
        sendMessage("Type '/help' for available commands or start chatting!");
        sendMessage("Type '/quit' to disconnect.");
        
        System.out.println("User '" + username + "' authenticated from " + getClientInfo());
    }
    
    /**
     * Main message handling loop
     */
    private void handleMessages() throws IOException {
        String message;
        while (connected.get() && (message = reader.readLine()) != null) {
            message = message.trim();
            
            if (message.isEmpty()) {
                continue;
            }
            
            // Handle special commands
            if (message.startsWith("/")) {
                handleCommand(message);
            } else {
                // Broadcast regular message
                server.broadcastMessage(message, this);
            }
        }
    }
    
    /**
     * Handles special commands from clients
     */
    private void handleCommand(String command) {
        String[] parts = command.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";
        
        switch (cmd) {
            case "/quit":
            case "/exit":
                sendMessage("Goodbye! Disconnecting...");
                disconnect();
                break;
                
            case "/help":
                sendHelpMessage();
                break;
                
            case "/users":
                sendUserList();
                break;
                
            case "/whisper":
                handleWhisper(args);
                break;
                
            case "/me":
                if (!args.isEmpty()) {
                    String actionMessage = "* " + username + " " + args + " *";
                    server.broadcastMessage(actionMessage, this);
                } else {
                    sendMessage("Usage: /me <action>");
                }
                break;
                
            default:
                sendMessage("Unknown command: " + cmd + ". Type /help for available commands.");
                break;
        }
    }
    
    /**
     * Sends help message to the client
     */
    private void sendHelpMessage() {
        sendMessage("=== Available Commands ===");
        sendMessage("/help - Show this help message");
        sendMessage("/users - Show list of online users");
        sendMessage("/whisper <username> <message> - Send private message");
        sendMessage("/me <action> - Perform an action");
        sendMessage("/quit or /exit - Disconnect from chat");
        sendMessage("==========================");
    }
    
    /**
     * Sends list of online users to the client
     */
    private void sendUserList() {
        sendMessage("=== Online Users ===");
        // This would need to be implemented in ChatServer to get user list
        sendMessage("Feature coming soon...");
        sendMessage("===================");
    }
    
    /**
     * Handles private messaging (whisper functionality)
     */
    private void handleWhisper(String args) {
        if (args.isEmpty()) {
            sendMessage("Usage: /whisper <username> <message>");
            return;
        }
        
        String[] parts = args.split("\\s+", 2);
        if (parts.length < 2) {
            sendMessage("Usage: /whisper <username> <message>");
            return;
        }
        
        String targetUsername = parts[0];
        String whisperMessage = parts[1];
        
        // This would need to be implemented in ChatServer
        sendMessage("Whisper feature coming soon...");
    }
    
    /**
     * Sends a message to this client
     */
    public void sendMessage(String message) {
        if (connected.get() && writer != null) {
            writer.println(message);
        }
    }
    
    /**
     * Disconnects the client
     */
    public void disconnect() {
        connected.set(false);
        cleanup();
    }
    
    /**
     * Cleans up resources when client disconnects
     */
    private void cleanup() {
        connected.set(false);
        
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client connection: " + e.getMessage());
        } finally {
            server.removeClient(this);
        }
    }
    
    /**
     * Gets the username of this client
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Checks if the client is still connected
     */
    public boolean isConnected() {
        return connected.get() && clientSocket != null && !clientSocket.isClosed();
    }
    
    /**
     * Gets client connection information
     */
    private String getClientInfo() {
        return clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
    }
    
    /**
     * Gets the client's socket
     */
    public Socket getClientSocket() {
        return clientSocket;
    }
} 