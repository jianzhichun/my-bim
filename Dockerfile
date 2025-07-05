FROM openjdk:8-alpine
COPY /target/bimplatform.jar /usr/local/lib/bimplatform.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/bimplatform.jar","--spring.profiles.active=prod,minio"]