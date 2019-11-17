package com.kaaphi.recipe.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Predicate;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class DockerSecretModule extends AbstractModule {
  public static final Path DEFAULT_SECRETS = Paths.get("/run/secrets");
  
  private final Path secrets;
  private final Predicate<String> filter;
  
  public DockerSecretModule() {
    this(DEFAULT_SECRETS, __ -> true);
  }
  
  public DockerSecretModule(Path secrets, Predicate<String> filter) {
    this.secrets = secrets;
    this.filter = filter;
  }
  
  @Override
  protected void configure() {
    Names.bindProperties(binder(), loadFromSecrets(filter));
  }

  private Properties loadFromSecrets(Predicate<String> filter) {
    try {
      Properties props =  new Properties();
      Files.list(secrets)
      .filter(p -> filter.test(p.getFileName().toString()))
      .forEach(p -> loadPropertyFromFile(props, p))
      ;
      return props;
    } catch (IOException e) {
      throw new Error(e);
    }
  }
  
  private void loadPropertyFromFile(Properties props, Path file) {
    try {
      String key = file.getFileName().toString();
      String value = Files.lines(file).findFirst().get();
      props.setProperty(key, value);
    } catch (IOException e) {
      throw new Error(e);
    }
  }
}
