# AGENTS.md

## Architecture and Scope

IODINE is a single-module Spring Boot API for a Google Drive–style application. This repository contains the API and its not-yet-implemented storage engine; it does not contain the frontend.

## Package Layout

Source code is organized by feature while retaining the controller → service → repository flow. Current packages include:

- `account`: account management and related DTOs
- `auth`: authentication and JWT security
- `config`: application configuration
- `error`: shared API error handling

## Completion Reports

Always provide a completion report after each task, summarizing the changes made and the verification performed.
