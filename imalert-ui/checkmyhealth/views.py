import json

from django.http import HttpResponse, HttpResponseRedirect
from django.template import loader

from constants import gconfig
from handlers.db_handler import DbHandler
from checkmyhealth.beans.checkmyhealth_config import CheckmyhealthConfig
from utils import edcrypt


def get_index(request):
    template = loader.get_template("./checkmyhealth/index.html")

    config = DbHandler.get_instance().get_config('checkmyhealth')

    checkmyhealth_info = {}
    for checkmyhealth_instance in config:
        checkmyhealth_instance_config_hash = gconfig.CONFIG_HASH.get(
            'checkmyhealth') + gconfig.HASH_DELIM + checkmyhealth_instance
        checkmyhealth_instance_config = DbHandler.get_instance().get_hash_entries(checkmyhealth_instance_config_hash)
        checkmyhealth_instance_job_config = DbHandler.get_instance().get_hash_member('H:IMALERT:JOB_HASH',
                                                                                     "CHECKMYHEALTH|{}".format(
                                                                                         checkmyhealth_instance))
        checkmyhealth_instance_config['cron'] = json.loads(checkmyhealth_instance_job_config)['cron']
        if checkmyhealth_instance_config:
            checkmyhealth_info[checkmyhealth_instance] = CheckmyhealthConfig(checkmyhealth_instance,
                                                                             checkmyhealth_instance_config)

    context = {
        'checkmyhealth_info': checkmyhealth_info
    }

    return HttpResponse(template.render(context, request))


def add(request):
    checkmyhealth_instance = request.POST.getlist('name')
    checkmyhealth_url = request.POST.getlist('url')
    checkmyhealth_response = request.POST.getlist('response')
    cron = request.POST.getlist('cron')

    config_hash = 'H:IMALERT:CHECKMYHEALTH:' + checkmyhealth_instance[0]
    config_dict = {'url': checkmyhealth_url[0], 'response': checkmyhealth_response[0]}

    job_config = {"job": "checkmyhealth", "instance": checkmyhealth_instance[0], "cron": cron[0]}

    job_name = "CHECKMYHEALTH|{}".format(checkmyhealth_instance[0])

    DbHandler.get_instance().hset_dict(config_hash, config_dict)
    DbHandler.get_instance().add_hash_member('H:IMALERT:JOB_HASH', job_name, json.dumps(job_config))
    DbHandler.get_instance().add_set_member('S:IMALERT:CHECKMYHEALTH:INSTANCES', checkmyhealth_instance[0])

    return HttpResponseRedirect('/checkmyhealth/')


def update(request):
    checkmyhealth_instance = request.POST.getlist('checkmyhealth_name_input')
    checkmyhealth_url = request.POST.getlist('url')
    checkmyhealth_response = request.POST.getlist('response')
    cron = request.POST.getlist('cron')

    config_hash = 'H:IMALERT:CHECKMYHEALTH:' + checkmyhealth_instance[0]
    config_dict = {'url': checkmyhealth_url[0], 'response': checkmyhealth_response[0]}

    DbHandler.get_instance().hset_dict(config_hash, config_dict)

    job_config = {"job": "checkmyhealth", "instance": checkmyhealth_instance[0], "cron": cron[0]}
    job_name = "CHECKMYHEALTH|{}".format(checkmyhealth_instance[0])
    DbHandler.get_instance().add_hash_member('H:IMALERT:JOB_HASH', job_name, json.dumps(job_config))

    return HttpResponseRedirect('/checkmyhealth/')


def delete(request):
    checkmyhealth_instance = request.POST.getlist('checkmyhealth_name_input')
    config_hash = 'H:IMALERT:CHECKMYHEALTH:' + checkmyhealth_instance[0]
    job_name = "CHECKMYHEALTH|{}".format(checkmyhealth_instance[0])
    DbHandler.get_instance().remove_hash_member('H:IMALERT:JOB_HASH', job_name)
    DbHandler.get_instance().remove_set_member('S:IMALERT:CHECKMYHEALTH:INSTANCES', checkmyhealth_instance[0])
    DbHandler.get_instance().del_key(config_hash)

    return HttpResponseRedirect('/checkmyhealth/')
