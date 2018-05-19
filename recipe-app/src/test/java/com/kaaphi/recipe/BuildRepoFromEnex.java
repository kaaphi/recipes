package com.kaaphi.recipe;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kaaphi.recipe.app.RecipeModule;
import com.kaaphi.recipe.enex.EnexToRecipe;
import com.kaaphi.recipe.repo.RecipeRepository;
import java.io.File;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

public class BuildRepoFromEnex {
  private RecipeRepository repo;
  
  @Inject
  public BuildRepoFromEnex(RecipeRepository repo) {
    this.repo = repo;
  }
  
  public void go(File file) throws JAXBException, XMLStreamException {
    EnexToRecipe converter = new EnexToRecipe();
    Set<RecipeBookEntry> book = converter.toRecipeBook(converter.loadExport(file), (note, exception) -> {
      System.out.format("Failed: <%s> (%s)%n", note.getTitle(), exception);
    });
    repo.saveAll(book);
  }
  
  public static void main(String[] args) throws JAXBException, XMLStreamException {
    Injector injector = Guice.createInjector(new RecipeModule());
    
    BuildRepoFromEnex builder = injector.getInstance(BuildRepoFromEnex.class);
    builder.go(new File("/Users/kaaphi/Documents/Evernote/Recipes.enex"));
  }
}
