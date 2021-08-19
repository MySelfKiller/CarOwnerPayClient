package com.kayu.utils;


import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DesCoderUtil {
    private static byte[] keys = { 0, 0, 0, 0, 0, 0, 0, 0 };

    private static String key = "leagsoft";

    public static String getKey() {
        return key;
    }

    public static void setKey(String key) {
        DesCoderUtil.key = key;
    }

    /**
     *
     * <p>
     * 对password进行MD5加密
     * @param source
     * @return
     * @return byte[]
     * author: Heweipo
     */
    public static byte[] getMD5(byte[] source) {
        byte tmp[] = null;
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            md.update(source);
            tmp = md.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }

    /**
     *
     * <p>
     * 采用JDK内置类进行真正的加密操作
     * @param byteS
     * @param password
     * @return
     * @return byte[]
     * author: Heweipo
     */
    private static byte[] encryptByte(byte[] byteS, byte password[]) {
        byte[] byteFina = null;
        try {// 初始化加密/解密工具
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(password);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(keys);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return byteFina;
    }

    /**
     *
     * <p>
     * 采用JDK对应的内置类进行解密操作
     * @param byteS
     * @param password
     * @return
     * @return byte[]
     * author: Heweipo
     */
    public static byte[] decryptByte(byte[] byteS, byte password[]) {
        byte[] byteFina = null;
        try {// 初始化加密/解密工具
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(password);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(keys);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return byteFina;
    }

    public static String decryptDES(String decryptString, String decryptKey) throws Exception {
        byte[] sourceBytes = Base64.decode(decryptString,0);
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        if (decryptKey.getBytes().length > 8) {
            for (int x = 0; x < keys.length; x++) {
                keys[x] = decryptKey.getBytes()[x];
            }
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keys, "DES"));
        } else {
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "DES"));
        }
        byte[] decoded = cipher.doFinal(sourceBytes);
        return new String(decoded, "UTF-8");
    }

    public static String encryptDES(String encryptString, String encryptKey) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "DES"));
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes("UTF-8"));
        return new String(Base64.encode(encryptedData,0),"UTF-8");
    }

}
