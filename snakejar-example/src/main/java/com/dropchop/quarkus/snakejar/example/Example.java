package com.dropchop.quarkus.snakejar.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;

/**
 * Example service
 *
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 15. 11. 21.
 */
@Path("/example")
public class Example {

  private static final Logger LOG = LoggerFactory.getLogger(Example.class);

  @Inject
  LanguageDetectionService service;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/lang_id")
  public String greeting(@QueryParam("text") String name) {
    try {
      Map<String, Double> lang = service.detectLanguage(name, 1);
      if (lang.isEmpty()) {
        return "EMPTY";
      }
      return lang.entrySet().iterator().next().getKey();
    } catch (Exception e) {
      LOG.warn("Got exception!", e);
      return "ERROR";
    }
  }
}