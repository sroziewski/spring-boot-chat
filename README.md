# Demo JMS Spring Boot Chat in Docker
Demo WebApp working with JMS API Apache ActiveMQ 5.15 working on Spring Boot 2  SpringFramework 5 and Primefaces 7

## Instructions

To build a war package run below command

```mvn clean package```

After successful package building, create a docker image

```docker build -t spring-boot-chat .```

and run with binding to localhost:8080

```docker run -p 127.0.0.1:8080:8080 -it spring-boot-chat sh```

Afterwards, visit the address in your browser and enjoy

```http://localhost:8080/index.xhtml```

## One-liner

You can clone the github repository to the *current_directory/spring-boot-chat*, create package and test, build a docker image and run it. Finally, visit the project WebApp location via *google-chrome*
     
```git clone https://github.com/sroziewski/spring-boot-chat.git && cd spring-boot-chat && mvn clean package && docker build -t spring-boot-chat . && docker run -p 127.0.0.1:8080:8080 -it -d spring-boot-chat sh && sleep 15 && google-chrome http://localhost:8080/index.xhtml```

