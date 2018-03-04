from django.contrib import admin

from django.contrib import admin

from album.models import Item, ItemAdmin, Photo


admin.site.register(Item, ItemAdmin)
admin.site.register(Photo)