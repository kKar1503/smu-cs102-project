@echo off
setlocal

:: Check if an argument is provided
if "%~1"=="" (
    echo Usage: run.bat [server|client|local]
    exit /b 1
)

:: Set the JAR type based on the argument
set JAR_TYPE=%1

:: Validate the argument
if NOT "%JAR_TYPE%"=="server" if NOT "%JAR_TYPE%"=="client" if NOT "%JAR_TYPE%"=="local" (
    echo Invalid argument: %JAR_TYPE%
    echo Usage: run.bat [server|client|local]
    exit /b 1
)

:: Get the artifactId dynamically from pom.xml
for /f "delims=" %%i in ('mvn help:evaluate -Dexpression^=project.artifactId -q -DforceStdout') do set ARTIFACT_ID=%%i

:: Get the version dynamically from pom.xml
for /f "delims=" %%i in ('mvn help:evaluate -Dexpression^=project.version -q -DforceStdout') do set VERSION=%%i

:: Run the JAR file with the dynamically extracted artifactId and version
java -jar target\%ARTIFACT_ID%-%VERSION%-%JAR_TYPE%.jar
