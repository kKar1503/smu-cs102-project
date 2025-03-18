@echo off
setlocal

if "%1"=="" (
    :: Check if an argument is provided
    echo Usage: script.bat ^<JavaFilePath^>
    exit /b 1
)

:: Extract the Java file path
set "JAVA_FILE=%1"

:: Extract the package path and class name
for %%F in (%JAVA_FILE%) do set "CLASS_NAME=%%~nF"
set "PACKAGE_PATH=%JAVA_FILE:src\main\java\=%"
set "PACKAGE_PATH=%PACKAGE_PATH:\=.%.%"
set "PACKAGE_PATH=%PACKAGE_PATH:.java=%"

:: Compile the Java file
javac -cp src/main/java -d out %JAVA_FILE%
if %ERRORLEVEL% neq 0 (
    echo Compilation failed.
    exit /b 1
)

:: Run the compiled Java class
java -cp out %PACKAGE_PATH%

endlocal
