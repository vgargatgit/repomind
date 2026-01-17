package com.repomind.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "repomind",
    mixinStandardHelpOptions = true,
    version = "repomind 0.1.0",
    subcommands = {
        DoctorCommand.class,
        IndexCommand.class,
        SearchCommand.class,
        ContextCommand.class
    }
)
public class RepoMindCli implements Runnable {
  @Override public void run() {
    CommandLine.usage(this, System.out);
  }

  public static void main(String[] args) {
    int exit = new CommandLine(new RepoMindCli()).execute(args);
    System.exit(exit);
  }
}

