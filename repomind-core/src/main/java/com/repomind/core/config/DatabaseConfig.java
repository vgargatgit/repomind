package com.repomind.core.config;

public class DatabaseConfig {
  private String host = "localhost";
  private Integer port = 5432;
  private String name = "repomind";
  private String user = "repomind";
  private String password = "repomind";

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void apply(DatabaseConfig other) {
    if (other.host != null && !other.host.isBlank()) {
      this.host = other.host;
    }
    if (other.port != null) {
      this.port = other.port;
    }
    if (other.name != null && !other.name.isBlank()) {
      this.name = other.name;
    }
    if (other.user != null && !other.user.isBlank()) {
      this.user = other.user;
    }
    if (other.password != null) {
      this.password = other.password;
    }
  }

  public String toSafeString() {
    return "DatabaseConfig{host=" + host + ", port=" + port + ", name=" + name + ", user=" + user
        + ", password=***}";
  }
}
