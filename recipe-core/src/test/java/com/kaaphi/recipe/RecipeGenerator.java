package com.kaaphi.recipe;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeGenerator {
  public static Recipe randomRecipe() {
    return new Recipe("ABCD", 
        Stream.of("one","two","three").map(Ingredient::new).collect(Collectors.toList()), 
        "Make the food\r\nand\r\neat it." 
        );
  }
}
