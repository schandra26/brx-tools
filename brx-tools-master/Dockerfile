ARG ARTIFACTORY_URL=artifacts.devops.bfsaws.net
FROM ${ARTIFACTORY_URL}/docker-proxy/maven:3-eclipse-temurin-21-alpine AS builder

WORKDIR /build

COPY . .
COPY maven_settings.xml ./settings.xml

RUN mvn clean dependency:go-offline install -Dmaven.test.skip=true --quiet --settings settings.xml

# runtime_base start
FROM $ARTIFACTORY_URL/docker-proxy/eclipse-temurin:21-jre-alpine AS runtime_base
USER root

# Installs setcap
RUN apk update && apk add libcap-setcap && apk info


FROM runtime_base AS runtime
USER root

ARG VERSION
ARG GIT_SHA
ARG SERVICE_NAME
ARG DESCRIPTION="BRx Tools"
ARG BRX_GITLAB_TOKEN

LABEL   Version="$VERSION" \
        Description="$DESCRIPTION" \
        Maintainer="EPM" \
        Maintainer_Email="corina.roman@broadridge.com" \
        Business_Unit="GTO" \
        Git_SHA="$GIT_SHA" \
        Product="${SERVICE_NAME}"

# Alow java process to bind to port 443
RUN setcap 'cap_net_bind_service=+ep' $(which java)
RUN mkdir /brx
RUN keytool -genkeypair \
    -alias brx-ssl \
    -keyalg RSA \
    -keysize 2048 \
    -validity 365 \
    -storetype PKCS12 \
    -keystore /brx/keystore.p12 \
    -storepass changeit \
    -dname "CN=eisl, OU=IT, O=Broadridge, L=Toronto, S=ON, C=CA"

EXPOSE 443
USER root

WORKDIR /app
COPY --from=builder /build/brx-matmap-parser/target/brx-matmap-parser-*.jar app.jar

ENV JAVA_OPTS="-DGITLAB_ACCESS_TOKEN=$BRX_GITLAB_TOKEN"

ENTRYPOINT java ${JAVA_OPTS} -jar app.jar
