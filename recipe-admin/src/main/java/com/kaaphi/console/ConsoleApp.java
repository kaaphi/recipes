package com.kaaphi.console;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class ConsoleApp {
  private final CommandContext baseContext;
  private final Map<Class<? extends Object>, Object> instances;
  private final ConsoleIO io;
    
  public <T> ConsoleApp(Object...objects) {
    instances = new HashMap<>();    
    baseContext = new CommandContext(new CommandToken("{base}"));
    io = getIo();
    
    Stream.of(objects).forEach(this::loadCommands);
  }

  public static ConsoleIO getIo() {
    if(System.console() != null) {
      return new SystemConsoleIO(System.console());
    } else {
      return new StandardConsoleIO();
    }
  }
  
  private void loadCommands(Object obj) {
    Class<?> cls = obj.getClass();
    if(instances.containsKey(cls)) {
      throw new IllegalArgumentException(String.format("Duplicate class %s in objects!", obj.getClass()));
    }
    
    instances.put(cls, obj);
    
    CommandContextClass classContext = cls.getAnnotation(CommandContextClass.class);
    CommandContext context = baseContext;
    if(classContext != null) {
      context = new CommandContext(new CommandToken(classContext.value()));
      baseContext.addSubContext(context);
    }
   
    Stream.of(cls.getMethods())
        .filter(m -> m.getAnnotation(ConsoleCommand.class) != null)
        .map(m -> getCommand(cls, m))
        .forEach(context::addCommand);
  }
  
  private static class StringArgsCommand implements Command {
    private int numArgs;
    private CommandToken cmd;
    private Method method;
    private Object target;
    
    public StringArgsCommand(CommandToken cmd, Method method, Object target) {
      super();
      this.cmd = cmd;
      this.method = method;
      this.target = target;
      this.numArgs = method.getParameterCount()-1;
    }
    
    public CommandToken getCommandName() {
      return cmd;
    }

    @Override
    public void run(ConsoleIO io, String rawArgs) throws Throwable {
      String[] stringArgs = numArgs > 0 && !rawArgs.isEmpty() ? 
          rawArgs.split("\\s+", numArgs) : new String[0];

      Object[] args = new Object[numArgs + 1];
      args[0] = io.writer();
      for(int i = 1, stringArgsIdx = 0; i < method.getParameterCount(); i++) {
        if(method.getParameters()[i].getAnnotation(Confidential.class) != null) {
          args[i] = readConfidential(io, method.getParameters()[i]);
        } else if(stringArgsIdx >= stringArgs.length) {
          if(method.getParameters()[i].getAnnotation(Nullable.class) == null) {
            io.writer().println("Not enough arguments!");
            return;
          } else {
            args[i] = null;
          }
        } else {
          args[i] = stringArgs[stringArgsIdx++];
        }
      }

      try {
        method.invoke(target, args);
      } catch (InvocationTargetException e) {
        throw e.getCause();
      }
    }

    private String readConfidential(ConsoleIO io, Parameter p) throws IOException {
      char[] confidential = io.readPassword("Enter %s: ", p.getName());
      char[] confirm = io.readPassword("Confirm %s: ", p.getName());
      if(!Arrays.equals(confidential, confirm)) {
        throw new IOException("Did not match!");
      } else {
        return new String(confidential);
      }
    }
    
    public void showHelp(PrintWriter out) {
      out.print(cmd);
      for(int i = 1; i < method.getParameterCount(); i++) {
        Parameter p = method.getParameters()[i];
        out.print(' ');
        if(p.getAnnotation(Confidential.class) != null) {
          //skip
        } else if(p.getAnnotation(Nullable.class) == null) {
          out.print(p.getName());
        } else {
          out.print('[');
          out.print(p.getName());
          out.print(']');
        }
      }
      out.println();
    }
  }
  
  private static CommandToken getCommandName(Method m) {
    ConsoleCommand cmd = m.getAnnotation(ConsoleCommand.class);
    return new CommandToken(("\u0000".equals(cmd.value()) ? m.getName() : cmd.value()));
  }
  
  private Command getCommand(Class<?> cls, Method m) {
    Parameter[] params = m.getParameters();
    if(!PrintWriter.class.isAssignableFrom(params[0].getType())) {
      throw new IllegalArgumentException(String.format("First parameter of method %s must be a PrintStream!", m));
    }
    
    for(int i = 1; i < params.length; i++) {
      if(!String.class.isAssignableFrom(params[i].getType())) {
        throw new IllegalArgumentException("Only string arguments are supported!");
      }
    }
    
    return new StringArgsCommand(getCommandName(m), m, instances.get(m.getDeclaringClass()));
  }

  public void run() {
    try(Scanner in = new Scanner(System.in)) {
      boolean shutdown = false;
      while(!shutdown) {
        try {
          shutdown = !command(in);
        } catch (Throwable th) {
          th.printStackTrace();
        }
      }
    }
  }
  
  private boolean command(Scanner in) throws Throwable {
    io.writer().print("> ");
    io.writer().flush();
    while(!in.hasNextLine());
    if(in.hasNextLine()) {
      String commandLine = in.nextLine();

      if(commandLine.trim().isEmpty()) {
        return true;
      }
      
      if(commandLine.startsWith("exit")) {
        return false;
      }

      baseContext.run(io, commandLine);
      
      return true;
    } else {
      io.writer().println("Exiting...");
      return false;
    }
  }
}
