package com.yesfoss.imworker.tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesfoss.imworker.constants.Constants.JobConfig;
import com.yesfoss.imworker.dao.RedisDao;

public class HealthCheckerTask extends Task {
  private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckerTask.class);

  public HealthCheckerTask(String name, String instance) {
    super(name, instance);
    setHeader("Check My Health");
  }

  @Override
  public void run() {
    LOGGER.trace("HealthCheckerTask: start");
    try {
      String healthCheckerHashPrefix = JobConfig.IMALERT_HASH_PREFIX + name.toUpperCase() + ":";
      String monitoringDetailsHash = healthCheckerHashPrefix + instance;

      Map<String, String> healthcheckDetails = RedisDao.getDao().hgetall(monitoringDetailsHash);
      String url = healthcheckDetails.get("url");
      String response = healthcheckDetails.get("response");

      String webpageBody = readWebpage(url);
      if (webpageBody == null || !webpageBody.contains(response)) {
        String message = String.format("Health not OK for url: %s", url);
        sendAlert(message);
      }
    } catch (Exception e) {
      LOGGER.error("HealthCheckerTask | ex: ", e);
    }
  }

  private String readWebpage(String urlToRead) {
    try {
      StringBuilder result = new StringBuilder();
      URL url = new URL(urlToRead);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
        for (String line; (line = reader.readLine()) != null;) {
          result.append(line);
        }
      }
      return result.toString();
    } catch (Exception e) {
      LOGGER.error("error while reading webpage. ex: ", e);
      return null;
    }

  }

}
