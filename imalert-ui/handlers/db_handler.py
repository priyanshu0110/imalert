from redis import Redis
from constants import gconfig


class DbHandler:
    __instance = None

    def __init__(self):
        if DbHandler.__instance is not None:
            raise Exception("DbHandler class is singleton!")
        else:
            DbHandler.__instance = self
            self.client = self.get_client()

    def __setattr__(self, name, value):
        if name in self.__dict__:
            raise Exception("immutable. can not change")
        self.__dict__[name] = value

    def get_client(self):
        return Redis(host=gconfig.CENTRAL_REDIS_HOST, port=gconfig.CENTRAL_REDIS_PORT,
                     password=gconfig.CENTRAL_REDIS_AUTH, decode_responses=True)

    def get_hash_entries(self, hash_name):
        try:
            return self.client.hgetall(hash_name)
        except Exception as e:
            print(e)
            return {}

    def hset_dict(self, hash_name, mapping):
        try:
            self.client.hmset(hash_name, mapping)
        except Exception as e:
            print(e)

    def set_key(self, key, val):
        try:
            self.client.set(key, val)
        except Exception as e:
            print(e)

    def get_key(self, key):
        try:
            return self.client.get(key)
        except Exception as e:
            print(e)

    def del_key(self, key):
        try:
            self.client.delete(key)
        except Exception as e:
            print(e)

    def add_hash_member(self, hash_name, key, value):
        try:
            self.client.hset(hash_name, key, value)
        except Exception as e:
            print(e)

    def get_hash_member(self, hash_name, member):
        try:
            return self.client.hget(hash_name, member)
        except Exception as e:
            print(e)

    def remove_hash_member(self, hash_name, key):
        try:
            self.client.hdel(hash_name, key)
        except Exception as e:
            print(e)

    def add_set_member(self, set_name, member):
        try:
            self.client.sadd(set_name, member)
        except Exception as e:
            print(e)

    def remove_set_member(self, set_name, member):
        try:
            self.client.srem(set_name, member)
        except Exception as e:
            print(e)

    def get_config(self, app=None):
        try:
            if app is None:
                raise Exception("got config query for app None")
            else:
                return self.client.smembers(gconfig.INSTANCE_SET.get(app))
        except Exception as e:
            print(e)
            return set()

    @staticmethod
    def get_instance():
        if DbHandler.__instance is None:
            DbHandler()
        return DbHandler.__instance
