@echo off
cd /d "%~dp0"
echo ========================================
echo  Starting Wooheso Desktop App (CMP)
echo ========================================
call gradlew.bat :desktopApp:run
pause
