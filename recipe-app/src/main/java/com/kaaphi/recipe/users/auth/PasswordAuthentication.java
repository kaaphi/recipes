package com.kaaphi.recipe.users.auth;

import io.javalin.http.Context;
import org.mindrot.jbcrypt.BCrypt;

public abstract class PasswordAuthentication extends AbstractAuthenticationMethod {
  public static final String PASSWORD_TYPE = "PasswordDetails";
  
  @Override
  protected boolean authenticate(String authDetails, Context ctx) {
    return authenticate(authDetails, getPassword(ctx));
  }

  @Override
  protected String generateNewAuthenticationDetails(Context ctx) {
    return generateNewDetails(getNewPassword(ctx));
  }

  @Override
  public String getDetailsType() {
    return PASSWORD_TYPE;
  }

  protected abstract String getPassword(Context ctx);
  protected String getNewPassword(Context ctx) {
    throw new UnsupportedOperationException();
  }

  public static boolean authenticate(String hashedPassword, String password) {
    return BCrypt.checkpw(password, hashedPassword);
  }

  public static String generateNewDetails(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }
}
