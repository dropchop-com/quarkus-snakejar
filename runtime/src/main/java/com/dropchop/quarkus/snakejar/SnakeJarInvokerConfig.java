package com.dropchop.quarkus.snakejar;

import io.quarkus.runtime.annotations.ConfigDocMapKey;
import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

import java.util.*;

/**
 * Configuration for single or default invoker
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 6. 11. 21.
 */
@ConfigGroup
public class SnakeJarInvokerConfig {

  /**
   * Order for module compilation
   */
  @ConfigItem
  public Optional<List<String>> moduleOrder;

  /**
   * Configuration for python modules
   */
  @ConfigItem
  @ConfigDocMapKey("module-name")
  @ConfigDocSection
  public Map<String, Module> modules = new LinkedHashMap<>();

  /**
   * Configuration for python module
   */
  @ConfigGroup
  public static class Module {

    /**
     * Configuration for python module source
     */
    @ConfigItem
    public String source;
  }
}
