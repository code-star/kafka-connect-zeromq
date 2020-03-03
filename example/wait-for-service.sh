#!/usr/bin/env bash

SERVICE=${1:-connect}
STARTED_TEXT=${2:-"Kafka Connect started"}

WORKING_DIR=`dirname $0`

echo "Waiting for ${SERVICE} service to start"
while [[ -z $RESPONSE ]]
do
    printf "."
    sleep 1;
    RESPONSE=`docker-compose -f ${WORKING_DIR}/docker-compose.yml logs connect | grep "$STARTED_TEXT"`
done
echo

echo "${SERVICE} service has started."
