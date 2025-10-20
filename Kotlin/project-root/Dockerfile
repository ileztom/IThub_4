FROM eclipse-temurin:17-jdk-jammy
ARG JAR_FILE=build/libs/app-all.jar
WORKDIR /app
COPY build/libs/*-all.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
ENV PORT=8080
CMD ["java","-jar","/app/app.jar"]
