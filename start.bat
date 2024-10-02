@echo off
call gradlew.bat clean
call gradlew.bat bootJar

call docker-compose down
call docker-compose up --build -d
