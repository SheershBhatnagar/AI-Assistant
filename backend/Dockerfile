# --- Stage 1: Build the Application ---
FROM gradle:8-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# This builds a 'Fat Jar' containing your app and all dependencies
RUN gradle buildFatJar --no-daemon

# --- Stage 2: Run the Application ---
FROM eclipse-temurin:21-jre-alpine
EXPOSE 8080
RUN mkdir /app
# Copy only the compiled Fat Jar from the build stage
COPY --from=build /home/gradle/src/build/libs/*-all.jar /app/ktor-app.jar

# Start the server
ENTRYPOINT ["java", "-jar", "/app/ktor-app.jar"]
