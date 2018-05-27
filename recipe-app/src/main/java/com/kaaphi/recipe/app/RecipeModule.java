package com.kaaphi.recipe.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.kaaphi.recipe.repo.RecipeRepository;
import com.kaaphi.recipe.repo.jsonfile.JsonRecipeRepository;
import com.kaaphi.recipe.repo.jsonfile.UserFileRepository;
import com.kaaphi.recipe.users.UserRepository;
import com.kaaphi.recipe.users.auth.LongTermAuthRepository;
import com.kaaphi.recipe.users.auth.MemoryLongTermAuthRepo;
import com.kaaphi.velocity.VelocitySLF4JLogChute;
import io.javalin.Javalin;
import io.javalin.translator.template.JavalinVelocityPlugin;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Properties;
import org.apache.velocity.app.VelocityEngine;

public class RecipeModule extends AbstractModule {

  @Override
  protected void configure() {
    Names.bindProperties(binder(), loadProperties());
    
    bind(RecipeRepository.class).to(JsonRecipeRepository.class);
    bind(UserRepository.class).to(UserFileRepository.class);
    bind(LongTermAuthRepository.class).to(MemoryLongTermAuthRepo.class);
  }
  
  @Provides
  Javalin provideJavalin(VelocityEngine engine) {
    JavalinVelocityPlugin.configure(engine);    
    return Javalin.create()
        .port(7000);        
  }
  
  @Provides
  VelocityEngine provideVelocityEngine() {
    VelocityEngine velocityEngine = new VelocityEngine();
    //velocityEngine.setProperty("resource.loader", "class");
    //velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    velocityEngine.setProperty("resource.loader", "file");
    velocityEngine.setProperty("velocimacro.library.autoreload", "true");
    velocityEngine.setProperty("file.resource.loader.cache", "false");
    velocityEngine.setProperty("file.resource.loader.path", "./src/main/resources");
    //velocityEngine.setProperty("velocimacro.permissions.allow.inline.to.replace.global", "true");
    velocityEngine.setProperty("runtime.log.logsystem", new VelocitySLF4JLogChute());
    
    return velocityEngine;
  }
  
  @Provides
  Gson provideGson() {
    return new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Instant.class, new InstantAdapter())
        .create();
  }
  
  @Provides @Named("repoGson")
  Gson provideRepoGson() {
    return new GsonBuilder()
        .create();
  }
  
  private Properties loadProperties() {
    Properties props = new Properties();
    try(InputStream in = Files.newInputStream(Paths.get("./localConfig/config.properties"))) {
      props.load(in);
    } catch (IOException e) {
      throw new Error(e);
    }
    return props;
  }
  
  private static class InstantAdapter implements JsonSerializer<Instant>,JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      return Instant.ofEpochMilli(json.getAsLong());
    }

    @Override
    public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.toEpochMilli());
    }    
  }
  
}
