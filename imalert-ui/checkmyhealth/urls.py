from django.conf.urls import include, url
from . import views

urlpatterns = [
    url(r'^add', views.add, name='add'),
    url(r'^update', views.update, name='update'),
    url(r'^delete', views.delete, name='delete'),
    url(r'^', views.get_index, name='get_index'),
]
