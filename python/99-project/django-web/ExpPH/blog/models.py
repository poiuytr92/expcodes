# -*- coding: utf8 -*-

from django.db import models
from django.contrib import admin


''' 
定义app_BlogPost表模型 

在 settings.py 文件的 INSTALLED_APPS 先注册模块（添加包名即可）

然后在控制台依次运行3个命令：
manage.py makemigrations    #创建迁移模块
manage.py migrate           #提交模块迁移请求
python manage.py syncdb     #同步创建表到数据库
'''
class BlogPost(models.Model):
    ''' 
    # 内部类，定义元组属性，影响页面显示
    # 这里使得 Blog 显示的文章按时间倒序显示
    # 若把前面的 - 去掉，则按时间正序排序 
    '''
    class Meta:
        ordering = ('-timestamp',)
    # End Class Meta
        
    # 定义了BlogPost所对应 app_blogpost表的字段 
    title = models.CharField(max_length = 150)
    body = models.TextField()
    timestamp = models.DateTimeField()
# End Class BlogPost
    

''' 定义app_BlogPost表模型 的管理类 '''
class BlogPostAdmin(admin.ModelAdmin):
    
    # 决定页面显示Blog的字段属性，必须在Blog模型中有定义这些字段
    list_display = ('title', 'timestamp')    
# End Class BlogPost
    
    
