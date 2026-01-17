package com.repomind.core.config;

public class EmbeddingsConfig {
  private String provider = "local-http";
  private String model = "sentence-transformers/code-bert-tiny-code-search";
  private String modelPath = "";
  private LocalHttpConfig localHttp = new LocalHttpConfig();

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getModelPath() {
    return modelPath;
  }

  public void setModelPath(String modelPath) {
    this.modelPath = modelPath;
  }

  public LocalHttpConfig getLocalHttp() {
    return localHttp;
  }

  public void setLocalHttp(LocalHttpConfig localHttp) {
    this.localHttp = localHttp;
  }

  public void setLocal_http(LocalHttpConfig localHttp) {
    setLocalHttp(localHttp);
  }

  public LocalHttpConfig getLocal_http() {
    return localHttp;
  }

  public void apply(EmbeddingsConfig other) {
    if (other.provider != null && !other.provider.isBlank()) {
      this.provider = other.provider;
    }
    if (other.model != null && !other.model.isBlank()) {
      this.model = other.model;
    }
    if (other.modelPath != null) {
      this.modelPath = other.modelPath;
    }
    if (other.localHttp != null) {
      if (this.localHttp == null) {
        this.localHttp = new LocalHttpConfig();
      }
      this.localHttp.apply(other.localHttp);
    }
  }

  public String toSafeString() {
    return "EmbeddingsConfig{provider="
        + provider
        + ", model="
        + model
        + ", modelPath="
        + modelPath
        + ", localHttp="
        + (localHttp == null ? "null" : localHttp.toSafeString())
        + "}";
  }
}
