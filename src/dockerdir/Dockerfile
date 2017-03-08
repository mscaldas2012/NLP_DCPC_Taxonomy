FROM openjdk
VOLUME /tmp

ADD CodingServices.jar .

RUN sh -c 'touch /CodingServices.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","CodingServices.jar"]