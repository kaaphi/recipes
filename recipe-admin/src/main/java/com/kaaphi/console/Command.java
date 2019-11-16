package com.kaaphi.console;

import java.io.PrintStream;

public interface Command {
  void run(PrintStream out, String rawArgs) throws Throwable;
  void showHelp(PrintStream out);
  CommandToken getCommandName();
}