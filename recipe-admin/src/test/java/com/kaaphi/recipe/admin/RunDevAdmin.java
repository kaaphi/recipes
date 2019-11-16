package com.kaaphi.recipe.admin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kaaphi.recipe.app.RunDevRecipeApp.DevRecipeModule;

public class RunDevAdmin {
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new DevRecipeModule());
    
    injector.getInstance(Admin.class).run();   
  }
}
