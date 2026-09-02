@echo off
setlocal enabledelayedexpansion

set "ROOT=%~dp0"
set "JAR=%ROOT%target\Barbershop-1.0-SNAPSHOT.jar"

where java >nul 2>nul
if not errorlevel 1 (
    set "JAVA_CMD=java"
) else (
    if defined JAVA_HOME (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
    ) else (
        echo Java nao encontrado no PATH e JAVA_HOME nao foi configurado.
        exit /b 1
    )
)

if not exist "%JAR%" (
    echo Compilando projeto...
    call mvn -f "%ROOT%pom.xml" package -DskipTests
    if errorlevel 1 exit /b %errorlevel%
)

set "M2=%USERPROFILE%\.m2\repository"
set "FX_PATH=%M2%\org\openjfx\javafx-base\21.0.2\javafx-base-21.0.2-win.jar;%M2%\org\openjfx\javafx-controls\21.0.2\javafx-controls-21.0.2-win.jar;%M2%\org\openjfx\javafx-graphics\21.0.2\javafx-graphics-21.0.2-win.jar;%M2%\org\openjfx\javafx-fxml\21.0.2\javafx-fxml-21.0.2-win.jar"

echo Iniciando aplica??o JavaFX...
"%JAVA_CMD%" --module-path "%FX_PATH%" --add-modules javafx.controls,javafx.fxml -jar "%JAR%"
