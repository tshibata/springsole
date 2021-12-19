FROM openjdk:11

COPY target/demo-0.0.1-SNAPSHOT.war /demo.war

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/demo.war", "-Dspring-boot.run.arguments=/mnt/external/standby.mv.db"]

