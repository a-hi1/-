param(
  [switch]$SkipTests = $true
)

$ErrorActionPreference = 'Stop'

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$pomPath = Join-Path $scriptDir 'pom.xml'

if (-not (Test-Path $pomPath)) {
  throw "pom.xml not found: $pomPath"
}

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
  throw "mvn command not found in PATH"
}

Set-Location $scriptDir
Write-Host ("Build project in: {0}" -f $scriptDir)

if ($SkipTests) {
  mvn -f $pomPath clean package -DskipTests
} else {
  mvn -f $pomPath clean package
}

$warPath = Join-Path $scriptDir 'target\mbti-web.war'
if (-not (Test-Path $warPath)) {
  throw "WAR file not generated: $warPath"
}

Write-Host ("Build success: {0}" -f $warPath)
