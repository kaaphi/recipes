package com.kaaphi.recipe.app;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.kaaphi.recipe.users.auth.LongTermAuthPair;
import com.kaaphi.recipe.users.auth.LongTermAuthPair.LongTermAuthTokenServer;
import com.kaaphi.recipe.users.auth.LongTermAuthRepository;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class should only be used for local testing to prevent having to re-login after each redeploy.
 */
public class SimpleFileLongTermAuthRepo implements LongTermAuthRepository {
  private static final Logger log = LoggerFactory.getLogger(SimpleFileLongTermAuthRepo.class);

  private Map<String, LongTermAuthTokenServer> cache = new ConcurrentHashMap<>();
  private Path file;

  @Inject
  public SimpleFileLongTermAuthRepo(@Named("longTermAuthFile") Path file) {
    this.file = file;
    loadFile();
  }

  @Override
  public LongTermAuthTokenServer getServerToken(String selector) {
    return cache.get(selector);
  }

  @Override
  public void saveServerToken(LongTermAuthTokenServer token) {
    cache.put(token.getSelector(), token);
    writeFile();
  }

  @Override
  public void deleteServerToken(String selector) {
    cache.remove(selector);
    writeFile();
  }

  private synchronized void writeFile() {
    try {
      Files.createDirectories(file.getParent());
      Files.write(
          file,
          cache.values().stream().map(LongTermAuthTokenServer::toString).collect(Collectors.toList()),
          Charset.forName("UTF-8"),
          StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    } catch (Throwable th) {
      log.error("Failed to write long term auth file {}", file);
      log.error("Exception:", th);
    }
  }

  private synchronized void loadFile() {
    try {
      cache.clear();
      if(Files.exists(file)) {
        log.trace("Loading long term auth details from cache {} ({})", file, file.toRealPath());
        cache.putAll(Files.lines(file, Charset.forName("UTF-8"))
            .map(LongTermAuthPair::parseServerToken)
            .collect(Collectors.toMap(LongTermAuthTokenServer::getSelector, Function.identity())));
      } else {
        log.trace("Auth cache does not exist: {}", file);
      }
    } catch (Throwable th) {
      log.error("Failed to load long term auth file {}", file);
      log.error("Exception:", th);
    }
  }
}
