package cn.hippo4j.common.toolkit;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.model.PoolParameter;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * MD5 util.
 *
 * @author chen.ma
 * @date 2021/6/22 17:55
 */
public class Md5Util {

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final ThreadLocal<MessageDigest> MESSAGE_DIGEST_LOCAL = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    });

    public static String md5Hex(byte[] bytes) throws NoSuchAlgorithmException {
        try {
            MessageDigest messageDigest = MESSAGE_DIGEST_LOCAL.get();
            if (messageDigest != null) {
                return encodeHexString(messageDigest.digest(bytes));
            }
            throw new NoSuchAlgorithmException("MessageDigest get MD5 instance error");
        } finally {
            MESSAGE_DIGEST_LOCAL.remove();
        }
    }

    public static String md5Hex(String value, String encode) {
        try {
            return md5Hex(value.getBytes(encode));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeHexString(byte[] bytes) {
        int l = bytes.length;
        char[] out = new char[l << 1];

        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & bytes[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & bytes[i]];
        }

        return new String(out);
    }

    public static String getTpContentMd5(PoolParameter config) {
        return Md5Util.md5Hex(ContentUtil.getPoolContent(config), "UTF-8");
    }

    public static String compareMd5ResultString(List<String> changedGroupKeys) throws IOException {
        if (null == changedGroupKeys) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (String groupKey : changedGroupKeys) {
            String[] dataIdGroupId = GroupKey.parseKey(groupKey);
            sb.append(dataIdGroupId[0]);
            sb.append(Constants.WORD_SEPARATOR);
            sb.append(dataIdGroupId[1]);
            // if have tenant, then set it
            if (dataIdGroupId.length == 3) {
                if (StringUtil.isNotBlank(dataIdGroupId[2])) {
                    sb.append(Constants.WORD_SEPARATOR);
                    sb.append(dataIdGroupId[2]);
                }
            } else if (dataIdGroupId.length == 4) {
                if (StringUtil.isNotBlank(dataIdGroupId[2])) {
                    sb.append(Constants.WORD_SEPARATOR);
                    sb.append(dataIdGroupId[2]);
                }
            }
            sb.append(Constants.LINE_SEPARATOR);
        }

        // To encode WORD_SEPARATOR and LINE_SEPARATOR invisible characters, encoded value is %02 and %01
        return URLEncoder.encode(sb.toString(), "UTF-8");
    }

}
