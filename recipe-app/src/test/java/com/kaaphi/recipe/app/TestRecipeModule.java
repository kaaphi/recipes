package com.kaaphi.recipe.app;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import com.kaaphi.recipe.module.RecipeModule;
import com.kaaphi.recipe.repo.RepoTestHelper.RepoTestTransaction;
import com.kaaphi.recipe.repo.SingleConnectionDataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;

public class TestRecipeModule {


  public static Module createPostgresRecipeModule() {
    return Modules.override(new RecipeModule())
        .with(new Module() {
          @Override
          public void configure(Binder binder) {
            Map<String,String> env = System.getenv();
            String dbUser = env.getOrDefault("POSTGRES_USER", "postgres");
            String dbPass = env.getOrDefault("POSTGRES_PASSWORD", "mysecretpassword");
            String dbHost = env.getOrDefault("POSTGRES_HOST", "localhost");
            String dbName = env.getOrDefault("POSTGRES_DB", "postgres");

            binder.bind(String.class)
                .annotatedWith(Names.named("dbUrl"))
                .toInstance(
                    String.format("jdbc:postgresql://%s/%s?user=%s&password=%s", dbHost, dbName, dbUser, dbPass));
          }

          @Provides @Singleton
          DataSource provideDataSource(@Named("dbUrl") String dbUrlString) throws SQLException, IOException {
            PGSimpleDataSource ds = new PGSimpleDataSource();
            ds.setURL(dbUrlString);

            return new SingleConnectionDataSource(ds);
          }

          @Provides
          RepoTestTransaction provideTestTransaction(DataSource dataSource) {
            return (SingleConnectionDataSource)dataSource;
          }

        });
  }
}
