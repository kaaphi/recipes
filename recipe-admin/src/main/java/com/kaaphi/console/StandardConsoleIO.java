package com.kaaphi.console;

import java.io.PrintWriter;
import java.util.Scanner;

public class StandardConsoleIO implements ConsoleIO {
  private final PrintWriter writer =  new PrintWriter(System.out, true);
  private final Scanner scanner = new Scanner(System.in);

  @Override
  public PrintWriter writer() {
    return writer;
  }

  @Override
  public Scanner scanner() {
    return scanner;
  }

  @Override
  public char[] readPassword(String fmt, Object...args) {
    writer.format(fmt, args);
    writer.flush();
    return scanner().next().toCharArray();
  }
}
