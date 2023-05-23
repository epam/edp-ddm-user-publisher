FROM nexus-docker-registry.apps.cicd2.mdtu-ddm.projects.epam.com/openjdk:11.0.16-jre-slim
ENV USER_UID=1001 \
    USER_NAME=user-publisher
RUN addgroup --gid ${USER_UID} ${USER_NAME} \
    && adduser --disabled-password --uid ${USER_UID} --ingroup ${USER_NAME} ${USER_NAME}
WORKDIR /app
USER user-publisher
COPY target/user-publisher-*.jar app.jar
