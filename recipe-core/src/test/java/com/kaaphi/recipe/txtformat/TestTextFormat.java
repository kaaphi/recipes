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
    assertEquals(2, r.getIngredients().size());
    assertEquals("Mix the butter\r\n\r\nand the sugar\r\n\r\ntogether\r\n", r.getMethod());
    
  }
}
