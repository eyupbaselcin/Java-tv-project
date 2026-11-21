# PowerShell Smart TV Simulator Unit Tests Runner
$ErrorActionPreference = "Stop"

Set-Location $PSScriptRoot

$javac = "C:\Program Files\Java\jdk-25\bin\javac.exe"
$java = "C:\Program Files\Java\jdk-25\bin\java.exe"

if (-not (Test-Path $javac)) {
    if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\javac.exe")) {
        $javac = "$env:JAVA_HOME\bin\javac.exe"
        $java = "$env:JAVA_HOME\bin\java.exe"
    } else {
        $javac = "javac"
        $java = "java"
    }
}

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$sources = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
$tests = Get-ChildItem -Path "src\test\java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName

& $javac -encoding UTF-8 -d bin $sources $tests

if ($LASTEXITCODE -eq 0) {
    & $java -cp bin com.javatv.TvServiceTest
} else {
    Write-Host "[HATA] Test derleme hatasi!" -ForegroundColor Red
}
