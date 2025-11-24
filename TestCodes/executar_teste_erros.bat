@echo off
echo ========================================
echo Executando Testes de ERROS
echo ========================================
echo.
echo Estes testes devem FALHAR na compilacao
echo.
echo ========================================
echo.

cd /d "%~dp0"

set SCRIPT_DIR=%~dp0
set JAR_PATH=%SCRIPT_DIR%..\compilador\target\compilador-1.0-SNAPSHOT.jar
set COMPILADOR_DIR=%SCRIPT_DIR%..\compilador
set TEST_DIR=Errors

if not exist "%JAR_PATH%" (
    echo ERRO: JAR nao encontrado em %JAR_PATH%
    echo Por favor, compile o projeto primeiro com: mvn package
    pause
    exit /b 1
)

echo Testando arquivos com ERROS em %TEST_DIR%...
echo.

for %%f in (%TEST_DIR%\*.mar) do (
    echo ========================================
    echo Testando: %%~nxf
    echo ========================================
    echo.
    
    copy "%%f" "%COMPILADOR_DIR%\teste.mar" >nul
    
    cd /d "%COMPILADOR_DIR%"
    
    REM Para testes de erro, fornece input padrão para evitar travamento
    REM caso algum programa gere código que precise de leia()
    REM Cria arquivo de input com valores padrão (0 para int, 0.0 para float, false para bool)
    echo 0 > input.txt
    echo 0.0 >> input.txt
    echo false >> input.txt
    java -jar "%JAR_PATH%" 2>&1 < input.txt
    del input.txt 2>nul
    
    cd /d "%SCRIPT_DIR%"
    
    echo.
)

echo.
echo ========================================
echo Todos os testes de erros foram executados!
echo ========================================
pause

