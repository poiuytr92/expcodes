# -*- coding: utf8 -*-

from django.db import models
from django.contrib import admin
from album.fields import ThumbnailImageField as TIF

''' 定义相册模型 '''
class Item(models.Model):
    name = models.CharField(max_length = 20)
    description = models.TextField()
    
    class Meta:
        ordering = ['name']
        
    # 当需要使用这个字段时，返回它的unicode字符串
    def __unicode__(self):
        return self.name
    
    # 避免URL硬编码，供模板使用，格式为{% xxx.get_absolute_url %}
    # 当在模板调用这个模型的get_absolute_url方法时，就可以得到一个URL
    # 返回值是一个三元组（在urls.py定义的URL名称，一列位置参数，一个命名参数字典）
    @models.permalink
    def get_absolute_url(self):
        return ('AlbumItemDetail', None, {'pk':self.id})
# End Class Item

    
''' 定义照片模型 '''
class Photo(models.Model):
    item = models.ForeignKey(Item)
    title = models.CharField(max_length = 100)
    # upload_to 用于设置上传图片的位置
    # 以'/'开头说明是绝对位置，以'/'开头说明是相对位置
    # 相对位置会自动把上传路径添加 MEDIA_ROOT，即 MEDIA_ROOT/photos
    # 而页面访问路径则自动添加MEDIA_URL，即MEDIA_URL/photos
    image = TIF.ThumbnailImageField(upload_to = 'photos') 
    caption = models.CharField(max_length = 250, blank = True)
    
    class Meta:
        ordering = ['title']
        
    def __unicode__(self):
        return self.title
    
    @models.permalink
    def get_absolute_url(self):
        return ('AlbumPhotoDetail', None, {'pk':self.id})
# End Class Photo

    
''' 定义内联对象 '''
class PhotoInline(admin.StackedInline):
    model = Photo    
# End Class PhotoInline
    
    
''' 定义子对象的引用 '''
class ItemAdmin(admin.ModelAdmin):
    inlines = [PhotoInline] # 将有外键的子类包含进视图
# End Class ItemAdmin
    
