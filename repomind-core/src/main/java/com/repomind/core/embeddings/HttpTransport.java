package com.repomind.core.embeddings;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

public interface HttpTransport {
  HttpResult post(URI uri, byte[] body, Duration timeout) throws IOException, InterruptedException;

  HttpResult get(URI uri, Duration timeout) throws IOException, InterruptedException;
}
