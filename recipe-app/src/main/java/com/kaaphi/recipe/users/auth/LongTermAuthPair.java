package com.kaaphi.recipe.users.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

public class LongTermAuthPair {
  private static final SecureRandom RANDOM = new SecureRandom();
  private static final Base64.Encoder ENCODER = Base64.getEncoder();
  private static final Base64.Decoder DECODER = Base64.getDecoder();
  
  private final LongTermAuthTokenClient client;
  private final LongTermAuthTokenServer server;
  
  public static LongTermAuthPair generatePair(String username, Instant expires) {
    byte[] selectorBytes = new byte[64];
    RANDOM.nextBytes(selectorBytes);
    
    String selector = ENCODER.encodeToString(selectorBytes);
 
    return generatePair(username, selector, expires);
  }
  
  public static LongTermAuthPair regeneratePair(LongTermAuthTokenServer serverToken, Instant expires) {
    return generatePair(
        serverToken.getUsername(), 
        serverToken.getSelector(), 
        Optional.ofNullable(expires).orElse(serverToken.getExpires())
        );
  }

  private static LongTermAuthPair generatePair(String username, String selector, Instant expires) {
    byte[] validatorBytes = new byte[128];
    RANDOM.nextBytes(validatorBytes);
    byte[] hashedValidator = hash(validatorBytes);

    return new LongTermAuthPair(new LongTermAuthTokenClient(selector, validatorBytes), 
        new LongTermAuthTokenServer(selector, hashedValidator, username, expires)
        );
  }
  
  private static byte[] hash(byte[] array) {
    try {
      return MessageDigest.getInstance("SHA-256").digest(array);
    } catch (NoSuchAlgorithmException e) {
      //All JVMs required to support SHA-256
      throw new Error(e);
    }
  }
  
  public static LongTermAuthTokenClient parseClientToken(String tokenString) {
    String[] parts = tokenString.split(":", 2);
    return new LongTermAuthTokenClient(parts[0], DECODER.decode(parts[1]));
  }
  
  private LongTermAuthPair(LongTermAuthTokenClient client, LongTermAuthTokenServer server) {
    this.client = client;
    this.server = server;
  }
  
  public LongTermAuthTokenClient getClient() {
    return client;
  }

  public LongTermAuthTokenServer getServer() {
    return server;
  }

  public static class LongTermAuthTokenClient {
    private final String selector;
    private final byte[] validator;
    public LongTermAuthTokenClient(String selector, byte[] validator) {
      super();
      this.selector = selector;
      this.validator = validator;
    }
    public String getSelector() {
      return selector;
    }
    public byte[] getValidator() {
      return validator;
    }
    public String toString() {
      return String.format("%s:%s", selector, ENCODER.encodeToString(validator));
    }
  }
  
  public static class LongTermAuthTokenServer {
    private final String selector;
    private final byte[] hashedValidator;
    private final String username;
    private final Instant expires;
    
    public LongTermAuthTokenServer(String selector, byte[] hashedValidator, String username,
        Instant expires) {
      super();
      this.selector = selector;
      this.hashedValidator = hashedValidator;
      this.username = username;
      this.expires = expires;
    }

    public String getSelector() {
      return selector;
    }

    public byte[] getHashedValidator() {
      return hashedValidator;
    }

    public String getUsername() {
      return username;
    }

    public Instant getExpires() {
      return expires;
    }
    
    public boolean validate(LongTermAuthTokenClient clientToken) {
      byte[] hashedClient = hash(clientToken.getValidator());
      return selector.equals(clientToken.getSelector()) && MessageDigest.isEqual(hashedValidator, hashedClient);
    }
  }
}
