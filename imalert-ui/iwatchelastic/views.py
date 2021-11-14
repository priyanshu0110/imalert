import json

from django.http import HttpResponse, HttpResponseRedirect
from django.template import loader

from constants import gconfig
from handlers.db_handler import DbHandler
from iwatchelastic.beans.elastic_config import ElasticConfig
from utils import edcrypt


def get_index(request):
    template = loader.get_template("./iwatchelastic/index.html")

    config = DbHandler.get_instance().get_config('iwatchelastic')

    elastic_info = {}
    for elastic_instance in config:
        elastic_instance_config_hash = gconfig.CONFIG_HASH.get('iwatchelastic') + gconfig.HASH_DELIM + elastic_instance
        elastic_instance_config = DbHandler.get_instance().get_hash_entries(elastic_instance_config_hash)
        elastic_instance_job_config = DbHandler.get_instance().get_hash_member('H:IMALERT:JOB_HASH', "IWATCHELASTIC|{}".format(elastic_instance))
        elastic_instance_config['cron'] = json.loads(elastic_instance_job_config)['cron']
        if elastic_instance_config:
            elastic_info[elastic_instance] = ElasticConfig(elastic_instance, elastic_instance_config)

    context = {
        'elastic_info': elastic_info
    }

    return HttpResponse(template.render(context, request))


def add(request):

    elastic_instance = request.POST.getlist('name')
    elastic_url = request.POST.getlist('url')
    elastic_user = request.POST.getlist('user')
    elastic_auth = request.POST.getlist('auth')
    auth_required = request.POST.getlist('auth_required')
    health_monitoring = request.POST.getlist('health_monitoring')
    cron = request.POST.getlist('cron')

    config_hash = 'H:IMALERT:IWATCHELASTIC:' + elastic_instance[0]
    config_dict = {'state': '0' if len(health_monitoring) == 0 else '1'}
    cred_dict = {'auth_required': '0' if len(auth_required) == 0 else '1', 'url': elastic_url[0],
                 'auth': elastic_auth[0], 'user': elastic_user[0]}

    job_config = {"job": "iwatchelastic", "instance": elastic_instance[0], "cron": cron[0]}

    job_name = "IWATCHELASTIC|{}".format(elastic_instance[0])

    DbHandler.get_instance().hset_dict(config_hash, config_dict)
    DbHandler.get_instance().hset_dict('H:IMALERT:IWATCHELASTIC:CREDS:{}'.format(elastic_instance[0]), cred_dict)
    DbHandler.get_instance().add_hash_member('H:IMALERT:JOB_HASH', job_name, json.dumps(job_config))

    DbHandler.get_instance().add_set_member('S:IMALERT:IWATCHELASTIC:INSTANCES', elastic_instance[0])

    return HttpResponseRedirect('/iwatchelastic/')


def update(request):
    elastic_instance = request.POST.getlist('elastic_name_input')
    health_monitoring = request.POST.getlist('health_monitoring')
    cron = request.POST.getlist('cron')

    config_hash = 'H:IMALERT:IWATCHELASTIC:' + elastic_instance[0]
    config_dict = {'state': '0' if len(health_monitoring) == 0 else '1'}

    DbHandler.get_instance().hset_dict(config_hash, config_dict)

    job_config = {"job": "iwatchelastic", "instance": elastic_instance[0], "cron": cron[0]}
    job_name = "IWATCHELASTIC|{}".format(elastic_instance[0])
    DbHandler.get_instance().add_hash_member('H:IMALERT:JOB_HASH', job_name, json.dumps(job_config))

    return HttpResponseRedirect('/iwatchelastic/')


def delete(request):
    elastic_instance = request.POST.getlist('elastic_name_input')
    config_hash = 'H:IMALERT:IWATCHELASTIC:' + elastic_instance[0]
    job_name = "IWATCHELASTIC|{}".format(elastic_instance[0])
    DbHandler.get_instance().remove_hash_member('H:IMALERT:JOB_HASH', job_name)
    DbHandler.get_instance().remove_set_member('S:IMALERT:IWATCHELASTIC:INSTANCES', elastic_instance[0])
    DbHandler.get_instance().del_key(config_hash)
    DbHandler.get_instance().del_key('H:IMALERT:IWATCHELASTIC:CREDS:{}'.format(elastic_instance[0]))

    return HttpResponseRedirect('/iwatchelastic/')
