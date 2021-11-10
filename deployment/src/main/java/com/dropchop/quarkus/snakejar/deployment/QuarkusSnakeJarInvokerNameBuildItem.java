package com.dropchop.quarkus.snakejar.deployment;

import io.quarkus.builder.item.MultiBuildItem;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.com> on 6. 11. 21.
 */
public final class QuarkusSnakeJarInvokerNameBuildItem extends MultiBuildItem {

  private final String name;

  public QuarkusSnakeJarInvokerNameBuildItem(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
