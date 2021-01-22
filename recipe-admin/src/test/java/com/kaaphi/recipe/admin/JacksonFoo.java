package com.kaaphi.recipe.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaaphi.recipe.users.User;
import java.util.List;

public class JacksonFoo {

  public static void main(String[] args) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    List<User> users = mapper.
        readValue("[{\"username\":\"test\",\"id\":1}]", new TypeReference<List<User>>() {});
    System.out.println(users);
  }
}
