#!/bin/bash

echo "========================================"
echo "华为NCE网管 - 一键构建脚本"
echo "========================================"

cd "$(dirname "$0")"

echo "[1/3] 清理并编译父项目..."
mvn clean compile -DskipTests

if [ $? -ne 0 ]; then
    echo "编译失败!"
    exit 1
fi

echo "[2/3] 构建 business 微服务..."
cd business
mvn package -DskipTests

if [ $? -ne 0 ]; then
    echo "business 构建失败!"
    exit 1
fi

echo "[3/3] 构建 website 微服务..."
cd ../website
mvn package -DskipTests

if [ $? -ne 0 ]; then
    echo "website 构建失败!"
    exit 1
fi

echo ""
echo "========================================"
echo "构建完成!"
echo "========================================"
echo "运行 ./start.sh 启动服务"
