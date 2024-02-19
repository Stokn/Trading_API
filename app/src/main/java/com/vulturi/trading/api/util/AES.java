package com.vulturi.trading.api.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class AES {

    private static byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public static String encrypt(String strToEncrypt, String key) {
        if (strToEncrypt == null) {
            return null;
        }
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = null;
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | InvalidAlgorithmParameterException e) {
            log.error("Error during AES encryption", e);
            throw new RuntimeException("Error during AES encryption", e);
        }
    }

    public static String decrypt(String strToDecrypt, String key) {
        if (strToDecrypt == null) {
            return null;
        }
        Cipher cipher = null;
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            log.error("Error during AES decryption", e);
            throw new RuntimeException("Error during AES decryption", e);
        }
    }
}
