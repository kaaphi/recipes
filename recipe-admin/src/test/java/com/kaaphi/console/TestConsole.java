package com.kaaphi.console;

import java.io.PrintStream;
import javax.annotation.Nullable;

public class TestConsole {
  @ConsoleCommand
  public void cmd1(PrintStream out) {
    out.println("Cmd1!");
  }
  
  @ConsoleCommand
  public void cmd2(PrintStream out, String arg1) {
    out.format("Cmd2 : <%s>!%n", arg1);
  }
  
  @ConsoleCommand("cmd3")
  public void third(PrintStream out, String arg1, @Nullable String arg2) {
    out.format("Cmd3 : <%s> <%s>!%n", arg1, arg2);
  }
  
  @CommandContextClass("sub")
  public static class TestConsoleSubContext {
    @ConsoleCommand
    public void thing(PrintStream out) {
      out.println("THING");
    }
    
    @ConsoleCommand
    public void otherThing(PrintStream out, String blah) {
      out.println("OTHER THING " + blah);
    }
  }
  
  public static void main(String[] args) {
    new ConsoleApp(new TestConsole(), new TestConsoleSubContext()).run();
  }
}
