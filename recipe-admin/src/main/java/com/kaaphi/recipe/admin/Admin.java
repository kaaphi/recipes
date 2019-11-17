package com.kaaphi.recipe.admin;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kaaphi.console.ConsoleApp;
import com.kaaphi.recipe.module.ProductionRecipeModule;

public class Admin {
  private final ConsoleApp console;
  
  @Inject
  public Admin(UserAdmin user) {
    this.console = new ConsoleApp(user);
  }
  
  public void run() {
    console.run();
  }
  
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(ProductionRecipeModule.getProductionModule());
    
    injector.getInstance(Admin.class).run();
  }
}
