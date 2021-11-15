package com.dropchop.quarkus.snakejar.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Map;

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