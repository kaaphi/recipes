package com.kaaphi.recipe.admin;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kaaphi.console.ConsoleApp;
import java.net.URI;
import java.net.URISyntaxException;

public class Admin {
  private final ConsoleApp console;
  
  @Inject
  public Admin(UserAdmin user) {
    this.console = new ConsoleApp(user);
  }
  
  public void run() {
    console.run();
  }
  
  public static void main(String[] args) throws Exception {
    Injector injector = Guice.createInjector(new AdminModule());
    AdminConfig config = injector.getInstance(AdminConfig.class);

    try {
      config.setBaseUri(new URI(args[0]));
      config.setUser(args[1]);
      config.setPassword(ConsoleApp.getIo().readPassword("Password for %s: ", config.getUser()));

      injector.getInstance(RecipeApiClient.class).authorize();

      injector.getInstance(Admin.class).run();
    } catch (URISyntaxException e) {
      System.err.println(e);
    }
  }
}
