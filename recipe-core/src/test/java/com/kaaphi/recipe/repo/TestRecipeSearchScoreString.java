package com.kaaphi.recipe.repo;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestRecipeSearchScoreString {

  @Parameters(name = "{0},{1} => {2}")
  public static Object[][] data() {
    return new Object[][]{
        {"abc", "abc", 2},
        {"abc", " abc", 2},
        {"abc", "abc ", 2},
        {"abc", " abc ", 2},
        {"abc", " abc,", 2},
        {"abc", "abc.", 2},
        {"abc", "123abcdefg abc ", 2},
        {"abc", "abcdefg", 1},
        {"abc", " abcdefg", 1},
        {"abc", "123abc.", 1},
        {"abc", "123abc ", 1},
        {"abc", "123abc", 1},
        {"abc", "123abc 123abcdefg", 1},
        {"abc", "123ab", 0},
        {"abc", "123", 0},
        {"abc", "", 0},
        {"", "abc", 0}
    };
  }

  @Rule
  public Timeout timeout = Timeout.millis(500);

  private String toSearch;
  private int expectedScore;
  private RecipeSearch search;

  public TestRecipeSearchScoreString(String searchString, String toSearch, int expectedScore) {
    super();
    this.toSearch = toSearch;
    this.expectedScore = expectedScore;
    search = new RecipeSearch(searchString);
  }


  @Test
  public void testScoreString() {
    assertEquals(expectedScore, search.scoreString(toSearch, false).getScore());
  }
}
