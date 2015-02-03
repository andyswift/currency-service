FROM dockerfile/java:oracle-java8
MAINTAINER andrew.swift@norwegian.no
EXPOSE 4567
CMD java -jar currency-service.jar
ADD target/currency-service-1.0-SNAPSHOT.jar /data/currency-service.jar
