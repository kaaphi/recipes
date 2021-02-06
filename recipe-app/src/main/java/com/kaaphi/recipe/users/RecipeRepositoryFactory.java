package com.kaaphi.recipe.users;

import com.kaaphi.recipe.repo.RecipeRepository;

public interface RecipeRepositoryFactory {
  RecipeRepository createRepository(User user);
}
