package org.spongycastle.jce.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPublicKeySpec;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.DSAParameter;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.crypto.params.DSAPublicKeyParameters;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/provider/JDKDSAPublicKey.class */
public class JDKDSAPublicKey implements DSAPublicKey {
    private static final long serialVersionUID = 1752452449903495175L;
    private BigInteger y;
    private DSAParams dsaSpec;

    JDKDSAPublicKey(DSAPublicKeySpec spec) {
        this.y = spec.getY();
        this.dsaSpec = new DSAParameterSpec(spec.getP(), spec.getQ(), spec.getG());
    }

    JDKDSAPublicKey(DSAPublicKey key) {
        this.y = key.getY();
        this.dsaSpec = key.getParams();
    }

    JDKDSAPublicKey(DSAPublicKeyParameters params) {
        this.y = params.getY();
        this.dsaSpec = new DSAParameterSpec(params.getParameters().getP(), params.getParameters().getQ(), params.getParameters().getG());
    }

    JDKDSAPublicKey(BigInteger y, DSAParameterSpec dsaSpec) {
        this.y = y;
        this.dsaSpec = dsaSpec;
    }

    JDKDSAPublicKey(SubjectPublicKeyInfo info) {
        try {
            ASN1Integer derY = (ASN1Integer) info.parsePublicKey();
            this.y = derY.getValue();
            if (isNotNull(info.getAlgorithm().getParameters())) {
                DSAParameter params = DSAParameter.getInstance(info.getAlgorithm().getParameters());
                this.dsaSpec = new DSAParameterSpec(params.getP(), params.getQ(), params.getG());
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid info structure in DSA public key");
        }
    }

    private boolean isNotNull(ASN1Encodable parameters) {
        return (parameters == null || DERNull.INSTANCE.equals(parameters)) ? false : true;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "DSA";
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            if (this.dsaSpec == null) {
                return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa), new ASN1Integer(this.y)).getEncoded("DER");
            }
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(this.dsaSpec.getP(), this.dsaSpec.getQ(), this.dsaSpec.getG())), new ASN1Integer(this.y)).getEncoded("DER");
        } catch (IOException e) {
            return null;
        }
    }

    @Override // java.security.interfaces.DSAKey
    public DSAParams getParams() {
        return this.dsaSpec;
    }

    @Override // java.security.interfaces.DSAPublicKey
    public BigInteger getY() {
        return this.y;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("DSA Public Key").append(nl);
        buf.append("            y: ").append(getY().toString(16)).append(nl);
        return buf.toString();
    }

    public int hashCode() {
        return ((getY().hashCode() ^ getParams().getG().hashCode()) ^ getParams().getP().hashCode()) ^ getParams().getQ().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof DSAPublicKey)) {
            return false;
        }
        DSAPublicKey other = (DSAPublicKey) o;
        return getY().equals(other.getY()) && getParams().getG().equals(other.getParams().getG()) && getParams().getP().equals(other.getParams().getP()) && getParams().getQ().equals(other.getParams().getQ());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.y = (BigInteger) in.readObject();
        this.dsaSpec = new DSAParameterSpec((BigInteger) in.readObject(), (BigInteger) in.readObject(), (BigInteger) in.readObject());
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.y);
        out.writeObject(this.dsaSpec.getP());
        out.writeObject(this.dsaSpec.getQ());
        out.writeObject(this.dsaSpec.getG());
    }
}
