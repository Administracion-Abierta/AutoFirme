package org.spongycastle.pqc.jcajce.provider.newhope;

import java.io.IOException;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.spongycastle.pqc.jcajce.interfaces.NHPrivateKey;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/newhope/BCNHPrivateKey.class */
public class BCNHPrivateKey implements NHPrivateKey {
    private static final long serialVersionUID = 1;
    private final NHPrivateKeyParameters params;

    public BCNHPrivateKey(NHPrivateKeyParameters params) {
        this.params = params;
    }

    public BCNHPrivateKey(PrivateKeyInfo keyInfo) throws IOException {
        this.params = new NHPrivateKeyParameters(convert(ASN1OctetString.getInstance(keyInfo.parsePrivateKey()).getOctets()));
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof BCNHPrivateKey)) {
            return false;
        }
        BCNHPrivateKey otherKey = (BCNHPrivateKey) o;
        return Arrays.areEqual(this.params.getSecData(), otherKey.params.getSecData());
    }

    public int hashCode() {
        return Arrays.hashCode(this.params.getSecData());
    }

    @Override // java.security.Key
    public final String getAlgorithm() {
        return "NH";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
            short[] privateKeyData = this.params.getSecData();
            byte[] octets = new byte[privateKeyData.length * 2];
            for (int i = 0; i != privateKeyData.length; i++) {
                Pack.shortToLittleEndian(privateKeyData[i], octets, i * 2);
            }
            PrivateKeyInfo pki = new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(octets));
            return pki.getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    @Override // org.spongycastle.pqc.jcajce.interfaces.NHPrivateKey
    public short[] getSecretData() {
        return this.params.getSecData();
    }

    CipherParameters getKeyParams() {
        return this.params;
    }

    private static short[] convert(byte[] octets) {
        short[] rv = new short[octets.length / 2];
        for (int i = 0; i != rv.length; i++) {
            rv[i] = Pack.littleEndianToShort(octets, i * 2);
        }
        return rv;
    }
}
