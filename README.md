Kafka Connect ZeroMQ
====================

A Kafka Connect source connector for the ZeroMQ messaging library. 

Requirements
------------

- Java 8
- Docker 19

Building
--------

Build the source code and create a uber jar containing all of the classfiles for the plugin and its third-party dependencies:

    ./gradlew clean shadowJar

Installation
------------

There are two ways of installing a Kafka Connect plugin. The first is to build a single uber jar and add it to a Kafka Connect plugin path.

The second is to create a directory on the file system that contains the jar files for the plugin and its third-party dependencies
(See [Create a Docker Image containing Local Connectors](https://docs.confluent.io/current/connect/managing/extending.html#create-a-docker-image-containing-local-connectors)).

For building the Docker image locally, run:

    ./build-docker.sh
    
Optional: check that the Docker image is installed with: `docker images | grep zeromq`.

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

For an example, see [this config file](docs/zeromq-source-config-example.json).

Authors
-------

- Hernán Vanzetto (https://github.com/hvanz)

TODO
----

- Add sink connector.
- Currently we use ZeroMQ in pub/sub mode. Check other modes of operation to make the connector more generic.
- Make source and sink two separate Gradle projects.