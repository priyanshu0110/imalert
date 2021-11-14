package com.yesfoss.imcontroller.props;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class PropertyStore {
  private final Properties props = new Properties();
  private static final PropertyStore INSTANCE = new PropertyStore();

  public static void loadProperties(final String fileName) throws IOException {
    try (Reader reader = new FileReader(fileName)) {
      getInstance().props.load(reader);
    }
  }

  private static PropertyStore getInstance() {
    return INSTANCE;
  }

  public static String getProperty(String prop) {
    return getInstance().props.getProperty(prop);
  }

  public static String getProperty(String prop, String defaultValue) {
    return (String) getInstance().props.getOrDefault(prop, defaultValue);
  }

}
