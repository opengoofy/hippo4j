package cn.hippo4j.auth.toolkit;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.experimental.UtilityClass;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

/**
 * Aes加解密算法工具类
 *
 * @author <a href="mailto:SerenitySir@outlook.com">Serenity</a>
 * @date 2023/5/21 14:37
 * @since JDK1.8+
 */
@UtilityClass
public class AESUtil {

    private static final String AES_GCM_CIPHER = "AES/GCM/PKCS5Padding";

    /**
     * 加密
     *
     * @param data 需要加密的内容
     * @param key  加密密码
     * @return byte[]
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws GeneralSecurityException {
            SecretKeySpec sKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(AES_GCM_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
            byte[] iv = cipher.getIV();
            byte[] encryptData = cipher.doFinal(data);
            byte[] message = new byte[12 + data.length + 16];
            System.arraycopy(iv, 0, message, 0, 12);
            System.arraycopy(encryptData, 0, message, 12, encryptData.length);
            return message;
    }

    /**
     * 解密
     *
     * @param data 待解密内容
     * @param key  解密密钥
     * @return byte[]
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws GeneralSecurityException{
            GCMParameterSpec iv = new GCMParameterSpec(128, data, 0, 12);
            Cipher cipher = Cipher.getInstance(AES_GCM_CIPHER);
            SecretKey key2 = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, key2, iv);
            return cipher.doFinal(data, 12, data.length - 12);
    }

    /**
     * 加密
     *
     * @param data 需要加密的内容
     * @param key  加密密码
     * @return String
     */
    public static String encrypt(String data, String key) throws GeneralSecurityException {
        byte[] valueByte = encrypt(data.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(valueByte);
    }

    /**
     * 解密
     *
     * @param data 待解密内容 base64 字符串
     * @param key  解密密钥
     * @return String
     */
    public static String decrypt(String data, String key) throws GeneralSecurityException {
        byte[] originalData = Base64.getDecoder().decode(data.getBytes());
        byte[] valueByte = decrypt(originalData, key.getBytes(StandardCharsets.UTF_8));
        return new String(valueByte);
    }

    /**
     * 生成一个随机字符串密钥
     *
     * @return 字符串密钥
     */
    public static String generateRandomKey() {
        return IdWorker.get32UUID().substring(0, 16);
    }
}
