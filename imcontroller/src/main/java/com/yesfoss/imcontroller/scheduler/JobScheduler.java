package com.yesfoss.imcontroller.scheduler;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesfoss.imcontroller.jobs.MessengerJob;

public class JobScheduler {
  private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);
  private Scheduler scheduler;

  private String schedulerTrigger;
  private String schedulerGroup;
  private String cronExpression;
  private String cronJobName;
  private String cronJobGroup;

  public JobScheduler(String schedulerTrigger, String schedulerGroup, String cronExpression,
      String cronJobName, String cronJobGroup) {

    this.schedulerTrigger = schedulerTrigger;
    this.schedulerGroup = schedulerGroup;
    this.cronExpression = cronExpression;
    this.cronJobName = cronJobName;
    this.cronJobGroup = cronJobGroup;
  }

  public void scheduleJob() throws SchedulerException {
    LOGGER.trace("scheduleJob() start");

    scheduler = new StdSchedulerFactory().getScheduler();
    scheduler.start();

    Trigger trigger = buildTrigger();
    JobDetail jobDetail = buildJobDetail();

    scheduler.scheduleJob(jobDetail, trigger);

    LOGGER.info("initialized job successfully");

  }

  private Trigger buildTrigger() {
    return TriggerBuilder.newTrigger().withIdentity(schedulerTrigger, schedulerGroup)
        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
  }

  private JobDetail buildJobDetail() {
    return JobBuilder.newJob(MessengerJob.class).withIdentity(cronJobName, cronJobGroup).build();
  }

  public Scheduler getScheduler() {
    return scheduler;
  }

  public void shutDown() throws SchedulerException {
    if (scheduler != null) {
      scheduler.shutdown();
    }
  }
}
