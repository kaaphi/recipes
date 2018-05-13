package com.kaaphi.recipe;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class RecipeBookEntry {
  private final UUID id;
  private final Recipe recipe;
  private final Instant created;
  private final Instant updated;
  public RecipeBookEntry(UUID id, Recipe recipe, Instant created, Instant updated) {
    super();
    this.id = id;
    this.recipe = recipe;
    this.created = created;
    this.updated = updated;
  }
  public UUID getId() {
    return id;
  }
  public Recipe getRecipe() {
    return recipe;
  }
  public Instant getCreated() {
    return created;
  }
  public Instant getUpdated() {
    return updated;
  }
  public boolean equals(Object o) {
    if(o instanceof RecipeBookEntry) {
      RecipeBookEntry that = (RecipeBookEntry)o;
      return Objects.equals(this.id, that.id)
          && Objects.equals(this.recipe, that.recipe)
          && Objects.equals(this.created, that.created)
          && Objects.equals(this.updated, that.updated);
    } else {
      return false;
    }
  }
  
  public int hashCode() {
    return Objects.hash(id, recipe, created, updated);
  }
  
  
}
