@echo off
setlocal
cd /d "%~dp0"

set "JAVAC_CMD=C:\Program Files\Java\jdk-25\bin\javac.exe"
set "JAVA_CMD=C:\Program Files\Java\jdk-25\bin\java.exe"

if not exist "%JAVAC_CMD%" (
    if defined JAVA_HOME (
        set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
    ) else (
        set "JAVAC_CMD=javac"
        set "JAVA_CMD=java"
    )
)

if not exist bin mkdir bin

echo [INFO] Derleme kontrol ediliyor...
"%JAVAC_CMD%" -encoding UTF-8 -d bin src\main\java\com\javatv\enums\*.java src\main\java\com\javatv\exception\*.java src\main\java\com\javatv\model\*.java src\main\java\com\javatv\event\*.java src\main\java\com\javatv\repository\*.java src\main\java\com\javatv\service\*.java src\main\java\com\javatv\controller\*.java src\main\java\com\javatv\ui\gui\*.java src\main\java\com\javatv\ui\console\*.java src\main\java\com\javatv\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo [HATA] Derleme basarisiz oldu!
    pause
    exit /b %ERRORLEVEL%
)

echo [OK] Konsol modu baslatiliyor...
"%JAVA_CMD%" -cp bin com.javatv.Main --cli
pause
