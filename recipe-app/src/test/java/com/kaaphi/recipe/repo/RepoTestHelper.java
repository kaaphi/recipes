package com.kaaphi.recipe.repo;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.kaaphi.recipe.Recipe;
import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.users.AuthenticatableUser;
import com.kaaphi.recipe.users.RecipeRepositoryFactory;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import com.kaaphi.recipe.users.auth.PasswordAuthentication;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

public class RepoTestHelper {
  private RecipeRepositoryFactory recipeRepositoryFactory;
  private UserRepository userRepo;
  private RepoTestTransaction transaction;

  public static RepoTestHelper getInstance(Module module) {
    return Guice.createInjector(module).getInstance(RepoTestHelper.class);
  }

  @Inject
  public RepoTestHelper(RecipeRepositoryFactory recipeRepositoryFactory,
      UserRepository userRepo,
      RepoTestTransaction transaction) {
    this.recipeRepositoryFactory = recipeRepositoryFactory;
    this.userRepo = userRepo;
    this.transaction = transaction;
  }

  public void rollback() throws Exception {
    transaction.rollbackTransaction();
  }

  public RecipeRepository getRecipeRepo(User user) {
    return recipeRepositoryFactory.createRepository(user);
  }

  public static RecipeBookEntry createBasicRecipeBookEntry(User user, String recipeName) {
    return createBasicRecipeBookEntry(UUID.randomUUID(), user, recipeName);
  }

  public static RecipeBookEntry createBasicRecipeBookEntry(UUID id, User user, String recipeName) {
    return new RecipeBookEntry(id, new Recipe(recipeName, Collections.emptyList(), "", Collections.emptyList()),
        Instant.now(), Instant.now(), user);
  }

  public UserRepository getUserRepo() {
    return userRepo;
  }

  public AuthenticatableUser createAuthenticatableUser(String username) {
    return new AuthenticatableUser(username, PasswordAuthentication.PASSWORD_TYPE, "fakehash");
  }

  public static interface RepoTestTransaction {
    void beginTransaction() throws Exception;
    void rollbackTransaction() throws Exception;
  }
}
