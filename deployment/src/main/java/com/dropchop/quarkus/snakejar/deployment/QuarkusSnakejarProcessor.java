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
import io.quarkus.deployment.builditem.RunTimeConfigurationDefaultBuildItem;
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
import java.util.Properties;

import static com.dropchop.quarkus.snakejar.SnakeJarPropertiesUtil.buildSnakeJarProperties;

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
    //registerClassesThatAreLoadedThroughReflection(reflectiveClasses, launchMode);
    //registerClassesThatAreAccessedViaJni(jniRuntimeAccessibleClasses);
    //addSupportForRocksDbLib(nativeLibs, config);
    //enableLoadOfNativeLibs(reinitialized);
  }

  private void registerClassesThatAreLoadedThroughReflection(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses,
                                                             LaunchModeBuildItem launchMode) {
    reflectiveClasses.produce(
      new ReflectiveClassBuildItem(true, true, true,
        com.dropchop.snakejar.impl.SnakeJarEmbedded.class,
        com.dropchop.snakejar.impl.SnakeJarSubProcess.class
      )
    );
  }

  private void registerClassesThatAreAccessedViaJni(BuildProducer<JniRuntimeAccessBuildItem> jniRuntimeAccessibleClasses) {
    jniRuntimeAccessibleClasses
      .produce(
        new JniRuntimeAccessBuildItem(true, true, true,
          com.dropchop.snakejar.embed.JepException.class,
          com.dropchop.snakejar.embed.python.PyCallable.class,
          com.dropchop.snakejar.embed.python.PyObject.class,
          com.dropchop.snakejar.embed.JepException.class,
          com.dropchop.snakejar.impl.EmbeddedInterpreter.class,

          java.lang.Class.class,
          java.lang.Object.class,
          java.lang.ClassLoader.class,
          java.lang.Thread.class,
          java.lang.Throwable.class,
          java.lang.AutoCloseable.class,

          java.lang.Exception.class,
          java.lang.ClassNotFoundException.class,
          java.lang.ClassCastException.class,
          java.lang.IndexOutOfBoundsException.class,
          java.lang.IllegalArgumentException.class,
          java.lang.ArithmeticException.class,
          java.lang.AssertionError.class,
          java.lang.OutOfMemoryError.class,


          java.lang.Boolean.class,
          java.lang.Byte.class,
          java.lang.Character.class,
          java.lang.Double.class,
          java.lang.Float.class,
          java.lang.Integer.class,
          java.lang.Long.class,
          java.lang.Number.class,
          java.lang.Short.class,
          java.lang.String.class,
          java.lang.Void.class,

          boolean[].class,
          byte[].class,
          char[].class,
          int[].class,
          short[].class,
          long[].class,
          float[].class,
          double[].class,

          java.math.BigInteger.class,

          java.lang.reflect.Constructor.class,
          java.lang.reflect.Field.class,
          java.lang.reflect.Member.class,
          java.lang.reflect.Method.class,
          java.lang.reflect.Modifier.class,
          java.lang.reflect.Proxy.class,

          java.nio.Buffer.class,
          java.nio.ByteBuffer.class,
          java.nio.ByteOrder.class,
          java.nio.CharBuffer.class,
          java.nio.DoubleBuffer.class,
          java.nio.FloatBuffer.class,
          java.nio.IntBuffer.class,
          java.nio.LongBuffer.class,
          java.nio.ShortBuffer.class,

          java.util.List.class,
          java.util.ArrayList.class,
          java.util.Collection.class,
          java.util.Collections.class,
          java.util.Map.class,
          java.util.Map.Entry.class,
          java.util.HashMap.class,
          java.util.Iterator.class
        )
      );
  }

  @BuildStep
  public void runtimeDefaultConfig(BuildProducer<RunTimeConfigurationDefaultBuildItem> runTimeConfigurationDefaultBuildItemBuildProducer) {
    runTimeConfigurationDefaultBuildItemBuildProducer.produce(
      new RunTimeConfigurationDefaultBuildItem("quarkus.class-loading.parent-first-artifacts", "com.dropchop:snakejar")
    );
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
