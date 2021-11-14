import json


class IsitreachableConfig:
    def __init__(self, name, config_json):
        self.name = name
        self.host = config_json['host']
        self.port = config_json['port']
        self.timeout = config_json['timeout']

        self.cron = config_json['cron']
