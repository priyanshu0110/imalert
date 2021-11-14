package com.yesfoss.imworker.dal;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesfoss.imworker.Processor;
import com.yesfoss.imworker.constants.Constants.ConsumerConfig;
import com.yesfoss.imworker.dao.RedisDao;
import com.yesfoss.imworker.managers.RedisConnectionManager;

import redis.clients.jedis.StreamEntry;

public class DataAccessLayer {
  private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessLayer.class);
  private Processor processor;

  public DataAccessLayer() {
    processor = new Processor();
  }

  public void start() {
    try {
      processor.start();
    } catch (Exception e) {
      LOGGER.error("start() exception while starting the processor. ex: ", e);
    }
  }

  public List<Entry<String, List<StreamEntry>>> consumeJobsFromStream() {
    try {
      return RedisDao.getDao().xreadgroup(ConsumerConfig.JOB_QUEUE,
          ConsumerConfig.JOB_CONSUMER_GROUP, ConsumerConfig.JOB_CONSUMER_ID,
          ConsumerConfig.POLL_COUNT);
    } catch (Exception e) {
      LOGGER.error("consumeJobsFromStream() ex: ", e);
    }

    return Collections.emptyList();
  }

  public void close() {
    try {
      LOGGER.info("shutting down jobmanager");
      processor.shutdown();
      LOGGER.info("destroying redis connection pool");
      RedisConnectionManager.destroyJedisPool();
    } catch (Exception e) {
      LOGGER.error("exception while cleaning up resources. ex: ", e);
    }
  }

}
