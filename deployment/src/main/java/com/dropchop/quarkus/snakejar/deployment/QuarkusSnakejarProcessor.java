package com.dropchop.quarkus.snakejar.deployment;

import com.dropchop.quarkus.snakejar.NamedSnakeJarInvoker;
import com.dropchop.quarkus.snakejar.SnakeJarInvokerFactory;
import com.dropchop.quarkus.snakejar.SnakeJarRecorder;
import com.dropchop.snakejar.Invoker;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.deployment.builditem.nativeimage.JniRuntimeAccessBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeReinitializedClassBuildItem;
import io.quarkus.deployment.pkg.NativeConfig;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

class QuarkusSnakejarProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(QuarkusSnakejarProcessor.class);
  private static final String FEATURE = "quarkus-snakejar";
  private static final DotName NAMED_SNAKEJAR_CLIENT_ANNOTATION = DotName.createSimple(NamedSnakeJarInvoker.class.getName());

  @BuildStep
  void build(BuildProducer<FeatureBuildItem> feature,
             BuildProducer<ReflectiveClassBuildItem> reflectiveClasses,
             BuildProducer<JniRuntimeAccessBuildItem> jniRuntimeAccessibleClasses,
             BuildProducer<RuntimeReinitializedClassBuildItem> reinitialized,
             BuildProducer<NativeImageResourceBuildItem> nativeLibs,
             LaunchModeBuildItem launchMode,
             NativeConfig config) throws IOException {

    LOG.debug("build start");
    feature.produce(new FeatureBuildItem(FEATURE));
    LOG.debug("build end");
  }

  @BuildStep
  public void registerBeans(BuildProducer<AdditionalBeanBuildItem> additionalBeanBuildItemProducer) {
    LOG.trace("registerBeans start");
    additionalBeanBuildItemProducer.produce(
      AdditionalBeanBuildItem
        .builder()
        .addBeanClasses(SnakeJarInvokerFactory.class)
        .setUnremovable()
        .setDefaultScope(DotNames.SINGLETON).build()
    );
    additionalBeanBuildItemProducer.produce(
        AdditionalBeanBuildItem
          .builder()
          .addBeanClass(NamedSnakeJarInvoker.class)
          .build()
    );
    additionalBeanBuildItemProducer.produce(
      AdditionalBeanBuildItem
        .builder()
        .addBeanClass(Invoker.class)
        .build()
    );
    LOG.trace("registerBeans end");
  }

  @BuildStep
  public void snakejarInvokerNames(BeanArchiveIndexBuildItem indexBuildItem,
                                   BuildProducer<QuarkusSnakeJarInvokerNameBuildItem> invokerName) {
    IndexView indexView = indexBuildItem.getIndex();
    Collection<AnnotationInstance> invokerAnnotations = indexView.getAnnotations(NAMED_SNAKEJAR_CLIENT_ANNOTATION);
    for (AnnotationInstance annotation : invokerAnnotations) {
      invokerName.produce(new QuarkusSnakeJarInvokerNameBuildItem(annotation.value().asString()));
    }
  }

  void addSnakeJarInvoker(SnakeJarRecorder recorder, String name,
                          BuildProducer<SyntheticBeanBuildItem> syntheticBeans) {
    SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator = SyntheticBeanBuildItem
      .configure(Invoker.class)
      .scope(Singleton.class)
      .setRuntimeInit()
      .unremovable()
      .supplier(recorder.snakeJarInvokerSupplier(name));

    if (name == null) {
      configurator.addQualifier(Default.class);
    } else {
      configurator.addQualifier().annotation(DotNames.NAMED).addValue("value", name).done();
      configurator.addQualifier().annotation(NamedSnakeJarInvoker.class).addValue("value", name).done();
    }
    syntheticBeans.produce(configurator.done());
  }

  @BuildStep
  @Record(ExecutionTime.RUNTIME_INIT)
  void processRunTimeConfig(SnakeJarRecorder recorder, ShutdownContextBuildItem shutdown,
                       List<QuarkusSnakeJarInvokerNameBuildItem> namedClients,
                       BuildProducer<SyntheticBeanBuildItem> syntheticBeans) {
    LOG.trace("processRunTimeConfig start");
    recorder.registerShutdownTask(shutdown);

    // create default invoker
    addSnakeJarInvoker(recorder, null, syntheticBeans);

    // create named invokers
    for (QuarkusSnakeJarInvokerNameBuildItem namedClient : namedClients) {
      addSnakeJarInvoker(recorder, namedClient.getName(), syntheticBeans);
    }
    LOG.debug("processRunTimeConfig end");
  }
}
