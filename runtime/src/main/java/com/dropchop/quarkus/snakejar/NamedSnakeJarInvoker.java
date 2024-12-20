package com.dropchop.quarkus.snakejar;

import jakarta.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.com> on 6. 11. 21.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface NamedSnakeJarInvoker {

  String value();
}
