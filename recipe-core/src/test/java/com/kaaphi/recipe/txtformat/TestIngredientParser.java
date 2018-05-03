package com.kaaphi.recipe.txtformat;

import static org.junit.Assert.assertEquals;
import com.kaaphi.recipe.Ingredient;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestIngredientParser {
  
  @Parameters(name = "{index} : {0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {     
      {"item", null, "item"},
      {"1b item", "1b", "item"},
      {"1 b item", "1 b", "item"},
      {"1 item", "1", "item"},
      {"1item", null, "1item"},
      {"four items", null, "four items"}
      });
    }

  public TestIngredientParser(String input, String quantity, String name) {
    this.input = input;
    this.expected = new Ingredient(name, quantity);
  }
  
  private String input;
  private Ingredient expected;
  
  @Test
  public void test() {
    assertEquals(expected, new IngredientParser().fromString(input));
  }
}
