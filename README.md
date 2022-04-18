# quarkus-snakejar
Quarkus extension that integrates [SnakeJar](https://github.com/ivlcic/snakejar) Java library with embedded CPython to Quarkus

This Quarkus extension enables calling Python native code from Quarkus REST services.

```xml
<dependency>
  <groupId>com.dropchop.quarkus</groupId>
  <artifactId>quarkus-snakejar</artifactId>
  <version>1.0.7</version>
</dependency>
```

## Example

Complete example can be found in snakejar-example sub project.

REST Endpoint

```java
@Path("/hello")
public class GreetingResource {
  private static final Logger LOG = LoggerFactory.getLogger(GreetingResource.class);

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
```

Service
```java
@ApplicationScoped
public class LanguageDetectionService {

  public static class InvokeLangIdClass extends InvokeClass<HashMap<String, Double>> {
    @SuppressWarnings("unchecked")
    public InvokeLangIdClass() {
      super("lang_detect", "lang_id", "LanguageDetect", (Class<HashMap<String, Double>>)(Class<?>)HashMap.class);
    }
  }

  public static final Invocation<HashMap<String, Double>> LANG_ID_CLASS = new InvokeLangIdClass();

  @Inject
  Invoker invoker;

  public Map<String, Double> detectLanguage(String text, int numRet) throws Exception {
    Map<String, Double> result = invoker.apply(LANG_ID_CLASS,
    () -> new Object[]{
      text,
      numRet
    }).get();
    return result;
  }
}
```

application.properties
```properties
quarkus.log.level=INFO
quarkus.log.category."com.dropchop.quarkus.snakejar".min-level=TRACE
quarkus.log.category."com.dropchop.quarkus.snakejar".level=TRACE
quarkus.log.category."com.dropchop.snakejar".min-level=TRACE
quarkus.log.category."com.dropchop.snakejar".level=INFO

quarkus.native.resources.includes=*.py

quarkus.snakejar.module-order=lang_detect_model,lang_detect
quarkus.snakejar.modules.lang_detect_model.source=classpath://lang_detect_model.py
quarkus.snakejar.modules.lang_detect.source=classpath://lang_detect.py
```

Python script from class path (that does actual language detection):

```python
from lang_detect_model import LanguageDetectModel


class LanguageDetect:

    @staticmethod
    def lang_id(text: str, num_ret: int = 1):
        fasttext_model = LanguageDetectModel.get_model()
        classification, confidence = fasttext_model.predict(text.replace("\n", " "), k=num_ret)
        result = {}
        for idx, val in enumerate(classification):
            new_label = classification[idx]
            result[new_label] = confidence[idx]
        return result
        
```

Python script from class path (that provides Fasttext model for language detection):

```python
import os
import fasttext

class LanguageDetectModel:

    fasttext_model = None

    @staticmethod
    def get_model():
        if not LanguageDetectModel.fasttext_model:
            path = os.path.normpath(
                os.path.join(
                    os.path.dirname(__file__), 
                    'lid.176.ftz.wiki.fasttext'))
            LanguageDetectModel.fasttext_model = fasttext.load_model(path)
        return LanguageDetectModel.fasttext_model
        
```
