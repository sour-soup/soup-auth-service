#!/bin/bash
./gradlew clean
./gradlew bootJar
docker-compose stop
docker-compose up --build -d