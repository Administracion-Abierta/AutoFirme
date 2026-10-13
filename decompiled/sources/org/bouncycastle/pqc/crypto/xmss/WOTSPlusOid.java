package org.bouncycastle.pqc.crypto.xmss;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/WOTSPlusOid.class */
final class WOTSPlusOid implements XMSSOid {
    private static final Map<String, WOTSPlusOid> oidLookupTable;
    private final int oid;
    private final String stringRepresentation;

    private WOTSPlusOid(int i, String str) {
        this.oid = i;
        this.stringRepresentation = str;
    }

    protected static WOTSPlusOid lookup(String str, int i, int i2, int i3) {
        if (str == null) {
            throw new NullPointerException("algorithmName == null");
        }
        return oidLookupTable.get(createKey(str, i, i2, i3));
    }

    private static String createKey(String str, int i, int i2, int i3) {
        if (str == null) {
            throw new NullPointerException("algorithmName == null");
        }
        return str + "-" + i + "-" + i2 + "-" + i3;
    }

    @Override // org.bouncycastle.pqc.crypto.xmss.XMSSOid
    public int getOid() {
        return this.oid;
    }

    @Override // org.bouncycastle.pqc.crypto.xmss.XMSSOid
    public String toString() {
        return this.stringRepresentation;
    }

    static {
        HashMap map = new HashMap();
        map.put(createKey("SHA-256", 32, 16, 67), new WOTSPlusOid(16777217, "WOTSP_SHA2-256_W16"));
        map.put(createKey("SHA-512", 64, 16, 131), new WOTSPlusOid(33554434, "WOTSP_SHA2-512_W16"));
        map.put(createKey("SHAKE128", 32, 16, 67), new WOTSPlusOid(50331651, "WOTSP_SHAKE128_W16"));
        map.put(createKey("SHAKE256", 64, 16, 131), new WOTSPlusOid(67108868, "WOTSP_SHAKE256_W16"));
        oidLookupTable = Collections.unmodifiableMap(map);
    }
}
