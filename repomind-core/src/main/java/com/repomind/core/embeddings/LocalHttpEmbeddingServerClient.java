package com.repomind.core.embeddings;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

public class LocalHttpEmbeddingServerClient {
  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(5);

  private final HttpTransport transport;
  private final URI healthUri;
  private final Duration timeout;

  public LocalHttpEmbeddingServerClient(URI baseUrl) {
    this(new JdkHttpTransport(java.net.http.HttpClient.newHttpClient()), baseUrl, DEFAULT_TIMEOUT);
  }

  public LocalHttpEmbeddingServerClient(HttpTransport transport, URI baseUrl, Duration timeout) {
    this.transport = Objects.requireNonNull(transport, "transport");
    this.healthUri = buildHealthUri(baseUrl);
    this.timeout = timeout == null ? DEFAULT_TIMEOUT : timeout;
  }

  public boolean isHealthy() {
    try {
      HttpResult response = transport.get(healthUri, timeout);
      return response.statusCode() == 200;
    } catch (Exception e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      return false;
    }
  }

  private static URI buildHealthUri(URI baseUrl) {
    if (baseUrl == null) {
      throw new IllegalArgumentException("baseUrl is required.");
    }
    String base = baseUrl.toString();
    if (!base.endsWith("/")) {
      base = base + "/";
    }
    return URI.create(base).resolve("health");
  }
}
