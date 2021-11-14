package com.yesfoss.imworker.tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yesfoss.imworker.constants.Constants.JobConfig;
import com.yesfoss.imworker.dao.RedisDao;
import com.yesfoss.imworker.utils.JacksonUtils;

public class ElasticWatcherTask extends Task {
  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticWatcherTask.class);

  public ElasticWatcherTask(String name, String instance) {
    super(name, instance);
    setHeader("I Watch Elastic");
  }

  @Override
  public void run() {
    LOGGER.trace("ElasticWatcherTask: start");
    try {

      String elasticWatcherHashPrefix = JobConfig.IMALERT_HASH_PREFIX + name.toUpperCase() + ":";
      String connectionDetailsHash = elasticWatcherHashPrefix + "CREDS:" + instance;
      String monitoringDetailsHash = elasticWatcherHashPrefix + instance;

      Map<String, String> connectionDetails = RedisDao.getDao().hgetall(connectionDetailsHash);
      Map<String, String> monitoringDetails = RedisDao.getDao().hgetall(monitoringDetailsHash);

      String state = monitoringDetails.get("state");

      if (Integer.parseInt(state) == 1) {
        String url = connectionDetails.get("url");
        String user = connectionDetails.get("user");
        String auth = connectionDetails.get("auth");
        String authRequired = connectionDetails.get("auth_required");

        String webpageBody = readHealth(url, authRequired, user, auth);
        if (webpageBody == null) {
          String message = String.format("Failed to reach url: %s", url);
          sendAlert(message);
          return;
        }

        parseResponseAndAlert(webpageBody);
      }

    } catch (Exception e) {
      LOGGER.error("ElasticWatcherTask | ex: ", e);
    }
  }

  private void parseResponseAndAlert(String webpageBody) {
    try {
      Map<String, String> response = JacksonUtils.convertJsonStringToMap(webpageBody);
      String status = response.get("status");
      String unassignedShards = response.get("unassigned_shards");
      StringBuilder msg = new StringBuilder();
      if (!"green".equals(status)) {
        msg.append("status is: `").append(status).append("`\n\n");
      }
      if (Integer.parseInt(unassignedShards) > 0) {
        msg.append("`").append(unassignedShards).append(" shards` are unassigned");
      }
      if (msg.length() > 0) {
        sendAlert(msg.toString());
      }
    } catch (JsonProcessingException e) {
      LOGGER.error("error parsing the elastic health response ex: ", e);
    }

  }

  private String readHealth(String urlToRead, String authRequired, String user, String auth) {
    try {
      StringBuilder result = new StringBuilder();
      urlToRead += "_cluster/health";
      URL url = new URL(urlToRead);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");

      if (Integer.parseInt(authRequired) == 1) {
        String authentication = user + ":" + auth;
        byte[] encodedAuth =
            Base64.getEncoder().encode(authentication.getBytes(StandardCharsets.UTF_8));
        String authHeaderValue = "Basic " + new String(encodedAuth);
        conn.setRequestProperty("Authorization", authHeaderValue);
      }

      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
        for (String line; (line = reader.readLine()) != null;) {
          result.append(line);
        }
      }
      return result.toString();
    } catch (Exception e) {
      LOGGER.error("error while reading response. ex: ", e);
      return null;
    }

  }

}
