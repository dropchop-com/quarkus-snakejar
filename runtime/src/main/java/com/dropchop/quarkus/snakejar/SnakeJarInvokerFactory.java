package com.dropchop.quarkus.snakejar;

import com.dropchop.snakejar.Invoker;
import com.dropchop.snakejar.ModuleSource;
import com.dropchop.snakejar.SnakeJar;
import com.dropchop.snakejar.Source;
import com.dropchop.snakejar.impl.SnakeJarFactory;
import io.quarkus.runtime.LaunchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 6. 11. 21.
 */
@Singleton
public class SnakeJarInvokerFactory {
  private static final Logger LOG = LoggerFactory.getLogger(SnakeJarInvokerFactory.class);
  private static final String DEFAULT_INVOKER_NAME = "<<SnakeJarDefaultInvokerName>>";

  private final LaunchMode launchMode;
  private final SnakeJar snakeJar;
  private final Map<String, Invoker> namedInvokers;

  private Invoker initInvoker(String invokerName, SnakeJarInvokerConfig config) {
    List<Source<?>> sources = new ArrayList<>();
    Map<String, SnakeJarInvokerConfig.Module> moduleMap = new LinkedHashMap<>();
    if (config.moduleOrder.isPresent() && !config.moduleOrder.get().isEmpty()) {
      for (String moduleName : config.moduleOrder.get()) {
        moduleName = moduleName.trim();
        SnakeJarInvokerConfig.Module module = config.modules.get(moduleName);
        if (module == null) {
          LOG.warn("Unbale to find module with name [{}]! Check your configuration!", moduleName);
        } else {
          moduleMap.put(moduleName, config.modules.get(moduleName));
        }
      }
    } else {
      moduleMap.putAll(config.modules);
    }
    for (Map.Entry<String, SnakeJarInvokerConfig.Module> moduleEntry : moduleMap.entrySet()) {
      String name = moduleEntry.getKey();
      String value = moduleEntry.getValue().source;
      if (
        !value.toLowerCase().startsWith("classpath://") &&
        (value.contains(FileSystems.getDefault().getSeparator()) || value.toLowerCase().endsWith(".py"))
        ) {
        try {
          Path path = Path.of(value);
          sources.add(new ModuleSource<>(name, () -> path));
          LOG.info("Registering Invoker [{}] module [{}] source [{}]",
            invokerName, name, path);
        } catch (InvalidPathException e) {
          LOG.warn("Got invalid module [{}] path [{}].", name, value, e);
        }
      } else {
        sources.add(new ModuleSource<>(name, () -> value));
        LOG.info("Registering Invoker [{}] module [{}] source [{}].", invokerName, name, value);
      }
    }
    Invoker invoker;
    try {
      if (config.threadPool.isPresent()) {
        invoker = this.snakeJar.prep(config.threadPool.get(), new Invoker.Params(config.coreThreads, config.maxThreads), sources);
        LOG.info("Compiled modules for Invoker [{}] on thread pool [{}].", invokerName, config.threadPool.get());
      } else {
        invoker = this.snakeJar.prep(new Invoker.Params(config.coreThreads, config.maxThreads), sources);
        LOG.info("Compiled modules for Invoker [{}] on default thread pool.", invokerName);
      }
    } catch (Exception e) {
      LOG.warn("Unable to prepare Invoker [{}] for sources [{}]", invokerName, sources);
      return null;
    }
    LOG.info("Registered Invoker [{}]", invokerName);
    return invoker;
  }

  public SnakeJarInvokerFactory(SnakeJarInvokersConfig invokersConfig, LaunchMode launchMode) {
    LOG.trace("SnakeJarInvokerFactory({}, {})", invokersConfig, launchMode);
    this.namedInvokers = new HashMap<>();
    this.launchMode = launchMode;
    this.snakeJar = SnakeJarFactory.get(invokersConfig.className);
    this.snakeJar.load();
    this.snakeJar.initialize();

    if (!invokersConfig.defaultInvoker.modules.isEmpty()) {
      Invoker invoker = this.initInvoker(DEFAULT_INVOKER_NAME, invokersConfig.defaultInvoker);
      if (invoker != null) {
        this.namedInvokers.put(DEFAULT_INVOKER_NAME, invoker);
      }
    } else {
      LOG.warn("Skipping creation of default SnakeJar Invoker since it has no Python modules configured.");
    }
    for (Map.Entry<String, SnakeJarInvokerConfig> invokerConfigEntry : invokersConfig.namedInvokers.entrySet()) {
      String invokerName = invokerConfigEntry.getKey();
      SnakeJarInvokerConfig config = invokerConfigEntry.getValue();
      Invoker invoker = this.initInvoker(invokerName, config);
      if (invoker != null) {
        this.namedInvokers.put(invokerName, invoker);
      }
    }
  }

  public Invoker getSnakeJarInvoker(String name) {
    LOG.trace("getSnakeJarInvoker({})", name);
    Invoker invoker = this.namedInvokers.get(name != null && !name.isEmpty() ? name : DEFAULT_INVOKER_NAME);
    if (invoker == null) {
      LOG.warn("Unable to get named invoker!");
    }
    return invoker;
  }

  public void destroy() {
    if (!this.launchMode.isDevOrTest()) {
      LOG.info("Destroying snakes in the jar...");
      this.snakeJar.destroy();
      this.snakeJar.unload();
      LOG.info("Snakes are free.");
    } else {
      LOG.trace("Skipping destroy in test or dev mode.");
    }
  }
}
