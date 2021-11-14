from django.http import HttpResponse
from django.template import loader


def get_index(request):
    return HttpResponse("OK status")
