package org.bouncycastle.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.CompositePrivateKey;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.jcajce.spec.CompositeAlgorithmSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.ExtendedContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.TeeOutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/jcajce/JcaContentSignerBuilder.class */
public class JcaContentSignerBuilder {
    private static final Set isAlgIdFromPrivate = new HashSet();
    private static final DefaultSignatureAlgorithmIdentifierFinder SIGNATURE_ALGORITHM_IDENTIFIER_FINDER = new DefaultSignatureAlgorithmIdentifierFinder();
    private final String signatureAlgorithm;
    private final AlgorithmIdentifier signatureDigestAlgorithm;
    private OperatorHelper helper;
    private SecureRandom random;
    private AlgorithmIdentifier sigAlgId;
    private AlgorithmParameterSpec sigAlgSpec;

    public JcaContentSignerBuilder(String str) {
        this(str, (AlgorithmIdentifier) null);
    }

    private static AlgorithmIdentifier getSigDigAlgId(PublicKey publicKey) {
        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        if (subjectPublicKeyInfo.getAlgorithm().getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_alg_hss_lms_hashsig)) {
            return new AlgorithmIdentifier(LMSigParameters.getParametersForType(Pack.bigEndianToInt(subjectPublicKeyInfo.getPublicKeyData().getOctets(), 4)).getDigestOID());
        }
        return null;
    }

    public JcaContentSignerBuilder(String str, PublicKey publicKey) {
        this(str, getSigDigAlgId(publicKey));
    }

    public JcaContentSignerBuilder(String str, AlgorithmIdentifier algorithmIdentifier) {
        this.helper = new OperatorHelper(new DefaultJcaJceHelper());
        this.signatureAlgorithm = str;
        this.signatureDigestAlgorithm = algorithmIdentifier;
    }

    public JcaContentSignerBuilder(String str, AlgorithmParameterSpec algorithmParameterSpec) {
        this(str, algorithmParameterSpec, null);
    }

    public JcaContentSignerBuilder(String str, AlgorithmParameterSpec algorithmParameterSpec, AlgorithmIdentifier algorithmIdentifier) {
        this.helper = new OperatorHelper(new DefaultJcaJceHelper());
        this.signatureAlgorithm = str;
        this.signatureDigestAlgorithm = algorithmIdentifier;
        if (algorithmParameterSpec instanceof PSSParameterSpec) {
            PSSParameterSpec pSSParameterSpec = (PSSParameterSpec) algorithmParameterSpec;
            this.sigAlgSpec = pSSParameterSpec;
            this.sigAlgId = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS, createPSSParams(pSSParameterSpec));
        } else {
            if (!(algorithmParameterSpec instanceof CompositeAlgorithmSpec)) {
                throw new IllegalArgumentException("unknown sigParamSpec: " + (algorithmParameterSpec == null ? "null" : algorithmParameterSpec.getClass().getName()));
            }
            CompositeAlgorithmSpec compositeAlgorithmSpec = (CompositeAlgorithmSpec) algorithmParameterSpec;
            this.sigAlgSpec = compositeAlgorithmSpec;
            this.sigAlgId = new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite, createCompParams(compositeAlgorithmSpec));
        }
    }

    public JcaContentSignerBuilder setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JcaContentSignerBuilder setProvider(String str) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(str));
        return this;
    }

    public JcaContentSignerBuilder setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public ContentSigner build(PrivateKey privateKey) throws OperatorCreationException {
        if ((privateKey instanceof CompositePrivateKey) && ((CompositePrivateKey) privateKey).getAlgorithmIdentifier().equals((ASN1Primitive) MiscObjectIdentifiers.id_composite_key)) {
            return buildComposite((CompositePrivateKey) privateKey);
        }
        try {
            if (this.sigAlgSpec == null) {
                this.sigAlgId = getSigAlgId(privateKey);
            }
            final AlgorithmIdentifier algorithmIdentifier = this.sigAlgId;
            final Signature signatureCreateSignature = this.helper.createSignature(this.sigAlgId);
            if (this.random != null) {
                signatureCreateSignature.initSign(privateKey, this.random);
            } else {
                signatureCreateSignature.initSign(privateKey);
            }
            final ContentSigner contentSigner = new ContentSigner() { // from class: org.bouncycastle.operator.jcajce.JcaContentSignerBuilder.1
                private OutputStream stream;

                {
                    this.stream = OutputStreamFactory.createStream(signatureCreateSignature);
                }

                @Override // org.bouncycastle.operator.ContentSigner
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return algorithmIdentifier;
                }

                @Override // org.bouncycastle.operator.ContentSigner
                public OutputStream getOutputStream() {
                    return this.stream;
                }

                @Override // org.bouncycastle.operator.ContentSigner
                public byte[] getSignature() {
                    try {
                        return signatureCreateSignature.sign();
                    } catch (SignatureException e) {
                        throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
                    }
                }
            };
            return this.signatureDigestAlgorithm != null ? new ExtendedContentSigner() { // from class: org.bouncycastle.operator.jcajce.JcaContentSignerBuilder.2
                private final AlgorithmIdentifier digestAlgorithm;
                private final ContentSigner signer;

                {
                    this.digestAlgorithm = JcaContentSignerBuilder.this.signatureDigestAlgorithm;
                    this.signer = contentSigner;
                }

                @Override // org.bouncycastle.operator.ExtendedContentSigner
                public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
                    return this.digestAlgorithm;
                }

                @Override // org.bouncycastle.operator.ContentSigner
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return this.signer.getAlgorithmIdentifier();
                }

                @Override // org.bouncycastle.operator.ContentSigner
                public OutputStream getOutputStream() {
                    return this.signer.getOutputStream();
                }

                @Override // org.bouncycastle.operator.ContentSigner
                public byte[] getSignature() {
                    return this.signer.getSignature();
                }
            } : contentSigner;
        } catch (GeneralSecurityException e) {
            throw new OperatorCreationException("cannot create signer: " + e.getMessage(), e);
        }
    }

    private AlgorithmIdentifier getSigAlgId(PrivateKey privateKey) {
        if (!isAlgIdFromPrivate.contains(Strings.toUpperCase(this.signatureAlgorithm))) {
            return SIGNATURE_ALGORITHM_IDENTIFIER_FINDER.find(this.signatureAlgorithm);
        }
        AlgorithmIdentifier algorithmIdentifierFind = SIGNATURE_ALGORITHM_IDENTIFIER_FINDER.find(privateKey.getAlgorithm());
        return algorithmIdentifierFind == null ? PrivateKeyInfo.getInstance(privateKey.getEncoded()).getPrivateKeyAlgorithm() : algorithmIdentifierFind;
    }

    private ContentSigner buildComposite(CompositePrivateKey compositePrivateKey) throws OperatorCreationException {
        try {
            List<PrivateKey> privateKeys = compositePrivateKey.getPrivateKeys();
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(this.sigAlgId.getParameters());
            final Signature[] signatureArr = new Signature[aSN1Sequence.size()];
            for (int i = 0; i != aSN1Sequence.size(); i++) {
                signatureArr[i] = this.helper.createSignature(AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(i)));
                if (this.random != null) {
                    signatureArr[i].initSign(privateKeys.get(i), this.random);
                } else {
                    signatureArr[i].initSign(privateKeys.get(i));
                }
            }
            OutputStream outputStreamCreateStream = OutputStreamFactory.createStream(signatureArr[0]);
            for (int i2 = 1; i2 != signatureArr.length; i2++) {
                outputStreamCreateStream = new TeeOutputStream(outputStreamCreateStream, OutputStreamFactory.createStream(signatureArr[i2]));
            }
            final OutputStream outputStream = outputStreamCreateStream;
            return new ContentSigner() { // from class: org.bouncycastle.operator.jcajce.JcaContentSignerBuilder.3
                OutputStream stream;

                {
                    this.stream = outputStream;
                }

                @Override // org.bouncycastle.operator.ContentSigner
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return JcaContentSignerBuilder.this.sigAlgId;
                }

                @Override // org.bouncycastle.operator.ContentSigner
                public OutputStream getOutputStream() {
                    return this.stream;
                }

                @Override // org.bouncycastle.operator.ContentSigner
                public byte[] getSignature() {
                    try {
                        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                        for (int i3 = 0; i3 != signatureArr.length; i3++) {
                            aSN1EncodableVector.add(new DERBitString(signatureArr[i3].sign()));
                        }
                        return new DERSequence(aSN1EncodableVector).getEncoded("DER");
                    } catch (IOException e) {
                        throw new RuntimeOperatorException("exception encoding signature: " + e.getMessage(), e);
                    } catch (SignatureException e2) {
                        throw new RuntimeOperatorException("exception obtaining signature: " + e2.getMessage(), e2);
                    }
                }
            };
        } catch (GeneralSecurityException e) {
            throw new OperatorCreationException("cannot create signer: " + e.getMessage(), e);
        }
    }

    private static RSASSAPSSparams createPSSParams(PSSParameterSpec pSSParameterSpec) {
        DefaultDigestAlgorithmIdentifierFinder defaultDigestAlgorithmIdentifierFinder = new DefaultDigestAlgorithmIdentifierFinder();
        AlgorithmIdentifier algorithmIdentifierFind = defaultDigestAlgorithmIdentifierFinder.find(pSSParameterSpec.getDigestAlgorithm());
        if (algorithmIdentifierFind.getParameters() == null) {
            algorithmIdentifierFind = new AlgorithmIdentifier(algorithmIdentifierFind.getAlgorithm(), DERNull.INSTANCE);
        }
        AlgorithmIdentifier algorithmIdentifierFind2 = defaultDigestAlgorithmIdentifierFinder.find(((MGF1ParameterSpec) pSSParameterSpec.getMGFParameters()).getDigestAlgorithm());
        if (algorithmIdentifierFind2.getParameters() == null) {
            algorithmIdentifierFind2 = new AlgorithmIdentifier(algorithmIdentifierFind2.getAlgorithm(), DERNull.INSTANCE);
        }
        return new RSASSAPSSparams(algorithmIdentifierFind, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, algorithmIdentifierFind2), new ASN1Integer(pSSParameterSpec.getSaltLength()), new ASN1Integer(pSSParameterSpec.getTrailerField()));
    }

    private static ASN1Sequence createCompParams(CompositeAlgorithmSpec compositeAlgorithmSpec) {
        DefaultSignatureAlgorithmIdentifierFinder defaultSignatureAlgorithmIdentifierFinder = new DefaultSignatureAlgorithmIdentifierFinder();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        List<String> algorithmNames = compositeAlgorithmSpec.getAlgorithmNames();
        List<AlgorithmParameterSpec> parameterSpecs = compositeAlgorithmSpec.getParameterSpecs();
        for (int i = 0; i != algorithmNames.size(); i++) {
            AlgorithmParameterSpec algorithmParameterSpec = parameterSpecs.get(i);
            if (algorithmParameterSpec == null) {
                aSN1EncodableVector.add(defaultSignatureAlgorithmIdentifierFinder.find(algorithmNames.get(i)));
            } else {
                if (!(algorithmParameterSpec instanceof PSSParameterSpec)) {
                    throw new IllegalArgumentException("unrecognized parameterSpec");
                }
                aSN1EncodableVector.add(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS, createPSSParams((PSSParameterSpec) algorithmParameterSpec)));
            }
        }
        return new DERSequence(aSN1EncodableVector);
    }

    static {
        isAlgIdFromPrivate.add("DILITHIUM");
        isAlgIdFromPrivate.add("SPHINCS+");
        isAlgIdFromPrivate.add("SPHINCSPlus");
        isAlgIdFromPrivate.add("ML-DSA");
        isAlgIdFromPrivate.add("SLH-DSA");
        isAlgIdFromPrivate.add("HASH-ML-DSA");
        isAlgIdFromPrivate.add("HASH-SLH-DSA");
    }
}
