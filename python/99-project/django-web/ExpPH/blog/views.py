# -*- coding: utf-8 -*-

from django.shortcuts import render
from blog.models import BlogPost
from django.template import loader, Context
from django.http import HttpResponse
from ExpPH.utils.BaseUtils import isLocalEnvironment
import ExpPH.conf.Consts as ECC

''' 博客主页 '''
def bolgPage(request):
    blogs = BlogPost.objects  # 从数据库提取所有blog记录
    posts = blogs.all()
    
    # 提取需要显示的页面模板
    template = loader.get_template("BlogPage.html")
    
    # 把从数据库提取页面请求的记录，响应到页面显示
    title = "Welcome to Exp's Blog (Version:1.0.187.20140518 Beta)"
    
    # 首页地址
    homeURL = (isLocalEnvironment() and ECC.LOCAL_ROOT_URL or ECC.SAE_ROOT_URL)
    
    content = Context({'posts': posts, 'title': title, 'homeURL': homeURL})
    return HttpResponse(template.render(content))

