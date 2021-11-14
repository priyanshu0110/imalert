package com.yesfoss.imworker.constants;

import com.yesfoss.imworker.props.PropertyStore;

public class Constants {
  private Constants() {}

  public static final class JobConfig {
    private JobConfig() {}

    public static final String IMALERT_HASH_PREFIX = "H:IMALERT:";
    public static final String ENCRYPTION_KEY = "K:IMALERT:ENCRYPTION:KEY";
  }

  public static final class JobFields {
    private JobFields() {}

    public static final String JOB = "job";
    public static final String INSTANCE = "instance";
  }

  public static final class Tasks {
    private Tasks() {}

    public static final String REDIS_WATCHER = "iwatchredis";
    public static final String ELASTIC_WATCHER = "iwatchelastic";
    public static final String HEALTH_CHECKER = "checkmyhealth";
    public static final String LATENCY_CHECKER = "whylatency";
    public static final String REACHABILITY_CHECKER = "isitreachable";
    public static final String REMINDER = "remindme";
  }

  public static final class ConsumerConfig {
    private ConsumerConfig() {}

    public static final String JOB_QUEUE = PropertyStore.getProperty("module.job.queue");
    public static final String JOB_CONSUMER_GROUP =
        PropertyStore.getProperty("module.job.consumer.group");
    public static final String JOB_CONSUMER_ID =
        PropertyStore.getProperty("module.job.consumer.id");
    public static final int POLL_COUNT =
        Integer.parseInt(PropertyStore.getProperty("module.job.stream.poll.count"));
    public static final int POLL_DELAY_MILLIS =
        Integer.parseInt(PropertyStore.getProperty("module.job.stream.poll.delay.millis"));
  }

  public static final class WorkerConfig {
    private WorkerConfig() {}

    public static final int CORE_WORKERS =
        Integer.parseInt(PropertyStore.getProperty("module.workers.core"));
    public static final int MAX_WORKERS =
        Integer.parseInt(PropertyStore.getProperty("module.workers.max"));
    public static final int WORKERS_QUEUE_LENGTH =
        Integer.parseInt(PropertyStore.getProperty("module.workers.queue.length"));
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

  public static final class SlackConfig {
    private SlackConfig() {}

    public static final String WEBHOOK_URL = PropertyStore.getProperty("module.slack.webhook.url");
  }

  public static final class Delim {
    private Delim() {}

    public static final String EMPTY_STRING = "";
  }

}
