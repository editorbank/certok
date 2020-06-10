jarfile=../target/certok-1.0-SNAPSHOT.jar
log4j2cfg=./log4j2-sample.xml
[ -f $log4j2cfg ] && log4j2key=-Dlog4j2.configurationFile=$log4j2cfg
[ -f $jarfile   ] && java $log4j2key -jar $jarfile -c -s ./test.jks -p secret
[ -f $jarfile   ] && java $log4j2key -jar $jarfile -c -s ./localhost.jks -p secret
[ -f $jarfile   ] && java $log4j2key -jar $jarfile -c -s ./2020-01-24.pfx -p secret
