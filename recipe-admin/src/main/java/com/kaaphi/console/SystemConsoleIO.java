package com.kaaphi.console;

import java.io.Console;
import java.io.PrintWriter;
import java.util.Scanner;

public class SystemConsoleIO implements ConsoleIO {
  private final Console console;

  public SystemConsoleIO(Console console) {
    this.console = console;
  }

  @Override
  public PrintWriter writer() {
    return console.writer();
  }

  @Override
  public Scanner scanner() {
    return new Scanner(console.reader());
  }

  @Override
  public char[] readPassword(String fmt, Object...args) {
    return console.readPassword(fmt, args);
  }
}
