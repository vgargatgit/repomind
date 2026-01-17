package com.repomind.core.config;

public class RepoMindConfig {
  private EmbeddingsConfig embeddings = new EmbeddingsConfig();
  private DatabaseConfig db = new DatabaseConfig();

  public EmbeddingsConfig getEmbeddings() {
    return embeddings;
  }

  public void setEmbeddings(EmbeddingsConfig embeddings) {
    this.embeddings = embeddings;
  }

  public DatabaseConfig getDb() {
    return db;
  }

  public void setDb(DatabaseConfig db) {
    this.db = db;
  }

  public void apply(RepoMindConfig other) {
    if (other == null) {
      return;
    }
    if (other.embeddings != null) {
      this.embeddings.apply(other.embeddings);
    }
    if (other.db != null) {
      this.db.apply(other.db);
    }
  }

  public String toSafeString() {
    return "RepoMindConfig{" + embeddings.toSafeString() + ", " + db.toSafeString() + "}";
  }

  @Override
  public String toString() {
    return toSafeString();
  }
}
