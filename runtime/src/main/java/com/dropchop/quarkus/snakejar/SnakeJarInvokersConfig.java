package com.dropchop.quarkus.snakejar;

import io.quarkus.runtime.annotations.*;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithParentName;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 6. 11. 21.
 */
@ConfigMapping(prefix = "quarkus.snakejar")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface SnakeJarInvokersConfig {

  /**
   * Log level configuration for JNI c library
   */
  @WithDefault("error")
  String logLevel();

  /**
   * SnakeJar implementation class
   */
  @WithDefault("com.dropchop.snakejar.impl.SnakeJarEmbedded")
  String className();

  /**
   * Number of always present threads in Invoker thread pool
   */
  @WithDefault("1")
  int coreThreads();

  /**
   * Maximum number of threads in Invoker thread pool
   */
  @WithDefault("1")
  int maxThreads();

  /**
   * Name of the thread pool for Invokers to run.
   */
  Optional<String> threadPoolName();


  interface SnakeJarInvokerConfig {

    /**
     * Order for module compilation
     */
    Optional<List<String>> moduleOrder();

    /**
     * Configuration for python modules
     */
    @ConfigDocMapKey("module-name")
    @ConfigDocSection
    Map<String, Module> modules();

    /**
     * Configuration for python module
     */
    @ConfigGroup
    interface Module {

      /**
       * Configuration for python module source
       */
      String source();
    }
  }


  /**
   * The default SnakeJar invoker.
   */
  @WithParentName
  SnakeJarInvokerConfig defaultInvoker();

  /**
   * Additional named SnakeJar invokers.
   */
  @ConfigDocSection
  @ConfigDocMapKey("invoker-name")
  @WithParentName
  Map<String, SnakeJarInvokerConfig> namedInvokers();

}
