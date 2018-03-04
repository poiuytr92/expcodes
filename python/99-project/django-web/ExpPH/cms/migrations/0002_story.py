# encoding: utf8
from __future__ import unicode_literals

import datetime

from django.conf import settings
from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('cms', '0001_initial'),
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name='Story',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('title', models.CharField(max_length=100)),
                ('slug', models.SlugField()),
                ('category', models.ForeignKey(to='cms.Category', to_field='id')),
                ('markdown_content', models.TextField()),
                ('html_content', models.TextField(editable=False)),
                ('owner', models.ForeignKey(to=settings.AUTH_USER_MODEL, to_field='id')),
                ('status', models.IntegerField(default=1, choices=[(1, b'Needs Edit'), (2, b'Needs Approval'), (3, b'Published'), (4, b'Archived')])),
                ('created', models.DateTimeField(default=datetime.datetime(2014, 8, 12, 0, 38, 18, 959000))),
                ('modified', models.DateTimeField(default=datetime.datetime(2014, 8, 12, 0, 38, 18, 959000))),
            ],
            options={
                'ordering': [b'modified'],
                'verbose_name_plural': b'stories',
            },
            bases=(models.Model,),
        ),
    ]
