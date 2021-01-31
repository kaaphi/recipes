package com.kaaphi.recipe.users;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserShare userShare = (UserShare) o;
    return fromUser.equals(userShare.fromUser) && toUser.equals(userShare.toUser);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fromUser, toUser);
  }

  @Override
  public String toString() {
    return "UserShare{" +
        "fromUser=" + fromUser +
        ", toUser=" + toUser +
        '}';
  }
}
