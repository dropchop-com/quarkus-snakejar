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
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 6. 11. 21.
 */
public class SnakeJarExtensionConfigMinimalTest {

  @RegisterExtension
  static final QuarkusUnitTest config = new QuarkusUnitTest()
    .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
      .addAsResource(SnakeJarExtensionConfigMinimalTest.class.getResource("/minimal.properties"),
        "application.properties"));

  @Inject
  SnakeJarInvokersConfig invokersConfig;

  @Test
  public void testSnakeJarMinimalConfig() {
    assertEquals("com.dropchop.snakejar.impl.SnakeJarEmbedded", invokersConfig.className);
    assertEquals("error", invokersConfig.logLevel);
    assertNotNull(invokersConfig.defaultInvoker);
    assertNotNull(invokersConfig.namedInvokers);
    assertEquals(0, invokersConfig.namedInvokers.size());
  }
}
