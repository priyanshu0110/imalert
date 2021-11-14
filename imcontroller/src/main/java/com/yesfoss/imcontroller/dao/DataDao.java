package com.yesfoss.imcontroller.dao;

import java.util.Map;

import redis.clients.jedis.StreamEntryID;

public interface DataDao {

  /**
   * read hash entries from redis
   * 
   * @param key
   * @return
   */
  public Map<String, String> hgetall(String key);

  /**
   * push map on redis stream
   * 
   * @param key
   * @param id
   * @param hash
   * @return
   */
  public void xadd(String key, StreamEntryID id, Map<String, String> hash, long maxLen);

}
