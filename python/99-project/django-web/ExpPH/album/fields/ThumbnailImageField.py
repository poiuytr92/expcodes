# -*- coding: utf8 -*-

from django.db.models.fields.files import ImageField, ImageFieldFile
import Image
import os

def _add_thumb(imgName):
    """
    " 修改图片的文件名，在文件名后缀之前插入'.thumb'，标识其为缩略图
    """
    parts = imgName.split('.')
    parts.insert(-1, "thumb")
    
    # 原本的文件名无后缀
    if parts[0] == 'thumb':
        parts[0] = parts[1]
        parts[1] = 'thumb'
        parts.append('jpg')
        
    # 原本的文件名不是图片后缀
    elif parts[-1].lower() not in ['jpeg', 'jpg']:
        parts[-1] = 'jpg'
        
    return '.'.join(parts)


class ThumbnailImageFieldFile(ImageFieldFile):
    """
    """
    
    def _get_thumb_path(self):
        return _add_thumb(self.path)
    thumb_path = property(_get_thumb_path)
    
    
    def _get_thumb_url(self):
        return _add_thumb(self.url)
    thumb_url = property(_get_thumb_url)
    
    
    def save(self, name, content, save=True):
        super(ThumbnailImageFieldFile, self).save(name, content, save)
        img = Image.open(self.path)
        img.thumbnail(
            (self.field.thumb_width, self.field.thumb_height),
            Image.ANTIALIAS
        )
        img.save(self.thumb_path, 'JPEG')


    def delete(self, save=True):
        if os.path.exists(self.thumb_path):
            os.remove(self.thumb_path)
        super(ThumbnailImageFieldFile, self).delete(save)
        


class ThumbnailImageField(ImageField):
    """
    """
    attr_class = ThumbnailImageFieldFile
    
    def __init__(self, thumb_width=128, thumb_height=128, *args, **kwargs):
        self.thumb_width = thumb_width
        self.thumb_height = thumb_height
        super(ThumbnailImageField, self).__init__(*args, **kwargs)


    