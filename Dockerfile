FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} market.jar
COPY media media
ENTRYPOINT ["java", "-jar", "/market.jar"]
