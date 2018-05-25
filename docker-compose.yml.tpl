version: '2'

services:

  missioni:
    image: docker.si.cnr.it/##{CONTAINER_ID}##
    volumes:
    - /tmp
    - /logs
    network_mode: bridge
    read_only: true
    command: java -Xmx512m -Xss512k -Dserver.port=8080 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8787 -Djava.security.egd=file:/dev/./urandom -jar /opt/missioni.war --spring.profiles.active=dev,cnr
    labels:
      SERVICE_NAME: "##{SERVICE_NAME}##"