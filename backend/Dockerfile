FROM gradle:7.3.3-jdk17 as builder
COPY --chown=gradle:gradle . /home/gradle/src/
WORKDIR /home/gradle/src
RUN gradle bootJar
RUN ls -l
COPY ./build/libs/backend-1.0.jar taggit.jar
RUN java -Djarmode=layertools -jar taggit.jar extract

#FROM openjdk:17.0.1-slim
#WORKDIR taggit
#COPY --from=builder taggit/dependencies/ ./
#COPY --from=builder taggit/spring-boot-loader/ ./
#COPY --from=builder taggit/snapshot-dependencies/ ./
#COPY --from=builder taggit/application/ ./
#EXPOSE 8080
#ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
