FROM eclipse-temurin:17

WORKDIR /app

COPY target/*.jar user-service.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","user-service.jar"]