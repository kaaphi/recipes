package com.kaaphi.recipe.module;

import org.apache.velocity.app.VelocityEngine;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.kaaphi.velocity.VelocitySLF4JLogChute;

public class VelocityModule extends AbstractModule {
  @Provides
  VelocityEngine provideVelocityEngine() {
    VelocityEngine velocityEngine = new VelocityEngine();
    velocityEngine.setProperty("runtime.log.logsystem", new VelocitySLF4JLogChute());
    configureVelocityEngine(velocityEngine);    
    return velocityEngine;
  }
  
  protected void configureVelocityEngine(VelocityEngine velocityEngine) {
    velocityEngine.setProperty("resource.loader", "class");
    velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    velocityEngine.setProperty("runtime.log.logsystem", new VelocitySLF4JLogChute());
  }
}
