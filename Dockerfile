# Use the base image with Java 17.0.3.6.1-amzn
FROM 416670754337.dkr.ecr.eu-west-2.amazonaws.com/ci-corretto-build-17:latest

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from your project directory to the container
COPY certificates_orders.api.ch.gov.uk.jar /app/certificates_orders.api.ch.gov.uk.jar

# Define the command to run your Java application
CMD ["java", "-jar", "certificates_orders.api.ch.gov.uk.jar"]