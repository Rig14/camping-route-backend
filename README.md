# Matkarajad

## Purpose

The purpose of the application is to enable users to
create and manage their camping routes. Users can share
routes they have visited and other users can comment on them.
Searching for routes and filtering them is also possible.

## Requirements

Requirements to run the Spring Boot application.

- Java 23
- Docker

Optionally

- Grafana and Prometheus for monitoring

## Setup

Required environment variables to run the application.

```dotenv
DB_URL=<postgres-jdbc-connection-string>
DB_USERNAME=<postgres-username>
DB_PASSWORD=<postgres-password>
GF_SECURITY_ADMIN_USER=<grafana-admin-username>
GF_SECURITY_ADMIN_PASSWORD=<grafana-admin-password>
PROFILE=<app-profile:production|development>
```

## Running the backend

For locally running the backend, it is enough to just run the ***src/main/java/Iti03022024BackendApplication***.
It will automatically connect with the database that is running in our server, if correct ***.env*** file is present.

## Building the backend

For dependencies and building, we are using Gradle. So building the project works with `./gradlew clean build`, if Java
is present in the system.

The built application will be under ***build/libs/iti0302-2024-backend-0.0.1-SNAPSHOT.jar***.
This can be run with `java -jar build/libs/iti0302-2024-backend-0.0.1-SNAPSHOT.jar` command.

## Docker container

Docker image for backend is built from the ***Dockerfile*** that is in the project root.

To build the docker image use `docker build -t <insert_your_tag_here> .` command.
In our CI we use: `docker build -t $DOCKER_REGISTRY/$DOCKER_IMAGE:$CI_COMMIT_SHORT_SHA .`

And then the image can be pushed to dockerhub: `docker push <insert_your_tag_here>`.
In our CI we use: `docker push $DOCKER_REGISTRY/$DOCKER_IMAGE:$CI_COMMIT_SHORT_SHA`.

After that the `docker compose pull && docker compose up` can be used to run docker.

> NB! In `compose.yaml` under ***services/spring/image:*** the image name should be changed to your tag.

## Used Technologies

- Java ☕ 23
- Spring Boot 👢
    - Loki4j Logging 🪵
    - Liquibase 🧊
    - Spring Security 🔒
    - Spring Data JPA 📦
    - Spring Web 🕸️
    - Lombok 🦙
- Gradle 📦
- Docker and docker compose 🐳
- PostgreSQL 🐘
- GitLab CI/CD 🚀
- Grafana 📈
    - Loki 🪵
    - Prometheus 📊
        - Node exporter 


