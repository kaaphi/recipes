package com.kaaphi.recipe;

import java.util.Collections;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.kaaphi.recipe.module.RecipeModule;
import com.kaaphi.recipe.repo.RecipeRepository;
import com.kaaphi.recipe.repo.jsonfile.JsonRecipeRepository;
import com.kaaphi.recipe.users.RecipeRepositoryFactory;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;

public class MigrateRecipeRepo {
  
  private RecipeRepository target;
  private Gson storeGson;
  
  @Inject
  public MigrateRecipeRepo(UserRepository userRepo, RecipeRepositoryFactory recipeRepoFactory, @Named("repoGson") Gson gson) {
    target = recipeRepoFactory.createRepository(userRepo.getUserByUsername("kaaphi"));
    this.storeGson = gson;
  }
  
  public static void main(String[] args) throws Exception {
    Injector injector = Guice.createInjector(new RecipeModule());
    
    MigrateRecipeRepo migrate = injector.getInstance(MigrateRecipeRepo.class);
        
    RecipeRepository source = new JsonRecipeRepository("/home/kaaphi/code/recipes_docker_volume", migrate.storeGson, new User("kaaphi", Collections.emptyMap()));
    
    //System.out.println("SOURCE:");
    migrate.target.saveAll(source.getAll());
    
    System.out.println("TARGET:");
    migrate.target.getAll().forEach(System.out::println);
  }
}
