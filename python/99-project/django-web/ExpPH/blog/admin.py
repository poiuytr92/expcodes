# -*- coding: utf8 -*-

from django.contrib import admin
from blog.models import BlogPost, BlogPostAdmin

''' 注册模块到 admin 后台管理 '''
admin.site.register(BlogPost, BlogPostAdmin)