package org.bouncycastle.crypto.util;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AlphabetMapper;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/util/BasicAlphabetMapper.class */
public class BasicAlphabetMapper implements AlphabetMapper {
    private Map<Character, Integer> indexMap;
    private Map<Integer, Character> charMap;

    public BasicAlphabetMapper(String str) {
        this(str.toCharArray());
    }

    public BasicAlphabetMapper(char[] cArr) {
        this.indexMap = new HashMap();
        this.charMap = new HashMap();
        for (int i = 0; i != cArr.length; i++) {
            if (this.indexMap.containsKey(Character.valueOf(cArr[i]))) {
                throw new IllegalArgumentException("duplicate key detected in alphabet: " + cArr[i]);
            }
            this.indexMap.put(Character.valueOf(cArr[i]), Integer.valueOf(i));
            this.charMap.put(Integer.valueOf(i), Character.valueOf(cArr[i]));
        }
    }

    @Override // org.bouncycastle.crypto.AlphabetMapper
    public int getRadix() {
        return this.indexMap.size();
    }

    @Override // org.bouncycastle.crypto.AlphabetMapper
    public byte[] convertToIndexes(char[] cArr) {
        byte[] bArr;
        if (this.indexMap.size() <= 256) {
            bArr = new byte[cArr.length];
            for (int i = 0; i != cArr.length; i++) {
                bArr[i] = this.indexMap.get(Character.valueOf(cArr[i])).byteValue();
            }
        } else {
            bArr = new byte[cArr.length * 2];
            for (int i2 = 0; i2 != cArr.length; i2++) {
                int iIntValue = this.indexMap.get(Character.valueOf(cArr[i2])).intValue();
                bArr[i2 * 2] = (byte) ((iIntValue >> 8) & 255);
                bArr[(i2 * 2) + 1] = (byte) (iIntValue & 255);
            }
        }
        return bArr;
    }

    @Override // org.bouncycastle.crypto.AlphabetMapper
    public char[] convertToChars(byte[] bArr) {
        char[] cArr;
        if (this.charMap.size() <= 256) {
            cArr = new char[bArr.length];
            for (int i = 0; i != bArr.length; i++) {
                cArr[i] = this.charMap.get(Integer.valueOf(bArr[i] & 255)).charValue();
            }
        } else {
            if ((bArr.length & 1) != 0) {
                throw new IllegalArgumentException("two byte radix and input string odd length");
            }
            cArr = new char[bArr.length / 2];
            for (int i2 = 0; i2 != bArr.length; i2 += 2) {
                cArr[i2 / 2] = this.charMap.get(Integer.valueOf(((bArr[i2] << 8) & 65280) | (bArr[i2 + 1] & 255))).charValue();
            }
        }
        return cArr;
    }
}
