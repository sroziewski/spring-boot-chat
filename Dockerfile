FROM adoptopenjdk/openjdk11:latest
COPY ./target/spring-boot-chat-1.0.0.war /run
WORKDIR /run
RUN sh -c 'touch spring-boot-chat-1.0.0.war'
ENTRYPOINT ["java", "-jar", "spring-boot-chat-1.0.0.war"]