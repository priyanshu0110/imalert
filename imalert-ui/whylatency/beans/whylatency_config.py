import json


class WhylatencyConfig:
    def __init__(self, name, config_json):
        self.name = name
        self.host = config_json['host']
        self.port = config_json['port']
        self.max_latency = config_json['max_latency']

        self.cron = config_json['cron']
