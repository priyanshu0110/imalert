"""imalert URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from django.conf.urls import include, url

urlpatterns = [
    path('admin/', admin.site.urls),
    url(r'^$', include('home.urls')),
    url(r'^iwatchredis/', include('iwatchredis.urls')),
    url(r'^health/', include('healthcheck.urls')),
    url(r'^iwatchelastic/', include('iwatchelastic.urls')),
    url(r'^checkmyhealth/', include('checkmyhealth.urls')),
    url(r'^isitreachable/', include('isitreachable.urls')),
    url(r'^whylatency/', include('whylatency.urls')),
    url(r'^remindme/', include('remindme.urls')),
]
