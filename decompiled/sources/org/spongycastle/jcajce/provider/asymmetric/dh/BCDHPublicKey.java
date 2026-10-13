package org.spongycastle.jcajce.provider.asymmetric.dh;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.pkcs.DHParameter;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x9.DomainParameters;
import org.spongycastle.asn1.x9.ValidationParams;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.crypto.params.DHParameters;
import org.spongycastle.crypto.params.DHPublicKeyParameters;
import org.spongycastle.crypto.params.DHValidationParameters;
import org.spongycastle.jcajce.provider.asymmetric.util.KeyUtil;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dh/BCDHPublicKey.class */
public class BCDHPublicKey implements DHPublicKey {
    static final long serialVersionUID = -216691575254424324L;
    private BigInteger y;
    private transient DHPublicKeyParameters dhPublicKey;
    private transient DHParameterSpec dhSpec;
    private transient SubjectPublicKeyInfo info;

    BCDHPublicKey(DHPublicKeySpec spec) {
        this.y = spec.getY();
        this.dhSpec = new DHParameterSpec(spec.getP(), spec.getG());
        this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(spec.getP(), spec.getG()));
    }

    BCDHPublicKey(DHPublicKey key) {
        this.y = key.getY();
        this.dhSpec = key.getParams();
        this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG()));
    }

    BCDHPublicKey(DHPublicKeyParameters params) {
        this.y = params.getY();
        this.dhSpec = new DHParameterSpec(params.getParameters().getP(), params.getParameters().getG(), params.getParameters().getL());
        this.dhPublicKey = params;
    }

    BCDHPublicKey(BigInteger y, DHParameterSpec dhSpec) {
        this.y = y;
        this.dhSpec = dhSpec;
        this.dhPublicKey = new DHPublicKeyParameters(y, new DHParameters(dhSpec.getP(), dhSpec.getG()));
    }

    public BCDHPublicKey(SubjectPublicKeyInfo info) {
        this.info = info;
        try {
            ASN1Integer derY = (ASN1Integer) info.parsePublicKey();
            this.y = derY.getValue();
            ASN1Sequence seq = ASN1Sequence.getInstance(info.getAlgorithm().getParameters());
            ASN1ObjectIdentifier id = info.getAlgorithm().getAlgorithm();
            if (id.equals(PKCSObjectIdentifiers.dhKeyAgreement) || isPKCSParam(seq)) {
                DHParameter params = DHParameter.getInstance(seq);
                if (params.getL() != null) {
                    this.dhSpec = new DHParameterSpec(params.getP(), params.getG(), params.getL().intValue());
                } else {
                    this.dhSpec = new DHParameterSpec(params.getP(), params.getG());
                }
                this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG()));
                return;
            }
            if (id.equals(X9ObjectIdentifiers.dhpublicnumber)) {
                DomainParameters params2 = DomainParameters.getInstance(seq);
                this.dhSpec = new DHParameterSpec(params2.getP(), params2.getG());
                ValidationParams validationParams = params2.getValidationParams();
                if (validationParams != null) {
                    this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(params2.getP(), params2.getG(), params2.getQ(), params2.getJ(), new DHValidationParameters(validationParams.getSeed(), validationParams.getPgenCounter().intValue())));
                    return;
                } else {
                    this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(params2.getP(), params2.getG(), params2.getQ(), params2.getJ(), (DHValidationParameters) null));
                    return;
                }
            }
            throw new IllegalArgumentException("unknown algorithm type: " + id);
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid info structure in DH public key");
        }
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "DH";
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        if (this.info != null) {
            return KeyUtil.getEncodedSubjectPublicKeyInfo(this.info);
        }
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement, new DHParameter(this.dhSpec.getP(), this.dhSpec.getG(), this.dhSpec.getL()).toASN1Primitive()), new ASN1Integer(this.y));
    }

    @Override // javax.crypto.interfaces.DHKey
    public DHParameterSpec getParams() {
        return this.dhSpec;
    }

    @Override // javax.crypto.interfaces.DHPublicKey
    public BigInteger getY() {
        return this.y;
    }

    public DHPublicKeyParameters engineGetKeyParameters() {
        return this.dhPublicKey;
    }

    private boolean isPKCSParam(ASN1Sequence seq) {
        if (seq.size() == 2) {
            return true;
        }
        if (seq.size() > 3) {
            return false;
        }
        ASN1Integer l = ASN1Integer.getInstance(seq.getObjectAt(2));
        ASN1Integer p = ASN1Integer.getInstance(seq.getObjectAt(0));
        if (l.getValue().compareTo(BigInteger.valueOf(p.getValue().bitLength())) > 0) {
            return false;
        }
        return true;
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
        this.dhSpec = new DHParameterSpec((BigInteger) in.readObject(), (BigInteger) in.readObject(), in.readInt());
        this.info = null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.dhSpec.getP());
        out.writeObject(this.dhSpec.getG());
        out.writeInt(this.dhSpec.getL());
    }
}
