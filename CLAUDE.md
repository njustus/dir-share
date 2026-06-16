# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this project is

A full-stack Scala web application for local file sharing. The backend serves a file browsing/upload/download API; the frontend is a single-page app that consumes it. Both share type-safe API definitions via a cross-compiled `shared` module.

## Build & run

**Prerequisites**: SBT, Node.js (npm), [Task](https://taskfile.dev)

```bash
# Full production build
task build                   # setup-client → build-client → package backend

# Development workflow (two terminals)
sbt ~backend/reStart         # auto-restart backend on changes (sbt-revolver)
task serve                   # Vite dev server with /api proxy to backend

# Scala.js compilation only
sbt client/fastLinkJS        # fast (dev), output in client/dist/
sbt client/fullLinkJS        # optimized (prod)

# Code quality
sbt cleanup                  # scalafmt + scalafix
sbt fix                      # alias for cleanup

# Package backend distribution
sbt backend/Universal/packageBin
```

## Architecture

**Monorepo structure**: `shared/` → `backend/` and `client/` (cross-compiled Scala 3.6.1)

### Shared module — single source of truth for the API

`shared/FilesEndpoints.scala` defines all Tapir endpoint schemas (list, upload, download). These compile to JVM for the backend and to JavaScript for the frontend client. Adding or changing an endpoint touches this file first.

### Backend

- Entry: `BackendMain.scala` (CLI via Decline, configures host/port/root path/password)
- DI: `BackendModule.scala` (MacWire wires `FilesService` → `FilesServerEndpoints`)
- Business logic: `FilesService.scala` — reads directory trees, streams file downloads, handles uploads
- HTTP: `Endpoints.scala` — Tapir server interpreters over http4s/Ember; also serves compiled frontend from `client/dist`
- Swagger UI is auto-generated from the Tapir endpoints

### Frontend

- Entry: `ClientMain.scala` (mounts Laminar app)
- Routing: `FrontRoutes.scala` (FrontRoute, path `/listing/<path>`)
- Main page: `ListingComponent.scala` — reactive state (Laminar Var/Signal), fetches listing via `ListEndpointsClient`
- HTTP client: `ListEndpointsClient.scala` + `SttpClientAdapter.scala` (STTP with Fetch backend)
- Components: `components/` — `BreadcrumbList`, `DirectoryItem`, etc.
- Styles: TailwindCSS 4 + DaisyUI 5 via Vite

### Frontend build integration

Vite (`client/vite.config.js`) proxies `/api/*` to the backend during development and bundles output to `client/dist/` for production. The backend's static file route serves `client/dist/`.

## Key tech stack

| Layer | Library |
|-------|---------|
| Backend HTTP | http4s 0.23 (Ember) |
| Endpoint DSL | Tapir 1.11 |
| JSON | Circe 0.14 |
| Effects | Cats Effect (IO) |
| Frontend UI | Laminar 17 |
| Frontend routing | FrontRoute 0.19 |
| Frontend HTTP | STTP 4 (Fetch backend) |
| Frontend bundler | Vite 6 / TailwindCSS 4 / DaisyUI 5 |
| Code style | Scalafmt (120 cols, IntelliJ preset) + Scalafix (Typelevel rules) |
