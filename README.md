# spring-boot-chat in Docker
Web App working with JMS API Apache ActiveMQ 5.15 working on Spring Boot Framework 5 and Primefaces 7

## Instructions

To build a war package run below command

```mvn clean package```

After successful package building, create a docker image

```docker build -t spring-boot-chat .```

and run with binding to localhost:8080

```docker run -p 127.0.0.1:8080:8080 -it spring-boot-chat sh```

Afterwards, visit the address in your browser and enjoy

```http://localhost:8080/index.xhtml```



