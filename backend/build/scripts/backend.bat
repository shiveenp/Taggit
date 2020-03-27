@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  backend startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and BACKEND_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\backend-0.0.1.jar;%APP_HOME%\lib\http4k-server-netty-3.206.0.jar;%APP_HOME%\lib\http4k-security-oauth-3.206.0.jar;%APP_HOME%\lib\http4k-format-jackson-3.206.0.jar;%APP_HOME%\lib\http4k-client-apache-3.206.0.jar;%APP_HOME%\lib\http4k-cloudnative-3.206.0.jar;%APP_HOME%\lib\http4k-core-3.206.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.3.61.jar;%APP_HOME%\lib\kotlinx-coroutines-core-1.3.3.jar;%APP_HOME%\lib\ktorm-support-postgresql-2.6.jar;%APP_HOME%\lib\ktorm-jackson-2.6.jar;%APP_HOME%\lib\ktorm-core-2.6.jar;%APP_HOME%\lib\flyway-core-6.1.3.jar;%APP_HOME%\lib\postgresql-42.2.0.jar;%APP_HOME%\lib\log4j-slf4j-impl-2.13.1.jar;%APP_HOME%\lib\log4j-core-2.13.1.jar;%APP_HOME%\lib\log4j-api-2.13.1.jar;%APP_HOME%\lib\kotlin-logging-1.7.7.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.3.61.jar;%APP_HOME%\lib\jackson-module-kotlin-2.10.0.jar;%APP_HOME%\lib\kotlin-reflect-1.3.41.jar;%APP_HOME%\lib\kotlin-stdlib-1.3.61.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.3.61.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.9.7.jar;%APP_HOME%\lib\jackson-dataformat-xml-2.10.1.jar;%APP_HOME%\lib\jackson-module-jaxb-annotations-2.10.1.jar;%APP_HOME%\lib\jackson-databind-2.10.1.jar;%APP_HOME%\lib\slf4j-api-1.7.29.jar;%APP_HOME%\lib\javax.servlet-api-4.0.1.jar;%APP_HOME%\lib\netty-codec-http2-4.1.44.Final.jar;%APP_HOME%\lib\httpclient-4.5.10.jar;%APP_HOME%\lib\result4k-2.0.0.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\jackson-annotations-2.10.1.jar;%APP_HOME%\lib\jackson-core-2.10.1.jar;%APP_HOME%\lib\netty-codec-http-4.1.44.Final.jar;%APP_HOME%\lib\netty-handler-4.1.44.Final.jar;%APP_HOME%\lib\netty-codec-4.1.44.Final.jar;%APP_HOME%\lib\netty-transport-4.1.44.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.44.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.44.Final.jar;%APP_HOME%\lib\netty-common-4.1.44.Final.jar;%APP_HOME%\lib\woodstox-core-6.0.2.jar;%APP_HOME%\lib\stax2-api-4.2.jar;%APP_HOME%\lib\httpcore-4.4.12.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.11.jar;%APP_HOME%\lib\jakarta.xml.bind-api-2.3.2.jar;%APP_HOME%\lib\jakarta.activation-api-1.2.1.jar

@rem Execute backend
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %BACKEND_OPTS%  -classpath "%CLASSPATH%" io.taggit.AppKt %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable BACKEND_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%BACKEND_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
