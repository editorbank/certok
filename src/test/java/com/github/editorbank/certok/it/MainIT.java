package com.github.editorbank.certok.it;

import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.LogManager.getLogger;
import static com.github.editorbank.certok.it.SystemExec.exec;


public class MainIT {
    Logger log = getLogger(MainIT.class);

    @Test
    public void test2(){
        deleteFile("./target/test.log");
        assert testKeystore("rsa", "4096", "10", "pkcs12", "./target");
        assert testKeystore("dsa", "2048", "11", "pkcs12", "./target");
        assert testKeystore("rsa", "1024", "55", "pkcs12", "./target");
        assert readFile("./target/test.log").equals(
                "ERROR [main] daysleft: 9, Subject: CN=rsa4096v10.pkcs12, OU=Test, C=RU\n" +
                "WARN  [main] daysleft: 10, Subject: CN=dsa2048v11.pkcs12, OU=Test, C=RU\n" +
                "INFO  [main] daysleft: 54, Subject: CN=rsa1024v55.pkcs12, OU=Test, C=RU"
        );
    }
    String getRecourcePath() {
        try {
            return Paths.get( getClass().getProtectionDomain().getCodeSource().getLocation().toURI() ).toString();
        } catch (URISyntaxException ignored) {
            return "";
        }
    }
    void deleteFile(String fileName) {
        deleteFile(fileName, null);
    }
    void deleteFile(String fileName, String workPath) {
        File file = null;
        if (workPath!=null) {
            file = Paths.get(workPath, fileName).toFile();
        }else {
            file = new File(fileName);
        }

        if ( file.exists() ) {
            file.delete();
        }
    }
    String maskeName(String keyalg, String keysize, String validity, String storetype) {
        return keyalg + keysize + 'v' + validity+'.'+storetype;
    }
    Boolean testKeystore(String keyalg, String keysize, String validity, String storetype, String workPath) {
        String alias = maskeName(keyalg,keysize,validity,storetype);
        String keystore = alias;
        String dname = "CN=" + alias + ",OU=Test,C=RU";
        String storepass = "secret";
        String keypass = storepass;
        String log4j2Config = Paths.get(getRecourcePath(), "MainIT.xml").toString();
        String jarFile = "certok-1.0-SNAPSHOT.jar";
        deleteFile(keystore, workPath);
        return exec(cmd4KeytoolGenkeypair(alias,keyalg,keysize,dname,validity,keypass,keystore,storepass,storetype), workPath)
            && exec(cmd4CertOk(log4j2Config, jarFile, keystore, storepass, alias),workPath)
        ;
    }

    String cmd4CertOk(String log4j2Config, String jarFile, String keystore, String storepass, String alias) {
        return String.format(
                "java -Dlog4j2.configurationFile=%s -jar %s -c -s %s -p %s -a %s",
                log4j2Config, jarFile, keystore, storepass, alias);
    }
    String cmd4KeytoolGenkeypair(String alias, String keyalg, String keysize, String dname, String validity, String keypass, String keystore, String storepass, String storetype) {
        return String.format(
                "keytool -genkeypair -alias %s -keyalg %s -keysize %s -dname %s -validity %s -keypass %s -keystore %s -storepass %s -storetype %s",
                alias, keyalg, keysize, dname, validity, keypass, keystore, storepass, storetype);
    }

    String readFile(String filename) {
        String content = null;
        try {
            content = new BufferedReader(new InputStreamReader(new FileInputStream(filename)))
                    .lines().collect(Collectors.joining("\n"));
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            return null;
        }
        return content;
    }
}
