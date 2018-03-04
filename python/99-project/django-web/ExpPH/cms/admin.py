# -*- coding: utf8 -*-

from cms.models import Category, CategoryAdmin, Story, StoryAdmin
from django.contrib import admin


# Register your models here.
admin.site.register(Category, CategoryAdmin)
admin.site.register(Story, StoryAdmin)