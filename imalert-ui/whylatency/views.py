import json

from django.http import HttpResponse, HttpResponseRedirect
from django.template import loader

from constants import gconfig
from handlers.db_handler import DbHandler
from whylatency.beans.whylatency_config import WhylatencyConfig
from utils import edcrypt


def get_index(request):
    template = loader.get_template("./whylatency/index.html")

    config = DbHandler.get_instance().get_config('whylatency')

    whylatency_info = {}
    for whylatency_instance in config:
        whylatency_instance_config_hash = gconfig.CONFIG_HASH.get(
            'whylatency') + gconfig.HASH_DELIM + whylatency_instance
        whylatency_instance_config = DbHandler.get_instance().get_hash_entries(whylatency_instance_config_hash)
        whylatency_instance_job_config = DbHandler.get_instance().get_hash_member('H:IMALERT:JOB_HASH',
                                                                                     "WHYLATENCY|{}".format(
                                                                                         whylatency_instance))
        whylatency_instance_config['cron'] = json.loads(whylatency_instance_job_config)['cron']
        if whylatency_instance_config:
            whylatency_info[whylatency_instance] = WhylatencyConfig(whylatency_instance,
                                                                             whylatency_instance_config)

    context = {
        'whylatency_info': whylatency_info
    }

    return HttpResponse(template.render(context, request))


def add(request):
    whylatency_instance = request.POST.getlist('name')
    whylatency_host = request.POST.getlist('host')
    whylatency_port = request.POST.getlist('port')
    whylatency_max_latency = request.POST.getlist('max_latency')
    cron = request.POST.getlist('cron')

    config_hash = 'H:IMALERT:WHYLATENCY:' + whylatency_instance[0]
    config_dict = {'host': whylatency_host[0], 'port': whylatency_port[0], 'max_latency': whylatency_max_latency[0]}

    job_config = {"job": "whylatency", "instance": whylatency_instance[0], "cron": cron[0]}

    job_name = "WHYLATENCY|{}".format(whylatency_instance[0])

    DbHandler.get_instance().hset_dict(config_hash, config_dict)
    DbHandler.get_instance().add_hash_member('H:IMALERT:JOB_HASH', job_name, json.dumps(job_config))
    DbHandler.get_instance().add_set_member('S:IMALERT:WHYLATENCY:INSTANCES', whylatency_instance[0])

    return HttpResponseRedirect('/whylatency/')


def update(request):
    whylatency_instance = request.POST.getlist('whylatency_name_input')
    whylatency_host = request.POST.getlist('host')
    whylatency_port = request.POST.getlist('port')
    whylatency_max_latency = request.POST.getlist('max_latency')
    cron = request.POST.getlist('cron')

    config_hash = 'H:IMALERT:WHYLATENCY:' + whylatency_instance[0]
    config_dict = {'host': whylatency_host[0], 'port': whylatency_port[0], 'max_latency': whylatency_max_latency[0]}

    DbHandler.get_instance().hset_dict(config_hash, config_dict)

    job_config = {"job": "whylatency", "instance": whylatency_instance[0], "cron": cron[0]}
    job_name = "WHYLATENCY|{}".format(whylatency_instance[0])
    DbHandler.get_instance().add_hash_member('H:IMALERT:JOB_HASH', job_name, json.dumps(job_config))

    return HttpResponseRedirect('/whylatency/')


def delete(request):
    whylatency_instance = request.POST.getlist('whylatency_name_input')
    config_hash = 'H:IMALERT:WHYLATENCY:' + whylatency_instance[0]
    job_name = "WHYLATENCY|{}".format(whylatency_instance[0])
    DbHandler.get_instance().remove_hash_member('H:IMALERT:JOB_HASH', job_name)
    DbHandler.get_instance().remove_set_member('S:IMALERT:WHYLATENCY:INSTANCES', whylatency_instance[0])
    DbHandler.get_instance().del_key(config_hash)

    return HttpResponseRedirect('/whylatency/')
