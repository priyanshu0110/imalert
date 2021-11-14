package com.yesfoss.imworker.tasks;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesfoss.imworker.constants.Constants.JobConfig;
import com.yesfoss.imworker.dao.RedisDao;

public class LatencyCheckerTask extends Task {
  private static final Logger LOGGER = LoggerFactory.getLogger(LatencyCheckerTask.class);

  public LatencyCheckerTask(String name, String instance) {
    super(name, instance);
    setHeader("Why Latency");
  }

  @Override
  public void run() {
    LOGGER.trace("LatencyCheckerTask: start");
    try {
      String whyLatencyHashPrefix = JobConfig.IMALERT_HASH_PREFIX + name.toUpperCase() + ":";
      String monitoringDetailsHash = whyLatencyHashPrefix + instance;

      Map<String, String> pingDetails = RedisDao.getDao().hgetall(monitoringDetailsHash);
      String host = pingDetails.get("host");
      int port = Integer.parseInt(pingDetails.getOrDefault("port", "80"));
      int maxLatency = Integer.parseInt(pingDetails.getOrDefault("max_latency", "1000"));
      int timeout = maxLatency + 10000;

      long startTime = System.currentTimeMillis();
      boolean isHostReachable = isHostReachable(host, port, timeout);
      long endTime = System.currentTimeMillis();
      long latency = endTime - startTime;
      if (!isHostReachable) {
        String message = String.format("could not reach host %s on port %d within %d milliseconds",
            host, port, timeout);
        sendAlert(message);
      } else if (latency > maxLatency) {
        String message = String.format(
            "latency for host %s port %d more than expected. %n*Expected Latency:* %d  *Actual Latency:* %d",
            host, port, maxLatency, latency);
        sendAlert(message);
      }
    } catch (Exception e) {
      LOGGER.error("LatencyCheckerTask | ex: ", e);
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
