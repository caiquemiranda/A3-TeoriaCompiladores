@echo off
echo ========================================
echo Executando Testes de SUCESSO
echo ========================================
echo.

cd /d "%~dp0"

set SCRIPT_DIR=%~dp0
set JAR_PATH=%SCRIPT_DIR%..\compilador\target\compilador-1.0-SNAPSHOT.jar
set COMPILADOR_DIR=%SCRIPT_DIR%..\compilador
set TEST_DIR=Success

if not exist "%JAR_PATH%" (
    echo ERRO: JAR nao encontrado em %JAR_PATH%
    echo Por favor, compile o projeto primeiro com: mvn package
    pause
    exit /b 1
)

echo Testando arquivos em %TEST_DIR%...
echo.

for %%f in (%TEST_DIR%\*.mar) do (
    echo ========================================
    echo Testando: %%~nxf
    echo ========================================
    echo.
    
    copy "%%f" "%COMPILADOR_DIR%\teste.mar" >nul
    
    cd /d "%COMPILADOR_DIR%"
    
    REM Fornece input automático para programas que precisam de leia()
    REM Para calculadora.mar: fornece 10 e 5
    if "%%~nxf"=="calculadora.mar" (
        echo 10 > input.txt
        echo 5 >> input.txt
        java -jar "%JAR_PATH%" < input.txt
        del input.txt 2>nul
    ) else (
        java -jar "%JAR_PATH%"
    )
    
    cd /d "%SCRIPT_DIR%"
    
    echo.
)

echo.
echo ========================================
echo Todos os testes de sucesso foram executados!
echo ========================================
pause

