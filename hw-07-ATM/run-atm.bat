@echo off
chcp 65001 >nul
echo ================================
echo     ATM Эмулятор - Docker
echo ================================
echo.

REM 1. Проверяем Docker
docker version >nul 2>&1
if errorlevel 1 (
    echo ОШИБКА: Docker не запущен!
    echo Запустите Docker Desktop
    pause
    exit /b 1
)

REM 2. Ищем образ atm-emulator
echo Поиск образа atm-emulator...
docker images | findstr "atm-emulator" >nul
if errorlevel 1 (
    echo Образ не найден. Собираем...
    docker build -t atm-emulator .
)

REM 3. Запускаем
echo.
echo Запуск ATM эмулятора...
echo ================================
docker run -it --rm atm-emulator

pause