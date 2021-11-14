import json


class CheckmyhealthConfig:
    def __init__(self, name, config_json):
        self.name = name
        self.url = config_json['url']
        self.response = config_json['response']

        self.cron = config_json['cron']
