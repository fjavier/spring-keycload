# =======================================================
# ETAPA 1: Construcción (Generar el JAR) - OPCIONAL, PERO RECOMENDADO
# Esto asume que el Dockerfile se usa para la compilación
# Si el JAR ya está construido, puedes saltar a la Etapa 2.
# =======================================================

# Usamos una imagen base de JDK para la compilación (necesita el compilador)
FROM gradle:8.5.0-jdk21 AS build

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar los archivos de configuración de Gradle y el código fuente
COPY build.gradle settings.gradle ./
COPY src ./src

# Ejecutar la tarea de construcción para generar el JAR ejecutable
# Usamos 'bootJar' para obtener el JAR que Spring Boot puede ejecutar
RUN gradle bootJar --no-daemon

# =======================================================
# ETAPA 2: Runtime (Ejecutar el JAR)
# Usamos JRE para un tamaño de imagen final mucho más pequeño y seguro
# =======================================================

# Usamos una imagen de Java Runtime Environment (JRE) ligera
# Eclipse Temurin es una opción popular y segura
FROM eclipse-temurin:21-jre-alpine

# Argumento para definir el nombre del archivo JAR (útil para consistencia)
ARG JAR_FILE=/app/build/libs/customer-service.jar

# Copiar el JAR compilado desde la etapa de construcción anterior
COPY --from=build ${JAR_FILE} app.jar

# Exponer el puerto en el que Spring Boot se ejecuta por defecto (o el que configuraste, ej: 8082)
EXPOSE 8082

# ENTRYPOINT para ejecutar la aplicación JAR
# El '-jar' es el comando estándar para ejecutar Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]