# AGENTS.md

## Architecture and Scope

IODINE is a single-module Spring Boot API for a Google Drive–style application. This repository contains the API and its not-yet-implemented storage engine; it does not contain the frontend.

## Package Layout

Source code is organized by feature while retaining the controller → service → repository flow. Current packages include:

- `account`: account management and related DTOs
- `auth`: authentication and JWT security
- `config`: application configuration
- `error`: shared API error handling

## Endpoint Documentation

Whenever an API endpoint is added or edited, update `doc/endpoints.md` so the
documentation accurately reflects the endpoint's current behavior.

## Completion Reports

Always provide a completion report after each task, summarizing the changes made and the verification performed.

Every completion report must contain an **Edited Files** section listing each file modified, added, or deleted during the task.
