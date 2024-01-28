FROM openjdk:11
WORKDIR /app
COPY ./target/eurekaserver-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT java -jar app.jar
