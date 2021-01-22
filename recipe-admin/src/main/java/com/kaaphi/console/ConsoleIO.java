package com.kaaphi.console;

import java.io.PrintWriter;
import java.util.Scanner;

public interface ConsoleIO {
  PrintWriter writer();
  Scanner scanner();
  char[] readPassword(String fmt, Object...args);
}
