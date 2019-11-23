package com.kaaphi.recipe.users;

public class UserShare {
  private final User fromUser;
  private final User toUser;
  
  public UserShare(User fromUser, User toUser) {
    this.fromUser = fromUser;
    this.toUser = toUser;
  }
  public User getFromUser() {
    return fromUser;
  }
  public User getToUser() {
    return toUser;
  }
}
