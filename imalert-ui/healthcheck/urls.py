from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'^', views.get_index, name='get_index'),
]
