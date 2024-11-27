# Use the official OpenJDK image to build the Spring Boot application
FROM openjdk:17-jdk-slim as build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven build file and download dependencies (this helps caching dependencies)
COPY cds-backend/pom.xml .
RUN mvn dependency:go-offline

# Copy the whole backend source code and build the application
COPY cds-backend/src ./src
RUN mvn clean package -DskipTests

# Create the final image from the JDK 17 base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled Spring Boot jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app is running on (default 8080)
EXPOSE 8080

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]