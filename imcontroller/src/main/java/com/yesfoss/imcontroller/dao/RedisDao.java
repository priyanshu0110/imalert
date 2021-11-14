package com.yesfoss.imcontroller.dao;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesfoss.imcontroller.managers.RedisConnectionManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;

public class RedisDao implements DataDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(RedisDao.class);
  private static final RedisDao INSTANCE = new RedisDao();

  private RedisDao() {}

  @Override
  public Map<String, String> hgetall(String key) {
    Jedis resource = null;
    try {
      resource = RedisConnectionManager.getResource();
      return resource.hgetAll(key);
    } catch (Exception e) {
      LOGGER.error("hgetall() ex: ", e);
    } finally {
      RedisConnectionManager.returnResource(resource);
    }

    return Collections.emptyMap();
  }

  @Override
  public void xadd(String key, StreamEntryID id, Map<String, String> hash, long maxLen) {
    Jedis resource = null;
    try {
      resource = RedisConnectionManager.getResource();
      resource.xadd(key, id, hash, maxLen, false);
    } catch (Exception e) {
      LOGGER.error("xadd() ex: ", e);
    } finally {
      RedisConnectionManager.returnResource(resource);
    }

  }

  public static RedisDao getDao() {
    return INSTANCE;
  }

}
