package com.kaaphi.recipe;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import com.kaaphi.recipe.users.User;

public class RecipeBookEntry {
  private final UUID id;
  private final Recipe recipe;
  private final Instant created;
  private final Instant updated;
  private final User owner;
  public RecipeBookEntry(UUID id, Recipe recipe, Instant created, Instant updated, User owner) {
    super();
    this.id = id;
    this.recipe = recipe;
    this.created = created;
    this.updated = updated;
    this.owner = owner;
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
  public User getOwner() {
    return owner;
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
