####
# Build image
####
FROM openjdk:21 AS build
LABEL maintainer=avvero

RUN microdnf install findutils

COPY gradlew /app/
COPY gradle /app/gradle
WORKDIR /app
RUN ./gradlew --version

WORKDIR /app
COPY . .
RUN ./gradlew installBootDist --no-daemon

####
# Runtime image
####
FROM openjdk:21

COPY --from=build /app/build/install/sandbox-boot sandbox

EXPOSE 8080

CMD ["./sandbox/bin/sandbox"]