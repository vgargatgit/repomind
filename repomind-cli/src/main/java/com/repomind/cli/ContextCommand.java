package com.repomind.cli;

import picocli.CommandLine.Command;

@Command(name = "context", description = "Generate a context pack (stub).")
public class ContextCommand implements Runnable {
  @Override
  public void run() {
    System.out.println("Context command not implemented yet.");
  }
}
