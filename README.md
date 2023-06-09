Requirements:
Java 17
Docker Desktop

Steps:
1. mvn clean install
2. docker-compose up --build

Calling a local environment will result in PrivateIpAddressException, in such case try localhost:8080/weather/{ip}