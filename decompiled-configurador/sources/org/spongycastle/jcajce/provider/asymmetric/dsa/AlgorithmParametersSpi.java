package org.spongycastle.jcajce.provider.asymmetric.dsa;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.x509.DSAParameter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/AlgorithmParametersSpi.class */
public class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi {
    DSAParameterSpec currentSpec;

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
        DSAParameter dsaP = new DSAParameter(this.currentSpec.getP(), this.currentSpec.getQ(), this.currentSpec.getG());
        try {
            return dsaP.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new RuntimeException("Error encoding DSAParameters");
        }
    }

    @Override // java.security.AlgorithmParametersSpi
    protected byte[] engineGetEncoded(String format) {
        if (isASN1FormatString(format)) {
            return engineGetEncoded();
        }
        return null;
    }

    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
        if (paramSpec == DSAParameterSpec.class || paramSpec == AlgorithmParameterSpec.class) {
            return this.currentSpec;
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to DSA parameters object.");
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        if (!(paramSpec instanceof DSAParameterSpec)) {
            throw new InvalidParameterSpecException("DSAParameterSpec required to initialise a DSA algorithm parameters object");
        }
        this.currentSpec = (DSAParameterSpec) paramSpec;
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(byte[] params) throws IOException {
        try {
            DSAParameter dsaP = DSAParameter.getInstance(ASN1Primitive.fromByteArray(params));
            this.currentSpec = new DSAParameterSpec(dsaP.getP(), dsaP.getQ(), dsaP.getG());
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Not a valid DSA Parameter encoding.");
        } catch (ClassCastException e2) {
            throw new IOException("Not a valid DSA Parameter encoding.");
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
        return "DSA Parameters";
    }
}
