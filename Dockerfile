#Build Stage
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:21-jdk AS runner

#Dynamic Args
ARG PROFILE=dev
ARG APP_VERSION=1.0.0

WORKDIR /app

COPY --from=builder /app/target/book-worms-*.jar /app/
# COPY --from=builder /app/target/book-worms-*.jar ./app.jar

EXPOSE 8088

ENV DB_URL=jdbc:postgresql://book-worms-db:5432/db
ENV ACTIVE_PROFILE=${PROFILE}
ENV JAR_VERSION=${APP_VERSION}
ENV EMAIL_HOSTNAME=missing_host_name 
ENV EMAIL_USER_NAME=missing_user_name
ENV EMAIL_PASSWORD=missing_password

# ENTRYPOINT ["java", "-jar" , "app.jar"]
CMD java -jar -Dspring.profiles.active=${ACTIVE_PROFILE} -Dspring.datasource.url=${DB_URL} book-worms-${JAR_VERSION}.jar
