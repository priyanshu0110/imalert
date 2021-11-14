import json

from django.http import HttpResponse, HttpResponseRedirect
from django.template import loader

from constants import gconfig
from handlers.db_handler import DbHandler
from iwatchredis.beans.redis_config import RedisConfig
from utils import edcrypt


def get_index(request):
    template = loader.get_template("./iwatchredis/index.html")

    config = DbHandler.get_instance().get_config('iwatchredis')

    redis_info = {}
    for redis_instance in config:
        redis_instance_config_hash = gconfig.CONFIG_HASH.get('iwatchredis') + gconfig.HASH_DELIM + redis_instance
        redis_instance_config = DbHandler.get_instance().get_hash_entries(redis_instance_config_hash)
        redis_instance_job_config = DbHandler.get_instance().get_hash_member('H:IMALERT:JOB_HASH', "IWATCHREDIS|{}".format(redis_instance))
        redis_instance_config['cron'] = json.loads(redis_instance_job_config)['cron']
        if redis_instance_config:
            redis_info[redis_instance] = RedisConfig(redis_instance, redis_instance_config)

    context = {
        'redis_info': redis_info
    }

    return HttpResponse(template.render(context, request))


def add(request):

    redis_instance = request.POST.getlist('name')
    redis_host = request.POST.getlist('host')
    redis_port = request.POST.getlist('port')
    redis_auth = request.POST.getlist('auth')
    redis_type = request.POST.getlist('type')
    health_monitoring = request.POST.getlist('health_monitoring')
    keys_monitoring = request.POST.getlist('keys_monitoring')
    iops_monitoring = request.POST.getlist('iops_monitoring')
    min_keys = request.POST.getlist('min_keys')
    max_keys = request.POST.getlist('max_keys')
    min_iops = request.POST.getlist('min_iops')
    max_iops = request.POST.getlist('max_iops')
    cron = request.POST.getlist('cron')

    config_hash = 'H:IMALERT:IWATCHREDIS:' + redis_instance[0]
    config_dict = {'state': '0' if len(health_monitoring) == 0 else '1'}
    cred_dict = {}
    iops_monitoring = '0' if len(iops_monitoring) == 0 else '1'
    keys_monitoring = '0' if len(keys_monitoring) == 0 else '1'
    cred_dict['host'] = redis_host[0]
    cred_dict['port'] = redis_port[0]
    # enc_key = DbHandler.get_instance().get_key("K:IMALERT:ENCRYPTION:KEY")
    # cred_dict['auth'] = edcrypt.encrypt(enc_key, redis_auth[0])
    cred_dict['auth'] = redis_auth[0]
    cred_dict['type'] = redis_type[0]
    config_dict['iops'] = "{}|{}|{}".format(iops_monitoring, min_iops[0], max_iops[0])
    config_dict['keys'] = "{}|{}|{}".format(keys_monitoring, min_keys[0], max_keys[0])

    job_config = {"job": "iwatchredis", "instance": redis_instance[0], "cron": cron[0]}

    job_name = "IWATCHREDIS|{}".format(redis_instance[0])

    DbHandler.get_instance().hset_dict(config_hash, config_dict)
    DbHandler.get_instance().hset_dict('H:IMALERT:IWATCHREDIS:CREDS:{}'.format(redis_instance[0]), cred_dict)
    DbHandler.get_instance().add_hash_member('H:IMALERT:JOB_HASH', job_name, json.dumps(job_config))

    DbHandler.get_instance().add_set_member('S:IMALERT:IWATCHREDIS:INSTANCES', redis_instance[0])

    return HttpResponseRedirect('/iwatchredis/')


def update(request):
    redis_instance = request.POST.getlist('redis_name_input')
    health_monitoring = request.POST.getlist('health_monitoring')
    keys_monitoring = request.POST.getlist('keys_monitoring')
    iops_monitoring = request.POST.getlist('iops_monitoring')
    min_keys = request.POST.getlist('min_keys')
    max_keys = request.POST.getlist('max_keys')
    min_iops = request.POST.getlist('min_iops')
    max_iops = request.POST.getlist('max_iops')
    cron = request.POST.getlist('cron')

    config_hash = 'H:IMALERT:IWATCHREDIS:' + redis_instance[0]
    config_dict = {'state': '0' if len(health_monitoring) == 0 else '1'}
    iops_monitoring = '0' if len(iops_monitoring) == 0 else '1'
    keys_monitoring = '0' if len(keys_monitoring) == 0 else '1'
    config_dict['iops'] = "{}|{}|{}".format(iops_monitoring, min_iops[0], max_iops[0])
    config_dict['keys'] = "{}|{}|{}".format(keys_monitoring, min_keys[0], max_keys[0])

    DbHandler.get_instance().hset_dict(config_hash, config_dict)

    job_config = {"job": "iwatchredis", "instance": redis_instance[0], "cron": cron[0]}
    job_name = "IWATCHREDIS|{}".format(redis_instance[0])
    DbHandler.get_instance().add_hash_member('H:IMALERT:JOB_HASH', job_name, json.dumps(job_config))

    return HttpResponseRedirect('/iwatchredis/')


def delete(request):
    redis_instance = request.POST.getlist('redis_name_input')
    config_hash = 'H:IMALERT:IWATCHREDIS:' + redis_instance[0]
    job_name = "IWATCHREDIS|{}".format(redis_instance[0])
    DbHandler.get_instance().remove_hash_member('H:IMALERT:JOB_HASH', job_name)
    DbHandler.get_instance().remove_set_member('S:IMALERT:IWATCHREDIS:INSTANCES', redis_instance[0])
    DbHandler.get_instance().del_key(config_hash)
    DbHandler.get_instance().del_key('H:IMALERT:IWATCHREDIS:CREDS:{}'.format(redis_instance[0]))

    return HttpResponseRedirect('/iwatchredis/')
