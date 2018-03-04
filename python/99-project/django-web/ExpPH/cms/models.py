# -*- coding: utf8 -*-

from datetime import *

from django.contrib import admin
from django.contrib.auth.models import User
from django.db import models
from markdown import markdown


VIEWABLE_STATUS = [3, 4]
class ViewableManager(models.Model):
    '''
    # 用于过滤文章类型的课件状态
    '''
    def get_query_set(self):
        default_queryset = super(ViewableManager, self).get_query_set()
        return default_queryset.filter(status__in=VIEWABLE_STATUS)


class Category(models.Model):
    '''
    # 文章类别
    '''
    label = models.CharField(blank=True, max_length=50)
    slug = models.SlugField()
    
    class Meta:
        verbose_name_plural = "categories"
        
    def __unicode__(self):
        return self.label
    
    
class CategoryAdmin(admin.ModelAdmin):
    perpopulated_fields = {'slug': ('label',)}



class Story(models.Model):
    '''
    # 站点内容
    '''
    
    # 文章编辑状�?，当1�?时不公开
    STATUS_CHOICES = (
        (1, "Needs Edit"),
        (2, "Needs Approval"),
        (3, "Published"),
        (4, "Archived"),
    )
    
    title = models.CharField(max_length=100)
    slug = models.SlugField()
    category = models.ForeignKey(Category)
    markdown_content = models.TextField()
    html_content = models.TextField(editable=False)
    owner = models.ForeignKey(User)
    status = models.IntegerField(choices=STATUS_CHOICES, default=1)
    created = models.DateTimeField(default=datetime.now())
    modified = models.DateTimeField(default=datetime.now())
    
    class Meta:
        ordering = ['modified']
        verbose_name_plural = 'stories'
    
    def save(self):
        self.html_content = markdown(self.markdown_content)
        self.modified = datetime.now()
        super(Story, self).save()
    
#     admin_objects = models.Manager()
#     objects = ViewableManager()
    
    # 生成永久连接
    @models.permalink
    def get_absolute_url(self):
        return ("cms-story", (), {'slug': self.slug})
    
    
    
class StoryAdmin(admin.ModelAdmin):
    list_display = ('title', 'owner', 'status', 'created', 'modified')
    search_fields = ('title', 'content')
    list_filter= ('status', 'owner', 'created', 'modified')
    perpopulated_fields = {'slug': ('title',)}
    
    
    
