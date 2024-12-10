package com.dropchop.quarkus.snakejar;

import java.util.Properties;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.com> on 6. 11. 21.
 */
@SuppressWarnings({"ClassCanBeRecord", "unused"})
public class SnakeJarSupport {

  private final Properties properties;

  public SnakeJarSupport(Properties properties) {
    this.properties = properties;
  }

  public Properties getProperties() {
    return properties;
  }
}
