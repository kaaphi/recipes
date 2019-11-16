package com.kaaphi.recipe.admin;

import java.io.PrintStream;
import java.util.Collections;
import com.google.inject.Inject;
import com.kaaphi.console.CommandContextClass;
import com.kaaphi.console.ConsoleCommand;
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
    userRepo.addUser(new User(username, Collections.singletonMap(PasswordAuthentication.PASSWORD_TYPE, PasswordAuthentication.generateNewDetails(password))));
  }
  
  @ConsoleCommand
  public void update(PrintStream out, String username, String password) {
    User user = userRepo.getUserByUsername(username);
    if(user == null) {
      out.println("No user with that name exists!");      
    } else {
      user.setAuthDetails(PasswordAuthentication.PASSWORD_TYPE, PasswordAuthentication.generateNewDetails(password));
      userRepo.updateUser(user);
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
  public void show(PrintStream out, String username) {
    User user = userRepo.getUserByUsername(username);
    out.println(user);
  }
}
