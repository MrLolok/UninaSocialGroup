#
# Setup JavaFX stage
#
FROM alpine
RUN apk add glib gtk+2.0 gtk+3.0 libx11 libxtst mesa-gl musl pango curl unzip
RUN curl --create-dirs -O --output-dir /libs https://download2.gluonhq.com/openjfx/21.0.1/openjfx-21.0.1_linux-x64_bin-sdk.zip
RUN unzip /libs/openjfx-*.zip -d /libs
RUN export JAVAFX_MODULES=/libs/javafx-sdk-21.0.1/lib

#
# Build stage
#
FROM maven:3.9.5 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Start stage
#
FROM openjdk:21 AS start
COPY --from=build /home/app/target/Unina-OO-BD-*.jar /tmp/app.jar
ENTRYPOINT ["java","--module-path","$JAVAFX_MODULES","--add-modules","javafx.controls,javafx.fxml","-jar","/tmp/app.jar"]