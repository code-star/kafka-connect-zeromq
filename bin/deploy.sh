#!/usr/bin/env bash

LIBS=build/libs

echo "== Building source code, testing, and creating fat jar..."
./gradlew clean shadowJar

CONNECTOR_OWNER=codestar
CONNECTOR_NAME=kafka-connect-zeromq
CONNECTOR_VERSION=0.1
ZIPFILE=$CONNECTOR_OWNER-$CONNECTOR_NAME-$CONNECTOR_VERSION.zip
JARFILE=$CONNECTOR_NAME-$CONNECTOR_VERSION-all.jar

echo "== Compressing fat jar into $LIBS/$ZIPFILE"
zip $LIBS/$ZIPFILE $LIBS/$JARFILE

echo "== Building Docker image"
docker build . -t $CONNECTOR_OWNER/$CONNECTOR_NAME:$CONNECTOR_VERSION