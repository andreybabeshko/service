set SCRIPT_DIR=%~dp0
set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_79
set DEBUG=-Djava.compiler=NONE -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=7777
"%JAVA_HOME%\bin\java" %DEBUG% -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256m -Xmx512M -Xss2M -jar "%SCRIPT_DIR%\sbt-launch-0.13.9.jar" %*
