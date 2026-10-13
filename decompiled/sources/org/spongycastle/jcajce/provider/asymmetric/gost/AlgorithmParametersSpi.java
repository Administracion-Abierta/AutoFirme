package org.spongycastle.jcajce.provider.asymmetric.gost;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.spongycastle.jce.spec.GOST3410ParameterSpec;
import org.spongycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/gost/AlgorithmParametersSpi.class */
public class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi {
    GOST3410ParameterSpec currentSpec;

    protected boolean isASN1FormatString(String format) {
        return format == null || format.equals("ASN.1");
    }

    @Override // java.security.AlgorithmParametersSpi
    protected AlgorithmParameterSpec engineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
        if (paramSpec == null) {
            throw new NullPointerException("argument to getParameterSpec must not be null");
        }
        return localEngineGetParameterSpec(paramSpec);
    }

    @Override // java.security.AlgorithmParametersSpi
    protected byte[] engineGetEncoded() {
        GOST3410PublicKeyAlgParameters gost3410P = new GOST3410PublicKeyAlgParameters(new ASN1ObjectIdentifier(this.currentSpec.getPublicKeyParamSetOID()), new ASN1ObjectIdentifier(this.currentSpec.getDigestParamSetOID()), new ASN1ObjectIdentifier(this.currentSpec.getEncryptionParamSetOID()));
        try {
            return gost3410P.getEncoded("DER");
        } catch (IOException e) {
            throw new RuntimeException("Error encoding GOST3410Parameters");
        }
    }

    @Override // java.security.AlgorithmParametersSpi
    protected byte[] engineGetEncoded(String format) {
        if (isASN1FormatString(format) || format.equalsIgnoreCase(XMLX509Certificate.JCA_CERT_ID)) {
            return engineGetEncoded();
        }
        return null;
    }

    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
        if (paramSpec == GOST3410PublicKeyParameterSetSpec.class || paramSpec == AlgorithmParameterSpec.class) {
            return this.currentSpec;
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to GOST3410 parameters object.");
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        if (!(paramSpec instanceof GOST3410ParameterSpec)) {
            throw new InvalidParameterSpecException("GOST3410ParameterSpec required to initialise a GOST3410 algorithm parameters object");
        }
        this.currentSpec = (GOST3410ParameterSpec) paramSpec;
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(byte[] params) throws IOException {
        try {
            ASN1Sequence seq = (ASN1Sequence) ASN1Primitive.fromByteArray(params);
            this.currentSpec = GOST3410ParameterSpec.fromPublicKeyAlg(new GOST3410PublicKeyAlgParameters(seq));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Not a valid GOST3410 Parameter encoding.");
        } catch (ClassCastException e2) {
            throw new IOException("Not a valid GOST3410 Parameter encoding.");
        }
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(byte[] params, String format) throws IOException {
        if (isASN1FormatString(format) || format.equalsIgnoreCase(XMLX509Certificate.JCA_CERT_ID)) {
            engineInit(params);
            return;
        }
        throw new IOException("Unknown parameter format " + format);
    }

    @Override // java.security.AlgorithmParametersSpi
    protected String engineToString() {
        return "GOST3410 Parameters";
    }
}
