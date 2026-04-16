$ErrorActionPreference = 'Stop'

$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$pomPath = Join-Path $projectDir 'pom.xml'

if (-not (Test-Path $pomPath)) {
  Write-Error "未找到 pom.xml：$pomPath"
}

Set-Location $projectDir
Write-Host "在目录启动: $projectDir"
Write-Host "访问地址: http://127.0.0.1:8080/mbti-web/"

# 用完整插件坐标避免 jetty 前缀解析失败
mvn -f $pomPath org.eclipse.jetty:jetty-maven-plugin:9.4.54.v20240208:run
