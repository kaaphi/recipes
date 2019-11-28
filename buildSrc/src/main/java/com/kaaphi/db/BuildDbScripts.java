package com.kaaphi.db;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class BuildDbScripts extends DefaultTask {
  
  
  @TaskAction
  public void buildScripts() throws IOException {
    Path scriptsDir = getProject().file("scripts").toPath();
    Path patchesDir = scriptsDir.resolve("patches");
  
    GraphBuilder<String, Depends, ? extends DirectedAcyclicGraph<String, Depends>> builder = DirectedAcyclicGraph.createBuilder(Depends::new);
    
    Files.list(patchesDir)
    .forEach(path -> parseDependencies(path, builder));
    getLogger().info(getPath());
    
    DirectedAcyclicGraph<String, Depends> graph = builder.build();
    
            
    Iterable<String> iterable = () -> new TopologicalOrderIterator<>(graph);
    
    System.out.println(graph.toString());
    
    List<Path> scripts = Stream.of(
        Stream.of(getProject().file("versioning").toPath().resolve("install.versioning.sql")),
        StreamSupport.stream(iterable.spliterator(), false).map(patch -> patchesDir.resolve(patch + ".sql")),
        Files.list(scriptsDir).filter(path -> !Files.isDirectory(path))
        )
        .flatMap(Function.identity())
        .collect(Collectors.toList());
    
    Path target = getProject().getBuildDir().toPath().resolve("scripts");
    Files.createDirectories(target);
    Files.list(target).map(Path::toFile).forEach(File::delete);
  
    int scriptIdx = 0;
    for(Path script : scripts) {
      String targetName = String.format("%03d-%s", scriptIdx++, script.getFileName());
      
      Files.copy(script, target.resolve(targetName));
    }
  }

  private static final Pattern PATCH_PATTERN = Pattern.compile("_v.register_patch\\('([^']+)'(,\\s*ARRAY\\s*\\[(.*)\\])?");
  private static final Pattern PATCH_NAME_PATTERN = Pattern.compile("'([^']+)'");
  private void parseDependencies(Path path, GraphBuilder<String, Depends, ? extends DirectedAcyclicGraph<String, Depends>> builder) {
    try {
      Files.lines(path)
      .filter(line -> line.contains("_v.register_patch"))
      .findFirst()
      .ifPresent(line -> {
        Matcher m = PATCH_PATTERN.matcher(line);
        if(m.find()) {
          String patch = m.group(1);
          builder.addVertex(patch);
          if(m.group(3) != null) {
            Matcher depends = PATCH_NAME_PATTERN.matcher(m.group(3));
            while(depends.find()) {
              builder.addEdge(depends.group(1), patch);
            }
          }
        }
      });
    } catch (IOException e) {
      throw new Error(e);
    }

  }
  
  private static class Depends {
    public String toString() {
      return "Depends";
    }
  }
}
