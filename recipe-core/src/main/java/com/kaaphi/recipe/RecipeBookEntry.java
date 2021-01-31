package com.kaaphi.recipe;

import com.kaaphi.recipe.users.User;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class RecipeBookEntry {
  private final UUID id;
  private final Recipe recipe;
  private final Instant created;
  private final Instant updated;
  private final User owner;
  private final boolean isArchived;

  public RecipeBookEntry(UUID id, Recipe recipe, Instant created, Instant updated, User owner, boolean isArchived) {
    super();
    this.id = id;
    this.recipe = recipe;
    this.created = created;
    this.updated = updated;
    this.owner = owner;
    this.isArchived = isArchived;
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
  public boolean isArchived() {
    return isArchived;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecipeBookEntry that = (RecipeBookEntry) o;
    return isArchived == that.isArchived && id.equals(that.id) && recipe.equals(that.recipe)
        && created.equals(that.created) && Objects.equals(updated, that.updated) && owner
        .equals(that.owner);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, recipe, created, updated, owner, isArchived);
  }
}
