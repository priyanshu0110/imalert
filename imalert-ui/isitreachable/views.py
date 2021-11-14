import json

from django.http import HttpResponse, HttpResponseRedirect
from django.template import loader

from constants import gconfig
from handlers.db_handler import DbHandler
from isitreachable.beans.isitreachable_config import IsitreachableConfig
from utils import edcrypt


def get_index(request):
    template = loader.get_template("./isitreachable/index.html")

    config = DbHandler.get_instance().get_config('isitreachable')

    isitreachable_info = {}
    for isitreachable_instance in config:
        isitreachable_instance_config_hash = gconfig.CONFIG_HASH.get(
            'isitreachable') + gconfig.HASH_DELIM + isitreachable_instance
        isitreachable_instance_config = DbHandler.get_instance().get_hash_entries(isitreachable_instance_config_hash)
        isitreachable_instance_job_config = DbHandler.get_instance().get_hash_member('H:IMALERT:JOB_HASH',
                                                                                     "ISITREACHABLE|{}".format(
                                                                                         isitreachable_instance))
        isitreachable_instance_config['cron'] = json.loads(isitreachable_instance_job_config)['cron']
        if isitreachable_instance_config:
            isitreachable_info[isitreachable_instance] = IsitreachableConfig(isitreachable_instance,
                                                                             isitreachable_instance_config)

    context = {
        'isitreachable_info': isitreachable_info
    }

    return HttpResponse(template.render(context, request))


def add(request):
    isitreachable_instance = request.POST.getlist('name')
    isitreachable_host = request.POST.getlist('host')
    isitreachable_port = request.POST.getlist('port')
    isitreachable_timeout = request.POST.getlist('timeout')
    cron = request.POST.getlist('cron')

    config_hash = 'H:IMALERT:ISITREACHABLE:' + isitreachable_instance[0]
    config_dict = {'host': isitreachable_host[0], 'port': isitreachable_port[0], 'timeout': isitreachable_timeout[0]}

    job_config = {"job": "isitreachable", "instance": isitreachable_instance[0], "cron": cron[0]}

    job_name = "ISITREACHABLE|{}".format(isitreachable_instance[0])

    DbHandler.get_instance().hset_dict(config_hash, config_dict)
    DbHandler.get_instance().add_hash_member('H:IMALERT:JOB_HASH', job_name, json.dumps(job_config))
    DbHandler.get_instance().add_set_member('S:IMALERT:ISITREACHABLE:INSTANCES', isitreachable_instance[0])

    return HttpResponseRedirect('/isitreachable/')


def update(request):
    isitreachable_instance = request.POST.getlist('isitreachable_name_input')
    isitreachable_host = request.POST.getlist('host')
    isitreachable_port = request.POST.getlist('port')
    isitreachable_timeout = request.POST.getlist('timeout')
    cron = request.POST.getlist('cron')

    config_hash = 'H:IMALERT:ISITREACHABLE:' + isitreachable_instance[0]
    config_dict = {'host': isitreachable_host[0], 'port': isitreachable_port[0], 'timeout': isitreachable_timeout[0]}

    DbHandler.get_instance().hset_dict(config_hash, config_dict)

    job_config = {"job": "isitreachable", "instance": isitreachable_instance[0], "cron": cron[0]}
    job_name = "ISITREACHABLE|{}".format(isitreachable_instance[0])
    DbHandler.get_instance().add_hash_member('H:IMALERT:JOB_HASH', job_name, json.dumps(job_config))

    return HttpResponseRedirect('/isitreachable/')


def delete(request):
    isitreachable_instance = request.POST.getlist('isitreachable_name_input')
    config_hash = 'H:IMALERT:ISITREACHABLE:' + isitreachable_instance[0]
    job_name = "ISITREACHABLE|{}".format(isitreachable_instance[0])
    DbHandler.get_instance().remove_hash_member('H:IMALERT:JOB_HASH', job_name)
    DbHandler.get_instance().remove_set_member('S:IMALERT:ISITREACHABLE:INSTANCES', isitreachable_instance[0])
    DbHandler.get_instance().del_key(config_hash)

    return HttpResponseRedirect('/isitreachable/')
