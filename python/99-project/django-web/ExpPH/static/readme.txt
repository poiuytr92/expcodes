admin 目录是直接从
.\Python\Lib\site-packages\Django-1.8-py2.7.egg\django\contrib\admin\static\admin
目录下复制的，实则上就是 Django 后台管理站点的CSS样式。

之所以要复制出来，是因为当settings设置 DEBUG=False 后，Django不会再自动加载静态文件，
导致后台管理站点的样式全丢。
而手动将其复制到根目录则可以保证在 DEBUG=False时，后台管理站点的样式依然有效。


================================

python manage.py collectstatic：

运行python manage.py collectstatic命令，
这将从Django资源包中复制必须的静态文件到STATIC_ROOT指示的static文件夹中，
其中包括admin界面所必须的样式表（style）、图片（image）及脚本（js）等