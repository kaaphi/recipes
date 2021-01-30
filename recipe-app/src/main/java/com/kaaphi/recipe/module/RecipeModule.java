package com.kaaphi.recipe.module;

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
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.kaaphi.recipe.app.SessionAttributes;
import com.kaaphi.recipe.repo.RecipeRepository;
import com.kaaphi.recipe.repo.postgres.PostgresRecipeRepository;
import com.kaaphi.recipe.repo.postgres.PostgresUserRepository;
import com.kaaphi.recipe.users.RecipeRepositoryFactory;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import com.kaaphi.recipe.users.auth.LongTermAuthRepository;
import com.kaaphi.recipe.users.auth.MemoryLongTermAuthRepo;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinVelocity;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.session.SessionHandler;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeModule extends AbstractModule {
  private static final Logger log = LoggerFactory.getLogger(RecipeModule.class);

  private final Class<? extends UserRepository> userRepoClass;
  private final Class<? extends LongTermAuthRepository> longTermAuthRepoClass;
  private final Class<? extends RecipeRepository> recipeRepoClass;
    
  public RecipeModule() {
    this(PostgresUserRepository.class, MemoryLongTermAuthRepo.class, PostgresRecipeRepository.class);
  }
  
  public RecipeModule(Class<? extends UserRepository> userRepoClass,
      Class<? extends LongTermAuthRepository> longTermAuthRepoClass,
      Class<? extends RecipeRepository> recipeRepoClass) {
    super();
    this.userRepoClass = userRepoClass;
    this.longTermAuthRepoClass = longTermAuthRepoClass;
    this.recipeRepoClass = recipeRepoClass;
  }

  @Override
  protected void configure() {
    Names.bindProperties(binder(), loadProperties());
    
    bind(UserRepository.class).to(userRepoClass);
    bind(LongTermAuthRepository.class).to(longTermAuthRepoClass);
    
    install(new FactoryModuleBuilder()
        .implement(RecipeRepository.class, recipeRepoClass)
        .build(RecipeRepositoryFactory.class)
        );
  }
  
  
  @Provides
  DataSource provideDataSource(@Named("dbUrl") String dbUrlString) throws SQLException, IOException {
      //TODO pooling
    PGSimpleDataSource ds = new PGSimpleDataSource();
    ds.setURL(dbUrlString);
        
    return ds;
  }
    
  @Provides
  Javalin provideJavalin(VelocityEngine engine, @Named("secureCookies") String secureCookies) {
    JavalinRenderer.register(JavalinVelocity.INSTANCE, ".vm", ".html");
    JavalinRenderer.baseModelFunction = ctx -> Optional.ofNullable(ctx.<User>sessionAttribute(SessionAttributes.CURRENT_USER))
        .map(User::getUsername)
        .map(username -> Collections.singletonMap("username",  username))
        .orElse(Collections.emptyMap());

    JavalinVelocity.configure(engine);
    return Javalin.create(config -> config
        .requestLogger((ctx, ms) -> {
          log.info("{} {} {} {} ms", ctx.method(), ctx.status(), getRequestPathLogFormat(ctx), ms);
        })
        .addStaticFiles("/static")
        .sessionHandler(() -> {
          SessionHandler sh = new SessionHandler();
          sh.getSessionCookieConfig().setHttpOnly(true);
          sh.getSessionCookieConfig().setSecure(Boolean.parseBoolean(secureCookies));
          return sh;
        })
    );
  }

  private static String getRequestPathLogFormat(Context ctx) {
    if(ctx.queryString() != null) {
      return ctx.path() + "?" + ctx.queryString();
    } else {
      return ctx.path();
    }
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
    try {
      Properties defaults = new Properties();
      try(InputStream in = getClass().getClassLoader().getResourceAsStream("defaults.properties")) {
        defaults.load(in);
      }


      Properties props = new Properties(defaults);

      Optional<InputStream> customProps = findCustomPropertiesInputStream();
      if(customProps.isPresent()) {
        try(InputStream in = customProps.get()) {
          props.load(in);
        }
      }
      return props;

    } catch (IOException e) {
      throw new Error(e);
    }
  }

  private Optional<InputStream> findCustomPropertiesInputStream() throws IOException {
    //First try to find path from system properties
    Optional<Path> path = Optional.ofNullable(System.getProperty("config"))
        .map(Paths::get)
        ;
    if(path.isPresent()) {
      log.info("Loading properties from system property path: {}", path.get());
      if(Files.exists(path.get())) {
        return Optional.of(Files.newInputStream(path.get()));
      } else {
        log.info("No config file exists.");
      }
    }
    
    //next try class path loading
    InputStream classpathStream = getClass().getClassLoader().getResourceAsStream("config.properties");
    if(classpathStream != null) {
      log.info("Loading config.properties from classpath");
      return Optional.of(classpathStream);
    }
    
    log.info("No custom configuration properties found!");
    return Optional.empty();
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
