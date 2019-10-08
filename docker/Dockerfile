FROM maven:3.6.2-jdk-11

VOLUME /tmp

ARG JAR_FILE

COPY ${JAR_FILE} keepmeawake.jar

EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/keepmeawake.jar"]
