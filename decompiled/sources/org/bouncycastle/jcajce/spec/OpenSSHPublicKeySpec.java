package org.bouncycastle.jcajce.spec;

import java.security.spec.EncodedKeySpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/OpenSSHPublicKeySpec.class */
public class OpenSSHPublicKeySpec extends EncodedKeySpec {
    private static final String[] allowedTypes = {"ssh-rsa", "ssh-ed25519", "ssh-dss"};
    private final String type;

    public OpenSSHPublicKeySpec(byte[] bArr) {
        super(bArr);
        int i = 0 + 1;
        int i2 = i + 1;
        int i3 = ((bArr[0] & 255) << 24) | ((bArr[i] & 255) << 16);
        int i4 = i2 + 1;
        int i5 = i3 | ((bArr[i2] & 255) << 8);
        int i6 = i4 + 1;
        int i7 = i5 | (bArr[i4] & 255);
        if (i6 + i7 >= bArr.length) {
            throw new IllegalArgumentException("invalid public key blob: type field longer than blob");
        }
        this.type = Strings.fromByteArray(Arrays.copyOfRange(bArr, i6, i6 + i7));
        if (this.type.startsWith("ecdsa")) {
            return;
        }
        for (int i8 = 0; i8 < allowedTypes.length; i8++) {
            if (allowedTypes[i8].equals(this.type)) {
                return;
            }
        }
        throw new IllegalArgumentException("unrecognised public key type " + this.type);
    }

    @Override // java.security.spec.EncodedKeySpec
    public String getFormat() {
        return "OpenSSH";
    }

    public String getType() {
        return this.type;
    }
}
