#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
info()  { echo -e "${GREEN}[INFO]${NC}  $*"; }
warn()  { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error() { echo -e "${RED}[ERROR]${NC} $*" >&2; }

# ── load .env ────────────────────────────────────────────────────────────────
load_env() {
  local env_file="$SCRIPT_DIR/.env"
  if [[ -f "$env_file" ]]; then
    info "Loading environment from .env"
    set -o allexport
    # shellcheck source=/dev/null
    source "$env_file"
    set +o allexport
  else
    warn ".env not found — using defaults from application.yml"
    warn "Tip: cp .env.example .env  and update values."
  fi
}

# ── prerequisite checks ───────────────────────────────────────────────────────
check_java() {
  local java_bin="java"

  if [[ -n "${JAVA_HOME:-}" ]]; then
    java_bin="$JAVA_HOME/bin/java"
    if [[ ! -x "$java_bin" ]]; then
      error "JAVA_HOME is set to '$JAVA_HOME' but '$java_bin' is not executable."
      exit 1
    fi
    info "Using JAVA_HOME: $JAVA_HOME"
  elif ! command -v java &>/dev/null; then
    error "Java not found. Install Java 21 and set JAVA_HOME (e.g. via SDKMAN: sdk install java 21.0.7-tem)."
    exit 1
  fi

  local version
  version=$("$java_bin" -version 2>&1 | awk -F '"' '/version/{print $2}' | cut -d'.' -f1)
  if [[ -z "$version" ]] || [[ "$version" -lt 21 ]]; then
    error "Java 21+ required (found major version '${version:-unknown}')."
    exit 1
  fi
  info "Java $version detected."
}

check_docker() {
  if ! command -v docker &>/dev/null; then
    error "Docker not found. Install Docker Desktop: https://www.docker.com/products/docker-desktop"
    exit 1
  fi
  if ! docker info &>/dev/null 2>&1; then
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
  until docker compose exec mongodb mongosh --quiet --eval "db.adminCommand('ping').ok" &>/dev/null 2>&1; do
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
  local port="${SERVER_PORT:-8080}"
  local profile="${SPRING_PROFILES_ACTIVE:-dev}"

  info "Starting Tripperr API (profile: $profile, port: $port)..."
  info "Swagger UI  → http://localhost:$port/swagger-ui/index.html"
  info "Health      → http://localhost:$port/actuator/health"
  info "Press Ctrl+C to stop."
  echo ""

  cd "$SCRIPT_DIR"
  SPRING_PROFILES_ACTIVE="$profile" ./mvnw spring-boot:run
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
load_env
check_java
check_docker
start_mongo
start_api
