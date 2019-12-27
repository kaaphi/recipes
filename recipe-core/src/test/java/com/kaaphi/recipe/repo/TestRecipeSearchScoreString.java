package com.kaaphi.recipe.repo;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestRecipeSearchScoreString {
  @Parameters
  public static Object[][] data() {
    return new Object[][] {
      {"abc", 2},
      {" abc", 2},
      {"abc ", 2},
      {" abc ", 2},
      {" abc,", 2},
      {"abc.", 2},
      {"123abcdefg abc ", 2},
      {"abcdefg", 1},
      {" abcdefg", 1},
      {"123abc.", 1},
      {"123abc ", 1},
      {"123abc", 1},
      {"123abc 123abcdefg", 1},
      {"123ab", 0},
      {"123", 0}

    };
  }
  
  private String toSearch;
  private int expectedScore;
  private RecipeSearch search;
  
  public TestRecipeSearchScoreString(String toSearch, int expectedScore) {
    super();
    this.toSearch = toSearch;
    this.expectedScore = expectedScore;
    search = new RecipeSearch("abc");
  }



  @Test
  public void testScoreString() {
    assertEquals(expectedScore, search.scoreString(toSearch, false).getScore());
  }
}
