package com.yesfoss.imworker.tasks;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesfoss.imworker.constants.Constants.JobConfig;
import com.yesfoss.imworker.dao.RedisDao;

public class ReminderTask extends Task {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReminderTask.class);

  public ReminderTask(String name, String instance) {
    super(name, instance);
    setHeader("Remind Me");
  }

  @Override
  public void run() {
    LOGGER.trace("ReminderTask: start");
    try {
      String reminderHashPrefix = JobConfig.IMALERT_HASH_PREFIX + name.toUpperCase() + ":";
      String reminderDetailsHash = reminderHashPrefix + instance;

      Map<String, String> reminderDetails = RedisDao.getDao().hgetall(reminderDetailsHash);
      String message = reminderDetails.get("message");
      sendAlert(message);
    } catch (Exception e) {
      LOGGER.error("ReminderTask | ex: ", e);
    }
  }

}
