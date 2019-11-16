package com.kaaphi.console;

import java.util.Objects;

public class CommandToken implements Comparable<CommandToken> {
  private final String name;
  private final String key;
  
  public CommandToken(String name) {
    this.name = name;
    this.key = name.toLowerCase();
  }
  
  public boolean isEmpty() {
    return name.isEmpty();
  }
  
  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof CommandToken)) {
      return false;
    }
    CommandToken other = (CommandToken) obj;
    return Objects.equals(key, other.key);
  }

  @Override
  public int compareTo(CommandToken o) {
    return name.compareTo(o.name);
  }

  
  
  
}
