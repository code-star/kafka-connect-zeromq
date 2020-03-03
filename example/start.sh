#!/usr/bin/env bash

# working directory for script
THIS_DIR=`dirname $0`

# Check if jq exists
which -s jq || { >&2 echo 'Missing requirement: jq (OS X: brew install jq)' ; exit 1; }

# Check if connector images are loaded
docker images | grep kafka-connect-zeromq || { >&2 echo 'Missing requirement: Docker image of kafka-connect-zeromq' ; exit 1; }

docker-compose -f ${THIS_DIR}/docker-compose.yml up -d
