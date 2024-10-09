# Base image with java 21
FROM --platform=linux/amd64 eclipse-temurin:23-jre-alpine

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy the jar file
COPY build/libs/iti0302-2024-backend-0.0.1-SNAPSHOT.jar app.jar

# Set ownership of the jar file
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Expose the port your application runs on (adjust as needed)
EXPOSE 8080

# Use ENTRYPOINT for the main command and CMD for default arguments
ENTRYPOINT ["java"]
CMD ["-Dspring.config.location=classpath:/application.properties,file:/app/application.properties", "-jar", "app.jar"]