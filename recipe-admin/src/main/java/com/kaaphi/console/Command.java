package com.kaaphi.console;

import java.io.PrintWriter;

public interface Command {
  void run(ConsoleIO id, String rawArgs) throws Throwable;
  void showHelp(PrintWriter out);
  CommandToken getCommandName();
}