package com.repomind.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

public class RepoMindConfigLoader {
  public RepoMindConfig load(Path configPath) {
    return load(configPath, System.getenv());
  }

  public RepoMindConfig load(Path configPath, Map<String, String> env) {
    if (configPath == null) {
      throw new RepoMindConfigException("Config path is required.");
    }
    if (!Files.exists(configPath)) {
      throw new RepoMindConfigException("Config file not found: " + configPath);
    }
    if (!Files.isRegularFile(configPath)) {
      throw new RepoMindConfigException("Config path is not a file: " + configPath);
    }

    RepoMindConfig config = new RepoMindConfig();
    RepoMindConfig fromFile = loadFromFile(configPath);
    config.apply(fromFile);
    applyEnvOverrides(config, env);

    return config;
  }

  private RepoMindConfig loadFromFile(Path configPath) {
    LoaderOptions options = new LoaderOptions();
    Constructor constructor = new Constructor(RepoMindConfig.class, options);
    Yaml yaml = new Yaml(constructor);

    try (InputStream input = Files.newInputStream(configPath)) {
      RepoMindConfig loaded = yaml.loadAs(input, RepoMindConfig.class);
      return loaded == null ? new RepoMindConfig() : loaded;
    } catch (IOException e) {
      throw new RepoMindConfigException("Failed to read config file: " + configPath, e);
    }
  }

  private void applyEnvOverrides(RepoMindConfig config, Map<String, String> env) {
    if (env == null || env.isEmpty()) {
      return;
    }

    String provider = env.get("REPOMIND_EMBEDDINGS_PROVIDER");
    if (provider != null && !provider.isBlank()) {
      config.getEmbeddings().setProvider(provider);
    }

    String model = env.get("REPOMIND_EMBEDDINGS_MODEL");
    if (model != null && !model.isBlank()) {
      config.getEmbeddings().setModel(model);
    }

    if (env.containsKey("REPOMIND_EMBEDDINGS_MODEL_PATH")) {
      config.getEmbeddings().setModelPath(env.get("REPOMIND_EMBEDDINGS_MODEL_PATH"));
    }

    String localHttpUrl = env.get("REPOMIND_EMBEDDINGS_LOCAL_HTTP_URL");
    if (localHttpUrl != null && !localHttpUrl.isBlank()) {
      if (config.getEmbeddings().getLocalHttp() == null) {
        config.getEmbeddings().setLocalHttp(new LocalHttpConfig());
      }
      config.getEmbeddings().getLocalHttp().setUrl(localHttpUrl);
    }

    String host = env.get("REPOMIND_DB_HOST");
    if (host != null && !host.isBlank()) {
      config.getDb().setHost(host);
    }

    String portValue = env.get("REPOMIND_DB_PORT");
    if (portValue != null && !portValue.isBlank()) {
      try {
        config.getDb().setPort(Integer.parseInt(portValue));
      } catch (NumberFormatException e) {
        throw new RepoMindConfigException("Invalid REPOMIND_DB_PORT: " + portValue, e);
      }
    }

    String name = env.get("REPOMIND_DB_NAME");
    if (name != null && !name.isBlank()) {
      config.getDb().setName(name);
    }

    String user = env.get("REPOMIND_DB_USER");
    if (user != null && !user.isBlank()) {
      config.getDb().setUser(user);
    }

    if (env.containsKey("REPOMIND_DB_PASSWORD")) {
      config.getDb().setPassword(env.get("REPOMIND_DB_PASSWORD"));
    }
  }
}
