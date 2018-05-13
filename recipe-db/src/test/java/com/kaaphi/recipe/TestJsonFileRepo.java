package com.kaaphi.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.Instant;
import java.util.UUID;

public class TestJsonFileRepo {
  
  
  public static void main(String[] args) {
    String json = "{\"title\":\"ABCD\",\"ingredients\":[{\"name\":\"one\"},{\"name\":\"two\"},{\"name\":\"three\"}],\"method\":\"Make the food\\r\\nand\\r\\neat it.\"}";
    
    System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(
        new RecipeBookEntry(UUID.randomUUID(),RecipeGenerator.randomRecipe(),Instant.now(), Instant.now())));
  }
}
