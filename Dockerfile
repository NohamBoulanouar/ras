# Image de déploiement : on compile avec Maven, puis on ne garde que le JAR sur un JRE 21
FROM eclipse-temurin:21-jdk AS construction
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline
COPY src src
RUN ./mvnw -B -q package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=construction /app/target/ras-*.jar ras.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ras.jar"]
