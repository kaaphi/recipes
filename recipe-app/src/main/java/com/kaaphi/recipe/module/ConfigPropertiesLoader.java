package com.kaaphi.recipe.module;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigPropertiesLoader {
  private static final Logger log = LoggerFactory.getLogger(ConfigPropertiesLoader.class);

  public static Properties loadProps(Supplier<Optional<InputStream>>...sources) {
    Properties props = new Properties();
    Properties parent = props;
    for(Supplier<Optional<InputStream>> supplier : sources) {
      Optional<InputStream> source = supplier.get();
      if(source.isEmpty()) {
        continue;
      }

      try(InputStream in = source.get()) {
        log.info("Loading config properties from {}", supplier);
        props.load(in);
        parent = props;
        props = new Properties(parent);
      } catch (IOException e) {
        log.error("Failed to load props from {}: {}", supplier, e);
      }
    }

    return parent;
  }

  public static class ClassPathPropsStreamProvider implements Supplier<Optional<InputStream>> {
    private final String name;

    public ClassPathPropsStreamProvider(String name) {
      this.name = name;
    }

    @Override
    public Optional<InputStream> get() {
      InputStream classpathStream = getClass().getClassLoader().getResourceAsStream(name);
      if (classpathStream == null) {
        log.debug("No config found from classpath resource {}", name);
      }
      return Optional.ofNullable(classpathStream);
    }

    @Override
    public String toString() {
      return "ClassPathPropsStreamProvider{" +
          "name='" + name + '\'' +
          '}';
    }
  }

  public static class PathPropsStreamProvider implements Supplier<Optional<InputStream>> {
    private final Path path;

    public PathPropsStreamProvider(String path) {
      this(Paths.get(path));
    }

    public PathPropsStreamProvider(Path path) {
      this.path = path;
    }

    @Override
    public Optional<InputStream> get() {
      if (Files.exists(path)) {
        try {
          return Optional.of(Files.newInputStream(path));
        } catch (IOException e) {
          log.error("Failed to load config data from path {}: {}", path, e);
        }
      } else {
        log.debug("Config file does not exist at path {}.", path);
      }

      return Optional.empty();
    }

    @Override
    public String toString() {
      return "PathPropsStreamProvider{" +
          "path=" + path +
          '}';
    }
  }
}
