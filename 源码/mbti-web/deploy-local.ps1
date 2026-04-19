param(
  [Parameter(Mandatory = $true)]
  [string]$TomcatWebapps,
  [switch]$BuildFirst = $true,
  [switch]$SkipTests = $true
)

$ErrorActionPreference = 'Stop'

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$warPath = Join-Path $scriptDir 'target\mbti-web.war'
$targetWar = Join-Path $TomcatWebapps 'mbti-web.war'
$explodedDir = Join-Path $TomcatWebapps 'mbti-web'

if ($BuildFirst) {
  & (Join-Path $scriptDir 'build-war.ps1') -SkipTests:$SkipTests
}

if (-not (Test-Path $warPath)) {
  throw "WAR file not found: $warPath"
}

if (-not (Test-Path $TomcatWebapps)) {
  throw "Tomcat webapps path not found: $TomcatWebapps"
}

if (Test-Path $targetWar) {
  Remove-Item $targetWar -Force
}
if (Test-Path $explodedDir) {
  Remove-Item $explodedDir -Recurse -Force
}

Copy-Item $warPath $targetWar -Force
Write-Host ("Deploy success: {0}" -f $targetWar)
Write-Host "If Tomcat is running, wait for auto-extract; otherwise start Tomcat first."
Write-Host "Open URL: http://127.0.0.1:8080/mbti-web/"
