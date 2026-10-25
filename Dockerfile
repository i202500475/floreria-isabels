# Imagen de la tienda Isabel's: compila Angular y Spring Boot y los empaqueta en un solo JAR.

# 1) Frontend Angular
FROM node:20-alpine AS frontend
WORKDIR /app
COPY floreria-frontend/package*.json ./
RUN npm install --no-audit --no-fund --loglevel=error
COPY floreria-frontend/ ./
RUN npx ng build --configuration production --base-href /tienda/

# 2) Backend Spring Boot (incluye el Angular compilado en /tienda)
FROM maven:3.9-eclipse-temurin-17 AS backend
WORKDIR /app
COPY ProyectoFloreria/pom.xml ./
RUN mvn -B -q dependency:go-offline
COPY ProyectoFloreria/src ./src
COPY --from=frontend /app/dist/floreria-frontend/browser/ ./src/main/resources/static/tienda/
RUN mvn -B -q package -DskipTests && cp $(ls target/*.jar | grep -v original | head -1) app.jar

# 3) Imagen final, solo con Java 17
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend /app/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
