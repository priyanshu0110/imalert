package com.yesfoss.imcontroller.constants;

import com.yesfoss.imcontroller.props.PropertyStore;

public class Constants {
  private Constants() {}

  public static final class JobConfig {
    private JobConfig() {}

    public static final String JOB_HASH = PropertyStore.getProperty("module.job.hash");
    public static final String JOB_QUEUE = PropertyStore.getProperty("module.job.queue");
    public static final long JOB_QUEUE_MAX_LEN =
        Long.parseLong(PropertyStore.getProperty("module.job.queue.max.length"));
  }

  public static final class MessengerConfig {
    private MessengerConfig() {}

    public static final String CRON_EXPRESSION = "0 * * ? * *";
    public static final String SCHEDULER_TRIGGER = "messengerSchedulerTrigger";
    public static final String SCHEDULER_GROUP = "messengerSchedulerGroup";
    public static final String CRONJOB_NAME = "messengerCronJob";
    public static final String CRONJOB_GROUP = "messengerCronJobGroup";
  }

  public static final class JobFields {
    private JobFields() {}

    public static final String CRON = "cron";
  }

  public static final class RedisConfig {
    private RedisConfig() {}

    public static final String HOST = PropertyStore.getProperty("module.redis.host");
    public static final int PORT = Integer.parseInt(PropertyStore.getProperty("module.redis.port"));
    public static final String AUTH = PropertyStore.getProperty("module.redis.password");
    public static final int TIMEOUT =
        Integer.parseInt(PropertyStore.getProperty("module.redis.timeout"));
    public static final int JEDISPOOL_MAX_TOTAL =
        Integer.parseInt(PropertyStore.getProperty("module.redis.pool.maxtotal"));
    public static final long JEDIS_POOL_CLOSE_AFTER_MS =
        Long.parseLong(PropertyStore.getProperty("module.redis.pool.close.after.ms"));
  }

}
