version: '2'

services:

  missioni:
    image: docker.si.cnr.it/##{CONTAINER_ID}##
    volumes:
    - /tmp
    - /logs
    network_mode: bridge
    read_only: true
    command: java -Xmx512m -Xss512k -Dserver.port=8080 -Djava.security.egd=file:/dev/./urandom -jar /opt/missioni.war --spring.profiles.active=dev,cnr
    labels:
    - SERVICE_NAME=##{SERVICE_NAME}##

  rabbitmq:
    image: rabbitmq:3-management-alpine
    network_mode: bridge
    hostname: 4d3645a27c68 # in order to mount rabbitmq/lib/mnesia/rabbit@4d3645a27c68
    environment:
    - RABBITMQ_DEFAULT_USER=cnr
    - RABBITMQ_DEFAULT_PASS=bubbazza
    labels:
    - SERVICE_NAME=missioni-rabbit
    - traefik.port=15672
    read_only: true
    volumes:
    - ./rabbitmq/etc/:/etc/rabbitmq/
    - ./rabbitmq/lib/:/var/lib/rabbitmq/
    ports:
    - "15672:15672"
    - "5672:5672"
