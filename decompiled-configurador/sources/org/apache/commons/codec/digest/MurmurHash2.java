package org.apache.commons.codec.digest;

import org.apache.commons.codec.binary.StringUtils;
import org.spongycastle.asn1.cmc.BodyPartID;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/digest/MurmurHash2.class */
public final class MurmurHash2 {
    private static final int M32 = 1540483477;
    private static final int R32 = 24;
    private static final long M64 = -4132994306676758123L;
    private static final int R64 = 47;

    private static int getLittleEndianInt(byte[] data, int index) {
        return (data[index] & 255) | ((data[index + 1] & 255) << 8) | ((data[index + 2] & 255) << 16) | ((data[index + 3] & 255) << 24);
    }

    private static long getLittleEndianLong(byte[] data, int index) {
        return (((long) data[index]) & 255) | ((((long) data[index + 1]) & 255) << 8) | ((((long) data[index + 2]) & 255) << 16) | ((((long) data[index + 3]) & 255) << 24) | ((((long) data[index + 4]) & 255) << 32) | ((((long) data[index + 5]) & 255) << 40) | ((((long) data[index + 6]) & 255) << 48) | ((((long) data[index + 7]) & 255) << 56);
    }

    public static int hash32(byte[] data, int length) {
        return hash32(data, length, -1756908916);
    }

    public static int hash32(byte[] data, int length, int seed) {
        int h = seed ^ length;
        int nblocks = length >> 2;
        for (int i = 0; i < nblocks; i++) {
            int k = getLittleEndianInt(data, i << 2) * M32;
            h = (h * M32) ^ ((k ^ (k >>> 24)) * M32);
        }
        int index = nblocks << 2;
        switch (length - index) {
            case 3:
                h ^= (data[index + 2] & 255) << 16;
            case 2:
                h ^= (data[index + 1] & 255) << 8;
            case 1:
                h = (h ^ (data[index] & 255)) * M32;
                break;
        }
        int h2 = (h ^ (h >>> 13)) * M32;
        return h2 ^ (h2 >>> 15);
    }

    public static int hash32(String text) {
        byte[] bytes = StringUtils.getBytesUtf8(text);
        return hash32(bytes, bytes.length);
    }

    public static int hash32(String text, int from, int length) {
        return hash32(text.substring(from, from + length));
    }

    public static long hash64(byte[] data, int length) {
        return hash64(data, length, -512093083);
    }

    public static long hash64(byte[] data, int length, int seed) {
        long h = (((long) seed) & BodyPartID.bodyIdMax) ^ (((long) length) * M64);
        int nblocks = length >> 3;
        for (int i = 0; i < nblocks; i++) {
            long k = getLittleEndianLong(data, i << 3) * M64;
            h = (h ^ ((k ^ (k >>> 47)) * M64)) * M64;
        }
        int index = nblocks << 3;
        switch (length - index) {
            case 7:
                h ^= (((long) data[index + 6]) & 255) << 48;
            case 6:
                h ^= (((long) data[index + 5]) & 255) << 40;
            case 5:
                h ^= (((long) data[index + 4]) & 255) << 32;
            case 4:
                h ^= (((long) data[index + 3]) & 255) << 24;
            case 3:
                h ^= (((long) data[index + 2]) & 255) << 16;
            case 2:
                h ^= (((long) data[index + 1]) & 255) << 8;
            case 1:
                h = (h ^ (((long) data[index]) & 255)) * M64;
                break;
        }
        long h2 = (h ^ (h >>> 47)) * M64;
        return h2 ^ (h2 >>> 47);
    }

    public static long hash64(String text) {
        byte[] bytes = StringUtils.getBytesUtf8(text);
        return hash64(bytes, bytes.length);
    }

    public static long hash64(String text, int from, int length) {
        return hash64(text.substring(from, from + length));
    }

    private MurmurHash2() {
    }
}
