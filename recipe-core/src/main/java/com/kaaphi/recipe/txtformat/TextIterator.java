package com.kaaphi.recipe.txtformat;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TextIterator {
  private final Iterator<String> delegate;
  
  private String nextLine;
  private String lastReadLine;
  
  public TextIterator(Iterator<String> delegate) {
    this.delegate = delegate;
  }
  
  public String next() {
    if(nextLine != null) {
      lastReadLine = nextLine;
      nextLine = null;
    } else {
      lastReadLine = delegate.next();
    }
    return lastReadLine;
  }
  
  public void undoRead() {
    if(nextLine != null) {
      throw new IllegalStateException("Cannot undo read!");
    }
    nextLine = lastReadLine;
  }
  
  public boolean hasNext() {
    return nextLine != null || delegate.hasNext();
  }

  public void skipEmptyLines() {
    while(hasNext()) {
      if(!next().isEmpty()) {
        undoRead();
        break;
      }
    }
  }
  
  public Stream<String> nextChunk(Predicate<String> until) {
    skipEmptyLines();
    if(!hasNext()) {
      return Stream.empty();
    }
    
    Stream.Builder<String> builder = Stream.builder();

    while(hasNext()) {
      if(until.test(next())) {
        undoRead();
        break;
      }
      builder.add(lastReadLine);
    }
    
    return builder.build();
  }
  
}
