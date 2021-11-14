import json

from django.http import HttpResponse, HttpResponseRedirect
from django.template import loader

from constants import gconfig
from handlers.db_handler import DbHandler
from remindme.beans.remindme_config import RemindmeConfig
from utils import edcrypt


def get_index(request):
    template = loader.get_template("./remindme/index.html")

    config = DbHandler.get_instance().get_config('remindme')

    remindme_info = {}
    for remindme_instance in config:
        remindme_instance_config_hash = gconfig.CONFIG_HASH.get('remindme') + gconfig.HASH_DELIM + remindme_instance
        remindme_instance_config = DbHandler.get_instance().get_hash_entries(remindme_instance_config_hash)
        remindme_instance_job_config = DbHandler.get_instance().get_hash_member('H:IMALERT:JOB_HASH', "REMINDME|{}".format(remindme_instance))
        remindme_instance_config['cron'] = json.loads(remindme_instance_job_config)['cron']
        if remindme_instance_config:
            remindme_info[remindme_instance] = RemindmeConfig(remindme_instance, remindme_instance_config)

    context = {
        'remindme_info': remindme_info
    }

    return HttpResponse(template.render(context, request))


def add(request):

    remindme_instance = request.POST.getlist('name')
    remindme_message = request.POST.getlist('message')
    cron = request.POST.getlist('cron')

    config_hash = 'H:IMALERT:REMINDME:' + remindme_instance[0]
    config_dict = {'message': remindme_message[0]}

    job_config = {"job": "remindme", "instance": remindme_instance[0], "cron": cron[0]}

    job_name = "REMINDME|{}".format(remindme_instance[0])

    DbHandler.get_instance().hset_dict(config_hash, config_dict)
    DbHandler.get_instance().add_hash_member('H:IMALERT:JOB_HASH', job_name, json.dumps(job_config))
    DbHandler.get_instance().add_set_member('S:IMALERT:REMINDME:INSTANCES', remindme_instance[0])

    return HttpResponseRedirect('/remindme/')


def update(request):
    remindme_instance = request.POST.getlist('remindme_name_input')
    remindme_message= request.POST.getlist('message')
    cron = request.POST.getlist('cron')

    config_hash = 'H:IMALERT:REMINDME:' + remindme_instance[0]
    config_dict = {'message': remindme_message[0]}

    DbHandler.get_instance().hset_dict(config_hash, config_dict)

    job_config = {"job": "remindme", "instance": remindme_instance[0], "cron": cron[0]}
    job_name = "REMINDME|{}".format(remindme_instance[0])
    DbHandler.get_instance().add_hash_member('H:IMALERT:JOB_HASH', job_name, json.dumps(job_config))

    return HttpResponseRedirect('/remindme/')


def delete(request):
    remindme_instance = request.POST.getlist('remindme_name_input')
    config_hash = 'H:IMALERT:REMINDME:' + remindme_instance[0]
    job_name = "REMINDME|{}".format(remindme_instance[0])
    DbHandler.get_instance().remove_hash_member('H:IMALERT:JOB_HASH', job_name)
    DbHandler.get_instance().remove_set_member('S:IMALERT:REMINDME:INSTANCES', remindme_instance[0])
    DbHandler.get_instance().del_key(config_hash)

    return HttpResponseRedirect('/remindme/')
