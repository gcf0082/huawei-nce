#!/bin/bash

echo "========================================"
echo "华为NCE网管 - 一键停止脚本"
echo "========================================"

cd "$(dirname "$0")"

echo "正在停止服务..."

PIDS=$(ps aux | grep -E 'spring-boot:run|WebsiteApplication|BusinessApplication' | grep -v grep | awk '{print $2}')

if [ -z "$PIDS" ]; then
    echo "没有正在运行的服务"
else
    echo "找到以下进程: $PIDS"
    kill $PIDS 2>/dev/null
    echo "已发送停止信号，等待进程退出..."
    sleep 3
    
    PIDS2=$(ps aux | grep -E 'spring-boot:run|WebsiteApplication|BusinessApplication' | grep -v grep | awk '{print $2}')
    if [ -n "$PIDS2" ]; then
        echo "强制终止剩余进程: $PIDS2"
        kill -9 $PIDS2 2>/dev/null
    fi
    
    echo "服务已停止"
fi

echo ""
echo "清理临时文件..."
rm -f nweb.pid 2>/dev/null

echo ""
echo "========================================"
echo "停止完成"
echo "========================================"
