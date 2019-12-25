package com.kaaphi.recipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeGenerator {
  public static Recipe randomRecipe() {
    return new Recipe("ABCD", 
        Arrays.asList(
            new IngredientList(null, Stream.of("one","two","three").map(Ingredient::new).collect(Collectors.toList())),
            new IngredientList("For the part:", Stream.of("other", "stuff").map(Ingredient::new).collect(Collectors.toList()))
            
            ), 
        "Make the food\r\nand\r\neat it.",
        Collections.emptyList()
        );
  }
}
