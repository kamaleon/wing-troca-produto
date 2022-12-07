FROM adoptopenjdk/openjdk11:jdk-11.0.5_10-alpine

ARG environment
ARG repo
ENV AWS_ENV_PATH=/$repo/$environment
ENV AWS_REGION=us-east-1

RUN apk update && apk upgrade && apk add --no-cache curl openssl ca-certificates wget
RUN apk add --no-cache tzdata
ENV TZ America/Recife

WORKDIR /opt
COPY ./target/wing*.jar /opt/wing.jar

RUN curl -sSL https://github.com/Droplr/aws-env/raw/master/bin/aws-env-linux-amd64 -o aws-env && chmod +x aws-env
CMD eval $(./aws-env) && java -jar wing.jar