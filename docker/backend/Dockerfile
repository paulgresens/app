#backend
FROM ubuntu
WORKDIR /app
RUN apt-get update -y && apt-get install -y
RUN apt-get install openjdk-17-jdk -y
COPY . .
ENTRYPOINT ["./gradlew", "bootRun"]
EXPOSE 8080