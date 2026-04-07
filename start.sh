#!/bin/bash

echo "========================================"
echo "华为NCE网管 - 一键启动脚本"
echo "========================================"

cd "$(dirname "$0")"

echo "[1/2] 启动 business 微服务 (端口8009)..."
cd business
nohup mvn spring-boot:run -DskipTests > ../logs/business.log 2>&1 &
BUSINESS_PID=$!
echo "business 已启动 (PID: $BUSINESS_PID)"

cd ..

echo "[2/2] 启动 website 微服务 (端口8008)..."
cd website
nohup mvn spring-boot:run -DskipTests > ../logs/website.log 2>&1 &
WEBSITE_PID=$!
echo "website 已启动 (PID: $WEBSITE_PID)"

cd ..

echo ""
echo "========================================"
echo "服务启动中，请等待约15秒..."
echo "========================================"
echo ""
echo "访问地址:"
echo "  - 前端页面: http://localhost:8008"
echo "  - Website Swagger: http://localhost:8008/rest/swagger-ui.html"
echo "  - Business Swagger: http://localhost:8009/rest/swagger-ui.html"
echo ""
echo "登录账号:"
echo "  - 用户名: admin"
echo "  - 密码: admin123"
echo ""
echo "查看日志:"
echo "  - business: tail -f logs/business.log"
echo "  - website: tail -f logs/website.log"
echo ""
echo "停止服务: ./stop.sh"
