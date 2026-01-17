#!/usr/bin/env bash
set -euo pipefail
docker compose -f docker/docker-compose.yml up -d
echo "Postgres up. Next: run Flyway migrations via your Java code or psql."

