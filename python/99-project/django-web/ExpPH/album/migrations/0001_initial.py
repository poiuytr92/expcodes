# encoding: utf8
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Item',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('name', models.CharField(max_length=20)),
                ('description', models.TextField()),
            ],
            options={
                'ordering': [b'name'],
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='Photo',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('item', models.ForeignKey(to='album.Item', to_field='id')),
                ('title', models.CharField(max_length=100)),
                ('image', models.ImageField(upload_to=b'photos')),
                ('caption', models.CharField(max_length=250, blank=True)),
            ],
            options={
                'ordering': [b'title'],
            },
            bases=(models.Model,),
        ),
    ]
