@title CertOk
    @set JAVA_HOME=%~dp0jre1.8

    @set CERTOK_HOME=%~dp0
    @set jarfile=certok-1.0-SNAPSHOT.jar
    @set cfgfile=log4j2.xml

    @call :PrepareEnvironment

    @call :CertOk %*

    ::@pause
@goto :eof

:CertOK
    @java -classpath "%CLASSPATH%"  -Dlog4j2.configurationFile="%cfgfile%" -jar "%jarfile%" %*
@goto :eof

:PrepareEnvironment
    @if "%CERTOK_HOME:~-1%"=="\" @set CERTOK_HOME=%CERTOK_HOME:~0,-1%
    @if not exist "%jarfile%" @set jarfile=%CERTOK_HOME%\%jarfile%
    @if not exist "%cfgfile%" @set cfgfile=%CERTOK_HOME%\%cfgfile%
	@call :test_file "%JAVA_HOME%\bin\java.exe"
	@call :test_file "%jarfile%"
	@call :test_file "%cfgfile%"

    @set PATH=%JAVA_HOME%\bin
    @set CLASSPATH=%JAVA_HOME%\lib

    @call :add_CLASSPATH "%JAVA_HOME%\lib\charsets.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\deploy.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\ext\access-bridge-64.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\ext\cldrdata.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\ext\dnsns.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\ext\jaccess.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\ext\jfxrt.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\ext\localedata.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\ext\nashorn.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\ext\sunec.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\ext\sunjce_provider.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\ext\sunmscapi.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\ext\sunpkcs11.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\ext\zipfs.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\javaws.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\jce.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\jfr.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\jfxswt.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\jsse.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\management-agent.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\plugin.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\resources.jar"
    @call :add_CLASSPATH "%JAVA_HOME%\lib\rt.jar"
    
    ::@set CLASSPATH
    ::@set JAVA_HOME
    ::@set CERTOK_HOME
    ::@set jarfile
    ::@set cfgfile
@goto :eof

:add_CLASSPATH
    @call :test_file "%~1"
    @set CLASSPATH=%CLASSPATH%;%~1
@goto :eof

:test_file
    @if not exist "%~1" ( @echo File "%~1" not found!!! >&2 & @exit /b 1)
@goto :eof


