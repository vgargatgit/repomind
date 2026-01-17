package com.repomind.core.embeddings;

import java.util.List;

public interface EmbeddingProvider {
  List<double[]> embed(List<String> inputs);
}
