missioni:
  image: docker.si.cnr.it/##{CONTAINER_ID}##
  volumes:
  - /tmp
  - /logs
  read_only: true
  command: java -Xmx512m -Xss512k -Dserver.port=8080 -Djava.security.egd=file:/dev/./urandom -jar /opt/missioni.war --spring.profiles.active=dev,cnr
  environment:
    - SERVICE_TAGS=webapp
    - SERVICE_NAME=##{SERVICE_NAME}##
