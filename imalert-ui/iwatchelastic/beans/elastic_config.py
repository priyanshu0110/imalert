import json


class ElasticConfig:
    def __init__(self, name, config_json):
        self.name = name
        self.health = True if config_json['state'] == '1' else False

        self.cron = config_json['cron']
