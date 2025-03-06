@echo off

:: Get the artifactId dynamically from pom.xml
for /f "delims=" %%i in ('mvn help:evaluate -Dexpression^=project.artifactId -q -DforceStdout') do set ARTIFACT_ID=%%i

:: Get the version dynamically from pom.xml
for /f "delims=" %%i in ('mvn help:evaluate -Dexpression^=project.version -q -DforceStdout') do set VERSION=%%i

:: Run the JAR file with the dynamically extracted artifactId and version
java -jar target\%ARTIFACT_ID%-%VERSION%.jar