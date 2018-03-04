# -*- coding: utf8 -*-
"""
Django settings for ExpPH project.

For more information on this file, see
https://docs.djangoproject.com/en/dev/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/dev/ref/settings/
"""

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
import os
BASE_DIR = os.path.dirname(os.path.dirname(__file__))

    
# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/dev/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = 'gm(w6+h2wf$ngzs6a^1(7x86bqqah9@#9u*vtxk&27v2$3!i(p'

# SECURITY WARNING: don't run with debug turned on in production!

# 当搭建到服务器上时，为了安全需要关闭DEBUG模式，此时需要指定可以访问的主机和文件路径
from ExpPH.utils.BaseUtils import isLocalEnvironment
if isLocalEnvironment():
    DEBUG = True
    TEMPLATE_DEBUG = True
else:
    DEBUG = False
    TEMPLATE_DEBUG = False


ALLOWED_HOSTS = ['expph.sinaapp.com', 'localhost', '127.0.0.1']

# 添加模板搜索路径
TEMPLATE_DIRS = (
    # SAE后台管理站点的模板目录
    # '/usr/local/sae/python/lib/python2.7/site-packages/django/contrib/admin/templates/admin',
            
    # 根目录下的 templates
    os.path.join(BASE_DIR, "templates").replace('\\', '/'),
    
    # 主项目下的 templates
    os.path.join(os.path.dirname(__file__), 'templates').replace('\\', '/'),
)

# Application definition

# 添加新模块后，需要运行 python manage.py syncdb 命令安装对应的表到数据库
INSTALLED_APPS = (
    'django.contrib.admin',
    'django.contrib.admindocs',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    #'django.contrib.comments',
    #'django.contrib.sites',
    
    # （album有第3方包，SAE不支持，要注释）
    'album',
    
    'blog',
    
    # （cms有第3方包，SAE不支持，要注释）
    'cms',
)

MIDDLEWARE_CLASSES = (
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
#    'django.contrib.auth.middleware.SessionAuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
)

ROOT_URLCONF = 'ExpPH.urls'

WSGI_APPLICATION = 'ExpPH.wsgi.application'


# Database
# https://docs.djangoproject.com/en/dev/ref/settings/#databases
from ExpPH.utils.DBUtils import getDbParams
from ExpPH.conf.Consts import DB_TYPE_MYSQL
import ExpPH.conf.Keys

dbPms = getDbParams(DB_TYPE_MYSQL)
DATABASES = {
    'default': {
        'ENGINE': dbPms[ExpPH.conf.Keys.DB_ENGINE],
        'NAME': dbPms[ExpPH.conf.Keys.DB_NAME], 
        'USER': dbPms[ExpPH.conf.Keys.DB_USER], 
        'PASSWORD': dbPms[ExpPH.conf.Keys.DB_PSWD], 
        'HOST': dbPms[ExpPH.conf.Keys.DB_IP_M], 
        'PORT': dbPms[ExpPH.conf.Keys.DB_PORT], 
    }
}

# Internationalization
# https://docs.djangoproject.com/en/dev/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'UTC'

USE_I18N = True

USE_L10N = True

USE_TZ = True


# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/dev/howto/static-files/

# 存放静态文件的目录
STATIC_ROOT = os.path.join(BASE_DIR, "static").replace('\\', '/')

# 页面访问 STATIC_ROOT 下静态文件的URL起始路径
# 如下则为http://127.0.0.1/static/... 
STATIC_URL = '/static/'

# 存放静态媒体文件的目录，一般用于存储上传的文件
MEDIA_ROOT = os.path.join(BASE_DIR, "media").replace('\\', '/')

# 页面访问 MEDIA_ROOT 下静态文件的URL起始路径
# 如下则为http://127.0.0.1/media/... 
MEDIA_URL = '/media/'

# URL prefix for admin static files -- CSS, JavaScript and images. 
# ADMIN_MEDIA_PREFIX = '/static/admin/'
# 
# # List of finder classes that know how to find static files in various locations.
# STATICFILES_FINDERS = (
#     'django.contrib.staticfiles.finders.FileSystemFinder',
#     'django.contrib.staticfiles.finders.AppDirectoriesFinder',
#     #'django.contrib.staticfiles.finders.DefaultStorageFinder',
# )
# 
# TEMPLATE_CONTEXT_PROCESSORS=(
#     "django.contrib.auth.context_processors.auth",
#     "django.core.context_processors.debug",
#     "django.core.context_processors.i18n",
#     "django.core.context_processors.media",
#     "django.core.context_processors.static",
#     "django.core.context_processors.tz",
#     "django.contrib.messages.context_processors.messages",
# )
