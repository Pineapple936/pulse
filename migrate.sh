#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
set -a
source .env
set +a
GOAL="${1:-update}"
shift || true
./mvnw "liquibase:$GOAL" \
  "-Dliquibase.url=jdbc:postgresql://localhost:5432/${POSTGRES_DB}" \
  "-Dliquibase.username=${POSTGRES_USER}" \
  "-Dliquibase.password=${POSTGRES_PASSWORD}" \
  "$@"
