package com.github.editorbank.certok;

import org.apache.logging.log4j.Logger;

import static org.apache.logging.log4j.LogManager.getLogger;
import static com.github.editorbank.certok.Base64.deBase64;
import static com.github.editorbank.certok.Base64.enBase64;
import static com.github.editorbank.certok.Crypt.enCrypt;
import static com.github.editorbank.certok.Crypt.deCrypt;
import static com.github.editorbank.certok.Salt.deSalt;
import static com.github.editorbank.certok.Salt.enSalt;
import static com.github.editorbank.certok.Utf8.deUtf8;
import static com.github.editorbank.certok.Utf8.enUtf8;

public class StringCoder {
    private static final Logger log = getLogger(StringCoder.class);

    public String encode(String openText) {
        log.trace("encode start");
        byte[] openData = deUtf8(openText);
        byte[] closeData = enCrypt(enSalt(openData));
        String closeText = enBase64(closeData);
        log.trace("encode done");
        return closeText;
    }

    public String decode(String closeText) {
        log.trace("decode start");
        byte[] closeData = deBase64(closeText);
        byte[] openData = deSalt(deCrypt(closeData));
        String openText = enUtf8(openData);
        log.trace("decode done");
        return openText;
    }
}
