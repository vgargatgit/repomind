#!/usr/bin/env bash
set -euo pipefail
mvn -q -DskipTests package
java -jar repomind-cli/target/repomind.jar index ./tests-fixtures/sample-repo --repo sample
java -jar repomind-cli/target/repomind.jar search "refund eligibility" --repo sample
