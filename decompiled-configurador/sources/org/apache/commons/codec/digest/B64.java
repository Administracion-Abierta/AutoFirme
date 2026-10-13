package org.apache.commons.codec.digest;

import java.security.SecureRandom;
import java.util.Random;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/digest/B64.class */
final class B64 {
    static final String B64T_STRING = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static final char[] B64T_ARRAY = B64T_STRING.toCharArray();

    B64() {
    }

    static void b64from24bit(byte b2, byte b1, byte b0, int outLen, StringBuilder buffer) {
        int w = ((b2 << 16) & 16777215) | ((b1 << 8) & 65535) | (b0 & 255);
        int n = outLen;
        while (true) {
            int i = n;
            n--;
            if (i > 0) {
                buffer.append(B64T_ARRAY[w & 63]);
                w >>= 6;
            } else {
                return;
            }
        }
    }

    static String getRandomSalt(int num) {
        return getRandomSalt(num, new SecureRandom());
    }

    static String getRandomSalt(int num, Random random) {
        StringBuilder saltString = new StringBuilder(num);
        for (int i = 1; i <= num; i++) {
            saltString.append(B64T_STRING.charAt(random.nextInt(B64T_STRING.length())));
        }
        return saltString.toString();
    }
}
