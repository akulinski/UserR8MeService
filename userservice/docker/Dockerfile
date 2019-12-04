FROM maven:3.6.2-jdk-11

VOLUME /tmp

ARG TEMPLATES
ARG JAR_FILE

COPY ${TEMPLATES} templates

COPY ${JAR_FILE} r8meuserservice.jar

EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring-boot.run.profiles=dev,log-web,mongo-cluster,admin-ui","-jar","/r8meuserservice.jar"]
