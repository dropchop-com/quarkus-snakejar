package com.dropchop.quarkus.snakejar;

import io.quarkus.runtime.annotations.*;

import java.util.Map;
import java.util.Optional;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 6. 11. 21.
 */
@ConfigRoot(name = "snakejar", phase = ConfigPhase.RUN_TIME)
public class SnakeJarInvokersConfig {

  /**
   * Log level configuration for JNI c library
   */
  @ConfigItem(defaultValue = "error")
  public String logLevel;

  /**
   * SnakeJar implementation class
   */
  @ConfigItem(defaultValue = "com.dropchop.snakejar.impl.SnakeJarEmbedded")
  public String className;

  /**
   * Number of always present threads in Invoker thread pool
   */
  @ConfigItem(defaultValue = "1")
  public int coreThreads;

  /**
   * Maximum number of threads in Invoker thread pool
   */
  @ConfigItem(defaultValue = "1")
  public int maxThreads;

  /**
   * Name of the thread pool for Invokers to run.
   */
  @ConfigItem()
  public Optional<String> threadPoolName;

  /**
   * The default SnakeJar invoker.
   */
  @ConfigItem(name = ConfigItem.PARENT)
  public SnakeJarInvokerConfig defaultInvoker;

  /**
   * Additional named SnakeJar invokers.
   */
  @ConfigDocSection
  @ConfigDocMapKey("invoker-name")
  @ConfigItem(name = ConfigItem.PARENT)
  public Map<String, SnakeJarInvokerConfig> namedInvokers;

}
