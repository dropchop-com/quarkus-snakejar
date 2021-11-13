package com.dropchop.quarkus.snakejar;

import com.dropchop.snakejar.Invoker;
import io.quarkus.arc.Arc;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 6. 11. 21.
 */
@Recorder
public class SnakeJarRecorder {

  private static final Logger LOG = LoggerFactory.getLogger(SnakeJarRecorder.class);

  public void registerShutdownTask(ShutdownContext shutdownContext) {
    SnakeJarInvokerFactory producer = Arc.container().instance(SnakeJarInvokerFactory.class).get();
    shutdownContext.addShutdownTask(producer::destroy);
  }

  public Supplier<Invoker> snakeJarInvokerSupplier(String name) {
    LOG.trace("snakeJarInvokerSupplier({})", name);
    SnakeJarInvokerFactory producer = Arc.container().instance(SnakeJarInvokerFactory.class).get();
    return () -> {
      LOG.trace("producer.getSnakeJarInvoker({}) ", name);
      return producer.getSnakeJarInvoker(name);
    };
  }
}
