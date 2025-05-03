FROM amazoncorretto:17.0.7-alpine3.17
LABEL maintainer="thanksdiaryofficial@gmail.com"

ARG SPRING_PROFILES_ACTIVE

ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE
COPY ./src/main/resources/application-develop.yml /app/config/
COPY ./src/main/resources/application-prod.yml /app/config/
COPY ./build/libs/thanks-diary-0.0.1-SNAPSHOT.jar application.jar
CMD java -jar application.jar --spring.config.location=classpath:/application.yml,/app/config/ \
  --spring.profiles.active=$SPRING_PROFILES_ACTIVE
