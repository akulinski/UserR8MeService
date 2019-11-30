FROM maven:3.6.2-jdk-11

VOLUME /tmp

ARG JAR_FILE

COPY ${JAR_FILE} r8meuserservice.jar

EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/r8meuserservice.jar","-Dspring.profiles.active=dev,log-web,mongo-cluster,admin-ui"]
