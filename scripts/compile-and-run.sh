#!/bin/bash

# Check if an argument is provided
if [ -z "$1" ]; then
    echo "Usage: script.sh <JavaFilePath>"
    exit 1
fi

# Extract the Java file path
JAVA_FILE="$1"

# Extract the package path and class name
CLASS_NAME=$(basename "$JAVA_FILE" .java)
PACKAGE_PATH=${JAVA_FILE#src/main/java/}
PACKAGE_PATH=${PACKAGE_PATH//\//.}
PACKAGE_PATH=${PACKAGE_PATH%.java}

# Compile the Java file
javac -cp src/main/java -d out "$JAVA_FILE"
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

# Run the compiled Java class
java -cp out "$PACKAGE_PATH"
