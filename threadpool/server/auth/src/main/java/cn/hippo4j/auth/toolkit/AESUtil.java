/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * AES encryption and decryption algorithm tool class
 *
 * @author <a href="mailto:SerenitySir@outlook.com">Serenity</a>
 * @date 2023/5/21 14:37
 * @since JDK1.8+
 */
@UtilityClass
public class AESUtil {

    private static final String AES_GCM_CIPHER = "AES/GCM/PKCS5Padding";

    /**
     * encrypt
     *
     * @param data Content that needs to be encrypted
     * @param key  Encrypt the password
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
     * decrypt
     *
     * @param data The content to be decrypted
     * @param key  Decryption key
     * @return byte[]
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws GeneralSecurityException {
        GCMParameterSpec iv = new GCMParameterSpec(128, data, 0, 12);
        Cipher cipher = Cipher.getInstance(AES_GCM_CIPHER);
        SecretKey key2 = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key2, iv);
        return cipher.doFinal(data, 12, data.length - 12);
    }

    /**
     * encrypt
     *
     * @param data Content that needs to be encrypted
     * @param key  Encrypt the password
     * @return String
     */
    public static String encrypt(String data, String key) throws GeneralSecurityException {
        byte[] valueByte = encrypt(data.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(valueByte);
    }

    /**
     * decrypt
     *
     * @param data The content to be decrypted is a base64 string
     * @param key  Decryption key
     * @return String
     */
    public static String decrypt(String data, String key) throws GeneralSecurityException {
        byte[] originalData = Base64.getDecoder().decode(data.getBytes());
        byte[] valueByte = decrypt(originalData, key.getBytes(StandardCharsets.UTF_8));
        return new String(valueByte);
    }

    /**
     * Generate a random string key
     *
     * @return The string key
     */
    public static String generateRandomKey() {
        return IdWorker.get32UUID().substring(0, 16);
    }
}
