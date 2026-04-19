param(
  [int]$Port = 8080,
  [switch]$KillExisting
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

$portToken = ':' + [string]$Port
$listenLine = netstat -ano | Select-String $portToken | Select-String 'LISTENING' | Select-Object -First 1

if ($listenLine) {
  $fields = ($listenLine.ToString() -split '\s+') | Where-Object { $_ -ne '' }
  $existingPid = [int]$fields[-1]

  if ($KillExisting) {
    Write-Host ("Port {0} is occupied by PID {1}. Stopping it..." -f $Port, $existingPid)
    Stop-Process -Id $existingPid -Force -ErrorAction Stop
  } else {
    Write-Host ("Port {0} is already occupied by PID {1}." -f $Port, $existingPid)
    Write-Host "Run with -KillExisting to stop it first."
    exit 1
  }
}

Set-Location $scriptDir
Write-Host ("Project dir: {0}" -f $scriptDir)
Write-Host ("Open URL: http://127.0.0.1:{0}/mbti-web/" -f $Port)

mvn -f $pomPath ("-Djetty.port={0}" -f $Port) org.eclipse.jetty:jetty-maven-plugin:9.4.54.v20240208:run
