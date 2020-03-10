ARG BASE_PREFIX=confluentinc
ARG CONNECT_IMAGE=cp-kafka-connect-base
ARG CP_VERSION=5.4.0
FROM $BASE_PREFIX/$CONNECT_IMAGE:$CP_VERSION

# In docker-compose, add CONNECT_PLUGIN_PATH: "/etc/kafka-connect/jars", or:
ARG IMAGE_DIR=/etc/kafka-connect/jars
#ENV CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/confluent-hub-components,${IMAGE_DIR}"

ARG COMPONENT_OWNER=codestar
ARG COMPONENT_NAME=kafka-connect-zeromq
ARG COMPONENT_VERSION=0.1
ARG ZIP_FILE=${COMPONENT_OWNER}-${COMPONENT_NAME}-${COMPONENT_VERSION}.zip
COPY build/libs/${ZIP_FILE} ${IMAGE_DIR}

# Instead of copying the zip file to IMAGE_DIR and adding IMAGE_DIR to the plugin path,
# we could install the zip file directly using:
# RUN confluent-hub install --no-prompt ${IMAGE_DIR}/${ZIP_FILE}
# but this command requires the zip file to have the proper component archive format
# (see https://docs.confluent.io/current/connect/managing/confluent-hub/component-archive.html).
