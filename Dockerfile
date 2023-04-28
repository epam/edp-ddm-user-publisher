FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/user-publisher-*.jar app.jar