package com.kaaphi.recipe.users;

public class UserShare {
  private User fromUser;
  private User toUser;

  //needed for jackson
  private UserShare() {}

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
