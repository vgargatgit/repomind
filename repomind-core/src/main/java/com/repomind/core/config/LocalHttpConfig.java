package com.repomind.core.config;

public class LocalHttpConfig {
  private String url = "http://localhost:8088";

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void apply(LocalHttpConfig other) {
    if (other == null) {
      return;
    }
    if (other.url != null && !other.url.isBlank()) {
      this.url = other.url;
    }
  }

  public String toSafeString() {
    return "LocalHttpConfig{url=" + url + "}";
  }
}
