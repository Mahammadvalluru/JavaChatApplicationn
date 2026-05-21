# Java Chat Application

A real-time client-server chat application built using Java socket programming and multi-threading. This project demonstrates fundamental networking concepts, concurrent programming, and TCP/IP communication protocols.

## 🚀 Features

### Core Functionality
- **Real-time messaging** with instant message delivery
- **Multi-client support** - handles multiple concurrent connections
- **Console-based interface** for intuitive user interaction
- **Message broadcasting** to all connected users
- **User authentication** with custom usernames
- **Connection management** with proper error handling

### Advanced Features
- **Command system** with built-in commands (`/help`, `/quit`, `/users`, etc.)
- **Action messages** using `/me` command
- **System notifications** for user join/leave events
- **Connection statistics** and monitoring
- **Graceful shutdown** handling
- **Cross-platform compatibility**

### Technical Features
- **Socket programming** using Java Socket API
- **Multi-threading** for concurrent client handling
- **TCP/IP networking** protocols
- **Thread pool management** for efficient resource usage
- **Network utilities** for IP detection and port validation
- **Error handling** and connection recovery

## 🏗️ Architecture

### Server Architecture
```
ChatServer (Main Server)
├── ClientHandler (Per Client Thread)
├── ThreadPool (Concurrent Client Management)
├── Message Broadcasting System
└── Connection Management
```

### Client Architecture
```
ChatClient (Main Client)
├── Message Receiver Thread
├── Message Sender Thread
├── Console Interface
└── Network Connection Handler
```

## 📁 Project Structure

```
JavaChatApplication/
├── src/
│   └── com/
│       └── chatapp/
│           ├── server/
│           │   ├── ChatServer.java      # Main server class
│           │   └── ClientHandler.java   # Individual client handler
│           ├── client/
│           │   └── ChatClient.java      # Main client class
│           └── util/
│               └── NetworkUtils.java    # Network utility functions
├── README.md
├── build.bat                           # Windows build script
├── run-server.bat                      # Windows server runner
├── run-client.bat                      # Windows client runner
└── .gitignore
```

## 🛠️ Prerequisites

- **Java Development Kit (JDK) 8 or higher**
- **Windows/Linux/macOS** (cross-platform compatible)
- **Network connectivity** for multi-machine testing

## 🚀 Quick Start

### 1. Compile the Application

**Windows:**
```batch
build.bat
```

**Manual compilation:**
```bash
javac -d bin src/com/chatapp/server/*.java src/com/chatapp/client/*.java src/com/chatapp/util/*.java
```

### 2. Start the Server

**Windows:**
```batch
run-server.bat
```

**Manual:**
```bash
java -cp bin com.chatapp.server.ChatServer [port]
```

**Default port:** 8080

### 3. Start the Client

**Windows:**
```batch
run-client.bat
```

**Manual:**
```bash
java -cp bin com.chatapp.client.ChatClient [host] [port]
```

**Default connection:** localhost:8080

## 📖 Usage Guide

### Server Commands
- Start server: `java -cp bin com.chatapp.server.ChatServer`
- Custom port: `java -cp bin com.chatapp.server.ChatServer 9000`
- Server will display connection status and active client count

### Client Commands
- Connect to localhost: `java -cp bin com.chatapp.client.ChatClient`
- Connect to remote server: `java -cp bin com.chatapp.client.ChatClient 192.168.1.100 8080`

### Chat Commands
- `/help` - Show available commands
- `/users` - Show online users
- `/me <action>` - Perform an action (e.g., `/me waves`)
- `/whisper <user> <message>` - Send private message
- `/quit` or `/exit` - Disconnect from chat
- `/clear` - Clear console (client only)
- `/status` - Show connection status (client only)

## 🔧 Configuration

### Server Configuration
- **Default Port:** 8080
- **Max Clients:** 100
- **Thread Pool Size:** 100 threads
- **Monitoring Interval:** 30 seconds

### Client Configuration
- **Default Host:** localhost
- **Default Port:** 8080
- **Connection Timeout:** System default
- **Buffer Size:** System default

## 🌐 Network Testing

### Local Testing
1. Start server on one terminal
2. Start multiple clients on different terminals
3. Exchange messages between clients

### Network Testing
1. Start server on one machine
2. Note the server's IP address
3. Start clients on different machines using the server's IP
4. Test cross-network communication

### Port Configuration
- **Firewall:** Ensure port 8080 (or custom port) is open
- **Router:** Forward port if testing across networks
- **Security:** Consider using non-standard ports for production

