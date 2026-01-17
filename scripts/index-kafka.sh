#!/usr/bin/env bash
set -euo pipefail

KAFKA_DIR="${1:-$HOME/code/oss/kafka}"
REPO_NAME="${2:-kafka}"

echo "== RepoMind Kafka Indexing =="
echo "Kafka dir: ${KAFKA_DIR}"
echo "Repo name: ${REPO_NAME}"

if [ ! -d "${KAFKA_DIR}/.git" ]; then
  echo "Kafka repo not found at ${KAFKA_DIR}"
  echo "Cloning..."
  mkdir -p "$(dirname "${KAFKA_DIR}")"
  git clone https://github.com/apache/kafka.git "${KAFKA_DIR}"
fi

echo "Updating Kafka repo..."
git -C "${KAFKA_DIR}" fetch --all -q
git -C "${KAFKA_DIR}" checkout -q trunk || true
git -C "${KAFKA_DIR}" pull -q || true

echo "Building RepoMind..."
mvn -q -DskipTests package

echo "Starting dependencies..."
docker compose -f docker/docker-compose.yml up -d

echo "Indexing Kafka..."
java -jar repomind-cli/target/repomind.jar index "${KAFKA_DIR}" --repo "${REPO_NAME}"

echo ""
echo "Running validation searches..."
java -jar repomind-cli/target/repomind.jar search "leader election" --repo "${REPO_NAME}" --limit 5 || true
java -jar repomind-cli/target/repomind.jar search "FetchRequest" --repo "${REPO_NAME}" --limit 5 || true
java -jar repomind-cli/target/repomind.jar search "consumer group rebalance" --repo "${REPO_NAME}" --limit 5 || true
java -jar repomind-cli/target/repomind.jar search "ISR in-sync replicas" --repo "${REPO_NAME}" --limit 5 || true
java -jar repomind-cli/target/repomind.jar search "ProduceRequest validation" --repo "${REPO_NAME}" --limit 5 || true

echo ""
echo "Generating sample context pack..."
java -jar repomind-cli/target/repomind.jar context "Explain how FetchRequest is processed end-to-end" --repo "${REPO_NAME}" --out context.md

echo ""
echo "Done."
echo "Generated: context.md"
