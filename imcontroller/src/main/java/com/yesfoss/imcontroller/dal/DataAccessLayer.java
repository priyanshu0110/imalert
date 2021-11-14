package com.yesfoss.imcontroller.dal;

import java.util.Collections;
import java.util.Map;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesfoss.imcontroller.constants.Constants.JobConfig;
import com.yesfoss.imcontroller.constants.Constants.MessengerConfig;
import com.yesfoss.imcontroller.dao.RedisDao;
import com.yesfoss.imcontroller.managers.RedisConnectionManager;
import com.yesfoss.imcontroller.scheduler.JobScheduler;

public class DataAccessLayer {
  private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessLayer.class);
  JobScheduler messengerJobScheduler;

  public Map<String, String> readJobHash() {
    LOGGER.trace("readJobHash() start");
    try {
      return RedisDao.getDao().hgetall(JobConfig.JOB_HASH);
    } catch (Exception e) {
      LOGGER.error("readJobHash() exception reading job hash. ex: ", e);
    }

    return Collections.emptyMap();
  }


  public void pushToJobQueue(Map<String, String> jobConfig) {
    LOGGER.trace("pushToJobQueue() start");
    try {
      RedisDao.getDao().xadd(JobConfig.JOB_QUEUE, null, jobConfig, JobConfig.JOB_QUEUE_MAX_LEN);
    } catch (Exception e) {
      LOGGER.error("pushToJobQueue() error pushing job to queue. jobConfig: {} ex: ", jobConfig, e);
    }

  }

  public void scheduleMessengerJob() {
    LOGGER.trace("scheduleMessengerJob() start");
    try {
      messengerJobScheduler = new JobScheduler(MessengerConfig.SCHEDULER_TRIGGER,
          MessengerConfig.SCHEDULER_GROUP, MessengerConfig.CRON_EXPRESSION,
          MessengerConfig.CRONJOB_NAME, MessengerConfig.CRONJOB_GROUP);
      messengerJobScheduler.scheduleJob();

      LOGGER.info("initialized messenger job successfully");

    } catch (SchedulerException e) {
      LOGGER.error("scheduleMessengerJob(): error initializing messenger job. exiting. ex: ", e);
      System.exit(-1);
    }
  }

  public void close() {
    try {
      LOGGER.info("shutting down messenger job scheduler");
      if (messengerJobScheduler != null) {
        messengerJobScheduler.shutDown();
      }
      LOGGER.info("destroying redis connection pool");
      RedisConnectionManager.destroyJedisPool();
    } catch (Exception e) {
      LOGGER.error("exception while cleaning up resources. ex: ", e);
    }
  }

}
