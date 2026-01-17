package com.repomind.cli;

import picocli.CommandLine.Command;

@Command(name = "index", description = "Index a repository (stub).")
public class IndexCommand implements Runnable {
  @Override
  public void run() {
    System.out.println("Index command not implemented yet.");
  }
}
