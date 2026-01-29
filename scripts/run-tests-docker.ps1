# Docker-based Maven test runner for BRx Tools
# Usage from project root:
#   .\scripts\run-tests-docker.ps1 -ModulePath "brx-tools-master/brx-matmap-parser" -MavenGoals "test"

param(
    [string]$ModulePath = "brx-tools-master/brx-matmap-parser",
    [string]$MavenGoals = "-DskipTests=false test"
)

$projectRoot = (Get-Location).Path
$moduleFullPath = Join-Path $projectRoot $ModulePath
Write-Host "Running Maven in Docker for module: $moduleFullPath"

# Use an image with Maven + JDK21. Tag may vary depending on available images; adjust if necessary.
$dockerImage = "maven:3.9.4-eclipse-temurin-21"

# Compose a docker run command that mounts the project and runs mvn inside the container.
$pwdPath = $projectRoot -replace '\\','/'

$cmd = "docker run --rm -v `"$pwdPath`":/workspace -w /workspace $dockerImage mvn -f `"$ModulePath/pom.xml`" $MavenGoals"
Write-Host $cmd

Invoke-Expression $cmd
