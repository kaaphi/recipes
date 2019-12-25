package com.kaaphi.recipe.txtformat;

import static org.junit.Assert.assertEquals;
import com.kaaphi.recipe.Ingredient;
import com.kaaphi.recipe.Recipe;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.Test;

public class TestTextFormat {
  @Test
  public void testBasicCompleteRecipe() {
    StringBuilder sb = new StringBuilder()
        .append("My Title").append("\n\n")
        .append("1c butter").append("\n")
        .append("sugar").append("\n\n")
        .append("Mix the butter").append("\n\n")
        .append("and the sugar").append("\n\n")
        .append("together").append("\n\n")
        ;
    
    Recipe r = new TextFormat().fromText(sb.toString());
    
    assertEquals("My Title", r.getTitle());
    assertEquals(1, r.getIngredientLists().size());
    assertEquals(2, r.getIngredientLists().get(0).getIngredients().size());
    assertEquals("Mix the butter\r\n\r\nand the sugar\r\n\r\ntogether\r\n", r.getMethod());
    
  }
  
  @Test
  public void testMultipleIngredientListRecipe() {
    StringBuilder sb = new StringBuilder()
        .append("My Title").append("\n\n")
        .append("1c butter").append("\n")
        .append("sugar").append("\n\n")
        .append("For the other thing:\n")
        .append("i1\n")
        .append("i2\n")
        .append("i3\n\n")
        .append("Mix the butter").append("\n\n")
        .append("and the sugar").append("\n\n")
        .append("together").append("\n\n")
        ;
    
    Recipe r = new TextFormat().fromText(sb.toString());
    
    assertEquals("My Title", r.getTitle());
    assertEquals(2, r.getIngredientLists().size());
    assertEquals(2, r.getIngredientLists().get(0).getIngredients().size());
    assertEquals(3, r.getIngredientLists().get(1).getIngredients().size());
    assertEquals("Mix the butter\r\n\r\nand the sugar\r\n\r\ntogether\r\n", r.getMethod());
    
  }
}
