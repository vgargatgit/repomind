package com.repomind.core.embeddings;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

public class JdkHttpTransport implements HttpTransport {
  private final HttpClient client;

  public JdkHttpTransport(HttpClient client) {
    this.client = Objects.requireNonNull(client, "client");
  }

  @Override
  public HttpResult post(URI uri, byte[] body, Duration timeout) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder(uri)
        .timeout(timeout)
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofByteArray(body))
        .build();
    HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
    return new HttpResult(response.statusCode(), response.body());
  }

  @Override
  public HttpResult get(URI uri, Duration timeout) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder(uri)
        .timeout(timeout)
        .GET()
        .build();
    HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
    return new HttpResult(response.statusCode(), response.body());
  }
}
