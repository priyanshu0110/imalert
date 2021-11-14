package com.yesfoss.imworker.tasks;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesfoss.imworker.constants.Constants.JobConfig;
import com.yesfoss.imworker.dao.RedisDao;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisWatcherTask extends Task {
  private static final Logger LOGGER = LoggerFactory.getLogger(RedisWatcherTask.class);

  public RedisWatcherTask(String name, String instance) {
    super(name, instance);
    setHeader("I Watch Redis");
  }

  @Override
  public void run() {
    LOGGER.trace("RedisWatcherTask: start");

    Jedis redisClient = null;
    JedisCluster redisClusterClient = null;

    try {
      String redisWatcherHashPrefix = JobConfig.IMALERT_HASH_PREFIX + name.toUpperCase() + ":";
      String connectionDetailsHash = redisWatcherHashPrefix + "CREDS:" + instance;
      String monitoringDetailsHash = redisWatcherHashPrefix + instance;

      Map<String, String> connectionDetails = RedisDao.getDao().hgetall(connectionDetailsHash);
      Map<String, String> monitoringDetails = RedisDao.getDao().hgetall(monitoringDetailsHash);

      String[] keysMonitoring = monitoringDetails.get("keys").split("\\|");
      String[] iopsMonitoring = monitoringDetails.get("iops").split("\\|");
      String state = monitoringDetails.get("state");

      String host = connectionDetails.get("host");
      String auth = connectionDetails.get("auth");
      String type = connectionDetails.get("type");
      int port = Integer.parseInt(connectionDetails.get("port"));

      if ("standalone".equals(type)) {
        redisClient = new Jedis(host, port);
        redisClient.auth(auth);
        scanRedisAndReport(redisClient, keysMonitoring, iopsMonitoring);
      } else if ("cluster".equals(type)) {
        HostAndPort hostPort = new HostAndPort(host, port);
        Set<HostAndPort> hostPortSet = new HashSet<>();
        hostPortSet.add(hostPort);
        redisClusterClient =
            new JedisCluster(hostPortSet, 5000, 5000, 50, auth, getJedisPoolConfig());
        scanRedisAndReport(redisClusterClient, state, keysMonitoring, iopsMonitoring);
      } else {
        LOGGER.error("invalid type {} for redis {}", type, instance);
      }

    } catch (Exception e) {
      LOGGER.error("RedisWatcherTask | ex: ", e);
      sendAlert("failed to check health of Redis: " + instance);
    } finally {
      if (redisClient != null) {
        redisClient.close();
      } else if (redisClusterClient != null) {
        redisClusterClient.close();
      }
    }
  }

  private void scanRedisAndReport(Jedis redisClient, String[] keysMonitoring,
      String[] iopsMonitoring) {

    StringBuilder alertMessage = new StringBuilder();

    prepareIopsMessageForStandalone(alertMessage, iopsMonitoring, redisClient);
    prepareKeysMessageForStandalone(alertMessage, keysMonitoring, redisClient);

    if (alertMessage.length() > 0) {
      sendAlert(alertMessage.toString());
    }
  }

  private void prepareIopsMessageForStandalone(StringBuilder message, String[] iopsMonitoring,
      Jedis redisClient) {

    if (Integer.parseInt(iopsMonitoring[0]) == 1) {

      long iops = getIops(redisClient);

      if (iops < Long.parseLong(iopsMonitoring[1])) {
        message.append("*iops lower than the expected.* \nExpected Min IOPS: ")
            .append(iopsMonitoring[1]).append("  Current IOPS: ").append(iops).append("\n\n");
      } else if (iops > Long.parseLong(iopsMonitoring[2])) {
        message.append("*iops higher than the expected.* \nExpected Max IOPS: ")
            .append(iopsMonitoring[2]).append("  Current IOPS: ").append(iops).append("\n\n");
      }
    }
  }

  private void prepareKeysMessageForStandalone(StringBuilder message, String[] keysMonitoring,
      Jedis redisClient) {

    if (Integer.parseInt(keysMonitoring[0]) == 1) {

      long numKeys = getDbSize(redisClient);

      if (numKeys < Long.parseLong(keysMonitoring[1])) {
        message.append("*keys lower than the expected.* \nExpected Min Keys: ")
            .append(keysMonitoring[1]).append("  Current Keys: ").append(numKeys);
      } else if (numKeys > Long.parseLong(keysMonitoring[2])) {
        message.append("*keys higher than the expected.* \nExpected Max Keys: ")
            .append(keysMonitoring[2]).append("  Current Keys: ").append(numKeys);
      }
    }
  }

  private void scanRedisAndReport(JedisCluster redisClusterClient, String state,
      String[] keysMonitoring, String[] iopsMonitoring) {

    StringBuilder alertMessage = new StringBuilder();

    prepareStateMessageForCluster(alertMessage, state, redisClusterClient);
    prepareIopsMessageForCluster(alertMessage, iopsMonitoring, redisClusterClient);
    prepareKeysMessageForCluster(alertMessage, keysMonitoring, redisClusterClient);

    if (alertMessage.length() > 0) {
      sendAlert(alertMessage.toString());
    }

  }

  private void prepareIopsMessageForCluster(StringBuilder message, String[] iopsMonitoring,
      JedisCluster redisClusterClient) {

    if (Integer.parseInt(iopsMonitoring[0]) == 1) {

      Map<String, Long> iopsMap = getIops(redisClusterClient);

      for (Map.Entry<String, Long> entry : iopsMap.entrySet()) {
        if (entry.getValue() < Long.parseLong(iopsMonitoring[1])) {
          message.append("*").append(entry.getKey()).append("*  Expected Min IOPS: ")
              .append(iopsMonitoring[1]).append("  Current IOPS: ").append(entry.getValue())
              .append("\n");
        } else if (entry.getValue() > Long.parseLong(iopsMonitoring[2])) {
          message.append("*").append(entry.getKey()).append("*  Expected Max IOPS: ")
              .append(iopsMonitoring[2]).append("  Current IOPS: ").append(entry.getValue())
              .append("\n");
        }
      }

      if (message.length() > 0) {
        message.append("\n");
      }
    }

  }

  private void prepareKeysMessageForCluster(StringBuilder message, String[] keysMonitoring,
      JedisCluster redisClusterClient) {

    if (Integer.parseInt(keysMonitoring[0]) == 1) {

      Map<String, Long> keysMap = getDbSize(redisClusterClient);

      for (Map.Entry<String, Long> entry : keysMap.entrySet()) {
        if (entry.getValue() < Long.parseLong(keysMonitoring[1])) {
          message.append("*").append(entry.getKey()).append("*  Expected Min Keys: ")
              .append(keysMonitoring[1]).append("  Current Keys: ").append(entry.getValue())
              .append("\n");
        } else if (entry.getValue() > Long.parseLong(keysMonitoring[2])) {
          message.append("*").append(entry.getKey()).append("*  Expected Max Keys: ")
              .append(keysMonitoring[2]).append("  Current Keys: ").append(entry.getValue())
              .append("\n");
        }
      }

    }

  }

  private void prepareStateMessageForCluster(StringBuilder message, String state,
      JedisCluster redisClusterClient) {

    if (Integer.parseInt(state) == 1) {

      Map<String, String> stateMap = getClusterState(redisClusterClient);

      for (Map.Entry<String, String> entry : stateMap.entrySet()) {
        if ("fail".equals(entry.getValue())) {
          message.append("*").append(entry.getKey()).append("*  State: ").append(entry.getValue())
              .append("\n");
        }
      }

      if (message.length() > 0) {
        message.append("\n");
      }
    }
  }

  private long getDbSize(Jedis redisClient) {
    return redisClient.dbSize();
  }

  private Map<String, Long> getDbSize(JedisCluster redisClient) {
    try {
      Map<String, JedisPool> nodes = redisClient.getClusterNodes();
      Map<String, Long> keysMap = new HashMap<>();
      nodes.forEach((node, pool) -> keysMap.put(node, pool.getResource().dbSize()));
      return keysMap;
    } catch (Exception e) {
      LOGGER.error("error fetching Keys for redis: {}", instance);
    }
    return Collections.emptyMap();
  }

  private long getIops(Jedis redisClient) {
    try {
      String info = redisClient.info();
      return parseIopsFromInfo(info);
    } catch (Exception e) {
      LOGGER.error("error fetching IOPS for redis: {}", instance);
    }
    return -1;
  }

  private Map<String, Long> getIops(JedisCluster redisClient) {
    try {
      Map<String, JedisPool> nodes = redisClient.getClusterNodes();
      Map<String, Long> iopsMap = new HashMap<>();
      nodes
          .forEach((node, pool) -> iopsMap.put(node, parseIopsFromInfo(pool.getResource().info())));
      return iopsMap;
    } catch (Exception e) {
      LOGGER.error("error fetching IOPS for redis: {}", instance);
    }
    return Collections.emptyMap();
  }

  private Map<String, String> getClusterState(JedisCluster redisClient) {
    try {
      Map<String, JedisPool> nodes = redisClient.getClusterNodes();
      Map<String, String> clusterStateMap = new HashMap<>();
      nodes.forEach((node, pool) -> clusterStateMap.put(node,
          parseStateFromClusterInfo(pool.getResource().clusterInfo())));
      return clusterStateMap;
    } catch (Exception e) {
      LOGGER.error("error fetching cluster state for redis: {}", instance);
    }
    return Collections.emptyMap();
  }

  private long parseIopsFromInfo(String info) {
    String[] infoArray = info.split("\n");
    for (String entry : infoArray) {
      if (entry.contains("instantaneous_ops_per_sec")) {
        return Long.parseLong((entry.split(":")[1]).trim());
      }
    }
    return -1;
  }

  private String parseStateFromClusterInfo(String clusterInfo) {
    String[] infoArray = clusterInfo.split("\n");
    for (String entry : infoArray) {
      if (entry.contains("cluster_state")) {
        return (entry.split(":")[1]).trim();
      }
    }
    return "fail";
  }

  private JedisPoolConfig getJedisPoolConfig() {
    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    jedisPoolConfig.setMaxTotal(3);

    return jedisPoolConfig;
  }

}
