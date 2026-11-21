# PowerShell Smart TV Simulator GUI Runner
$ErrorActionPreference = "Stop"

Set-Location $PSScriptRoot

$javac = "C:\Program Files\Java\jdk-25\bin\javac.exe"
$javaw = "C:\Program Files\Java\jdk-25\bin\javaw.exe"

if (-not (Test-Path $javac)) {
    if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\javac.exe")) {
        $javac = "$env:JAVA_HOME\bin\javac.exe"
        $javaw = "$env:JAVA_HOME\bin\javaw.exe"
    } else {
        $javac = "javac"
        $javaw = "javaw"
    }
}

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host "[INFO] Smart TV Pro Simulator Derleniyor..." -ForegroundColor Cyan

$sources = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
& $javac -encoding UTF-8 -d bin $sources

if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Derleme basarili! Gorsel Arayuz (GUI) aciliyor..." -ForegroundColor Green
    Start-Process $javaw -ArgumentList "-cp bin com.javatv.Main"
} else {
    Write-Host "[HATA] Derleme hatasi!" -ForegroundColor Red
}
