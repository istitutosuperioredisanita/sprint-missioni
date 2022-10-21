# DOCKER-VERSION 1.10
FROM anapsix/alpine-java:jdk8
MAINTAINER Marcin I. Trycz marcinireneusz.trycz@cnr.it

COPY target/*.war /opt/missioni.war

EXPOSE 8080 8899

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8899", "-jar", "/opt/missioni.war"]
