package com.repomind.core.embeddings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class LocalHttpEmbeddingProviderTest {
  @Test
  void embedBatchesRequestsAndParsesResponse() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RecordingTransport transport = new RecordingTransport(mapper);
    URI baseUrl = URI.create("http://localhost:8088");
    LocalHttpEmbeddingProvider provider = new LocalHttpEmbeddingProvider(
        transport, baseUrl, mapper, 2, Duration.ofSeconds(5));

    List<double[]> results = provider.embed(List.of("one", "two", "three"));

    assertEquals(2, transport.calls.get());
    assertEquals(3, results.size());
    assertEquals(3.0, results.get(0)[0]);
    assertEquals(3.0, results.get(1)[0]);
    assertEquals(5.0, results.get(2)[0]);
  }

  @Test
  void embedReturnsEmptyWhenNoInputs() {
    ObjectMapper mapper = new ObjectMapper();
    RecordingTransport transport = new RecordingTransport(mapper);
    URI baseUrl = URI.create("http://localhost:8088");
    LocalHttpEmbeddingProvider provider = new LocalHttpEmbeddingProvider(
        transport, baseUrl, mapper, 8, Duration.ofSeconds(5));

    List<double[]> results = provider.embed(List.of());

    assertTrue(results.isEmpty());
  }

  private static final class RecordingTransport implements HttpTransport {
    private final ObjectMapper mapper;
    private final AtomicInteger calls = new AtomicInteger();

    private RecordingTransport(ObjectMapper mapper) {
      this.mapper = mapper;
    }

    @Override
    public HttpResult post(URI uri, byte[] body, Duration timeout) throws IOException {
      calls.incrementAndGet();
      Map<String, Object> payload = mapper.readValue(body, new TypeReference<Map<String, Object>>() {});
      @SuppressWarnings("unchecked")
      List<String> inputs = (List<String>) payload.get("inputs");
      List<List<Double>> embeddings = new ArrayList<>();
      for (String input : inputs) {
        embeddings.add(List.of((double) input.length()));
      }
      byte[] response = mapper.writeValueAsBytes(Map.of("embeddings", embeddings));
      return new HttpResult(200, response);
    }

    @Override
    public HttpResult get(URI uri, Duration timeout) {
      return new HttpResult(200, new byte[0]);
    }
  }
}
