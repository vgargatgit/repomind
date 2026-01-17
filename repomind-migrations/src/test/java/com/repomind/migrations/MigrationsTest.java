package com.repomind.migrations;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

class MigrationsTest {
  @Test
  void migratesAndCreatesCodeChunksTable() throws SQLException {
    Assumptions.assumeTrue(isDockerAvailable(), "Docker not available for Testcontainers");
    try (PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16")) {
      postgres.start();

      Flyway.configure()
          .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
          .locations("classpath:db/migration")
          .load()
          .migrate();

      try (Connection connection = java.sql.DriverManager.getConnection(
          postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
        assertTrue(tableExists(connection, "code_chunks"));
      }
    }
  }

  private boolean isDockerAvailable() {
    try {
      return DockerClientFactory.instance().isDockerAvailable();
    } catch (Exception e) {
      return false;
    }
  }

  private boolean tableExists(Connection connection, String tableName) throws SQLException {
    String sql =
        "select exists (" +
            "select 1 from information_schema.tables " +
            "where table_schema = 'public' and table_name = ?" +
        ")";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, tableName);
      try (ResultSet resultSet = statement.executeQuery()) {
        return resultSet.next() && resultSet.getBoolean(1);
      }
    }
  }
}
