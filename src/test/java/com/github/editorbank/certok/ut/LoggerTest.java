package com.github.editorbank.certok.ut;

import org.junit.Test;

import org.apache.logging.log4j.Logger;
import static org.apache.logging.log4j.LogManager.getLogger;

public class LoggerTest {
    Logger log = getLogger();

    @Test
    public void testLogger() {

        assert (log != null);
        log.info("It is Info");
        log.warn("It is Warning");
        log.error("It is Error");
        log.info("Logger name: {}",log.getName());
    }
}