package com.github.editorbank.certok;


import org.apache.logging.log4j.Logger;
import java.io.UnsupportedEncodingException;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Utf8 {
    private static final Logger log = getLogger(Utf8.class);

    private static final String CHAR_SET = "UTF-8";

    public static String enUtf8(byte[] bytes) {
        try {
            return new String(bytes, CHAR_SET);
        } catch (UnsupportedEncodingException e) {
            log.fatal("UnsupportedEncodingException: {}", e.getMessage());
        }
        return "";
    }

    public static byte[] deUtf8(String utf8) {
        try {
            return utf8.getBytes(CHAR_SET);
        } catch (UnsupportedEncodingException e) {
            log.fatal("UnsupportedEncodingException: {}", e.getMessage());
        }
        return new byte[0];
    }
}
