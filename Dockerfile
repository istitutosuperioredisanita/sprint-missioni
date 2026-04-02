# Immagine base Java 21 leggera
FROM eclipse-temurin:21-jdk-alpine

# Maintainer (informativo)
LABEL maintainer="Davide Mirra <davide.mirra@iss.it>"

# Crea utente non root (best practice sicurezza)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Directory di lavoro
WORKDIR /opt

# Copia il WAR (deve esistere in target/)
COPY target/*.war /opt/missioni.war

# Permessi corretti
RUN chown appuser:appgroup /opt/missioni.war

# Usa utente non root
USER appuser

# Porta applicazione
EXPOSE 8088

# Avvio applicazione
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/missioni.war"]