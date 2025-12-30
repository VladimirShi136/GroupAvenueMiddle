# ============================================
# ATM Эмулятор - Docker
# ============================================

# Основные варианты для Java 23:

# 1. Официальный OpenJDK 23 (совместимый с Oracle) не работает
# FROM openjdk:23-jdk-alpine

# 2. ИЛИ Eclipse Temurin 23 (рекомендуется)
FROM eclipse-temurin:23-jdk-alpine

# 3. ИЛИ BellSoft Liberica 23
# FROM bellsoft/liberica-openjdk-alpine:23

# Настройки для русского языка (ОБЯЗАТЕЛЬНО!)
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

# Создаем не-root пользователя для безопасности
RUN addgroup -S atmgroup && adduser -S atmuser -G atmgroup

# Рабочая директория
WORKDIR /app

# Копируем собранный JAR
COPY build/libs/*.jar app.jar

# Меняем владельца файлов
RUN chown -R atmuser:atmgroup /app

# Переключаемся на не-root пользователя
USER atmuser

# Точка входа
ENTRYPOINT ["java", "-jar", "app.jar"]