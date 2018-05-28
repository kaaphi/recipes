package com.kaaphi.recipe.users;

import com.kaaphi.recipe.repo.RecipeRepository;

public interface RecipeRepositoryFactory {
  public RecipeRepository createRepository(User user);
}
