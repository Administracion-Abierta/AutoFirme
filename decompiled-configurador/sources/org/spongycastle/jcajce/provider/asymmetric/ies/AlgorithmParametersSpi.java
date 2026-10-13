package org.spongycastle.jcajce.provider.asymmetric.ies;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.jce.spec.IESParameterSpec;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ies/AlgorithmParametersSpi.class */
public class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi {
    IESParameterSpec currentSpec;

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
        try {
            ASN1EncodableVector v = new ASN1EncodableVector();
            if (this.currentSpec.getDerivationV() != null) {
                v.add(new DERTaggedObject(false, 0, new DEROctetString(this.currentSpec.getDerivationV())));
            }
            if (this.currentSpec.getEncodingV() != null) {
                v.add(new DERTaggedObject(false, 1, new DEROctetString(this.currentSpec.getEncodingV())));
            }
            v.add(new ASN1Integer(this.currentSpec.getMacKeySize()));
            if (this.currentSpec.getNonce() != null) {
                ASN1EncodableVector cV = new ASN1EncodableVector();
                cV.add(new ASN1Integer(this.currentSpec.getCipherKeySize()));
                cV.add(new ASN1Integer(this.currentSpec.getNonce()));
                v.add(new DERSequence(cV));
            }
            return new DERSequence(v).getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new RuntimeException("Error encoding IESParameters");
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
        if (paramSpec == IESParameterSpec.class || paramSpec == AlgorithmParameterSpec.class) {
            return this.currentSpec;
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to ElGamal parameters object.");
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        if (!(paramSpec instanceof IESParameterSpec)) {
            throw new InvalidParameterSpecException("IESParameterSpec required to initialise a IES algorithm parameters object");
        }
        this.currentSpec = (IESParameterSpec) paramSpec;
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(byte[] params) throws IOException {
        try {
            ASN1Sequence s = (ASN1Sequence) ASN1Primitive.fromByteArray(params);
            if (s.size() == 1) {
                this.currentSpec = new IESParameterSpec(null, null, ASN1Integer.getInstance(s.getObjectAt(0)).getValue().intValue());
            } else if (s.size() == 2) {
                ASN1TaggedObject tagged = ASN1TaggedObject.getInstance(s.getObjectAt(0));
                if (tagged.getTagNo() == 0) {
                    this.currentSpec = new IESParameterSpec(ASN1OctetString.getInstance(tagged, false).getOctets(), null, ASN1Integer.getInstance(s.getObjectAt(1)).getValue().intValue());
                } else {
                    this.currentSpec = new IESParameterSpec(null, ASN1OctetString.getInstance(tagged, false).getOctets(), ASN1Integer.getInstance(s.getObjectAt(1)).getValue().intValue());
                }
            } else if (s.size() == 3) {
                ASN1TaggedObject tagged1 = ASN1TaggedObject.getInstance(s.getObjectAt(0));
                ASN1TaggedObject tagged2 = ASN1TaggedObject.getInstance(s.getObjectAt(1));
                this.currentSpec = new IESParameterSpec(ASN1OctetString.getInstance(tagged1, false).getOctets(), ASN1OctetString.getInstance(tagged2, false).getOctets(), ASN1Integer.getInstance(s.getObjectAt(2)).getValue().intValue());
            } else if (s.size() == 4) {
                ASN1TaggedObject tagged3 = ASN1TaggedObject.getInstance(s.getObjectAt(0));
                ASN1TaggedObject tagged4 = ASN1TaggedObject.getInstance(s.getObjectAt(1));
                ASN1Sequence cipherDet = ASN1Sequence.getInstance(s.getObjectAt(3));
                this.currentSpec = new IESParameterSpec(ASN1OctetString.getInstance(tagged3, false).getOctets(), ASN1OctetString.getInstance(tagged4, false).getOctets(), ASN1Integer.getInstance(s.getObjectAt(2)).getValue().intValue(), ASN1Integer.getInstance(cipherDet.getObjectAt(0)).getValue().intValue(), ASN1OctetString.getInstance(cipherDet.getObjectAt(1)).getOctets());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Not a valid IES Parameter encoding.");
        } catch (ClassCastException e2) {
            throw new IOException("Not a valid IES Parameter encoding.");
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
        return "IES Parameters";
    }
}
