package com.yesfoss.imcontroller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesfoss.imcontroller.dal.DALManager;
import com.yesfoss.imcontroller.props.PropertyStore;

public class App {
  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  private static void registerShutDownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        LOGGER.warn("closing the application");
        DALManager.getDal().close();
      }
    });
  }

  private static void loadProperties(String[] args) {
    if (args.length < 1) {
      LOGGER.error("property file path not passed in the arguments. exiting");
      System.exit(0);
    }
    try {
      String filePath = args[0];
      PropertyStore.loadProperties(filePath);
    } catch (IOException e) {
      LOGGER.error("failed to load properties. exiting. ex: ", e);
      System.exit(0);
    }
  }

  public static void main(String[] args) {

    LOGGER.info("loading properties");
    loadProperties(args);

    LOGGER.info("registering shutdown hook");
    registerShutDownHook();

    LOGGER.info("initializing imcontroller");
    DALManager.initialize();

    LOGGER.info("scheduling messenger job");
    DALManager.getDal().scheduleMessengerJob();
  }
}
