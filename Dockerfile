FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar /app/

EXPOSE 8087

CMD ["sh", "-c", "java -jar $(ls /app/*.jar | head -n 1)"]
