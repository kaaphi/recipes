package com.kaaphi.recipe.admin;

import java.io.PrintStream;
import java.util.Collections;
import com.google.inject.Inject;
import com.kaaphi.console.CommandContextClass;
import com.kaaphi.console.ConsoleCommand;
import com.kaaphi.recipe.users.AuthenticatableUser;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import com.kaaphi.recipe.users.auth.PasswordAuthentication;

@CommandContextClass("user")
public class UserAdmin {
  
  private UserRepository userRepo;
  
  @Inject
  public UserAdmin(UserRepository userRepo) {
    this.userRepo = userRepo;
  }
  
  @ConsoleCommand
  public void add(PrintStream out, String username, String password) {
    userRepo.addUser(new AuthenticatableUser(username, PasswordAuthentication.PASSWORD_TYPE, PasswordAuthentication.generateNewDetails(password)));
  }
  
  @ConsoleCommand
  public void update(PrintStream out, String username, String password) {
    User user = userRepo.getUserByUsername(username);
    if(user == null) {
      out.println("No user with that name exists!");      
    } else {
      userRepo.setAuthDetails(user, Collections.singletonMap(PasswordAuthentication.PASSWORD_TYPE, PasswordAuthentication.generateNewDetails(password)));
    }    
  }
  
  @ConsoleCommand
  public void delete(PrintStream out, String username) {
    User user = userRepo.getUserByUsername(username);
    if(user == null) {
      out.println("No user with that name exists!");      
    } else {
      userRepo.deleteUser(user);
    }    
  }
  
  @ConsoleCommand
  public void showAll(PrintStream out) {
    userRepo.getAll().stream()
    .map(User::getUsername)
    .forEach(out::println);
  }
  
  @ConsoleCommand
  public void show(PrintStream out, String username) {
    User user = userRepo.getUserByUsername(username);
    out.println(user);
  }
}
