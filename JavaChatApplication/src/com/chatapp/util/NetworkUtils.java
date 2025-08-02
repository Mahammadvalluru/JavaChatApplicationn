package com.chatapp.util;

import java.net.*;
import java.io.IOException;
import java.util.Enumeration;

/**
 * NetworkUtils - Utility class for common networking operations
 * Provides helper methods for network configuration and validation
 */
public class NetworkUtils {
    
    /**
     * Gets the local IP address of the machine
     */
    public static String getLocalIPAddress() {
        try {
            // Try to get the local IP address
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("8.8.8.8", 80));
            String localIP = socket.getLocalAddress().getHostAddress();
            socket.close();
            return localIP;
        } catch (IOException e) {
            // Fallback: try to get from network interfaces
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = interfaces.nextElement();
                    if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                        continue;
                    }
                    
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (address instanceof Inet4Address) {
                            return address.getHostAddress();
                        }
                    }
                }
            } catch (SocketException ex) {
                // Ignore
            }
        }
        return "127.0.0.1"; // Default fallback
    }
    
    /**
     * Checks if a port is available for binding
     */
    public static boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Finds an available port starting from the given port
     */
    public static int findAvailablePort(int startPort) {
        int port = startPort;
        while (!isPortAvailable(port)) {
            port++;
            if (port > 65535) {
                throw new RuntimeException("No available ports found");
            }
        }
        return port;
    }
    
    /**
     * Validates if a hostname is reachable
     */
    public static boolean isHostReachable(String hostname, int timeout) {
        try {
            InetAddress address = InetAddress.getByName(hostname);
            return address.isReachable(timeout);
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Validates if a port is valid (between 1 and 65535)
     */
    public static boolean isValidPort(int port) {
        return port > 0 && port <= 65535;
    }
    
    /**
     * Validates if a hostname is valid
     */
    public static boolean isValidHostname(String hostname) {
        if (hostname == null || hostname.trim().isEmpty()) {
            return false;
        }
        
        // Check if it's a valid IP address
        try {
            InetAddress.getByName(hostname);
            return true;
        } catch (UnknownHostException e) {
            // Not a valid IP, check if it's a valid hostname
            return hostname.matches("^[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?$");
        }
    }
    
    /**
     * Gets network interface information
     */
    public static void printNetworkInterfaces() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            System.out.println("=== Network Interfaces ===");
            
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isUp()) {
                    System.out.println("Interface: " + networkInterface.getDisplayName());
                    System.out.println("  Name: " + networkInterface.getName());
                    System.out.println("  Loopback: " + networkInterface.isLoopback());
                    
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        System.out.println("  Address: " + address.getHostAddress());
                    }
                    System.out.println();
                }
            }
        } catch (SocketException e) {
            System.err.println("Error getting network interfaces: " + e.getMessage());
        }
    }
    
    /**
     * Formats bytes to human-readable format
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * Gets connection statistics
     */
    public static class ConnectionStats {
        private final long startTime;
        private long bytesReceived;
        private long bytesSent;
        private int messagesReceived;
        private int messagesSent;
        
        public ConnectionStats() {
            this.startTime = System.currentTimeMillis();
        }
        
        public void addBytesReceived(int bytes) {
            this.bytesReceived += bytes;
        }
        
        public void addBytesSent(int bytes) {
            this.bytesSent += bytes;
        }
        
        public void incrementMessagesReceived() {
            this.messagesReceived++;
        }
        
        public void incrementMessagesSent() {
            this.messagesSent++;
        }
        
        public long getUptime() {
            return System.currentTimeMillis() - startTime;
        }
        
        public String getFormattedStats() {
            long uptime = getUptime();
            long seconds = uptime / 1000;
            long minutes = seconds / 60;
            seconds %= 60;
            
            return String.format(
                "Uptime: %02d:%02d | Messages: %d sent, %d received | Data: %s sent, %s received",
                minutes, seconds, messagesSent, messagesReceived,
                formatBytes(bytesSent), formatBytes(bytesReceived)
            );
        }
    }
} 