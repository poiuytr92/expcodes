# -*- coding: utf-8 -*-

from django.conf.urls import include, url, patterns
from django.conf.urls.static import static
from ExpPH.settings import STATIC_ROOT, STATIC_URL, MEDIA_ROOT, MEDIA_URL
from django.contrib import admin
from ExpPH.views import homePage, videoPage


# 动态加载 settings.py的 INSTALLED_APPS
# 以获得权限在 admin 后台进行编辑操作
admin.autodiscover() 

urlpatterns = [
               
    # 跳转到主页的 URL
    url(r'^$', 'ExpPH.views.homePage', name='index'),
    url(r'(?i)^index/$', homePage, name='index'),
    url(r'(?i)^expph/$', homePage, name='index'),
    url(r'(?i)^lyy289065406/$', homePage, name='index'),
    url(r'(?i)^homePage/$', homePage, name='index'),
    url(r'(?i)^home/$', homePage, name='index'),
    
    # 跳转到后台管理站点
    url(r'^expAdmin/', include(admin.site.urls)),
    
    # 跳转到相册站点（album有第3方包，SAE不支持，要注释）
    url(r'^album/', include('album.urls')),
    
    # 跳转到博客站点
    url(r'^blog/', include('blog.urls')),
    
    # 跳转到cms站点（cms有第3方包，SAE不支持，要注释）
    url(r'^cms/', include('cms.urls')),
    
    
    # 跳转到视频页面
    url(r'(?i)^video/$', videoPage, name='video'),
]

# 当settings的DEBUG=False时，Django不会自动加载静态资源，此时需要手动指定静态资源路由，
# 为了让页面可以访问 static 和 media 静态资源的路由规则
# show_indexes 属性表示可以通过浏览器直接访问该目录的资源，即完全公开了
urlpatterns += patterns('',
    url(r'^media/(?P<path>.*)$', 'django.views.static.serve', {'document_root': MEDIA_ROOT, 'show_indexes': True }, name='media'),
    url(r'^static/(?P<path>.*)$', 'django.views.static.serve', {'document_root': STATIC_ROOT }, name='static'),
)
urlpatterns += static(MEDIA_URL , document_root = MEDIA_ROOT)
urlpatterns += static(STATIC_URL, document_root = STATIC_ROOT)
