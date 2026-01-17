package com.repomind.core.embeddings;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class LocalHttpEmbeddingServerClientTest {
  @Test
  void healthCheckReturnsTrueForOk() throws Exception {
    URI baseUrl = URI.create("http://localhost:8088");
    LocalHttpEmbeddingServerClient client = new LocalHttpEmbeddingServerClient(
        new StubTransport(200), baseUrl, Duration.ofSeconds(2));

    assertTrue(client.isHealthy());
  }

  @Test
  void healthCheckReturnsFalseForErrors() throws Exception {
    URI baseUrl = URI.create("http://localhost:8088");
    LocalHttpEmbeddingServerClient client = new LocalHttpEmbeddingServerClient(
        new StubTransport(503), baseUrl, Duration.ofSeconds(2));

    assertFalse(client.isHealthy());
  }

  private static final class StubTransport implements HttpTransport {
    private final int status;

    private StubTransport(int status) {
      this.status = status;
    }

    @Override
    public HttpResult post(URI uri, byte[] body, Duration timeout) {
      return new HttpResult(status, new byte[0]);
    }

    @Override
    public HttpResult get(URI uri, Duration timeout) {
      return new HttpResult(status, new byte[0]);
    }
  }
}
