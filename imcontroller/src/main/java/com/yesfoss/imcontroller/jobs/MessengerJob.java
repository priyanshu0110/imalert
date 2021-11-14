package com.yesfoss.imcontroller.jobs;

import java.time.ZonedDateTime;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.yesfoss.imcontroller.constants.Constants.JobFields;
import com.yesfoss.imcontroller.dal.DALManager;
import com.yesfoss.imcontroller.utils.JacksonUtils;

public class MessengerJob implements Job {
  private static final Logger LOGGER = LoggerFactory.getLogger(MessengerJob.class);

  static final CronDefinition cronDefinition =
      CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
  static final CronParser cronParser = new CronParser(cronDefinition);

  public void execute(JobExecutionContext context) throws JobExecutionException {
    LOGGER.trace("execute() start");
    Map<String, String> jobs = DALManager.getDal().readJobHash();
    jobs.entrySet().forEach(job -> {
      try {
        Map<String, String> jobConfigMap = JacksonUtils.convertJsonStringToMap(job.getValue());
        if (hasToBeProcessed(jobConfigMap)) {
          DALManager.getDal().pushToJobQueue(jobConfigMap);
          LOGGER.info("pushed job: {}", jobConfigMap);
        }
      } catch (Exception e) {
        LOGGER.error("execute() error pushing job to queue. job: {} ex: ", job, e);
      }
    });
  }

  private boolean hasToBeProcessed(Map<String, String> jobConfigMap) {
    String cron = jobConfigMap.get(JobFields.CRON);
    if (cron == null) {
      LOGGER.error("hasToBeProcessed() cron field not present in job config {}. skipping",
          jobConfigMap);
      return false;
    }
    Cron parsedCron = cronParser.parse(cron);
    return ExecutionTime.forCron(parsedCron).isMatch(ZonedDateTime.now());
  }

}
