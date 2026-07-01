FROM eclipse-temurin:8-jre
WORKDIR /app
COPY game-be-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-Duser.timezone=GMT+08", "-jar", "app.jar"]
