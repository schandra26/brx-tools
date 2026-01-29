# Validates the compose file syntax for the backend compose
# Usage: Run from project root in PowerShell

$composePath = Join-Path -Path (Get-Location) -ChildPath 'brx-tools-master\compose.yaml'
Write-Host "Validating compose file: $composePath"

docker compose -f $composePath config
if ($LASTEXITCODE -ne 0) {
    Write-Error "docker compose config failed. Please ensure Docker is installed and running."
    exit $LASTEXITCODE
}
Write-Host "Compose file is valid."