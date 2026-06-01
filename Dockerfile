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

# Non-root runtime user. su-exec lets the entrypoint drop privileges after it
# has fixed ownership of the volume mounted at runtime.
RUN addgroup -S appgroup && adduser -S appuser -G appgroup \
    && apk add --no-cache su-exec

COPY --from=build /app/target/*.jar app.jar

# Upload directory (used when STORAGE_PROVIDER=local).
# No VOLUME instruction: Railway rejects Dockerfile VOLUME and manages
# persistence via Railway Volumes mounted at /app/uploads instead.
RUN mkdir -p /app/uploads && chown -R appuser:appgroup /app

EXPOSE 8080

# The container starts as root so the entrypoint can chown the Railway Volume
# (mounted root-owned at /app/uploads, which masks the build-time chown above),
# then su-exec drops to appuser so the JVM itself never runs as root.
ENTRYPOINT ["sh", "-c", "mkdir -p /app/uploads && chown -R appuser:appgroup /app/uploads && exec su-exec appuser:appgroup java -Xms128m -Xmx256m -Xss256k -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m -jar app.jar"]
