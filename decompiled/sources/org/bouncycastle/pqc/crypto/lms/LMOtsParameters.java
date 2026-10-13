package org.bouncycastle.pqc.crypto.lms;

import com.sun.jna.platform.win32.LMErr;
import com.sun.jna.platform.win32.WinError;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LMOtsParameters.class */
public class LMOtsParameters {
    public static final int reserved = 0;
    public static final LMOtsParameters sha256_n32_w1 = new LMOtsParameters(1, 32, 1, 265, 7, WinError.ERROR_DS_GLOBAL_CANT_HAVE_LOCAL_MEMBER, NISTObjectIdentifiers.id_sha256);
    public static final LMOtsParameters sha256_n32_w2 = new LMOtsParameters(2, 32, 2, 133, 6, 4292, NISTObjectIdentifiers.id_sha256);
    public static final LMOtsParameters sha256_n32_w4 = new LMOtsParameters(3, 32, 4, 67, 4, LMErr.NERR_ServiceTableLocked, NISTObjectIdentifiers.id_sha256);
    public static final LMOtsParameters sha256_n32_w8 = new LMOtsParameters(4, 32, 8, 34, 0, WinError.ERROR_FLOPPY_UNKNOWN_ERROR, NISTObjectIdentifiers.id_sha256);
    public static final LMOtsParameters sha256_n24_w1 = new LMOtsParameters(5, 24, 1, 200, 8, 5436, NISTObjectIdentifiers.id_sha256);
    public static final LMOtsParameters sha256_n24_w2 = new LMOtsParameters(6, 24, 2, 101, 6, 2940, NISTObjectIdentifiers.id_sha256);
    public static final LMOtsParameters sha256_n24_w4 = new LMOtsParameters(7, 24, 4, 51, 4, WinError.ERROR_EVENTLOG_FILE_CORRUPT, NISTObjectIdentifiers.id_sha256);
    public static final LMOtsParameters sha256_n24_w8 = new LMOtsParameters(8, 24, 8, 26, 0, WinError.ERROR_KEY_HAS_CHILDREN, NISTObjectIdentifiers.id_sha256);
    public static final LMOtsParameters shake256_n32_w1 = new LMOtsParameters(9, 32, 1, 265, 7, WinError.ERROR_DS_GLOBAL_CANT_HAVE_LOCAL_MEMBER, NISTObjectIdentifiers.id_shake256_len);
    public static final LMOtsParameters shake256_n32_w2 = new LMOtsParameters(10, 32, 2, 133, 6, 4292, NISTObjectIdentifiers.id_shake256_len);
    public static final LMOtsParameters shake256_n32_w4 = new LMOtsParameters(11, 32, 4, 67, 4, LMErr.NERR_ServiceTableLocked, NISTObjectIdentifiers.id_shake256_len);
    public static final LMOtsParameters shake256_n32_w8 = new LMOtsParameters(12, 32, 8, 34, 0, WinError.ERROR_FLOPPY_UNKNOWN_ERROR, NISTObjectIdentifiers.id_shake256_len);
    public static final LMOtsParameters shake256_n24_w1 = new LMOtsParameters(13, 24, 1, 200, 8, 5436, NISTObjectIdentifiers.id_shake256_len);
    public static final LMOtsParameters shake256_n24_w2 = new LMOtsParameters(14, 24, 2, 101, 6, 2940, NISTObjectIdentifiers.id_shake256_len);
    public static final LMOtsParameters shake256_n24_w4 = new LMOtsParameters(15, 24, 4, 51, 4, WinError.ERROR_EVENTLOG_FILE_CORRUPT, NISTObjectIdentifiers.id_shake256_len);
    public static final LMOtsParameters shake256_n24_w8 = new LMOtsParameters(16, 24, 8, 26, 0, WinError.ERROR_KEY_HAS_CHILDREN, NISTObjectIdentifiers.id_shake256_len);
    private static final Map<Object, LMOtsParameters> suppliers = new HashMap<Object, LMOtsParameters>() { // from class: org.bouncycastle.pqc.crypto.lms.LMOtsParameters.1
        {
            put(Integer.valueOf(LMOtsParameters.sha256_n32_w1.type), LMOtsParameters.sha256_n32_w1);
            put(Integer.valueOf(LMOtsParameters.sha256_n32_w2.type), LMOtsParameters.sha256_n32_w2);
            put(Integer.valueOf(LMOtsParameters.sha256_n32_w4.type), LMOtsParameters.sha256_n32_w4);
            put(Integer.valueOf(LMOtsParameters.sha256_n32_w8.type), LMOtsParameters.sha256_n32_w8);
            put(Integer.valueOf(LMOtsParameters.sha256_n24_w1.type), LMOtsParameters.sha256_n24_w1);
            put(Integer.valueOf(LMOtsParameters.sha256_n24_w2.type), LMOtsParameters.sha256_n24_w2);
            put(Integer.valueOf(LMOtsParameters.sha256_n24_w4.type), LMOtsParameters.sha256_n24_w4);
            put(Integer.valueOf(LMOtsParameters.sha256_n24_w8.type), LMOtsParameters.sha256_n24_w8);
            put(Integer.valueOf(LMOtsParameters.shake256_n32_w1.type), LMOtsParameters.shake256_n32_w1);
            put(Integer.valueOf(LMOtsParameters.shake256_n32_w2.type), LMOtsParameters.shake256_n32_w2);
            put(Integer.valueOf(LMOtsParameters.shake256_n32_w4.type), LMOtsParameters.shake256_n32_w4);
            put(Integer.valueOf(LMOtsParameters.shake256_n32_w8.type), LMOtsParameters.shake256_n32_w8);
            put(Integer.valueOf(LMOtsParameters.shake256_n24_w1.type), LMOtsParameters.shake256_n24_w1);
            put(Integer.valueOf(LMOtsParameters.shake256_n24_w2.type), LMOtsParameters.shake256_n24_w2);
            put(Integer.valueOf(LMOtsParameters.shake256_n24_w4.type), LMOtsParameters.shake256_n24_w4);
            put(Integer.valueOf(LMOtsParameters.shake256_n24_w8.type), LMOtsParameters.shake256_n24_w8);
        }
    };
    private final int type;
    private final int n;
    private final int w;
    private final int p;
    private final int ls;
    private final int sigLen;
    private final ASN1ObjectIdentifier digestOID;

    protected LMOtsParameters(int i, int i2, int i3, int i4, int i5, int i6, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.type = i;
        this.n = i2;
        this.w = i3;
        this.p = i4;
        this.ls = i5;
        this.sigLen = i6;
        this.digestOID = aSN1ObjectIdentifier;
    }

    public int getType() {
        return this.type;
    }

    public int getN() {
        return this.n;
    }

    public int getW() {
        return this.w;
    }

    public int getP() {
        return this.p;
    }

    public int getLs() {
        return this.ls;
    }

    public int getSigLen() {
        return this.sigLen;
    }

    public ASN1ObjectIdentifier getDigestOID() {
        return this.digestOID;
    }

    public static LMOtsParameters getParametersForType(int i) {
        return suppliers.get(Integer.valueOf(i));
    }
}
