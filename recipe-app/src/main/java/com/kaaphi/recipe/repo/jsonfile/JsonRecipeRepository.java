package com.kaaphi.recipe.repo.jsonfile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.repo.RecipeRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonRecipeRepository extends AbstractFileRepo implements RecipeRepository {
  private final Gson gson;
  
  @Inject
  public JsonRecipeRepository(@Named("jsonRepoPath") String store, @Named("repoGson") Gson gson) {
    super(store, "recipes.json");
    this.gson = gson;
  }
    
  @Override
  public Set<RecipeBookEntry> getAll() {
    return read(in -> {
      if(in.isPresent()) {
        Set<RecipeBookEntry> book = new TreeSet<>((a,b) -> a.getRecipe().getTitle().compareTo(b.getRecipe().getTitle()));
        book.addAll(gson.fromJson(in.get(), new TypeToken<LinkedHashSet<RecipeBookEntry>>(){}.getType()));
        return book;        
      } else {
        return Collections.emptySet();
      }      
    });
  }
  
  @Override
  public RecipeBookEntry get(UUID id) {
    return getAll().stream()
    .filter(r -> id.equals(r.getId()))
    .findFirst()
    .orElse(null);
  }

  @Override
  public void deleteById(Set<UUID> toRemove) {
    Map<UUID, RecipeBookEntry> current = toMap(getAll());
    current.keySet().removeAll(toRemove);
    writeList(current.values()); 
  }

  @Override
  public void saveAll(Set<RecipeBookEntry> recipes) {
    Map<UUID, RecipeBookEntry> current = toMap(getAll());
    current.putAll(toMap(recipes));
    writeList(current.values());    
  }
  
  private void writeList(Collection<RecipeBookEntry> all) {
    write(out -> gson.toJson(all, new TypeToken<Collection<RecipeBookEntry>>(){}.getType(), out));
  }
  
  private static Map<UUID, RecipeBookEntry> toMap(Set<RecipeBookEntry> recipes) {
    return recipes.stream()
        .collect(Collectors.toMap(RecipeBookEntry::getId, Function.identity(), (a,b)->{throw new IllegalStateException();}, LinkedHashMap::new));
  }



}
