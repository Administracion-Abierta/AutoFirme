package org.spongycastle.jcajce.provider.asymmetric.elgamal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.oiw.ElGamalParameter;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.params.ElGamalPublicKeyParameters;
import org.spongycastle.jce.interfaces.ElGamalPublicKey;
import org.spongycastle.jce.spec.ElGamalParameterSpec;
import org.spongycastle.jce.spec.ElGamalPublicKeySpec;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/elgamal/BCElGamalPublicKey.class */
public class BCElGamalPublicKey implements ElGamalPublicKey, DHPublicKey {
    static final long serialVersionUID = 8712728417091216948L;
    private BigInteger y;
    private transient ElGamalParameterSpec elSpec;

    BCElGamalPublicKey(ElGamalPublicKeySpec spec) {
        this.y = spec.getY();
        this.elSpec = new ElGamalParameterSpec(spec.getParams().getP(), spec.getParams().getG());
    }

    BCElGamalPublicKey(DHPublicKeySpec spec) {
        this.y = spec.getY();
        this.elSpec = new ElGamalParameterSpec(spec.getP(), spec.getG());
    }

    BCElGamalPublicKey(ElGamalPublicKey key) {
        this.y = key.getY();
        this.elSpec = key.getParameters();
    }

    BCElGamalPublicKey(DHPublicKey key) {
        this.y = key.getY();
        this.elSpec = new ElGamalParameterSpec(key.getParams().getP(), key.getParams().getG());
    }

    BCElGamalPublicKey(ElGamalPublicKeyParameters params) {
        this.y = params.getY();
        this.elSpec = new ElGamalParameterSpec(params.getParameters().getP(), params.getParameters().getG());
    }

    BCElGamalPublicKey(BigInteger y, ElGamalParameterSpec elSpec) {
        this.y = y;
        this.elSpec = elSpec;
    }

    BCElGamalPublicKey(SubjectPublicKeyInfo info) {
        ElGamalParameter params = ElGamalParameter.getInstance(info.getAlgorithm().getParameters());
        try {
            ASN1Integer derY = (ASN1Integer) info.parsePublicKey();
            this.y = derY.getValue();
            this.elSpec = new ElGamalParameterSpec(params.getP(), params.getG());
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid info structure in DSA public key");
        }
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "ElGamal";
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(OIWObjectIdentifiers.elGamalAlgorithm, new ElGamalParameter(this.elSpec.getP(), this.elSpec.getG())), new ASN1Integer(this.y));
            return info.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            return null;
        }
    }

    @Override // org.spongycastle.jce.interfaces.ElGamalKey
    public ElGamalParameterSpec getParameters() {
        return this.elSpec;
    }

    @Override // javax.crypto.interfaces.DHKey
    public DHParameterSpec getParams() {
        return new DHParameterSpec(this.elSpec.getP(), this.elSpec.getG());
    }

    @Override // org.spongycastle.jce.interfaces.ElGamalPublicKey, javax.crypto.interfaces.DHPublicKey
    public BigInteger getY() {
        return this.y;
    }

    public int hashCode() {
        return ((getY().hashCode() ^ getParams().getG().hashCode()) ^ getParams().getP().hashCode()) ^ getParams().getL();
    }

    public boolean equals(Object o) {
        if (!(o instanceof DHPublicKey)) {
            return false;
        }
        DHPublicKey other = (DHPublicKey) o;
        return getY().equals(other.getY()) && getParams().getG().equals(other.getParams().getG()) && getParams().getP().equals(other.getParams().getP()) && getParams().getL() == other.getParams().getL();
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.elSpec = new ElGamalParameterSpec((BigInteger) in.readObject(), (BigInteger) in.readObject());
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.elSpec.getP());
        out.writeObject(this.elSpec.getG());
    }
}
