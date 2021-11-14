package com.yesfoss.imworker.tasks;

import com.yesfoss.imworker.notifiers.SlackNotifier;

public abstract class Task implements Runnable {

  protected String name;
  protected String instance;
  private String header;

  public Task(String name, String instance) {
    this.setName(name);
    this.setInstance(instance);
    this.setHeader(name);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getInstance() {
    return instance;
  }

  public void setInstance(String instance) {
    this.instance = instance;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    String finalHeader = String.format("`%s` | *%s*%n%n", header, instance);
    this.header = finalHeader;
  }

  protected void sendAlert(String message) {
    SlackNotifier.send(header + message);
  }

}
