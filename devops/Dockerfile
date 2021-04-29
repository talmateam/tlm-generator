FROM openjdk:11-jre-slim

# Create app directory
RUN mkdir -p /opt/logs
WORKDIR /opt

RUN apt update && \
    apt install -y openssh-client && \
    mkdir -p /root/.ssh && \
    ssh-keyscan -t rsa bitbucket.org > /root/.ssh/known_hosts && \
    apt remove -y openssh-client && \
    rm -rf /var/lib/apt/lists/
ADD id_rsa* /root/.ssh/
ADD *.jar /opt/
RUN java -jar /opt/sbt-launch.jar sbtVersion
#COPY target/${artifact} /usr/src/app/${artifact_id}/elasticapm.properties
EXPOSE 8080

CMD ["sh", "-c", "java -Duser.timezone=America/Lima  -jar *-app-*.jar"]


