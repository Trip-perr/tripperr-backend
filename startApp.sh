#!/usr/bin/env bash
set -euo pipefail

# ── colours ──────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
info()  { echo -e "${GREEN}[INFO]${NC}  $*"; }
warn()  { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error() { echo -e "${RED}[ERROR]${NC} $*" >&2; }

# ── prerequisite checks ───────────────────────────────────────────────────────
check_java() {
  if ! command -v java &>/dev/null; then
    error "Java not found. Install Java 21 (e.g. via SDKMAN: sdk install java 21)."
    exit 1
  fi
  local version
  version=$(java -version 2>&1 | awk -F '"' '/version/{print $2}' | cut -d'.' -f1)
  if [[ "$version" -lt 21 ]]; then
    error "Java 21+ required (found $version)."
    exit 1
  fi
  info "Java $version detected."
}

check_docker() {
  if ! command -v docker &>/dev/null; then
    error "Docker not found. Install Docker Desktop: https://www.docker.com/products/docker-desktop"
    exit 1
  fi
  if ! docker info &>/dev/null; then
    error "Docker daemon is not running. Start Docker Desktop and retry."
    exit 1
  fi
  info "Docker is running."
}

# ── MongoDB ───────────────────────────────────────────────────────────────────
start_mongo() {
  info "Starting MongoDB..."
  docker compose up mongodb -d

  info "Waiting for MongoDB to be healthy..."
  local retries=15
  until docker compose exec mongodb mongosh --quiet --eval "db.adminCommand('ping').ok" &>/dev/null; do
    retries=$((retries - 1))
    if [[ $retries -le 0 ]]; then
      error "MongoDB failed to become healthy. Check: docker compose logs mongodb"
      exit 1
    fi
    sleep 2
  done
  info "MongoDB is ready."
}

# ── Spring Boot ───────────────────────────────────────────────────────────────
start_api() {
  info "Starting Tripperr API (profile: dev, port: 8080)..."
  info "Swagger UI → http://localhost:8080/swagger-ui.html"
  info "Press Ctrl+C to stop."
  echo ""
  SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
}

# ── cleanup on exit ───────────────────────────────────────────────────────────
cleanup() {
  echo ""
  warn "Shutting down MongoDB..."
  docker compose stop mongodb
  info "Done."
}
trap cleanup EXIT INT TERM

# ── main ──────────────────────────────────────────────────────────────────────
check_java
check_docker
start_mongo
start_api
