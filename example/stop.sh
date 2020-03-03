#!/usr/bin/env bash

# working directory for script
THIS_DIR=`dirname $0`

docker-compose -f ${THIS_DIR}/docker-compose.yml down
