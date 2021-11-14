import os

CENTRAL_REDIS_HOST = os.getenv("central_redis_host")
CENTRAL_REDIS_PORT = os.getenv("central_redis_port")
CENTRAL_REDIS_AUTH = os.getenv("central_redis_auth")

HASH_DELIM = ':'
CONFIG_HASH_PREFIX = 'H:IMALERT' + HASH_DELIM
INSTANCE_SET_PREFIX = 'S:IMALERT' + HASH_DELIM

CONFIG_HASH = {'iwatchredis': CONFIG_HASH_PREFIX + 'IWATCHREDIS',
               'iwatchelastic': CONFIG_HASH_PREFIX + 'IWATCHELASTIC',
               'remindme': CONFIG_HASH_PREFIX + 'REMINDME',
               'checkmyhealth': CONFIG_HASH_PREFIX + 'CHECKMYHEALTH',
               'isitreachable': CONFIG_HASH_PREFIX + 'ISITREACHABLE',
               'whylatency': CONFIG_HASH_PREFIX + 'WHYLATENCY'}

INSTANCE_SET = {'iwatchredis': INSTANCE_SET_PREFIX + 'IWATCHREDIS:INSTANCES',
                'iwatchelastic': INSTANCE_SET_PREFIX + 'IWATCHELASTIC:INSTANCES',
                'remindme': INSTANCE_SET_PREFIX + 'REMINDME:INSTANCES',
                'checkmyhealth': INSTANCE_SET_PREFIX + 'CHECKMYHEALTH:INSTANCES',
                'isitreachable': INSTANCE_SET_PREFIX + 'ISITREACHABLE:INSTANCES',
                'whylatency': INSTANCE_SET_PREFIX + 'WHYLATENCY:INSTANCES'}
