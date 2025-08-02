@echo off
echo Building Java Chat Application...
echo.

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile all Java files
echo Compiling source files...
javac -d bin -cp src src/com/chatapp/server/*.java src/com/chatapp/client/*.java src/com/chatapp/util/*.java

REM Check if compilation was successful
if %ERRORLEVEL% EQU 0 (
    echo.
    echo Build successful! Compiled classes are in the 'bin' directory.
    echo.
    echo To run the server: java -cp bin com.chatapp.server.ChatServer
    echo To run the client: java -cp bin com.chatapp.client.ChatClient
    echo.
) else (
    echo.
    echo Build failed! Please check the error messages above.
    echo Make sure you have Java JDK installed and JAVA_HOME is set correctly.
    echo.
)

pause 