@echo off
echo Starting Chat Client...
echo.

REM Check if bin directory exists
if not exist "bin" (
    echo Error: Compiled classes not found in 'bin' directory.
    echo Please run 'build.bat' first to compile the application.
    echo.
    pause
    exit /b 1
)

REM Check if ChatClient class exists
if not exist "bin\com\chatapp\client\ChatClient.class" (
    echo Error: ChatClient class not found.
    echo Please run 'build.bat' first to compile the application.
    echo.
    pause
    exit /b 1
)

echo Chat Client is starting...
echo Default connection: localhost:8080
echo To connect to a different server, run: java -cp bin com.chatapp.client.ChatClient [host] [port]
echo.
echo Make sure the server is running before starting the client.
echo.

REM Run the client
java -cp bin com.chatapp.client.ChatClient %*

echo.
echo Client disconnected.
pause 