@echo off
setlocal

if "%1"=="" (
    :: Check if an argument is provided
    echo Usage: script.bat ^<JavaFilePath^>
    exit /b 1
)

:: Extract the Java file path
set "JAVA_FILE=%1"

:: Extract the relative path from src\main\java\
set "REL_PATH=%JAVA_FILE:src\main\java\=%"

:: Remove leading backslashes (if any)
for /f "tokens=* delims=\" %%A in ("%REL_PATH%") do set "REL_PATH=%%A"

:: Convert path to package notation
set "PACKAGE_PATH=%REL_PATH:\=.%"
set "PACKAGE_PATH=%PACKAGE_PATH:.java=%"

:: Trim trailing periods (if any)
for /f "tokens=* delims=." %%A in ("%PACKAGE_PATH%") do set "PACKAGE_PATH=%%A"

:: Compile the Java file
javac -cp src/main/java -d out %JAVA_FILE%
if %ERRORLEVEL% neq 0 (
    echo Compilation failed.
    exit /b 1
)

:: Run the compiled Java class
java -cp out %PACKAGE_PATH%

endlocal
