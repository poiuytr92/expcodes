#! /usr/bin/env python
#coding=utf-8

# 2.x启动步骤
# 终端执行命令启动HTTP服务: python -m CGIHTTPServer 8000
# 另起终端，切入当前目录
# 终端执行命令启动CGI程序：python simple_http.py（或者直接在这里启动python）


# 这是 2.x 的代码
from BaseHTTPServer import HTTPServer
from CGIHTTPServer import CGIHTTPRequestHandler

# 这是 3.x 的代码
# from http.server import HTTPServer, CGIHTTPRequestHandler

port = 8080 # 这是浏览器的访问端口，不能与CGI服务的端口相同

# 这段是CGI程序的固定入口代码
httpd = HTTPServer(('', port), CGIHTTPRequestHandler)
print("Starting simple_httpd on port: " + str(httpd.server_port))
httpd.serve_forever()

