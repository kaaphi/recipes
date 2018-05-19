package com.kaaphi.recipe;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.kaaphi.recipe.app.RecipeModule;
import com.kaaphi.recipe.enex.EnexToRecipe;
import com.kaaphi.recipe.repo.RecipeRepository;
import java.io.File;
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
  
  public void go() throws JAXBException, XMLStreamException {
    EnexToRecipe converter = new EnexToRecipe();
    Set<RecipeBookEntry> book = converter.toRecipeBook(converter.loadExport(source), (note, exception) -> {
      log.warn("Failed: <{}> ({})", note.getTitle(), exception.getMessage());
    });
    repo.saveAll(book);
  }
  
  public static void main(String[] args) throws JAXBException, XMLStreamException {
    Injector injector = Guice.createInjector(new RecipeModule());
    
    BuildRepoFromEnex builder = injector.getInstance(BuildRepoFromEnex.class);
    builder.go();
  }
}
