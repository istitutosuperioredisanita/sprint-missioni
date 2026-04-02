version: '3.8'

services:

  missioni:

    image: docker.si.cnr.it/##{CONTAINER_ID}##
    container_name: missioni

    restart: unless-stopped  # riavvio automatico

    volumes:
      - /data/sprint-missioni-config:/opt/config
      # Config esterna per Spring Boot

    command: >
      java
      -Xmx1g
      -Xss512k
      -Duser.language=it
      -Dspring.profiles.active=prod,cnr,iss
      -Dspring.config.additional-location=file:/opt/config/
      -Djava.security.egd=file:/dev/./urandom
      -jar /opt/missioni.war

    ports:
      - "8088:8088"

    # Debug DISABILITATO (attivalo solo se serve)
    # - "8787:8787"

    labels:
      SERVICE_NAME: "##{SERVICE_NAME}##"

    healthcheck:
      test: ["CMD", "wget", "-qO-", "http://localhost:8088"]
      interval: 30s
      timeout: 10s
      retries: 5