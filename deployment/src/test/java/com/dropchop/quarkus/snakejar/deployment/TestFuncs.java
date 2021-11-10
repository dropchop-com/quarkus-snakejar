package com.dropchop.quarkus.snakejar.deployment;

import com.dropchop.snakejar.Invocation;
import com.dropchop.snakejar.InvokeFunction;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 9. 11. 21.
 */
public class TestFuncs {

  public static class InvokeAddFunc extends InvokeFunction<Integer> {
    public InvokeAddFunc() {
      super("add-module", "add", Integer.class);
    }
  }

  public static class InvokeSubFunc extends InvokeFunction<Integer> {
    public InvokeSubFunc() {
      super("sub-module", "sub", Integer.class);
    }
  }

  public static final Invocation<?> ADD_FUNC = new InvokeAddFunc();
  public static final Invocation<?> SUB_FUNC = new InvokeSubFunc();
}
