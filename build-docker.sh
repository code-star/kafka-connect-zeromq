#!/usr/bin/env bash

LIBS=build/libs

CONNECTOR_OWNER=codestar
CONNECTOR_NAME=kafka-connect-zeromq
CONNECTOR_VERSION=0.1
JARFILE=$CONNECTOR_NAME-$CONNECTOR_VERSION-all.jar
ZIPFILE=$CONNECTOR_OWNER-$CONNECTOR_NAME-$CONNECTOR_VERSION.zip

if [ ! -f "$LIBS/$JARFILE" ]; then
    echo "$LIBS/$JARFILE does not exist; rebuild source code"
    exit 1
fi

echo "== Compressing uber jar into $LIBS/$ZIPFILE"
zip $LIBS/$ZIPFILE $LIBS/$JARFILE || exit 1

if [ ! -f "$LIBS/$ZIPFILE" ]; then
    echo "$LIBS/$ZIPFILE does not exist"
    exit 1
fi

echo "== Building Docker image"
docker build . -t $CONNECTOR_OWNER/$CONNECTOR_NAME:$CONNECTOR_VERSION