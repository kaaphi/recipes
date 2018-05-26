package com.kaaphi.recipe.users.auth;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import org.mindrot.jbcrypt.BCrypt;

public class LongTermAuthPair {
  private static final SecureRandom RANDOM;
  static {
    try {
      RANDOM = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      throw new Error(e);
    }
  }
  
  private final LongTermAuthTokenClient client;
  private final LongTermAuthTokenServer server;
  
  public static LongTermAuthPair generatePair(String username, Instant expires) {
    byte[] selectorBytes = new byte[16];
    byte[] validatorBytes = new byte[64];
    RANDOM.nextBytes(selectorBytes);
    RANDOM.nextBytes(validatorBytes);
    
    Base64.Encoder encoder = Base64.getEncoder();
    String selector = encoder.encodeToString(selectorBytes);
    String validator = encoder.encodeToString(validatorBytes);
    String hashedValidator = BCrypt.hashpw(validator, BCrypt.gensalt());
    
    return new LongTermAuthPair(new LongTermAuthTokenClient(selector, validator), 
        new LongTermAuthTokenServer(selector, hashedValidator, username, expires)
        );
  }
  
  private LongTermAuthPair(LongTermAuthTokenClient client, LongTermAuthTokenServer server) {
    this.client = client;
    this.server = server;
  }

  public static class LongTermAuthTokenClient {
    private final String selector;
    private final String validator;
    public LongTermAuthTokenClient(String selector, String validator) {
      super();
      this.selector = selector;
      this.validator = validator;
    }
  }
  
  public static class LongTermAuthTokenServer {
    private final String selector;
    private final String hashedValidator;
    private final String username;
    private final Instant expires;
    
    public LongTermAuthTokenServer(String selector, String hashedValidator, String username,
        Instant expires) {
      super();
      this.selector = selector;
      this.hashedValidator = hashedValidator;
      this.username = username;
      this.expires = expires;
    }

    
  }
}
