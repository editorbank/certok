package com.github.editorbank.certok;


import org.apache.logging.log4j.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Salt {
    private static final Logger log = getLogger(Salt.class);

    private static final int keyBitSize = 128;
    private static final int KEY_BYTE_SIZE = keyBitSize/8;
    private static final String algorithm = "AES";

    private static byte[] getKey(){
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
        }
        SecureRandom secureRandom = new SecureRandom();
        assert keyGenerator != null;
        keyGenerator.init(keyBitSize, secureRandom);
        SecretKey key = keyGenerator.generateKey();
        log.debug("key.getAlgorithm: {}", key.getAlgorithm());
        log.debug("key.getFormat: {}", key.getFormat());
        log.debug("key.isDestroyed: {}", key.isDestroyed());
        log.debug("key.getEncoded().length: {}", key.getEncoded().length);
        log.debug("key.getClass: {}", key.getClass());
        byte[] keyEncoded = key.getEncoded();
        Arrays.toString(keyEncoded);
        return keyEncoded;
    }

    public static byte[] enSalt(byte[] data) {
        log.trace("enSalt start");
        byte[] saltData = new byte[data.length + KEY_BYTE_SIZE];
        System.arraycopy(data,0,saltData,0,data.length);
        System.arraycopy(getKey(),0,saltData,data.length,KEY_BYTE_SIZE);
        log.trace("enSalt done");
        return saltData;
    }

    public static byte[] deSalt(byte[] saltData) {
        log.trace("deSalt start");
        int dataSize = saltData.length - KEY_BYTE_SIZE;
        byte[] data = new byte[dataSize];
        System.arraycopy(saltData,0,data,0,dataSize);
        log.trace("deSalt done");
        return data;
    }
}
