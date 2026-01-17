package com.repomind.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RepoMindConfigLoaderTest {
  @Test
  void loadsConfigFromYaml() throws IOException {
    Path dir = Files.createTempDirectory("repomind-config");
    Path configPath = dir.resolve("repomind.config.yaml");
    Files.writeString(configPath,
        "embeddings:\n"
            + "  provider: local-http\n"
            + "  model: sentence-transformers/code-bert-tiny-code-search\n"
            + "  modelPath: /models/code-bert\n"
            + "  local_http:\n"
            + "    url: http://localhost:8088\n"
            + "db:\n"
            + "  host: db.local\n"
            + "  port: 5544\n"
            + "  name: repomind_test\n"
            + "  user: repomind_user\n"
            + "  password: secret\n");

    RepoMindConfigLoader loader = new RepoMindConfigLoader();
    RepoMindConfig config = loader.load(configPath, Map.of());

    assertNotNull(config);
    assertEquals("local-http", config.getEmbeddings().getProvider());
    assertEquals("sentence-transformers/code-bert-tiny-code-search", config.getEmbeddings().getModel());
    assertEquals("/models/code-bert", config.getEmbeddings().getModelPath());
    assertEquals("http://localhost:8088", config.getEmbeddings().getLocalHttp().getUrl());
    assertEquals("db.local", config.getDb().getHost());
    assertEquals(5544, config.getDb().getPort());
    assertEquals("repomind_test", config.getDb().getName());
    assertEquals("repomind_user", config.getDb().getUser());
    assertEquals("secret", config.getDb().getPassword());
  }

  @Test
  void overridesWithEnv() throws IOException {
    Path dir = Files.createTempDirectory("repomind-config-env");
    Path configPath = dir.resolve("repomind.config.yaml");
    Files.writeString(configPath,
        "embeddings:\n"
            + "  provider: sentence-transformers\n"
            + "  model: sentence-transformers/code-bert-tiny-code-search\n"
            + "  local_http:\n"
            + "    url: http://localhost:8088\n"
            + "db:\n"
            + "  host: localhost\n"
            + "  port: 5432\n");

    Map<String, String> env = new HashMap<>();
    env.put("REPOMIND_EMBEDDINGS_MODEL", "sentence-transformers/custom-model");
    env.put("REPOMIND_EMBEDDINGS_LOCAL_HTTP_URL", "http://localhost:18088");
    env.put("REPOMIND_DB_HOST", "db.internal");
    env.put("REPOMIND_DB_PORT", "6543");

    RepoMindConfigLoader loader = new RepoMindConfigLoader();
    RepoMindConfig config = loader.load(configPath, env);

    assertEquals("sentence-transformers/custom-model", config.getEmbeddings().getModel());
    assertEquals("http://localhost:18088", config.getEmbeddings().getLocalHttp().getUrl());
    assertEquals("db.internal", config.getDb().getHost());
    assertEquals(6543, config.getDb().getPort());
  }

  @Test
  void missingConfigThrows() {
    RepoMindConfigLoader loader = new RepoMindConfigLoader();
    Path missing = Path.of("does-not-exist.yaml");

    RepoMindConfigException ex = assertThrows(RepoMindConfigException.class,
        () -> loader.load(missing, Map.of()));
    assertEquals("Config file not found: " + missing, ex.getMessage());
  }
}
