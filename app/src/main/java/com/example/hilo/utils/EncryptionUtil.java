package com.example.hilo.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.net.Uri;
import android.content.Context;

public class EncryptionUtil {

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String ENCRYPTION_MODE = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY = "B9E38AC4D87EBA46370473F7E8B130C1";
    private static SecretKey key = hexStringToSecretKey(SECRET_KEY);
//    private static SecretKey key = generateSecretKey();

    public static String encrypt(String message) {
        try {
            Cipher aesCipher = Cipher.getInstance(ENCRYPTION_MODE);

            byte[] iv = new byte[aesCipher.getBlockSize()];
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            aesCipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
            byte[] textEncrypted = aesCipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + textEncrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(textEncrypted, 0, combined, iv.length, textEncrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encryptedMessage) {
        try {
            Cipher aesCipher = Cipher.getInstance(ENCRYPTION_MODE);

            byte[] combined = Base64.getDecoder().decode(encryptedMessage);
            byte[] iv = new byte[aesCipher.getBlockSize()];
            byte[] textEncrypted = new byte[combined.length - iv.length];

            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, textEncrypted, 0, textEncrypted.length);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            aesCipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            byte[] textDecrypted = aesCipher.doFinal(textEncrypted);

            return new String(textDecrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encryptImage(byte[] imageBytes) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(imageBytes);
//            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
            return new String(Base64.getDecoder().decode(encryptedBytes));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decryptImage(String encryptedImage) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_MODE);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedImage);
            return cipher.doFinal(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Convert image to byte array
    public static byte[] convertImageToBytes(Uri imageUri, Context context) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }

    // String -> byte[] -> SecretKey
    private static SecretKey hexStringToSecretKey(String hex) {
        int len = hex.length();
        byte[] keyBytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            keyBytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return new SecretKeySpec(keyBytes, ENCRYPTION_ALGORITHM);
    }

    // 2 functions below are not needed because the value of SECRET_KEY is hard assigned
    private static SecretKey generateSecretKey() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return keyGenerator.generateKey();
    }

    // SecretKey -> byte[] -> String
    private static String secretKeyToHexString(SecretKey key) {
        byte[] keyBytes = key.getEncoded();
        StringBuilder result = new StringBuilder();
        for (byte b : keyBytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }
}
