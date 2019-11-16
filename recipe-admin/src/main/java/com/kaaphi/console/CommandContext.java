package com.kaaphi.console;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class CommandContext {
  private static final CommandToken HELP = new CommandToken("help");
  
  private final Map<CommandToken, CommandContext> subContexts;
  private final Map<CommandToken, Command> commands;
  private final CommandToken name;
  
  public CommandContext(CommandToken name) {
    super();
    this.subContexts = new HashMap<>();
    this.commands = new HashMap<>();
    this.name = name;
  }
  
  public void addCommand(Command command) {
    if(commands.containsKey(command.getCommandName())) {
      throw new IllegalArgumentException(String.format("Context %s already contains token %s!", this, command.getCommandName()));
    }
    
    commands.put(command.getCommandName(), command);
  }
  
  public void addSubContext(CommandContext context) {
    if(subContexts.containsKey(context.getName())) {
      throw new IllegalArgumentException(String.format("Context %s already contains subcontext token %s!", this, context.getName()));
    }
    
    subContexts.put(context.getName(), context);
  }
  
  public void run(PrintStream out, String rawCommandString) throws Throwable {
    String[] split = rawCommandString.split("\\s+", 2);
    CommandToken token = new CommandToken(split[0]);
    String rawArgString = split.length == 2 ? split[1] : "";
    
    if(HELP.equals(token) || token.isEmpty()) {
      help(out, rawArgString);
    } else {
      CommandContext context = subContexts.get(token);
      Command cmd = commands.get(token);
      if(context != null) {
        context.run(out, rawArgString);
      } else if(cmd != null) {
        cmd.run(System.out, rawArgString);
      } else {
        System.out.println("Command does not exist!");
      }
    }
  }
  
  public void help(PrintStream out, @Nullable String command) {
    if(command != null && !command.isEmpty()) {
      CommandToken token = new CommandToken(command);
            
      CommandContext context = subContexts.get(token);
      if(context != null) {
        context.help(out, null);
      } else {
        Optional.ofNullable(commands.get(token))
        .ifPresentOrElse(cmd -> cmd.showHelp(out), () -> out.println("Command does not exist!"));
      }      
      
    } else {
      Stream.concat(commands.keySet().stream(), subContexts.keySet().stream())
      .sorted()
      .forEach(out::println);
    }
  }
  
  public CommandContext getSubContext(CommandToken token) {
    return subContexts.get(token);
  }
  
  public Command getCommand(CommandToken token) {
    return commands.get(token);
  }
  
  public CommandToken getName() {
    return name;
  }
  
  public String toString() {
    return name.toString();
  }
  
  
}
