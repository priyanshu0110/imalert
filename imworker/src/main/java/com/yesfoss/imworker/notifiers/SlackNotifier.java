package com.yesfoss.imworker.notifiers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.yesfoss.imworker.constants.Constants.SlackConfig;

public class SlackNotifier {
  private static final Logger LOGGER = LoggerFactory.getLogger(SlackNotifier.class);
  private static final SlackNotifier INSTANCE = new SlackNotifier();
  private Slack slack = null;

  private SlackNotifier() {
    slack = Slack.getInstance();
  }

  public static void send(String message) {
    try {
      Payload payload = Payload.builder().text(message).build();
      getInstance().slack.send(SlackConfig.WEBHOOK_URL, payload);
    } catch (IOException connectivityIssue) {
      LOGGER.error("failed to connection to slack. ex: ", connectivityIssue);
    }
  }

  private static SlackNotifier getInstance() {
    return INSTANCE;
  }

}
