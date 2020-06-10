@set jarfile="..\target\certok-1.0-SNAPSHOT.jar"
@set log4j2cfg=".\log4j2-sample.xml"
@if exist %log4j2cfg% set log4j2key=-Dlog4j2.configurationFile=%log4j2cfg%
@if exist %jarfile% java %log4j2key% -jar %jarfile% -c -s .\test.jks -p secret
@if exist %jarfile% java %log4j2key% -jar %jarfile% -c -s .\localhost.jks -p secret
@if exist %jarfile% java %log4j2key% -jar %jarfile% -c -s .\2020-01-24.pfx -p secret
