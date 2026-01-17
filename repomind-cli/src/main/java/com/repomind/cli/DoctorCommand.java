package com.repomind.cli;

import picocli.CommandLine.Command;

@Command(name = "doctor", description = "Validate configuration and dependencies.")
public class DoctorCommand implements Runnable {
  @Override public void run() {
    // TODO: load config, check env vars, test DB connection, print actionable hints.
    System.out.println("OK (stub): implement config + DB checks.");
  }
}

