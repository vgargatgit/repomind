package com.repomind.core.config;

public class RepoMindConfigException extends RuntimeException {
  public RepoMindConfigException(String message) {
    super(message);
  }

  public RepoMindConfigException(String message, Throwable cause) {
    super(message, cause);
  }
}
