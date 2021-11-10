package com.dropchop.quarkus.snakejar;

import java.util.Properties;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 6. 11. 21.
 */
public class SnakeJarSupport {

  private final Properties properties;

  public SnakeJarSupport(Properties properties) {
    this.properties = properties;
  }

  public Properties getProperties() {
    return properties;
  }
}
