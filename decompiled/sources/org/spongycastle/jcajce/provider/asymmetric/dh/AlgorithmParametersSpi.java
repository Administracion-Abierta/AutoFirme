package org.spongycastle.jcajce.provider.asymmetric.dh;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.DHParameterSpec;
import org.spongycastle.asn1.pkcs.DHParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dh/AlgorithmParametersSpi.class */
public class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi {
    DHParameterSpec currentSpec;

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
        DHParameter dhP = new DHParameter(this.currentSpec.getP(), this.currentSpec.getG(), this.currentSpec.getL());
        try {
            return dhP.getEncoded("DER");
        } catch (IOException e) {
            throw new RuntimeException("Error encoding DHParameters");
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
        if (paramSpec == DHParameterSpec.class || paramSpec == AlgorithmParameterSpec.class) {
            return this.currentSpec;
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to DH parameters object.");
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        if (!(paramSpec instanceof DHParameterSpec)) {
            throw new InvalidParameterSpecException("DHParameterSpec required to initialise a Diffie-Hellman algorithm parameters object");
        }
        this.currentSpec = (DHParameterSpec) paramSpec;
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(byte[] params) throws IOException {
        try {
            DHParameter dhP = DHParameter.getInstance(params);
            if (dhP.getL() != null) {
                this.currentSpec = new DHParameterSpec(dhP.getP(), dhP.getG(), dhP.getL().intValue());
            } else {
                this.currentSpec = new DHParameterSpec(dhP.getP(), dhP.getG());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Not a valid DH Parameter encoding.");
        } catch (ClassCastException e2) {
            throw new IOException("Not a valid DH Parameter encoding.");
        }
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(byte[] params, String format) throws IOException {
        if (isASN1FormatString(format)) {
            engineInit(params);
            return;
        }
        throw new IOException("Unknown parameter format " + format);
    }

    @Override // java.security.AlgorithmParametersSpi
    protected String engineToString() {
        return "Diffie-Hellman Parameters";
    }
}
