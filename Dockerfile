FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/openlims.jar /app/openlims.jar
CMD ["java", "-jar", "openlims.jar"]
