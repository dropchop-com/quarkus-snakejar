package com.dropchop.quarkus.snakejar.deployment;

import com.dropchop.quarkus.snakejar.NamedSnakeJarInvoker;
import com.dropchop.snakejar.Invoker;
import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.com> on 7. 11. 21.
 */
@SuppressWarnings("CdiInjectionPointsInspection")
public class SnakeJarInvokerNamedTest {
  @RegisterExtension
  static final QuarkusUnitTest config = new QuarkusUnitTest()
    .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
      .addClass(TestFuncs.class)
      .addAsResource(SnakeJarExtensionConfigInvokersTest.class.getResource("/invokers.properties"),
        "application.properties"));

  @Inject
  @NamedSnakeJarInvoker("invoker1")
  Invoker invoker1;

  @Inject
  Invoker defaultInvoker;

  @Test
  public void testSnakeJarInvoker() throws Exception {
    assertNotNull(invoker1);

    assertEquals(3, invoker1.apply(TestFuncs.ADD_FUNC, () -> new Object[]{1, 2}).get());
    assertEquals(1, invoker1.apply(TestFuncs.SUB_FUNC, () -> new Object[]{3, 2}).get());

    assertEquals(3, defaultInvoker.apply(TestFuncs.ADD_FUNC, () -> new Object[]{1, 2}).get());
    assertEquals(1, defaultInvoker.apply(TestFuncs.SUB_FUNC, () -> new Object[]{3, 2}).get());
  }
}
