package com.kaaphi.recipe;

import java.util.Collections;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kaaphi.recipe.module.RecipeModule;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import com.kaaphi.recipe.users.auth.PasswordAuthentication;

public class PopulateUsers {
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new RecipeModule());
    
    injector.getInstance(PopulateUsers.class);
    
    UserRepository userRepo = injector.getInstance(UserRepository.class);
    
    userRepo.addUser(new User("kaaphi", Collections.singletonMap(PasswordAuthentication.PASSWORD_TYPE, PasswordAuthentication.generateNewDetails("p"))));
  }

}
