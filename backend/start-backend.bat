@echo off
chcp 65001 >nul
setlocal

rem ============================================================
rem  Mini Mall 后端启动脚本（本地开发）
rem  双击运行即可：自动设置 JDK 17、Maven、数据库与 JWT 密钥后启动
rem ============================================================

rem --- JDK 17（SpringBoot 3.x 必需）---
set "JAVA_HOME=D:\Users\Fire\.jdks\ms-17.0.20.1"
set "PATH=%JAVA_HOME%\bin;%PATH%"

rem --- Maven 安装目录与本地仓库 ---
set "MAVEN_HOME=D:\soft\install\maven\apache-maven-3.9.16"
set "MAVEN_REPO=D:\soft\install\maven\localRepository"

rem --- 数据库账号密码（改成你本机 MySQL 实际口令）---
set "DB_USERNAME=root"
set "DB_PASSWORD=123456"

rem --- JWT 密钥：首次运行自动生成到 jwt-secret.txt（已加入 .gitignore），之后复用 ---
set "SECRET_FILE=%~dp0jwt-secret.txt"
if not exist "%SECRET_FILE%" (
    powershell -NoProfile -Command "[guid]::NewGuid().ToString('N') + [guid]::NewGuid().ToString('N') | Out-File -FilePath '%SECRET_FILE%' -Encoding ascii -NoNewline"
)
set /p JWT_SECRET=<"%SECRET_FILE%"

rem --- 可选：默认管理员 admin 的初始密码（留空则在启动日志里打印随机密码）---
rem set "ADMIN_INIT_PASSWORD=change-me"

rem --- 启动后端 ---
cd /d "%~dp0"
call "%MAVEN_HOME%\bin\mvn.cmd" -Dmaven.repo.local="%MAVEN_REPO%" spring-boot:run

if errorlevel 1 (
    echo.
    echo [启动失败] 请查看上方日志。
    pause
)

endlocal
