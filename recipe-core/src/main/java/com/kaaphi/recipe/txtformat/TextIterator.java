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
    return delegate.hasNext();
  }
  
  public String nextNonEmpty() {
    lastReadLine = null;
    while(delegate.hasNext() && next().isEmpty());
    return lastReadLine;
  }
  
  public Stream<String> nextChunk(Predicate<String> until) {
    String first = nextNonEmpty();
    if(first == null) {
      return Stream.empty();
    }
    
    Stream.Builder<String> builder = Stream.builder();
    builder.add(first);
    
    while(delegate.hasNext() && !(until.test(next()))) {
        builder.add(lastReadLine);  
    }
    undoRead();
    
    return builder.build();
  }
  
}
