# -*- coding: utf-8 -*-
#!/usr/bin/env python
import os
import sys

if __name__ == "__main__":
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "ExpPH.settings")
 
    from django.core.management import execute_from_command_line
    execute_from_command_line(sys.argv)


# 创建一个子项目的方法：
# 在CMD下运行命令 manage.py startapp 新子项目名
# 如：
#  manage.py startapp album
#  manage.py startapp blog
#  manage.py startapp cms