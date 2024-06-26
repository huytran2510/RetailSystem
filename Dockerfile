FROM openjdk:11
ADD target/retail_system.jar retail_system.jar
ENTRYPOINT ["java","-jar","/retail_system.jar"]