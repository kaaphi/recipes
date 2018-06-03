package com.kaaphi.recipe.admin;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kaaphi.recipe.app.ProductionRecipeModule;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import com.kaaphi.recipe.users.auth.PasswordAuthentication;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class UserAdmin {
  
  private UserRepository userRepo;
  
  private enum Command {
    ADD(2, UserAdmin::add),
    UPDATE(2, UserAdmin::update),
    DELETE(1, UserAdmin::delete),
    SHOW(1, UserAdmin::show)
    ;
    private final BiConsumer<UserAdmin, List<String>> action;
    private final int numArgs;
    
   private Command(int numArgs, BiConsumer<UserAdmin, List<String>> action) {
     this.action = action;
     this.numArgs = numArgs;
   }
  }
  
  @Inject
  public UserAdmin(UserRepository userRepo) {
    this.userRepo = userRepo;
  }
  
  public void run() {
    try(Scanner in = new Scanner(System.in)) {
      boolean shutdown = false;
      while(!shutdown) {
        try {
          shutdown = !command(in);
        } catch (Throwable th) {
          th.printStackTrace();
        }
      }
    }
  }
  
  private boolean command(Scanner in) {
    System.out.print("> ");
    while(!in.hasNextLine());
    if(in.hasNextLine()) {
      String commandLine = in.nextLine();

      if(commandLine.trim().isEmpty()) {
        return true;
      }

      String[] split = commandLine.split("\\s+", 2);
      Optional<Command> command = Stream.of(Command.values())
          .filter(cmd -> split[0].equalsIgnoreCase(cmd.name()))
          .findAny();    

      if(command.isPresent()) {
        String[] args = split[1].split("\\s+", command.get().numArgs);
        command.get().action.accept(this, Arrays.asList(args));
        System.out.println("Success!");
      } else {
        System.out.println("Command does not exist!");
      }
      return true;
    } else {
      System.out.println("Exiting...");
      return false;
    }
  }
  
  private void add(List<String> args) {
    String username = args.get(0);
    String password = args.get(1);
    userRepo.addUser(new User(username, Collections.singletonMap(PasswordAuthentication.PASSWORD_TYPE, PasswordAuthentication.generateNewDetails(password))));
  }
  
  private void update(List<String> args) {
    String username = args.get(0);
    String password = args.get(1);
    User user = userRepo.getUserByUsername(username);
    if(user == null) {
      System.out.println("No user with that name exists!");      
    } else {
      user.setAuthDetails(PasswordAuthentication.PASSWORD_TYPE, PasswordAuthentication.generateNewDetails(password));
      userRepo.updateUser(user);
    }    
  }
  
  private void delete(List<String> args) {
    String username = args.get(0);
    User user = userRepo.getUserByUsername(username);
    if(user == null) {
      System.out.println("No user with that name exists!");      
    } else {
      userRepo.deleteUser(user);
    }    
  }
  
  private void show(List<String> args) {
    String username = args.get(0);
    User user = userRepo.getUserByUsername(username);
    System.out.println(user);
  }
  
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new ProductionRecipeModule());
    
    injector.getInstance(UserAdmin.class).run();    
  }
}
