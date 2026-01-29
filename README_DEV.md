# BRx Tools — Local Development

This document describes how to run the BRx Tools services locally for development.

Services (compose file location):
- `brx-matmap-parser` (backend) and `postgres` are defined in `brx-tools-master/compose.yaml`.

Quick start (PowerShell):

```powershell
# From project root
cd .\brx-tools-master
# Validate compose file
docker compose -f compose.yaml config
# Start services (build backend image if needed)
docker compose -f compose.yaml up --build
```

Notes:
- PostgreSQL default connection: `jdbc:postgresql://localhost:5432/postgres` user `postgres` / password `mysecretpassword`.
- Backend container maps container port `8080` to host `8081` (see `compose.yaml`), adjust ports when connecting from host.

If you want to validate the compose file programmatically, run `scripts\validate-compose.ps1`.
