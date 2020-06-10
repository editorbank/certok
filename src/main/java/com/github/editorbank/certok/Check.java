package com.github.editorbank.certok;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;

import static org.apache.logging.log4j.LogManager.getLogger;

class Check {
    private static final Integer DAYSLEFT_WARNING = 45;
    private static final Integer DAYSLEFT_ERROR = 10;
    private Logger log = getLogger(Check.class);

    public static long daysBetween(final Date a, final Date b) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(a);
        long aMs = calendar.getTimeInMillis();
        calendar.setTime(b);
        long bMs = calendar.getTimeInMillis();
        long diffDays = (bMs - aMs) /1000 /60 /60 /24; // convert Mllisesconds in to days.
        return diffDays;
    }

    public void certPrinter(final Certificate certificate) {

        if (certificate == null) return;
        log.debug("Certificate {");
        log.debug("type: " + certificate.getType());
        PublicKey publicKey = certificate.getPublicKey();

        log.debug("Algorithm: " + publicKey.getAlgorithm());
        log.debug("Format: " + publicKey.getFormat());
        log.debug("javaClass: " + publicKey.getClass());
        byte[] bytes = publicKey.getEncoded();
        log.debug("Encoded.length: " + bytes.length);

        Date dateNow = new Date();
        if (certificate.getType().equals("X.509")) {
            X509Certificate x509Certificate = (X509Certificate) (certificate);
            Principal subjectDN = x509Certificate.getSubjectDN();
            log.debug("Subject: " + subjectDN.getName());
            log.debug("Issuer: " + x509Certificate.getIssuerDN().getName());
            log.debug("NotBefore: " + x509Certificate.getNotBefore());
            log.debug("dateNow: " + dateNow);
            log.debug("NotAfter: " + x509Certificate.getNotAfter());
            Long daysleft = daysBetween(dateNow, x509Certificate.getNotAfter());
            Level level = Level.INFO;
            if (daysleft < DAYSLEFT_ERROR) {
                level = Level.ERROR;
            }else if (daysleft < DAYSLEFT_WARNING) {
                level = Level.WARN;
            }
            log.log(level,"daysleft: " + daysleft +", "+"Subject: " + subjectDN.getName());

        } else {

        }

        log.debug("}");
    }

    public void checkKeyStore(String keystoreFile, String password, String alias) throws IOException {
        try {
            InputStream inputStream = new FileInputStream(keystoreFile);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(inputStream, password.toCharArray());

            log.debug("keyStore.size: " + keyStore.size());
            log.debug("keyStore.type: " + keyStore.getType());

            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String currentAlias = aliases.nextElement();
                log.debug("Certificate alias: " + currentAlias);
                if(alias != null && !alias.equalsIgnoreCase(currentAlias)) {
                    continue;
                }
                Certificate certificate = keyStore.getCertificate(currentAlias);
                certPrinter(certificate);

                Certificate[] chains = keyStore.getCertificateChain(currentAlias);
                if (chains != null) {
                    log.debug(currentAlias + ".chains.length: " + chains.length);
                    for (int i = 0; i < chains.length; i++) {
                        Certificate chain = chains[i];
                        if (!certificate.equals(chain)) {
                            log.debug(currentAlias + ".chains[" + i + "]:... ");
                            certPrinter(chain);
                        }
                    }
                }
            }
        } catch (KeyStoreException e) {
            log.error("KeyStoreException: {}",e.getMessage());
        } catch (CertificateException e) {
            log.error("CertificateException: {}",e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException: {}",e.getMessage());
        }

    }
}