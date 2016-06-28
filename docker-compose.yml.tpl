missioni:
  image: docker.si.cnr.it/##{CONTAINER_ID}##
  links:
  - postgres:missioni-postgres
  volumes:
  - /tmp
  - /logs
  read_only: true
  command: java -Xmx512m -Xss512k -Dserver.port=8080 -Djava.security.egd=file:/dev/./urandom -jar /opt/missioni.war --spring.profiles.active=dev --spring.datasource.url=jdbc:postgresql://missioni-postgres:5432/missioni
  environment:
    - SERVICE_TAGS=webapp
    - SERVICE_NAME=##{SERVICE_NAME}##
postgres:
  image: docker.si.cnr.it/postgres-missioni
  ports:
  - "54321:5432"

