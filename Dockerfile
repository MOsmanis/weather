# Use a base image with Java 17
FROM openjdk:17-oracle

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/weather-1.jar app.jar

# Expose the port that your application listens on
EXPOSE 8080 8080
EXPOSE 5050 5050

# Run the application
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5050", "-jar", "/app/app.jar"]