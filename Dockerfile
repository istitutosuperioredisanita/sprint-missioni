# DOCKER-VERSION 1.10
FROM anapsix/alpine-java:jdk8
MAINTAINER Francesco Uliana <francesco.uliana@cnr.it>

RUN wget "http://maven.si.cnr.it/service/local/artifact/maven/redirect?r=releases&v=LATEST&g=it.cnr.si.missioni&a=missioni&e=war" -O /opt/missioni.war

EXPOSE 8080

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/missioni.war"]
