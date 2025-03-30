#!/bin/bash

# Check if an argument is provided
if [ -z "$1" ]; then
    echo "Usage: ./run.sh [server|client|local]"
    exit 1
fi

# Validate the argument
case "$1" in
    server|client|local)
        JAR_TYPE="$1"
        ;;
    *)
        echo "Invalid argument: $1"
        echo "Usage: ./run.sh [server|client|local]"
        exit 1
        ;;
esac

# Get the artifactId and version from pom.xml dynamically
ARTIFACT_ID=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

# Run the JAR file with the dynamically extracted artifactId and version
java -jar target/"${ARTIFACT_ID}"-"${VERSION}"-"${JAR_TYPE}".jar