## 📊 Performance Features

### Server Performance
- **Concurrent Connections:** Up to 100 simultaneous clients
- **Message Broadcasting:** O(n) complexity for n clients
- **Memory Management:** Automatic cleanup of disconnected clients
- **Thread Pool:** Efficient resource utilization

### Client Performance
- **Real-time Messaging:** Non-blocking I/O operations
- **Console Interface:** Responsive user input handling
- **Connection Recovery:** Graceful error handling
- **Resource Management:** Automatic cleanup on disconnect

## 🔍 Troubleshooting

### Common Issues

**Server won't start:**
- Check if port is already in use
- Verify Java installation
- Check firewall settings

**Client can't connect:**
- Verify server is running
- Check hostname/IP address
- Ensure port is correct
- Check network connectivity

**Messages not received:**
- Check network connection
- Verify client is still connected
- Check for firewall interference

### Debug Information
- Server logs connection events
- Client shows connection status
- Use `/status` command for client diagnostics
- Check console for error messages

## 🧪 Testing Scenarios

### Basic Functionality
1. **Single Client:** Connect and send messages
2. **Multiple Clients:** Test message broadcasting
3. **User Join/Leave:** Verify system notifications
4. **Commands:** Test all available commands

### Network Scenarios
1. **Connection Loss:** Test reconnection behavior
2. **Server Restart:** Test client reconnection
3. **High Load:** Test with multiple concurrent clients
4. **Cross-Network:** Test across different networks

### Error Handling
1. **Invalid Commands:** Test command validation
2. **Network Errors:** Test connection failure handling
3. **Resource Cleanup:** Test proper cleanup on disconnect

## 📈 Future Enhancements

### Planned Features
- **Private Messaging:** Direct user-to-user communication
- **File Transfer:** Send files between clients
- **Message History:** Persistent message storage
- **User Authentication:** Secure login system
- **GUI Interface:** Graphical user interface
- **Encryption:** End-to-end message encryption
- **Room System:** Multiple chat rooms
- **User Profiles:** Extended user information

### Technical Improvements
- **Database Integration:** Message persistence
- **WebSocket Support:** Web-based clients
- **Load Balancing:** Multiple server instances
- **Monitoring Dashboard:** Real-time server statistics
- **Logging System:** Comprehensive event logging

## 🎯 Learning Objectives

This project demonstrates:

### Networking Concepts
- **Socket Programming:** TCP/IP communication
- **Client-Server Architecture:** Distributed system design
- **Network Protocols:** Understanding of TCP/IP stack
- **Connection Management:** Handling multiple connections

### Programming Skills
- **Multi-threading:** Concurrent programming
- **I/O Operations:** Stream-based communication
- **Error Handling:** Robust error management
- **Object-Oriented Design:** Modular code structure

### Real-world Applications
- **Instant Messaging:** Similar to WhatsApp, Telegram
- **Chat Applications:** Discord, Slack-like functionality
- **Network Services:** Understanding of network applications
- **Distributed Systems:** Multi-client architecture

## 📚 Technical Documentation

### Key Classes

**ChatServer.java**
- Main server implementation
- Handles client connections
- Manages message broadcasting
- Implements thread pool management

**ClientHandler.java**
- Individual client connection handler
- Implements Runnable for threading
- Manages client authentication
- Handles message processing

**ChatClient.java**
- Client-side implementation
- Dual-threaded design (send/receive)
- Console-based user interface
- Connection management

**NetworkUtils.java**
- Utility functions for networking
- IP address detection
- Port validation
- Network diagnostics

### Design Patterns
- **Thread Pool Pattern:** Efficient resource management
- **Observer Pattern:** Message broadcasting
- **Factory Pattern:** Client handler creation
- **Singleton Pattern:** Server instance management

## 🤝 Contributing

This project is designed for educational purposes and demonstrates fundamental networking concepts. Feel free to:

1. **Fork the repository**
2. **Add new features**
3. **Improve documentation**
4. **Report issues**
5. **Submit pull requests**

## 📄 License

This project is open source and available under the MIT License.

## 🎓 Educational Value

This chat application serves as an excellent learning resource for:

- **Computer Science Students:** Networking and concurrent programming
- **Software Engineers:** Real-world application development
- **Network Administrators:** Understanding network protocols
- **System Architects:** Distributed system design

The project demonstrates practical implementation of theoretical concepts and provides a solid foundation for understanding modern network applications.

---

**Built with ❤️ using Java Socket Programming and Multi-threading** 
