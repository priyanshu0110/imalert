import json


class RedisConfig:
    def __init__(self, name, config_json):
        self.name = name
        self.health = True if config_json['state'] == '1' else False

        self.iops = config_json['iops']
        iops_config = self.iops.split('|')
        self.iops_check = True if iops_config[0] == '1' else False
        self.iops_min = iops_config[1]
        self.iops_max = iops_config[2]

        self.keys = config_json['keys']
        keys_config = self.keys.split('|')
        self.keys_check = True if keys_config[0] == '1' else False
        self.keys_min = keys_config[1]
        self.keys_max = keys_config[2]

        self.cron = config_json['cron']
