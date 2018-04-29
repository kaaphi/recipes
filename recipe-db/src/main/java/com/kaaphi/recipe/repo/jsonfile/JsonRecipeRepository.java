package com.kaaphi.recipe.repo.jsonfile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.kaaphi.recipe.Recipe;
import com.kaaphi.recipe.repo.RecipeRepository;
import com.kaaphi.recipe.repo.RecipeRepositoryException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonRecipeRepository implements RecipeRepository {
  private static final Charset UTF8 = Charset.forName("UTF-8");

  private final Path store;
  private final Gson gson;
  
  @Inject
  public JsonRecipeRepository(@Named("jsonRepoPath") String store, Gson gson) {
    this.store = Paths.get(store);
    this.gson = gson;
  }
    
  @Override
  public Set<Recipe> getAll() {
    if(Files.exists(store)) {
      try(Reader in = Files.newBufferedReader(store, UTF8)) {
        return gson.fromJson(in, new TypeToken<LinkedHashSet<Recipe>>(){}.getType());
      } catch (IOException e) {
        throw new RecipeRepositoryException(e);
      }
    } else {
      return Collections.emptySet();
    }
  }
  
  @Override
  public Recipe get(UUID id) {
    return getAll().stream()
    .filter(r -> id.equals(r.getId()))
    .findFirst()
    .orElse(null);
  }

  @Override
  public void deleteById(Set<UUID> toRemove) {
    Map<UUID, Recipe> current = toMap(getAll());
    current.keySet().removeAll(toRemove);
    writeList(current.values()); 
  }

  @Override
  public void saveAll(Set<Recipe> recipes) {
    Map<UUID, Recipe> current = toMap(getAll());
    current.putAll(toMap(recipes));
    writeList(current.values());    
  }
  
  private void writeList(Collection<Recipe> all) {
    try(BufferedWriter out = Files.newBufferedWriter(store, UTF8)) {
      gson.toJson(all, new TypeToken<Collection<Recipe>>(){}.getType(), out);
    } catch (IOException e) {
      throw new RecipeRepositoryException(e);
    }
  }
  
  private static Map<UUID, Recipe> toMap(Set<Recipe> recipes) {
    return recipes.stream()
        .collect(Collectors.toMap(Recipe::getId, Function.identity(), (a,b)->{throw new IllegalStateException();}, LinkedHashMap::new));
  }



}
