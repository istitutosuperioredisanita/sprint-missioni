FROM eclipse-temurin:21-jdk-alpine
LABEL maintainer="Davide Mirra <davide.mirra@iss.it>"

COPY target/*.war /opt/missioni.war

EXPOSE 8080

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/missioni.war"]