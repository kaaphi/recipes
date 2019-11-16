package com.kaaphi.console;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class ConsoleApp {
  private final CommandContext baseContext;
  private final Map<Class<? extends Object>, Object> instances;
    
  public <T> ConsoleApp(Object...objects) {
    instances = new HashMap<>();    
    baseContext = new CommandContext(new CommandToken("{base}"));
    
    Stream.of(objects).forEach(this::loadCommands);
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
    public void run(PrintStream out, String rawArgs) throws Throwable {
      String[] stringArgs = numArgs > 0 && !rawArgs.isEmpty() ? 
          rawArgs.split("\\s+", numArgs) : new String[0];

      Object[] args = new Object[numArgs + 1];
      args[0] = out;
      System.arraycopy(stringArgs, 0, args, 1, stringArgs.length);
      
      for(int i = stringArgs.length+1; i < method.getParameterCount(); i++) {
        if(method.getParameters()[i].getAnnotation(Nullable.class) == null) {
          out.println("Not enough arguments!");
          return;
        }
      }

      try {
        method.invoke(target, args);
      } catch (InvocationTargetException e) {
        throw e.getCause();
      }
    }
    
    public void showHelp(PrintStream out) {
      out.print(cmd);
      for(int i = 1; i < method.getParameterCount(); i++) {
        Parameter p = method.getParameters()[i];
        out.print(' ');
        if(p.getAnnotation(Nullable.class) == null) {
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
    if(!PrintStream.class.isAssignableFrom(params[0].getType())) {
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
    System.out.print("> ");
    while(!in.hasNextLine());
    if(in.hasNextLine()) {
      String commandLine = in.nextLine();

      if(commandLine.trim().isEmpty()) {
        return true;
      }
      
      if(commandLine.startsWith("exit")) {
        return false;
      }

      baseContext.run(System.out, commandLine);
      
      return true;
    } else {
      System.out.println("Exiting...");
      return false;
    }
  }
}
