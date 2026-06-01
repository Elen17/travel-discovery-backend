# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Cache dependency layer separately (only re-downloads when pom.xml changes)
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/target/*.jar app.jar

# Upload directory (used when STORAGE_PROVIDER=local).
# No VOLUME instruction: Railway rejects Dockerfile VOLUME and manages
# persistence via Railway Volumes mounted at /app/uploads instead.
RUN mkdir -p /app/uploads
EXPOSE 8080

ENTRYPOINT ["java", \
  "-Xms128m", "-Xmx256m", \
  "-Xss256k", \
  "-XX:+UseSerialGC", \
  "-XX:MaxMetaspaceSize=128m", \
  "-jar", "app.jar"]
