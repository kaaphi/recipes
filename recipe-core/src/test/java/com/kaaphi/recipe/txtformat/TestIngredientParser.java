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
      {"four items", null, "four items"},
      {"1/2 cup butter", "1/2 cup", "butter"},
      {"1/4 - 1/3 cup rice", "1/4 - 1/3 cup", "rice"},
      {"1.25 scoops stuff", "1.25 scoops", "stuff"},
      {"1/4 apple", "1/4", "apple"},
      {"1c. sugar", "1c.", "sugar"},
      {"1 tbps. sugar", "1 tbps.", "sugar"}
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
