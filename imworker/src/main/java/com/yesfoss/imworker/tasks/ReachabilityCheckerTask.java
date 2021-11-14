package com.yesfoss.imworker.tasks;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesfoss.imworker.constants.Constants.JobConfig;
import com.yesfoss.imworker.dao.RedisDao;

public class ReachabilityCheckerTask extends Task {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReachabilityCheckerTask.class);

  public ReachabilityCheckerTask(String name, String instance) {
    super(name, instance);
    setHeader("Is it reachable");
  }

  @Override
  public void run() {
    LOGGER.trace("ReachabilityCheckerTask: start");
    try {
      String reachabilityCheckerHashPrefix =
          JobConfig.IMALERT_HASH_PREFIX + name.toUpperCase() + ":";
      String monitoringDetailsHash = reachabilityCheckerHashPrefix + instance;

      Map<String, String> pingDetails = RedisDao.getDao().hgetall(monitoringDetailsHash);
      String host = pingDetails.get("host");
      int port = Integer.parseInt(pingDetails.getOrDefault("port", "80"));
      int timeout = Integer.parseInt(pingDetails.getOrDefault("timeout", "10000"));

      if (!isHostReachable(host, port, timeout)) {
        String message = String.format("host %s not reachable on port %d", host, port);
        sendAlert(message);
      }
    } catch (Exception e) {
      LOGGER.error("ReachabilityCheckerTask | ex: ", e);
    }
  }

  private boolean isHostReachable(String host, int port, int timeout) {
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(host, port), timeout);
      return true;
    } catch (IOException e) {
      return false; // Either timeout or unreachable or failed DNS lookup.
    }
  }

}
