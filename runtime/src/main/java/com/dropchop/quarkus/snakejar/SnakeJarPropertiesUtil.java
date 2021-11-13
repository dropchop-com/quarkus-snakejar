package com.dropchop.quarkus.snakejar;

import io.quarkus.runtime.LaunchMode;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.Optional;
import java.util.Properties;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 6. 11. 21.
 */
public class SnakeJarPropertiesUtil {

  private static final String SNAKEJAR_OPTION_PREFIX = "snakejar.";
  private static final String QUARKUS_SNAKEJAR_OPTION_PREFIX = "quarkus." + SNAKEJAR_OPTION_PREFIX;

  private static boolean isSnakeJarProperty(String prefix, String property) {
    return property.startsWith(prefix);
  }

  private static void includeSnakeJarProperty(Config config, Properties snakeJarProperties, String prefix, String property) {
    Optional<String> value = config.getOptionalValue(property, String.class);
    value.ifPresent(s -> snakeJarProperties.setProperty(property.substring(prefix.length()), s));
  }

  private static Properties snakeJarProperties(String prefix) {
    Properties snakeJarProperties = new Properties();
    Config config = ConfigProvider.getConfig();
    for (String property : config.getPropertyNames()) {
      if (isSnakeJarProperty(prefix, property)) {
        includeSnakeJarProperty(config, snakeJarProperties, prefix, property);
      }
    }

    return snakeJarProperties;
  }

  public static Properties appSnakeJarProperties() {
    return snakeJarProperties(SNAKEJAR_OPTION_PREFIX);
  }

  public static Properties quarkusSnakeJarProperties() {
    return snakeJarProperties(QUARKUS_SNAKEJAR_OPTION_PREFIX);
  }

  public static Properties buildSnakeJarProperties(LaunchMode launchMode) {
    return appSnakeJarProperties();
  }

}
