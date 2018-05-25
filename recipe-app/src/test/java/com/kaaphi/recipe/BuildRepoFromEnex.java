package com.kaaphi.recipe;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.kaaphi.recipe.app.RecipeModule;
import com.kaaphi.recipe.enex.EnexToRecipe;
import com.kaaphi.recipe.repo.RecipeRepository;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildRepoFromEnex {
  private static final Logger log = LoggerFactory.getLogger(BuildRepoFromEnex.class);
  
  private RecipeRepository repo;
  private File source;
  
  @Inject
  public BuildRepoFromEnex(@Named("enexSourcePath") String enexPath, RecipeRepository repo) {
    this.repo = repo;
    this.source = new File(enexPath);
  }
  
  public void go() throws JAXBException, XMLStreamException, IOException {
    EnexToRecipe converter = new EnexToRecipe();
    Path failedDir = source.toPath().getParent().resolve("failed");
    Files.createDirectories(failedDir);
    Files.list(failedDir)
    .map(Path::toFile)
    .forEach(File::delete);
    
    Set<RecipeBookEntry> book = converter.toRecipeBook(converter.loadExport(source), (note, exception) -> {
      log.warn("Failed: <{}> ({})", note.getTitle(), exception.getMessage());
      try(BufferedWriter out = Files.newBufferedWriter(failedDir.resolve(note.getTitle().replaceAll("\\s+", "_")), Charset.forName("UTF-8"))) {
        out.write(note.getContent());
      } catch (IOException e) {
        log.error("Failed!", e);    
      }
    });
    repo.delete(repo.getAll());
    repo.saveAll(book);
  }
  
  public static void main(String[] args) throws Exception {
    Injector injector = Guice.createInjector(new RecipeModule());
    
    BuildRepoFromEnex builder = injector.getInstance(BuildRepoFromEnex.class);
    builder.go();
  }
}
