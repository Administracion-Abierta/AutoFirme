package org.bouncycastle.pqc.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.CMCEPrivateKey;
import org.bouncycastle.pqc.asn1.CMCEPublicKey;
import org.bouncycastle.pqc.asn1.FalconPrivateKey;
import org.bouncycastle.pqc.asn1.FalconPublicKey;
import org.bouncycastle.pqc.asn1.McElieceCCA2PrivateKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTPrivateKey;
import org.bouncycastle.pqc.asn1.XMSSPrivateKey;
import org.bouncycastle.pqc.crypto.bike.BIKEPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.Composer;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.BDS;
import org.bouncycastle.pqc.crypto.xmss.BDSStateMap;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.pqc.legacy.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.bouncycastle.pqc.legacy.crypto.qtesla.QTESLAPrivateKeyParameters;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/util/PrivateKeyInfoFactory.class */
public class PrivateKeyInfoFactory {
    private PrivateKeyInfoFactory() {
    }

    public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter asymmetricKeyParameter) throws IOException {
        return createPrivateKeyInfo(asymmetricKeyParameter, null);
    }

    public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter asymmetricKeyParameter, ASN1Set aSN1Set) throws IOException {
        if (asymmetricKeyParameter instanceof QTESLAPrivateKeyParameters) {
            QTESLAPrivateKeyParameters qTESLAPrivateKeyParameters = (QTESLAPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(Utils.qTeslaLookupAlgID(qTESLAPrivateKeyParameters.getSecurityCategory()), new DEROctetString(qTESLAPrivateKeyParameters.getSecret()), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof SPHINCSPrivateKeyParameters) {
            SPHINCSPrivateKeyParameters sPHINCSPrivateKeyParameters = (SPHINCSPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(Utils.sphincs256LookupTreeAlgID(sPHINCSPrivateKeyParameters.getTreeDigest()))), new DEROctetString(sPHINCSPrivateKeyParameters.getKeyData()));
        }
        if (asymmetricKeyParameter instanceof NHPrivateKeyParameters) {
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
            short[] secData = ((NHPrivateKeyParameters) asymmetricKeyParameter).getSecData();
            byte[] bArr = new byte[secData.length * 2];
            for (int i = 0; i != secData.length; i++) {
                Pack.shortToLittleEndian(secData[i], bArr, i * 2);
            }
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(bArr));
        }
        if (asymmetricKeyParameter instanceof LMSPrivateKeyParameters) {
            LMSPrivateKeyParameters lMSPrivateKeyParameters = (LMSPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig), new DEROctetString(Composer.compose().u32str(1).bytes(lMSPrivateKeyParameters).build()), aSN1Set, Composer.compose().u32str(1).bytes(lMSPrivateKeyParameters.getPublicKey()).build());
        }
        if (asymmetricKeyParameter instanceof HSSPrivateKeyParameters) {
            HSSPrivateKeyParameters hSSPrivateKeyParameters = (HSSPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig), new DEROctetString(Composer.compose().u32str(hSSPrivateKeyParameters.getL()).bytes(hSSPrivateKeyParameters).build()), aSN1Set, Composer.compose().u32str(hSSPrivateKeyParameters.getL()).bytes(hSSPrivateKeyParameters.getPublicKey().getLMSPublicKey()).build());
        }
        if (asymmetricKeyParameter instanceof SPHINCSPlusPrivateKeyParameters) {
            SPHINCSPlusPrivateKeyParameters sPHINCSPlusPrivateKeyParameters = (SPHINCSPlusPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(Utils.sphincsPlusOidLookup(sPHINCSPlusPrivateKeyParameters.getParameters())), new DEROctetString(sPHINCSPlusPrivateKeyParameters.getEncoded()), aSN1Set, sPHINCSPlusPrivateKeyParameters.getPublicKey());
        }
        if (asymmetricKeyParameter instanceof SLHDSAPrivateKeyParameters) {
            SLHDSAPrivateKeyParameters sLHDSAPrivateKeyParameters = (SLHDSAPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(Utils.slhdsaOidLookup(sLHDSAPrivateKeyParameters.getParameters())), sLHDSAPrivateKeyParameters.getEncoded(), aSN1Set, sLHDSAPrivateKeyParameters.getPublicKey());
        }
        if (asymmetricKeyParameter instanceof PicnicPrivateKeyParameters) {
            PicnicPrivateKeyParameters picnicPrivateKeyParameters = (PicnicPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(Utils.picnicOidLookup(picnicPrivateKeyParameters.getParameters())), new DEROctetString(picnicPrivateKeyParameters.getEncoded()), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof CMCEPrivateKeyParameters) {
            CMCEPrivateKeyParameters cMCEPrivateKeyParameters = (CMCEPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(Utils.mcElieceOidLookup(cMCEPrivateKeyParameters.getParameters())), new CMCEPrivateKey(0, cMCEPrivateKeyParameters.getDelta(), cMCEPrivateKeyParameters.getC(), cMCEPrivateKeyParameters.getG(), cMCEPrivateKeyParameters.getAlpha(), cMCEPrivateKeyParameters.getS(), new CMCEPublicKey(cMCEPrivateKeyParameters.reconstructPublicKey())), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof XMSSPrivateKeyParameters) {
            XMSSPrivateKeyParameters xMSSPrivateKeyParameters = (XMSSPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.xmss, new XMSSKeyParams(xMSSPrivateKeyParameters.getParameters().getHeight(), Utils.xmssLookupTreeAlgID(xMSSPrivateKeyParameters.getTreeDigest()))), xmssCreateKeyStructure(xMSSPrivateKeyParameters), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof XMSSMTPrivateKeyParameters) {
            XMSSMTPrivateKeyParameters xMSSMTPrivateKeyParameters = (XMSSMTPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt, new XMSSMTKeyParams(xMSSMTPrivateKeyParameters.getParameters().getHeight(), xMSSMTPrivateKeyParameters.getParameters().getLayers(), Utils.xmssLookupTreeAlgID(xMSSMTPrivateKeyParameters.getTreeDigest()))), xmssmtCreateKeyStructure(xMSSMTPrivateKeyParameters), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof McElieceCCA2PrivateKeyParameters) {
            McElieceCCA2PrivateKeyParameters mcElieceCCA2PrivateKeyParameters = (McElieceCCA2PrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.mcElieceCca2), new McElieceCCA2PrivateKey(mcElieceCCA2PrivateKeyParameters.getN(), mcElieceCCA2PrivateKeyParameters.getK(), mcElieceCCA2PrivateKeyParameters.getField(), mcElieceCCA2PrivateKeyParameters.getGoppaPoly(), mcElieceCCA2PrivateKeyParameters.getP(), Utils.getAlgorithmIdentifier(mcElieceCCA2PrivateKeyParameters.getDigest())));
        }
        if (asymmetricKeyParameter instanceof FrodoPrivateKeyParameters) {
            FrodoPrivateKeyParameters frodoPrivateKeyParameters = (FrodoPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(Utils.frodoOidLookup(frodoPrivateKeyParameters.getParameters())), new DEROctetString(frodoPrivateKeyParameters.getEncoded()), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof SABERPrivateKeyParameters) {
            SABERPrivateKeyParameters sABERPrivateKeyParameters = (SABERPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(Utils.saberOidLookup(sABERPrivateKeyParameters.getParameters())), new DEROctetString(sABERPrivateKeyParameters.getEncoded()), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof NTRUPrivateKeyParameters) {
            NTRUPrivateKeyParameters nTRUPrivateKeyParameters = (NTRUPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(Utils.ntruOidLookup(nTRUPrivateKeyParameters.getParameters())), new DEROctetString(nTRUPrivateKeyParameters.getEncoded()), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof FalconPrivateKeyParameters) {
            FalconPrivateKeyParameters falconPrivateKeyParameters = (FalconPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(Utils.falconOidLookup(falconPrivateKeyParameters.getParameters())), new FalconPrivateKey(0, falconPrivateKeyParameters.getSpolyf(), falconPrivateKeyParameters.getG(), falconPrivateKeyParameters.getSpolyF(), new FalconPublicKey(falconPrivateKeyParameters.getPublicKey())), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof MLKEMPrivateKeyParameters) {
            MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters = (MLKEMPrivateKeyParameters) asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier2 = new AlgorithmIdentifier(Utils.mlkemOidLookup(mLKEMPrivateKeyParameters.getParameters()));
            byte[] seed = mLKEMPrivateKeyParameters.getSeed();
            return seed == null ? new PrivateKeyInfo(algorithmIdentifier2, mLKEMPrivateKeyParameters.getEncoded(), aSN1Set) : new PrivateKeyInfo(algorithmIdentifier2, seed, aSN1Set);
        }
        if (asymmetricKeyParameter instanceof NTRULPRimePrivateKeyParameters) {
            NTRULPRimePrivateKeyParameters nTRULPRimePrivateKeyParameters = (NTRULPRimePrivateKeyParameters) asymmetricKeyParameter;
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            aSN1EncodableVector.add(new DEROctetString(nTRULPRimePrivateKeyParameters.getEnca()));
            aSN1EncodableVector.add(new DEROctetString(nTRULPRimePrivateKeyParameters.getPk()));
            aSN1EncodableVector.add(new DEROctetString(nTRULPRimePrivateKeyParameters.getRho()));
            aSN1EncodableVector.add(new DEROctetString(nTRULPRimePrivateKeyParameters.getHash()));
            return new PrivateKeyInfo(new AlgorithmIdentifier(Utils.ntrulprimeOidLookup(nTRULPRimePrivateKeyParameters.getParameters())), new DERSequence(aSN1EncodableVector), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof SNTRUPrimePrivateKeyParameters) {
            SNTRUPrimePrivateKeyParameters sNTRUPrimePrivateKeyParameters = (SNTRUPrimePrivateKeyParameters) asymmetricKeyParameter;
            ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
            aSN1EncodableVector2.add(new DEROctetString(sNTRUPrimePrivateKeyParameters.getF()));
            aSN1EncodableVector2.add(new DEROctetString(sNTRUPrimePrivateKeyParameters.getGinv()));
            aSN1EncodableVector2.add(new DEROctetString(sNTRUPrimePrivateKeyParameters.getPk()));
            aSN1EncodableVector2.add(new DEROctetString(sNTRUPrimePrivateKeyParameters.getRho()));
            aSN1EncodableVector2.add(new DEROctetString(sNTRUPrimePrivateKeyParameters.getHash()));
            return new PrivateKeyInfo(new AlgorithmIdentifier(Utils.sntruprimeOidLookup(sNTRUPrimePrivateKeyParameters.getParameters())), new DERSequence(aSN1EncodableVector2), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof MLDSAPrivateKeyParameters) {
            MLDSAPrivateKeyParameters mLDSAPrivateKeyParameters = (MLDSAPrivateKeyParameters) asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier3 = new AlgorithmIdentifier(Utils.mldsaOidLookup(mLDSAPrivateKeyParameters.getParameters()));
            if (mLDSAPrivateKeyParameters.getSeed() == null) {
                return new PrivateKeyInfo(algorithmIdentifier3, mLDSAPrivateKeyParameters.getEncoded(), aSN1Set, mLDSAPrivateKeyParameters.getPublicKeyParameters().getEncoded());
            }
            mLDSAPrivateKeyParameters.getPublicKeyParameters();
            return new PrivateKeyInfo(algorithmIdentifier3, mLDSAPrivateKeyParameters.getSeed(), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof DilithiumPrivateKeyParameters) {
            DilithiumPrivateKeyParameters dilithiumPrivateKeyParameters = (DilithiumPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(Utils.dilithiumOidLookup(dilithiumPrivateKeyParameters.getParameters())), new DEROctetString(dilithiumPrivateKeyParameters.getEncoded()), aSN1Set, dilithiumPrivateKeyParameters.getPublicKeyParameters().getEncoded());
        }
        if (asymmetricKeyParameter instanceof BIKEPrivateKeyParameters) {
            BIKEPrivateKeyParameters bIKEPrivateKeyParameters = (BIKEPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(Utils.bikeOidLookup(bIKEPrivateKeyParameters.getParameters())), new DEROctetString(bIKEPrivateKeyParameters.getEncoded()), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof HQCPrivateKeyParameters) {
            HQCPrivateKeyParameters hQCPrivateKeyParameters = (HQCPrivateKeyParameters) asymmetricKeyParameter;
            return new PrivateKeyInfo(new AlgorithmIdentifier(Utils.hqcOidLookup(hQCPrivateKeyParameters.getParameters())), new DEROctetString(hQCPrivateKeyParameters.getEncoded()), aSN1Set);
        }
        if (!(asymmetricKeyParameter instanceof RainbowPrivateKeyParameters)) {
            throw new IOException("key parameters not recognized");
        }
        RainbowPrivateKeyParameters rainbowPrivateKeyParameters = (RainbowPrivateKeyParameters) asymmetricKeyParameter;
        return new PrivateKeyInfo(new AlgorithmIdentifier(Utils.rainbowOidLookup(rainbowPrivateKeyParameters.getParameters())), new DEROctetString(rainbowPrivateKeyParameters.getEncoded()), aSN1Set);
    }

    private static XMSSPrivateKey xmssCreateKeyStructure(XMSSPrivateKeyParameters xMSSPrivateKeyParameters) throws IOException {
        byte[] encoded = xMSSPrivateKeyParameters.getEncoded();
        int treeDigestSize = xMSSPrivateKeyParameters.getParameters().getTreeDigestSize();
        int height = xMSSPrivateKeyParameters.getParameters().getHeight();
        int iBytesToXBigEndian = (int) XMSSUtil.bytesToXBigEndian(encoded, 0, 4);
        if (!XMSSUtil.isIndexValid(height, iBytesToXBigEndian)) {
            throw new IllegalArgumentException("index out of bounds");
        }
        int i = 0 + 4;
        byte[] bArrExtractBytesAtOffset = XMSSUtil.extractBytesAtOffset(encoded, i, treeDigestSize);
        int i2 = i + treeDigestSize;
        byte[] bArrExtractBytesAtOffset2 = XMSSUtil.extractBytesAtOffset(encoded, i2, treeDigestSize);
        int i3 = i2 + treeDigestSize;
        byte[] bArrExtractBytesAtOffset3 = XMSSUtil.extractBytesAtOffset(encoded, i3, treeDigestSize);
        int i4 = i3 + treeDigestSize;
        byte[] bArrExtractBytesAtOffset4 = XMSSUtil.extractBytesAtOffset(encoded, i4, treeDigestSize);
        int i5 = i4 + treeDigestSize;
        byte[] bArrExtractBytesAtOffset5 = XMSSUtil.extractBytesAtOffset(encoded, i5, encoded.length - i5);
        try {
            BDS bds = (BDS) XMSSUtil.deserialize(bArrExtractBytesAtOffset5, BDS.class);
            return bds.getMaxIndex() != (1 << height) - 1 ? new XMSSPrivateKey(iBytesToXBigEndian, bArrExtractBytesAtOffset, bArrExtractBytesAtOffset2, bArrExtractBytesAtOffset3, bArrExtractBytesAtOffset4, bArrExtractBytesAtOffset5, bds.getMaxIndex()) : new XMSSPrivateKey(iBytesToXBigEndian, bArrExtractBytesAtOffset, bArrExtractBytesAtOffset2, bArrExtractBytesAtOffset3, bArrExtractBytesAtOffset4, bArrExtractBytesAtOffset5);
        } catch (ClassNotFoundException e) {
            throw new IOException("cannot parse BDS: " + e.getMessage());
        }
    }

    private static XMSSMTPrivateKey xmssmtCreateKeyStructure(XMSSMTPrivateKeyParameters xMSSMTPrivateKeyParameters) throws IOException {
        byte[] encoded = xMSSMTPrivateKeyParameters.getEncoded();
        int treeDigestSize = xMSSMTPrivateKeyParameters.getParameters().getTreeDigestSize();
        int height = xMSSMTPrivateKeyParameters.getParameters().getHeight();
        int i = (height + 7) / 8;
        int iBytesToXBigEndian = (int) XMSSUtil.bytesToXBigEndian(encoded, 0, i);
        if (!XMSSUtil.isIndexValid(height, iBytesToXBigEndian)) {
            throw new IllegalArgumentException("index out of bounds");
        }
        int i2 = 0 + i;
        byte[] bArrExtractBytesAtOffset = XMSSUtil.extractBytesAtOffset(encoded, i2, treeDigestSize);
        int i3 = i2 + treeDigestSize;
        byte[] bArrExtractBytesAtOffset2 = XMSSUtil.extractBytesAtOffset(encoded, i3, treeDigestSize);
        int i4 = i3 + treeDigestSize;
        byte[] bArrExtractBytesAtOffset3 = XMSSUtil.extractBytesAtOffset(encoded, i4, treeDigestSize);
        int i5 = i4 + treeDigestSize;
        byte[] bArrExtractBytesAtOffset4 = XMSSUtil.extractBytesAtOffset(encoded, i5, treeDigestSize);
        int i6 = i5 + treeDigestSize;
        byte[] bArrExtractBytesAtOffset5 = XMSSUtil.extractBytesAtOffset(encoded, i6, encoded.length - i6);
        try {
            BDSStateMap bDSStateMap = (BDSStateMap) XMSSUtil.deserialize(bArrExtractBytesAtOffset5, BDSStateMap.class);
            return bDSStateMap.getMaxIndex() != (1 << height) - 1 ? new XMSSMTPrivateKey(iBytesToXBigEndian, bArrExtractBytesAtOffset, bArrExtractBytesAtOffset2, bArrExtractBytesAtOffset3, bArrExtractBytesAtOffset4, bArrExtractBytesAtOffset5, bDSStateMap.getMaxIndex()) : new XMSSMTPrivateKey(iBytesToXBigEndian, bArrExtractBytesAtOffset, bArrExtractBytesAtOffset2, bArrExtractBytesAtOffset3, bArrExtractBytesAtOffset4, bArrExtractBytesAtOffset5);
        } catch (ClassNotFoundException e) {
            throw new IOException("cannot parse BDSStateMap: " + e.getMessage());
        }
    }
}
