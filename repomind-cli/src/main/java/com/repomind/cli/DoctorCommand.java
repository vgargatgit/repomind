package com.repomind.cli;

import com.repomind.core.config.RepoMindConfig;
import com.repomind.core.config.RepoMindConfigException;
import com.repomind.core.config.RepoMindConfigLoader;
import com.repomind.core.embeddings.LocalHttpEmbeddingServerClient;
import java.net.URI;
import java.nio.file.Path;
import picocli.CommandLine.Command;

@Command(name = "doctor", description = "Validate configuration and dependencies.")
public class DoctorCommand implements Runnable {
  @Override
  public void run() {
    RepoMindConfigLoader loader = new RepoMindConfigLoader();
    Path configPath = Path.of("repomind.config.yaml");

    try {
      RepoMindConfig config = loader.load(configPath);
      System.out.println("Config OK");
      System.out.println(config.toSafeString());
      validateEmbeddings(config);
    } catch (RepoMindConfigException e) {
      System.err.println("Config error: " + e.getMessage());
      throw e;
    }
  }

  private void validateEmbeddings(RepoMindConfig config) {
    String provider = config.getEmbeddings().getProvider();
    if (!"local-http".equals(provider)) {
      return;
    }
    String url = config.getEmbeddings().getLocalHttp() == null
        ? null
        : config.getEmbeddings().getLocalHttp().getUrl();
    if (url == null || url.isBlank()) {
      throw new RepoMindConfigException("Embeddings local_http.url is required for provider local-http.");
    }
    LocalHttpEmbeddingServerClient client = new LocalHttpEmbeddingServerClient(URI.create(url));
    if (!client.isHealthy()) {
      throw new RepoMindConfigException("Embedding server is not reachable at " + url);
    }
    System.out.println("Embedding server OK");
  }
}
