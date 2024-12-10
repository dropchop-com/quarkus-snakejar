package com.dropchop.quarkus.snakejar.deployment;

import com.dropchop.quarkus.snakejar.SnakeJarInvokersConfig;
import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.com> on 6. 11. 21.
 */
public class SnakeJarExtensionConfigInvokersTest {

  @RegisterExtension
  static final QuarkusUnitTest config = new QuarkusUnitTest()
    .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
      .addAsResource(SnakeJarExtensionConfigInvokersTest.class.getResource("/invokers.properties"),
        "application.properties"));

  @Inject
  SnakeJarInvokersConfig invokersConfig;

  @Test
  public void testSnakeJarMinimalConfig() {
    assertEquals("com.dropchop.snakejar.impl.SnakeJarEmbedded", invokersConfig.className());
    assertEquals("debug", invokersConfig.logLevel());
    assertNotNull(invokersConfig.defaultInvoker());
    assertNotNull(invokersConfig.namedInvokers());
    assertEquals(1, invokersConfig.namedInvokers().size());
  }
}
