package es.gob.afirma.standalone.configurator.common;

import es.gob.afirma.core.misc.AOUtil;
import java.security.MessageDigest;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/configurator/common/ConfigDataInfo.class */
public class ConfigDataInfo {
    private static final String HASH_ALGORITHM = "SHA-256";
    private final byte[] data;
    private String hash;

    public ConfigDataInfo(byte[] bArr) {
        this.data = bArr;
        try {
            this.hash = AOUtil.hexify(MessageDigest.getInstance("SHA-256").digest(this.data), false);
        } catch (Exception e) {
            this.hash = "";
        }
    }

    public byte[] getData() {
        return this.data;
    }

    public String getHash() {
        return this.hash;
    }
}
