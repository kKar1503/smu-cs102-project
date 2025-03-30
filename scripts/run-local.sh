#!/bin/bash

# Get the artifactId and version from pom.xml dynamically
ARTIFACT_ID=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

# Run the JAR file with the dynamically extracted artifactId and version
java -jar target/"${ARTIFACT_ID}"-"${VERSION}"-local.jar
