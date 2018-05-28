package com.kaaphi.recipe.repo.jsonfile;

import com.kaaphi.recipe.repo.RecipeRepositoryException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class AbstractFileRepo {
  private static final Charset UTF8 = Charset.forName("UTF-8");

  private final Path store;
  
  public AbstractFileRepo(String storeDirectory, String storeName) {
    Path directory = Paths.get(storeDirectory);
    try {
      Files.createDirectories(directory);
    } catch (IOException e) {
      throw new Error("Could not create repo directory!", e);
    }
    this.store = directory.resolve(storeName);
  }
  
  protected <T> T read(ReaderHandler<T> handler) {
    try {
      if(Files.exists(store)) {
        try(BufferedReader in = Files.newBufferedReader(store, UTF8)) {
          return handler.read(Optional.of(in));
        } 
      } else {
        return handler.read(Optional.empty());
      }
    } catch (IOException e) {
      throw new RecipeRepositoryException(e);
    }
  }
  
  protected void write(WriterHandler handler) {
    try(BufferedWriter out = Files.newBufferedWriter(store, UTF8)) {
      handler.write(out);
    } catch (IOException e) {
      throw new RecipeRepositoryException(e);
    }
  }
  
  @FunctionalInterface
  protected interface ReaderHandler<T> {
    T read(Optional<BufferedReader> reader) throws IOException;
  }
  
  @FunctionalInterface
  protected interface WriterHandler {
    void write(BufferedWriter out) throws IOException;
  }
}
