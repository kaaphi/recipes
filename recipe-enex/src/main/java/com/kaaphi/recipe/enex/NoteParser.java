package com.kaaphi.recipe.enex;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class NoteParser extends DefaultHandler {
  private List<String> ingredients = new ArrayList<>();
  private StringBuilder method = new StringBuilder();;
  
  private ParseState initState = new ParseState() {
    @Override
    public ParseState startElement(String qName) {
      if("ul".equalsIgnoreCase(qName)) {
        return ingredientState;
      } else {
        return this;
      }
    }
  };
  
 
  
  private ParseState methodState = new ParseState() {
    
    
    @Override
    public ParseState startElement(String qName) throws SAXException {
      if("ol".equalsIgnoreCase(qName)) {
        methodListState.ordered = true;
        return methodListState;
      } else if("ul".equalsIgnoreCase(qName)) {
        methodListState.ordered = false;
        return methodListState;
      }
      
      return this;
    }

    @Override
    public ParseState endElement(String qName) {
      if("br".equalsIgnoreCase(qName)) {
        method.append("\r\n");
      }
      
      if("method".equalsIgnoreCase(method.toString().trim())) {
        method = new StringBuilder();
      }
      return this;
    }
    
    @Override
    public ParseState characters(char[] ch, int start, int length) {
      
      
      method.append(ch, start, length);
      
      return this;
    }
  };
  
  private ParseState ingredientState = new ListParseState(methodState, (ordered, item) -> {
    ingredients.add(item);
  });
  
  private ListParseState methodListState = new ListParseState(methodState, (ordered, item) -> {
    method
    .append(ordered ? "1. " : "* ")
    .append(item)
    .append("\r\n");
  });
  
  private ParseState currentState = initState;
  
  public List<String> getIngredients() {
    return ingredients;
  }
  
  public String getMethod() {
    return method.toString();
  }
  
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    currentState = currentState.characters(ch, start, length);
  }
  
  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    currentState = currentState.endElement(qName);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    currentState = currentState.startElement(qName);
  }
  
  @Override
  public void endDocument() throws SAXException {
    if(currentState != methodState) {
      throw new SAXException("Bad state!");
    }
  }

  private static class ListParseState implements ParseState {
    private StringBuilder currentListItem;
    private boolean ordered;
    private BiConsumer<Boolean, String> itemConsumer;
    private ParseState nextState;
    
    public ListParseState(ParseState nextState, BiConsumer<Boolean, String> itemConsumer) {
      this.nextState = nextState;
      this.itemConsumer = itemConsumer;
    }
    
    @Override
    public ParseState startElement(String qName) throws SAXException {
      if("li".equalsIgnoreCase(qName)) {
        currentListItem = new StringBuilder();
      } else if("ul".equalsIgnoreCase(qName) || "ol".equalsIgnoreCase(qName)) {
        throw new SAXException("Cannot handle nested list!");
      }
      return this;
    }

    @Override
    public ParseState characters(char[] ch, int start, int length) throws SAXException {
      if(currentListItem != null) {
        currentListItem.append(ch, start, length);
      }
      return this;
    }
    
    

    @Override
    public ParseState endElement(String qName) throws SAXException {
      if("li".equalsIgnoreCase(qName)) {
        itemConsumer.accept(ordered, currentListItem.toString().trim());
        currentListItem = null;
      } else if (!ordered && "ul".equalsIgnoreCase(qName)) {
        return nextState;
      } else if(ordered && "ol".equalsIgnoreCase(qName)) {
        return nextState;
      }
      return this;
    }
    
    
    
  }

  private interface ParseState {
    public default ParseState startElement(String qName) throws SAXException {
      return this;
    }
    public default ParseState characters(char[] ch, int start, int length) throws SAXException {
      return this;
    }
    public default ParseState endElement(String qName) throws SAXException {
      return this;
    }
  }
  
}
