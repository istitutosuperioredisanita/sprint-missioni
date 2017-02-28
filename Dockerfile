# DOCKER-VERSION 1.10
FROM anapsix/alpine-java:jdk8
MAINTAINER Francesco Uliana <francesco.uliana@cnr.it>

COPY target/*.war /opt/missioni.war

EXPOSE 8080

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/missioni.war"]
