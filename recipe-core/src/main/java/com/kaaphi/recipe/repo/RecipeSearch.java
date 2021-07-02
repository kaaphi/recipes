package com.kaaphi.recipe.repo;

import com.kaaphi.recipe.Ingredient;
import com.kaaphi.recipe.IngredientList;
import com.kaaphi.recipe.RecipeBookEntry;

public class RecipeSearch {
  private static final int CONTEXT_RADIUS = 10;
  
  private final String searchString;
  
  public RecipeSearch(String searchString) {
    this.searchString = searchString.toLowerCase();
  }
  
  public RecipeSearchResult createResultForRecipe(RecipeBookEntry e) {
    int maxScore = 0;
    int score = 0;
    
    StringMatch match;
    StringMatch lastValidMatch = null;
    
    match = scoreString(e.getRecipe().getMethod(), false);
    score += match.getScore();
    if(match.getScore() > 0) {
      lastValidMatch = match;
    }
    maxScore += 2;
    
    match = null;
    list: for(IngredientList list : e.getRecipe().getIngredientLists()) {
      for(Ingredient ingredient : list.getIngredients() ) {
        StringMatch iMatch = scoreString(ingredient.getName(), true);
        
        if(iMatch.getScore() > 0) {
          match = iMatch;
          if(match.getScore() == 2) {
            break list;
          }
        }
      }
    }

    if(match != null) {
      match = new StringMatch(match.getScore(), "ingredient: " + match.getPreContext(), match.getMatch(), match.getPostContext());
      score += match.getScore();
      if(match.getScore() > 0) {
        lastValidMatch = match;
      }
    }
    maxScore += 2;

    match = scoreString(e.getRecipe().getTitle(), true);
    boolean titleMatch = match.getScore() > 0;
    if(match.getScore() > 0) {
      lastValidMatch = match;
    }
    score += 2 * match.getScore();
    maxScore += 2 * 2;
    
    if(score > 0) {
      return new RecipeSearchResult(e, (double)score/(double)maxScore, lastValidMatch, titleMatch);
    } else {
      return null;
    }
  }
  
  StringMatch scoreString(String string, boolean fullContext) {
    if(searchString.isEmpty()) {
      return new StringMatch(0, null, null, null);
    }

    String toSearch = string.toLowerCase();
    
    int score = 0;
    int i = -1;
    int matchIdx = -1;
    while((i = toSearch.indexOf(searchString, i+1)) >= 0) {
      matchIdx = i;
      score = 1;

      //check for whole word match
      if((i == 0 || string.charAt(i-1) == ' ')
          && (i+searchString.length() == string.length() || isWordEndingChar(string.charAt(i + searchString.length())))
          ) {
        score =  2;
        break;
      }
    }
    
    String preContext, postContext, match;
    if(score > 0) {
      //get pre context
      int preContextStart;
      if(fullContext || matchIdx == 0) {
        preContextStart = 0;
      } else {
        //if not full context, include context going back to the first full word that ends after CONTEXT_RADIUS 
        preContextStart = string.lastIndexOf(' ', matchIdx - CONTEXT_RADIUS);
        if(preContextStart < 0) {
          preContextStart = 0;
        } else {
          preContextStart++;
        }
      }
      
      
      preContext = string.substring(preContextStart, matchIdx);
      int endOfMatch = matchIdx + searchString.length();
      match = string.substring(matchIdx, endOfMatch);
      
      int postContextEnd;
      if(fullContext) {
        postContextEnd = string.length();
      } else {
        //if not full context, include context going forward to the last full word that starts before CONTEXT_RADIUS 
        postContextEnd = string.indexOf(' ', endOfMatch + CONTEXT_RADIUS);
        if(postContextEnd < 0) {
          postContextEnd = string.length();
        }
      }
      
      postContext = string.substring(endOfMatch,postContextEnd);
    } else {
      preContext = null;
      match = null;
      postContext = null;
    }
    
    return new StringMatch(score, preContext, match, postContext);
  }
  
  private static boolean isWordEndingChar(char ch) {
    switch(ch) {
      case ' ':
      case '.':
      case ';':
      case ',':
      case ':':
        return true;
        
      default:
        return false;
    }
  }
  
  public static class StringMatch {
    private final int score;
    private final String preContext;
    private final String match;
    private final String postContext;
    
    public StringMatch(int score, String preContext, String match, String postContext) {
      this.score = score;
      this.preContext = preContext;
      this.match = match;
      this.postContext = postContext;
    }

    public int getScore() {
      return score;
    }

    public String getPreContext() {
      return preContext;
    }
    
    public String getMatch() {
      return match;
    }

    public String getPostContext() {
      return postContext;
    }
  }
}
