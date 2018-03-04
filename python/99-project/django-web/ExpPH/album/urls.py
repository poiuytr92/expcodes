# -*- coding: utf-8 -*-

from django.conf.urls import url, patterns
from album.models import Item, Photo
from django.views.generic import TemplateView, DetailView, ListView

urlpatterns = patterns('',
    url(r'^$', 
        ListView.as_view(
            queryset = Item.objects.all(),
            context_object_name = 'item_list',
            template_name = 'AlbumIndex.html',
        ),
        name = 'AlbumIndex'
    ),
                   
    url(r'^items/$', 
        ListView.as_view(
            queryset = Item.objects.all(),
            context_object_name = 'item_list',
            template_name = 'AlbumItemList.html'
        ),
        name = 'AlbumItemList'
    ),
         
    url(r'^items/(?P<pk>\d+)/$', 
        DetailView.as_view(
            queryset = Item.objects.all(),
            context_object_name = 'item',
            template_name = 'AlbumItemDetail.html'
        ),
        name = 'AlbumItemDetail'
    ),      
                          
    url(r'^photos/(?P<pk>\d+)/$',
        DetailView.as_view(
            queryset = Photo.objects.all(),
            context_object_name = 'photo',
            template_name = 'AlbumPhotoDetail.html'
        ),
        name = 'AlbumPhotoDetail'
    ),
)