package com.kaaphi.recipe.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.util.Modules;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class ProductionRecipeModule extends AbstractModule {
  public static com.google.inject.Module getProductionModule() {
    return Modules.override(
        new RecipeModule(),
        new ProductionRecipeModule(),
        new VelocityModule())
        .with(new DockerSecretModule());
  }

  @Provides
  @Singleton
  JedisPool provideJedisPool(@Named("redisHost") String redisHost) {
    JedisPoolConfig config = new JedisPoolConfig();
    return new JedisPool(config, redisHost);
  }
}
