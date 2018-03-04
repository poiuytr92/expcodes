# -*- coding: utf-8 -*-

from django.conf.urls import url, patterns
from blog.views import bolgPage


urlpatterns = patterns('',
    
    # 访问 Blog 页面的URL
    # 之所以是空的正则式，是因为这个页面是由主页跳转过来的，避免设限访问不了
    url(r'^$', bolgPage),
)

