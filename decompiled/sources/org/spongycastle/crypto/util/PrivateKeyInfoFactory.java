package org.spongycastle.crypto.util;

import java.io.IOException;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Null;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.pkcs.RSAPrivateKey;
import org.spongycastle.asn1.sec.ECPrivateKey;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.DSAParameter;
import org.spongycastle.asn1.x9.X962Parameters;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.DSAParameters;
import org.spongycastle.crypto.params.DSAPrivateKeyParameters;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECNamedDomainParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.RSAKeyParameters;
import org.spongycastle.crypto.params.RSAPrivateCrtKeyParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/util/PrivateKeyInfoFactory.class */
public class PrivateKeyInfoFactory {
    private PrivateKeyInfoFactory() {
    }

    public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter privateKey) throws IOException {
        ASN1Encodable params;
        int orderBitLength;
        if (privateKey instanceof RSAKeyParameters) {
            RSAPrivateCrtKeyParameters priv = (RSAPrivateCrtKeyParameters) privateKey;
            return new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), new RSAPrivateKey(priv.getModulus(), priv.getPublicExponent(), priv.getExponent(), priv.getP(), priv.getQ(), priv.getDP(), priv.getDQ(), priv.getQInv()));
        }
        if (privateKey instanceof DSAPrivateKeyParameters) {
            DSAPrivateKeyParameters priv2 = (DSAPrivateKeyParameters) privateKey;
            DSAParameters params2 = priv2.getParameters();
            return new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(params2.getP(), params2.getQ(), params2.getG())), new ASN1Integer(priv2.getX()));
        }
        if (privateKey instanceof ECPrivateKeyParameters) {
            ECPrivateKeyParameters priv3 = (ECPrivateKeyParameters) privateKey;
            ECDomainParameters domainParams = priv3.getParameters();
            if (domainParams == null) {
                params = new X962Parameters((ASN1Null) DERNull.INSTANCE);
                orderBitLength = priv3.getD().bitLength();
            } else if (domainParams instanceof ECNamedDomainParameters) {
                params = new X962Parameters(((ECNamedDomainParameters) domainParams).getName());
                orderBitLength = domainParams.getN().bitLength();
            } else {
                X9ECParameters ecP = new X9ECParameters(domainParams.getCurve(), domainParams.getG(), domainParams.getN(), domainParams.getH(), domainParams.getSeed());
                params = new X962Parameters(ecP);
                orderBitLength = domainParams.getN().bitLength();
            }
            return new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params), new ECPrivateKey(orderBitLength, priv3.getD(), params));
        }
        throw new IOException("key parameters not recognised.");
    }
}
