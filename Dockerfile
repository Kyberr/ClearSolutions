#syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /opt/app
COPY .mvn .mvn/
COPY pom.xml mvnw ./
COPY src ./src
RUN ./mvnw clean package -Dmaven.test.skip=true
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17-jre-alpine as environment
WORKDIR /opt/app
ARG DEPENDENCY=/opt/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib ./lib/
COPY --from=build ${DEPENDENCY}/META-INF ./META-INF/
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes ./
CMD ["java", "-cp", "/opt/app:/opt/app/lib/*", "com.clearsolutions.ClearSolutionsApplication"]
