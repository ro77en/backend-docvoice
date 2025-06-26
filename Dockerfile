FROM openjdk:21-jdk-slim

RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-por \
    curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

EXPOSE 8080

CMD ["sh", "-c", "java -jar target/*.jar"] 