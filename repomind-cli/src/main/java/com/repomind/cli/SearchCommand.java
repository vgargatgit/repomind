package com.repomind.cli;

import picocli.CommandLine.Command;

@Command(name = "search", description = "Search indexed code (stub).")
public class SearchCommand implements Runnable {
  @Override
  public void run() {
    System.out.println("Search command not implemented yet.");
  }
}
