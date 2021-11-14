import json


class RemindmeConfig:
    def __init__(self, name, config_json):
        self.name = name
        self.message = config_json['message']

        self.cron = config_json['cron']
