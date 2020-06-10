package com.github.editorbank.certok.it;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;

public class SystemExec {

    public static boolean exec(String cmd) {
        return exec(cmd, null);
    }
    public static boolean exec(String cmd, String workPath) {
        return exec(cmd.split("\\s+"), workPath);
    }

    public static boolean exec(String[] cmd) {
        return exec(cmd, null);
    }
    public static boolean exec(String[] cmd, String workPath) {
        return exec(Arrays.asList(cmd), workPath);
    }

    public static boolean exec(List<String> cmd) {
        return exec(cmd, null);
    }
    public static boolean exec(List<String> cmd, String workPath) {
        Logger log = getLogger(SystemExec.class);


        ProcessBuilder processBuilder = new ProcessBuilder();

        if (workPath != null ) {
            processBuilder.directory(new File(workPath));   log.debug("pwd: {}", workPath);
        }
        processBuilder.command(cmd);                        log.debug("cmd: {}", () -> String.join(" ",(String[])cmd.toArray()) );

        Boolean isOk = true;
        try {

            Process process = processBuilder.start();

            String line;
            BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = outReader.readLine()) != null) {
                //System.out.println(line);
                log.info("out: {}", line);
            }
            BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errReader.readLine()) != null) {
                isOk = false;
                //System.out.println(line);
                log.error("err: {}", line);
            }
            int exitCode = process.waitFor();
            log.log(exitCode == 0 ? Level.DEBUG : Level.ERROR, "ret: {}", exitCode);

        } catch (Exception e) {
            isOk = false;
            log.error("Exc: {}",e.getMessage());
            log.error("Trc: {}",e.getStackTrace());
        }
        log.debug(" ok: {}", isOk);
        return isOk;
    }
}
