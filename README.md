Kafka Connect ZeroMQ
====================

A Kafka Connect source connector for the ZeroMQ messaging library. 

For the moment, it only works in publish/subscribe mode.

Requirements
------------

- Java 8
- Gradle 6.2
- Docker 19

Installation
------------

Build the source code, and then build and deploy locally the Docker image with:

    ./bin/deploy.sh
    
You can check that the Docker image is installed with:

    docker images | grep zeromq 

Configuration
-------------

In addition to the default configuration for Kafka connectors (e.g. `name`, `connector.class`, etc.) the following options are available:

| name                     | data type       | required | default | description                                                   |
|:-------------------------|:----------------|:---------|:--------|:--------------------------------------------------------------|
| `zeromq.address`         | string          | yes      |         | ZeroMQ server address                                         |
| `zeromq.envelopes`       | list of strings | yes      |         | comma separated list of envelopes to subscribe                |
| `zeromq.pollInterval`    | string          | yes      |    PT1S | how often the ZeroMQ source is polled; in ISO8601 format      |
| `zeromq.maxBackoff`      | string          | yes      |   PT60S | on failure, exponentially backoff time cap; in ISO8601 format |
| `zeromq.maxPollRecords`  | integer         | yes      |    1000 | maximum number of records returned per poll                   |
| `zeromq.nrIoThreads`     | integer         | no       |       1 | number of ZeroMQ threads                                      |
| `zeromq.kafka.topic`     | string          | yes      |         | Kafka topic to write the messages to                          |

For an example, see [this config file](example/zeromq-source-config.ndovloket-example.json).

How to use
----------

The following example in distributed mode will use the ZeroMQ connector for fetching NS train locations records.

Start Zookeeper, Kafka, Kafka Connect, etc:

    ./example/start.sh

Check that all containers are running and started, specially the `connect` service:

    ./example/wait-for-service.sh

check that our connector is listed as available to the `connect` service: 

    curl localhost:8083/connector-plugins | jq

and then add the connector by posting a configuration to the Kafka Connect REST API:

    curl -s -X POST -H 'Content-Type: application/json' \
        --data @example/zeromq-source-config.ndovloket-example.json \
        http://localhost:8083/connectors | jq

To check that the connector is running, we can try to consume some messages:

    docker-compose -f example/docker-compose.yml exec connect kafka-console-consumer \
        --topic kafka-topic-1 --bootstrap-server kafka-broker-0:19092 \
        --property print.key=true --max-messages 5

or we can verify the `connect` service logs:

    docker logs -f connect

We can also verify whether our setup is working as expected by 
using the Confluent Control Center, which gives us the possibility 
to monitor our Kafka broker, topics, consumers, connectors etc. 
To access it, navigate to the host and port where it’s running 
--in our case it would be http://localhost:9021/.

Finally, stop all running services with:

    ./example/stop.sh

TODO
----

- Add sink connector.
- Currently we use ZeroMQ in pub/sub mode. Check other modes of operation to make the connector more generic.
- Make source and sink two separate Gradle projects.