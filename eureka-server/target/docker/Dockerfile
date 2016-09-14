FROM java:8
VOLUME /tmp
ADD eureka-server-0.1.0-SNAPSHOT.jar eureka.jar
EXPOSE 8761
RUN bash -c 'touch /eureka.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/eureka.jar"]