package com.yesfoss.imcontroller.managers;

import com.yesfoss.imcontroller.constants.Constants.RedisConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnectionManager {
  private static final RedisConnectionManager INSTANCE = new RedisConnectionManager();
  private JedisPool jedisPool = null;

  private RedisConnectionManager() {
    final JedisPoolConfig jedisPoolConfig = getJedisPoolConfig();
    jedisPool = new JedisPool(jedisPoolConfig, RedisConfig.HOST, RedisConfig.PORT,
        RedisConfig.TIMEOUT, RedisConfig.AUTH);
  }

  public static RedisConnectionManager getInstance() {
    return INSTANCE;
  }

  public static Jedis getResource() {
    return getInstance().jedisPool.getResource();
  }

  public static void returnResource(Jedis resource) {
    if (resource != null) {
      resource.close();
    }
  }

  public static void destroyJedisPool() throws InterruptedException {
    if (getInstance().jedisPool != null) {
      try {
        Thread.sleep(RedisConfig.JEDIS_POOL_CLOSE_AFTER_MS);
      } finally {
        getInstance().jedisPool.destroy();
        getInstance().jedisPool = null;
      }
    }
  }

  private JedisPoolConfig getJedisPoolConfig() {
    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    jedisPoolConfig.setMaxTotal(RedisConfig.JEDISPOOL_MAX_TOTAL);

    return jedisPoolConfig;
  }

}
