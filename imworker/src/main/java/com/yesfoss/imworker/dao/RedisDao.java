package com.yesfoss.imworker.dao;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesfoss.imworker.managers.RedisConnectionManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntry;
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
  public List<Entry<String, List<StreamEntry>>> xreadgroup(String stream, String group,
      String consumer, int count) {
    Jedis resource = null;
    try {
      resource = RedisConnectionManager.getResource();
      Entry<String, StreamEntryID> streamQeury =
          new AbstractMap.SimpleImmutableEntry<>(stream, StreamEntryID.UNRECEIVED_ENTRY);
      return resource.xreadGroup(group, consumer, count, 0, true, streamQeury);
    } catch (Exception e) {
      LOGGER.error("xreadgroup() ex: ", e);
    } finally {
      RedisConnectionManager.returnResource(resource);
    }

    return Collections.emptyList();
  }

  @Override
  public String get(String key) {
    Jedis resource = null;
    try {
      resource = RedisConnectionManager.getResource();
      return resource.get(key);
    } catch (Exception e) {
      LOGGER.error("get() ex: ", e);
    } finally {
      RedisConnectionManager.returnResource(resource);
    }

    return null;
  }

  public static RedisDao getDao() {
    return INSTANCE;
  }

}
