package com.repomind.core.embeddings;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LocalHttpEmbeddingProvider implements EmbeddingProvider {
  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
  private static final int DEFAULT_BATCH_SIZE = 32;

  private final HttpTransport transport;
  private final URI embedUri;
  private final ObjectMapper mapper;
  private final int batchSize;
  private final Duration timeout;

  public LocalHttpEmbeddingProvider(URI baseUrl) {
    this(new JdkHttpTransport(java.net.http.HttpClient.newHttpClient()),
        baseUrl,
        new ObjectMapper(),
        DEFAULT_BATCH_SIZE,
        DEFAULT_TIMEOUT);
  }

  public LocalHttpEmbeddingProvider(
      HttpTransport transport,
      URI baseUrl,
      ObjectMapper mapper,
      int batchSize,
      Duration timeout) {
    this.transport = Objects.requireNonNull(transport, "transport");
    this.mapper = Objects.requireNonNull(mapper, "mapper");
    this.embedUri = buildEmbedUri(baseUrl);
    if (batchSize <= 0) {
      throw new IllegalArgumentException("batchSize must be positive.");
    }
    this.batchSize = batchSize;
    this.timeout = timeout == null ? DEFAULT_TIMEOUT : timeout;
  }

  @Override
  public List<double[]> embed(List<String> inputs) {
    if (inputs == null) {
      throw new IllegalArgumentException("inputs are required.");
    }
    if (inputs.isEmpty()) {
      return List.of();
    }

    List<double[]> results = new ArrayList<>(inputs.size());
    for (int start = 0; start < inputs.size(); start += batchSize) {
      int end = Math.min(inputs.size(), start + batchSize);
      List<String> batch = inputs.subList(start, end);
      List<double[]> batchResults = fetchBatch(batch);
      results.addAll(batchResults);
    }
    return List.copyOf(results);
  }

  private List<double[]> fetchBatch(List<String> batch) {
    try {
      byte[] payload = mapper.writeValueAsBytes(new EmbedRequest(batch));
      HttpResult response = transport.post(embedUri, payload, timeout);
      if (response.statusCode() != 200) {
        String body = new String(response.body(), StandardCharsets.UTF_8);
        throw new EmbeddingProviderException(
            "Embedding server error: HTTP " + response.statusCode() + " - " + body);
      }
      EmbedResponse embedResponse = mapper.readValue(response.body(), EmbedResponse.class);
      if (embedResponse.embeddings() == null) {
        throw new EmbeddingProviderException("Embedding server response missing embeddings.");
      }
      if (embedResponse.embeddings().size() != batch.size()) {
        throw new EmbeddingProviderException(
            "Embedding server response size mismatch: expected "
                + batch.size()
                + ", got "
                + embedResponse.embeddings().size());
      }
      List<double[]> results = new ArrayList<>(embedResponse.embeddings().size());
      for (List<Double> row : embedResponse.embeddings()) {
        results.add(toPrimitive(row));
      }
      return results;
    } catch (IOException e) {
      throw new EmbeddingProviderException("Failed to read embedding response.", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new EmbeddingProviderException("Embedding request interrupted.", e);
    }
  }

  private static double[] toPrimitive(List<Double> values) {
    if (values == null) {
      return new double[0];
    }
    double[] result = new double[values.size()];
    for (int i = 0; i < values.size(); i++) {
      Double value = values.get(i);
      result[i] = value == null ? 0.0 : value;
    }
    return result;
  }

  private static URI buildEmbedUri(URI baseUrl) {
    if (baseUrl == null) {
      throw new IllegalArgumentException("baseUrl is required.");
    }
    String base = baseUrl.toString();
    if (!base.endsWith("/")) {
      base = base + "/";
    }
    return URI.create(base).resolve("embed");
  }

  private record EmbedRequest(List<String> inputs) {}

  private record EmbedResponse(List<List<Double>> embeddings) {}
}
