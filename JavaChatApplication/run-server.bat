@echo off
echo Starting Chat Server...
echo.

REM Check if bin directory exists
if not exist "bin" (
    echo Error: Compiled classes not found in 'bin' directory.
    echo Please run 'build.bat' first to compile the application.
    echo.
    pause
    exit /b 1
)

REM Check if ChatServer class exists
if not exist "bin\com\chatapp\server\ChatServer.class" (
    echo Error: ChatServer class not found.
    echo Please run 'build.bat' first to compile the application.
    echo.
    pause
    exit /b 1
)

echo Chat Server is starting...
echo Default port: 8080
echo To use a different port, run: java -cp bin com.chatapp.server.ChatServer [port]
echo.
echo Press Ctrl+C to stop the server.
echo.

REM Run the server
java -cp bin com.chatapp.server.ChatServer %*

echo.
echo Server stopped.
pause 