# 使用官方 OpenJDK 镜像作为基础镜像
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 将构建好的 JAR 文件复制到 Docker 容器中的 /app 目录
COPY target/autoSubmitTask-0.0.1-SNAPSHOT.jar /app/autoSubmitTask.jar

# 暴露 Spring Boot 应用监听的端口
EXPOSE 8088

# 设置容器启动时执行的命令
ENTRYPOINT ["java", "-jar", "/app/autoSubmitTask.jar"]
