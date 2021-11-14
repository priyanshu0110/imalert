package com.yesfoss.imworker.dao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.StreamEntry;

public interface DataDao {

  /**
   * read hash entries from redis
   * 
   * @param key
   * @return
   */
  public Map<String, String> hgetall(String key);

  /**
   * read from redis streams as group member
   * 
   * @param stream
   * @param group
   * @param consumer
   * @param count
   */
  public List<Entry<String, List<StreamEntry>>> xreadgroup(String stream, String group,
      String consumer, int count);

  /**
   * read the key from redis
   * 
   * @param key
   * @return
   */
  public String get(String key);

}
