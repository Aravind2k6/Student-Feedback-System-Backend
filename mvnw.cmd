@ECHO OFF
SETLOCAL

SET "BASE_DIR=%~dp0"
IF "%BASE_DIR:~-1%"=="\" SET "BASE_DIR=%BASE_DIR:~0,-1%"
SET "WRAPPER_JAR=%BASE_DIR%\.mvn\wrapper\maven-wrapper.jar"
IF NOT DEFINED MAVEN_USER_HOME SET "MAVEN_USER_HOME=%BASE_DIR%\.m2"
IF NOT DEFINED MAVEN_REPO_LOCAL SET "MAVEN_REPO_LOCAL=%MAVEN_USER_HOME%\repository"

IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Maven wrapper JAR not found at "%WRAPPER_JAR%"
  EXIT /B 1
)

IF DEFINED JAVA_HOME (
  SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) ELSE (
  SET "JAVA_EXE=java"
)

"%JAVA_EXE%" "-Dmaven.user.home=%MAVEN_USER_HOME%" "-Dmaven.repo.local=%MAVEN_REPO_LOCAL%" "-Dmaven.multiModuleProjectDirectory=%BASE_DIR%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
SET "MVNW_EXIT=%ERRORLEVEL%"

ENDLOCAL & EXIT /B %MVNW_EXIT%
