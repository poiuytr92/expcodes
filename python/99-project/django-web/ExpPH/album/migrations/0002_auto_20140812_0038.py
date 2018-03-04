# encoding: utf8
from __future__ import unicode_literals

from django.db import models, migrations

import album.fields.ThumbnailImageField


class Migration(migrations.Migration):

    dependencies = [
        ('album', '0001_initial'),
    ]

    operations = [
        migrations.AlterField(
            model_name='photo',
            name='image',
            field=album.fields.ThumbnailImageField.ThumbnailImageField(upload_to=b'photos'),
        ),
    ]
