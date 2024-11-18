# Imagen base de Maven para construir el proyecto
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar el archivo pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar todo el proyecto y construir
COPY . .
RUN mvn clean package -DskipTests

# Usar una imagen base de Java más ligera para ejecutar el JAR
FROM eclipse-temurin:17-jdk-alpine

# Crear un directorio para la aplicación
WORKDIR /app

# Establecer la zona horaria en el contenedor
ENV TZ=America/Lima
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    apk del tzdata

# Copiar el archivo JAR generado desde la imagen anterior
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto que usa la aplicación Spring Boot
EXPOSE 8080

# Definir las variables de entorno que usarán los valores de Render
ENV SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}
ENV SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
ENV SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
ENV SPRING_MAIL_HOST=${SPRING_MAIL_HOST}
ENV SPRING_MAIL_PORT=${SPRING_MAIL_PORT}
ENV SPRING_MAIL_USERNAME=${SPRING_MAIL_USERNAME}
ENV SPRING_MAIL_PASSWORD=${SPRING_MAIL_PASSWORD}
ENV JWT_SECRET=${JWT_SECRET}
ENV CLOUDINARY_CLOUD_NAME=${CLOUDINARY_CLOUD_NAME}
ENV CLOUDINARY_API_KEY=${CLOUDINARY_API_KEY}
ENV CLOUDINARY_API_SECRET=${CLOUDINARY_API_SECRET}
ENV SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=${SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE}
ENV SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=${SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE}
ENV API_RENIEC_SUNAT_TOKEN=${API_RENIEC_SUNAT_TOKEN}

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
