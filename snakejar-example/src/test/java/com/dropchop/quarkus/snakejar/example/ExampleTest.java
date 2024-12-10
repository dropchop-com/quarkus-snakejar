package com.dropchop.quarkus.snakejar.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ExampleTest {

  @Test
  public void testLangIdEndpoint() {
    String text = "Nadzorniki Gen energije so, neuradno, predlagali dva kandidata za predsednika uprave Gen-I.";
    given()
      .when().get("/example/lang_id?text=" + text)
      .then()
      .statusCode(200)
      .body(is("sl"));
  }

}