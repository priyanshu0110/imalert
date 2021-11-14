package com.yesfoss.imworker;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesfoss.imworker.constants.Constants.ConsumerConfig;
import com.yesfoss.imworker.constants.Constants.JobFields;
import com.yesfoss.imworker.constants.Constants.Tasks;
import com.yesfoss.imworker.constants.Constants.WorkerConfig;
import com.yesfoss.imworker.dal.DALManager;
import com.yesfoss.imworker.tasks.ElasticWatcherTask;
import com.yesfoss.imworker.tasks.HealthCheckerTask;
import com.yesfoss.imworker.tasks.LatencyCheckerTask;
import com.yesfoss.imworker.tasks.ReachabilityCheckerTask;
import com.yesfoss.imworker.tasks.RedisWatcherTask;
import com.yesfoss.imworker.tasks.ReminderTask;
import com.yesfoss.imworker.tasks.Task;

import redis.clients.jedis.StreamEntry;

public class Processor {
  private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);
  private final ThreadPoolExecutor jobManager =
      new ThreadPoolExecutor(WorkerConfig.CORE_WORKERS, WorkerConfig.MAX_WORKERS, 60L,
          TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(WorkerConfig.WORKERS_QUEUE_LENGTH),
          Executors.defaultThreadFactory());

  public Processor() {
    jobManager.prestartAllCoreThreads();
  }

  public void start() {
    while (true) {
      try {
        List<Entry<String, List<StreamEntry>>> records =
            DALManager.getDal().consumeJobsFromStream();
        if (records != null) {
          for (Entry<String, List<StreamEntry>> record : records) {
            record.getValue().forEach(streamEntry -> submitTask(streamEntry.getFields()));
          }
        }
      } catch (Exception e) {
        LOGGER.error("start() ex: ", e);
      } finally {
        sleep(ConsumerConfig.POLL_DELAY_MILLIS);
      }
    }
  }

  private void submitTask(Map<String, String> jobConfigMap) {
    try {
      String jobName = jobConfigMap.get(JobFields.JOB);
      String instance = jobConfigMap.get(JobFields.INSTANCE);
      Task task = null;

      switch (jobName) {
        case Tasks.REDIS_WATCHER:
          task = new RedisWatcherTask(jobName, instance);
          break;

        case Tasks.ELASTIC_WATCHER:
          task = new ElasticWatcherTask(jobName, instance);
          break;

        case Tasks.REMINDER:
          task = new ReminderTask(jobName, instance);
          break;

        case Tasks.LATENCY_CHECKER:
          task = new LatencyCheckerTask(jobName, instance);
          break;

        case Tasks.HEALTH_CHECKER:
          task = new HealthCheckerTask(jobName, instance);
          break;

        case Tasks.REACHABILITY_CHECKER:
          task = new ReachabilityCheckerTask(jobName, instance);
          break;

        default:
          break;
      }
      if (task != null) {
        submitTaskToJobManager(task);
      }
    } catch (Exception e) {
      LOGGER.error("exception while submitting task. jobConfig: {}. ex: ", jobConfigMap, e);
    }

  }

  private void submitTaskToJobManager(Task task) {
    try {
      jobManager.submit(task);
    } catch (RejectedExecutionException e) {
      LOGGER.error("submitTaskToJobManager() worker queue full. rejected task: {}", task);
    } catch (Exception e) {
      LOGGER.error("submitTaskToJobManager() ex: ", e);
    }
  }

  private void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ignoreException) {
    }
  }

  public void shutdown() {
    if (!jobManager.isShutdown()) {
      jobManager.shutdown();
    }
  }

}
