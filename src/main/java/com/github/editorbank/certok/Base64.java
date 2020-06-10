package com.github.editorbank.certok;


import org.apache.logging.log4j.Logger;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Base64 {
    private static final Logger log = getLogger(Base64.class);

    private static final String CHAR_SET = "UTF-8";
    private static final java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
    private static final java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();

    public static String enBase64(byte[] bytes) {
        return encoder.encodeToString(bytes);
    }

    public static byte[] deBase64(String base64) {
        return decoder.decode(base64);
    }
}
