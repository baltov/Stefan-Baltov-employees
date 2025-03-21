# Stage 1: Build the Backend with Gradle
FROM gradle:7.6-jdk17 as builder-backend
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle build -x test

# Stage 2: Build the Frontend with Node.js
FROM node:18-alpine AS builder-frontend
WORKDIR /frontend

# Copy frontend package files and install dependencies
COPY ./frontend/package.json ./frontend/package-lock.json ./
RUN npm install

# Copy and build the frontend
COPY ./frontend/. .
RUN npm run build

# Stage 3: Nginx and Java Final Image
FROM nginx:alpine AS runtime

# Remove default Nginx website
RUN rm -rf /usr/share/nginx/html/*

# Copy built frontend files from builder-frontend stage into Nginx HTML folder
COPY --from=builder-frontend /frontend/dist /usr/share/nginx/html

# Copy backend JAR file from builder-backend stage
WORKDIR /opt/app
COPY --from=builder-backend /app/build/libs/*.jar app.jar

# Install OpenJDK and Supervisor (to manage Nginx + Java processes)
RUN apk add --no-cache openjdk17 supervisor

# Add Supervisor configuration for managing both Nginx and Java
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Start Supervisor to manage Nginx and Java
CMD ["supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]