@echo off
REM Script para gerar um relatório de todos os testes
REM Gera um arquivo de texto com os resultados

echo ========================================
echo Gerando Relatorio de Testes
echo ========================================
echo.

cd /d "%~dp0"

set SCRIPT_DIR=%~dp0
set JAR_PATH=%SCRIPT_DIR%..\compilador\target\compilador-1.0-SNAPSHOT.jar
set COMPILADOR_DIR=%SCRIPT_DIR%..\compilador
set RELATORIO=relatorio_testes.txt

if not exist "%JAR_PATH%" (
    echo ERRO: JAR nao encontrado em %JAR_PATH%
    pause
    exit /b 1
)

echo Relatorio de Testes - Compilador Marte > "%RELATORIO%"
echo Gerado em: %date% %time% >> "%RELATORIO%"
echo. >> "%RELATORIO%"
echo ======================================== >> "%RELATORIO%"
echo. >> "%RELATORIO%"

REM Testes de Sucesso
echo TESTES DE SUCESSO >> "%RELATORIO%"
echo ======================================== >> "%RELATORIO%"
echo. >> "%RELATORIO%"

for %%f in (Success\*.mar) do (
    echo Testando: %%~nxf >> "%RELATORIO%"
    echo ---------------------------------------- >> "%RELATORIO%"
    
    copy "%%f" "%COMPILADOR_DIR%\teste.mar" >nul
    
    cd /d "%COMPILADOR_DIR%"
    
    REM Fornece input automático para calculadora.mar
    if "%%~nxf"=="calculadora.mar" (
        echo 10 > input.txt
        echo 5 >> input.txt
        java -jar "%JAR_PATH%" < input.txt >> "%SCRIPT_DIR%%RELATORIO%" 2>&1
        del input.txt 2>nul
    ) else (
        java -jar "%JAR_PATH%" >> "%SCRIPT_DIR%%RELATORIO%" 2>&1
    )
    
    if %errorlevel% equ 0 (
        echo [OK] Compilacao bem-sucedida >> "%SCRIPT_DIR%%RELATORIO%"
    ) else (
        echo [FALHA] Erro na compilacao >> "%SCRIPT_DIR%%RELATORIO%"
    )
    
    echo. >> "%SCRIPT_DIR%%RELATORIO%"
    
    cd /d "%SCRIPT_DIR%"
)

echo. >> "%RELATORIO%"
echo ======================================== >> "%RELATORIO%"
echo TESTES DE ERROS >> "%RELATORIO%"
echo ======================================== >> "%RELATORIO%"
echo. >> "%RELATORIO%"

REM Testes de Erros
for %%f in (Errors\*.mar) do (
    echo Testando: %%~nxf >> "%RELATORIO%"
    echo ---------------------------------------- >> "%RELATORIO%"
    
    copy "%%f" "%COMPILADOR_DIR%\teste.mar" >nul
    
    cd /d "%COMPILADOR_DIR%"
    
    REM Fornece input padrão para evitar travamento
    echo 0 > input.txt
    echo 0.0 >> input.txt
    echo false >> input.txt
    java -jar "%JAR_PATH%" < input.txt >> "%SCRIPT_DIR%%RELATORIO%" 2>&1
    del input.txt 2>nul
    
    if %errorlevel% equ 0 (
        echo [ATENCAO] Este teste deveria falhar mas compilou! >> "%SCRIPT_DIR%%RELATORIO%"
    ) else (
        echo [OK] Erro detectado corretamente >> "%SCRIPT_DIR%%RELATORIO%"
    )
    
    echo. >> "%SCRIPT_DIR%%RELATORIO%"
    
    cd /d "%SCRIPT_DIR%"
)

echo.
echo Relatorio gerado em: %RELATORIO%
echo.
pause

