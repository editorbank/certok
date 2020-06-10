package com.github.editorbank.certok;


import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Crypt {
    private static final Logger log = getLogger(Crypt.class);

    private static final String CHAR_SET = "UTF-8";
    private static final String algorithm = "AES";
    private static final byte[] keyBytes = new byte[]{
            82,10,48,64,-116,-71,-106,93,104,-21,5,-70,-82,-47,56,26
    };


    private static SecretKey getKey(){
        SecretKey key = new SecretKeySpec(keyBytes, algorithm);
        log.debug("key.getAlgorithm: {}", key.getAlgorithm());
        log.debug("key.getFormat: {}", key.getFormat());
        log.debug("key.isDestroyed: {}", key.isDestroyed());
        log.debug("key.getEncoded().length: {}", key.getEncoded().length);
        log.debug("key.getClass: {}", key.getClass());
        return key;
    }
    private static Cipher getCipher(){
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(algorithm);
            log.debug("cipher.getAlgorithm: {}", cipher.getAlgorithm());
            log.debug("cipher.getBlockSize: {}", cipher.getBlockSize());
            log.debug("cipher.getParameters(): {}", cipher.getParameters());
        } catch (NoSuchAlgorithmException e) {
            log.fatal("NoSuchAlgorithmException: {}", e.getMessage());
        } catch (NoSuchPaddingException e) {
            log.fatal("NoSuchPaddingException: {}", e.getMessage());
        }
        return cipher;
    }

    public static byte[] enCrypt(byte[] openData) {
        log.trace("encode start");
        byte[] cipherData = openData;
        try {
            log.debug("openData.length: {}", openData.length);
            Cipher cipher = getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            cipherData = cipher.doFinal(openData);
            log.debug("cipherData.length: {}", cipherData.length);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        log.trace("encode done");
        return cipherData;
    }

    public static byte[] deCrypt(byte[] cipherData) {
        log.trace("decode start");
        byte[] openData = cipherData;
        try {
            log.debug("cipherData.length: {}", cipherData.length);
            Cipher cipher = getCipher();
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            openData = cipher.doFinal(cipherData);
            log.debug("openData.length: {}", openData.length);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        log.trace("decode done");
        return openData;
    }
}
