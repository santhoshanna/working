FROM java:8
VOLUME /tmp
ADD plm-subscriber-ms-0.1.0-SNAPSHOT.jar subscriber.jar
RUN sh -c 'touch /subscriber.jar'
EXPOSE 8000
ENTRYPOINT ["java","-jar","/subscriber.jar","8000"]
CMD [""]