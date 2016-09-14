FROM java:8
VOLUME /tmp
ADD api-gateway-0.1.0-SNAPSHOT.jar apigateway.jar
RUN sh -c 'touch /apigateway.jar'
EXPOSE 8765
ENTRYPOINT ["java","-jar","/apigateway.jar","8765"]
CMD [""]