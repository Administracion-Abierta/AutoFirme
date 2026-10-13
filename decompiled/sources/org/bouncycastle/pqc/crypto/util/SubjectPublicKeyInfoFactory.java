package org.bouncycastle.pqc.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.internal.asn1.isara.IsaraObjectIdentifiers;
import org.bouncycastle.pqc.asn1.McElieceCCA2PublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTPublicKey;
import org.bouncycastle.pqc.asn1.XMSSPublicKey;
import org.bouncycastle.pqc.crypto.bike.BIKEPublicKeyParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEPublicKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPublicKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPublicKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.Composer;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPublicKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPublicKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;
import org.bouncycastle.pqc.legacy.crypto.mceliece.McElieceCCA2PublicKeyParameters;
import org.bouncycastle.pqc.legacy.crypto.qtesla.QTESLAPublicKeyParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/util/SubjectPublicKeyInfoFactory.class */
public class SubjectPublicKeyInfoFactory {
    private SubjectPublicKeyInfoFactory() {
    }

    public static SubjectPublicKeyInfo createSubjectPublicKeyInfo(AsymmetricKeyParameter asymmetricKeyParameter) throws IOException {
        if (asymmetricKeyParameter instanceof QTESLAPublicKeyParameters) {
            QTESLAPublicKeyParameters qTESLAPublicKeyParameters = (QTESLAPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(Utils.qTeslaLookupAlgID(qTESLAPublicKeyParameters.getSecurityCategory()), qTESLAPublicKeyParameters.getPublicData());
        }
        if (asymmetricKeyParameter instanceof SPHINCSPublicKeyParameters) {
            SPHINCSPublicKeyParameters sPHINCSPublicKeyParameters = (SPHINCSPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(Utils.sphincs256LookupTreeAlgID(sPHINCSPublicKeyParameters.getTreeDigest()))), sPHINCSPublicKeyParameters.getKeyData());
        }
        if (asymmetricKeyParameter instanceof NHPublicKeyParameters) {
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.newHope), ((NHPublicKeyParameters) asymmetricKeyParameter).getPubData());
        }
        if (asymmetricKeyParameter instanceof LMSPublicKeyParameters) {
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig), Composer.compose().u32str(1).bytes((LMSPublicKeyParameters) asymmetricKeyParameter).build());
        }
        if (asymmetricKeyParameter instanceof HSSPublicKeyParameters) {
            HSSPublicKeyParameters hSSPublicKeyParameters = (HSSPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig), Composer.compose().u32str(hSSPublicKeyParameters.getL()).bytes(hSSPublicKeyParameters.getLMSPublicKey()).build());
        }
        if (asymmetricKeyParameter instanceof SLHDSAPublicKeyParameters) {
            SLHDSAPublicKeyParameters sLHDSAPublicKeyParameters = (SLHDSAPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.slhdsaOidLookup(sLHDSAPublicKeyParameters.getParameters())), sLHDSAPublicKeyParameters.getEncoded());
        }
        if (asymmetricKeyParameter instanceof SPHINCSPlusPublicKeyParameters) {
            SPHINCSPlusPublicKeyParameters sPHINCSPlusPublicKeyParameters = (SPHINCSPlusPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.sphincsPlusOidLookup(sPHINCSPlusPublicKeyParameters.getParameters())), sPHINCSPlusPublicKeyParameters.getEncoded());
        }
        if (asymmetricKeyParameter instanceof CMCEPublicKeyParameters) {
            CMCEPublicKeyParameters cMCEPublicKeyParameters = (CMCEPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.mcElieceOidLookup(cMCEPublicKeyParameters.getParameters())), cMCEPublicKeyParameters.getEncoded());
        }
        if (asymmetricKeyParameter instanceof XMSSPublicKeyParameters) {
            XMSSPublicKeyParameters xMSSPublicKeyParameters = (XMSSPublicKeyParameters) asymmetricKeyParameter;
            byte[] publicSeed = xMSSPublicKeyParameters.getPublicSeed();
            byte[] root = xMSSPublicKeyParameters.getRoot();
            byte[] encoded = xMSSPublicKeyParameters.getEncoded();
            return encoded.length > publicSeed.length + root.length ? new SubjectPublicKeyInfo(new AlgorithmIdentifier(IsaraObjectIdentifiers.id_alg_xmss), new DEROctetString(encoded)) : new SubjectPublicKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.xmss, new XMSSKeyParams(xMSSPublicKeyParameters.getParameters().getHeight(), Utils.xmssLookupTreeAlgID(xMSSPublicKeyParameters.getTreeDigest()))), new XMSSPublicKey(publicSeed, root));
        }
        if (asymmetricKeyParameter instanceof XMSSMTPublicKeyParameters) {
            XMSSMTPublicKeyParameters xMSSMTPublicKeyParameters = (XMSSMTPublicKeyParameters) asymmetricKeyParameter;
            byte[] publicSeed2 = xMSSMTPublicKeyParameters.getPublicSeed();
            byte[] root2 = xMSSMTPublicKeyParameters.getRoot();
            byte[] encoded2 = xMSSMTPublicKeyParameters.getEncoded();
            return encoded2.length > publicSeed2.length + root2.length ? new SubjectPublicKeyInfo(new AlgorithmIdentifier(IsaraObjectIdentifiers.id_alg_xmssmt), new DEROctetString(encoded2)) : new SubjectPublicKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt, new XMSSMTKeyParams(xMSSMTPublicKeyParameters.getParameters().getHeight(), xMSSMTPublicKeyParameters.getParameters().getLayers(), Utils.xmssLookupTreeAlgID(xMSSMTPublicKeyParameters.getTreeDigest()))), new XMSSMTPublicKey(xMSSMTPublicKeyParameters.getPublicSeed(), xMSSMTPublicKeyParameters.getRoot()));
        }
        if (asymmetricKeyParameter instanceof McElieceCCA2PublicKeyParameters) {
            McElieceCCA2PublicKeyParameters mcElieceCCA2PublicKeyParameters = (McElieceCCA2PublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.mcElieceCca2), new McElieceCCA2PublicKey(mcElieceCCA2PublicKeyParameters.getN(), mcElieceCCA2PublicKeyParameters.getT(), mcElieceCCA2PublicKeyParameters.getG(), Utils.getAlgorithmIdentifier(mcElieceCCA2PublicKeyParameters.getDigest())));
        }
        if (asymmetricKeyParameter instanceof FrodoPublicKeyParameters) {
            FrodoPublicKeyParameters frodoPublicKeyParameters = (FrodoPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.frodoOidLookup(frodoPublicKeyParameters.getParameters())), new DEROctetString(frodoPublicKeyParameters.getEncoded()));
        }
        if (asymmetricKeyParameter instanceof SABERPublicKeyParameters) {
            SABERPublicKeyParameters sABERPublicKeyParameters = (SABERPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.saberOidLookup(sABERPublicKeyParameters.getParameters())), new DERSequence(new DEROctetString(sABERPublicKeyParameters.getEncoded())));
        }
        if (asymmetricKeyParameter instanceof PicnicPublicKeyParameters) {
            PicnicPublicKeyParameters picnicPublicKeyParameters = (PicnicPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.picnicOidLookup(picnicPublicKeyParameters.getParameters())), new DEROctetString(picnicPublicKeyParameters.getEncoded()));
        }
        if (asymmetricKeyParameter instanceof NTRUPublicKeyParameters) {
            NTRUPublicKeyParameters nTRUPublicKeyParameters = (NTRUPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.ntruOidLookup(nTRUPublicKeyParameters.getParameters())), nTRUPublicKeyParameters.getEncoded());
        }
        if (asymmetricKeyParameter instanceof FalconPublicKeyParameters) {
            FalconPublicKeyParameters falconPublicKeyParameters = (FalconPublicKeyParameters) asymmetricKeyParameter;
            byte[] h = falconPublicKeyParameters.getH();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.falconOidLookup(falconPublicKeyParameters.getParameters()));
            byte[] bArr = new byte[h.length + 1];
            bArr[0] = (byte) (0 + falconPublicKeyParameters.getParameters().getLogN());
            System.arraycopy(h, 0, bArr, 1, h.length);
            return new SubjectPublicKeyInfo(algorithmIdentifier, bArr);
        }
        if (asymmetricKeyParameter instanceof MLKEMPublicKeyParameters) {
            MLKEMPublicKeyParameters mLKEMPublicKeyParameters = (MLKEMPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.mlkemOidLookup(mLKEMPublicKeyParameters.getParameters())), mLKEMPublicKeyParameters.getEncoded());
        }
        if (asymmetricKeyParameter instanceof NTRULPRimePublicKeyParameters) {
            NTRULPRimePublicKeyParameters nTRULPRimePublicKeyParameters = (NTRULPRimePublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.ntrulprimeOidLookup(nTRULPRimePublicKeyParameters.getParameters())), new DEROctetString(nTRULPRimePublicKeyParameters.getEncoded()));
        }
        if (asymmetricKeyParameter instanceof SNTRUPrimePublicKeyParameters) {
            SNTRUPrimePublicKeyParameters sNTRUPrimePublicKeyParameters = (SNTRUPrimePublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.sntruprimeOidLookup(sNTRUPrimePublicKeyParameters.getParameters())), new DEROctetString(sNTRUPrimePublicKeyParameters.getEncoded()));
        }
        if (asymmetricKeyParameter instanceof DilithiumPublicKeyParameters) {
            DilithiumPublicKeyParameters dilithiumPublicKeyParameters = (DilithiumPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.dilithiumOidLookup(dilithiumPublicKeyParameters.getParameters())), dilithiumPublicKeyParameters.getEncoded());
        }
        if (asymmetricKeyParameter instanceof MLDSAPublicKeyParameters) {
            MLDSAPublicKeyParameters mLDSAPublicKeyParameters = (MLDSAPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.mldsaOidLookup(mLDSAPublicKeyParameters.getParameters())), mLDSAPublicKeyParameters.getEncoded());
        }
        if (asymmetricKeyParameter instanceof BIKEPublicKeyParameters) {
            BIKEPublicKeyParameters bIKEPublicKeyParameters = (BIKEPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.bikeOidLookup(bIKEPublicKeyParameters.getParameters())), bIKEPublicKeyParameters.getEncoded());
        }
        if (asymmetricKeyParameter instanceof HQCPublicKeyParameters) {
            HQCPublicKeyParameters hQCPublicKeyParameters = (HQCPublicKeyParameters) asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.hqcOidLookup(hQCPublicKeyParameters.getParameters())), hQCPublicKeyParameters.getEncoded());
        }
        if (!(asymmetricKeyParameter instanceof RainbowPublicKeyParameters)) {
            throw new IOException("key parameters not recognized");
        }
        RainbowPublicKeyParameters rainbowPublicKeyParameters = (RainbowPublicKeyParameters) asymmetricKeyParameter;
        return new SubjectPublicKeyInfo(new AlgorithmIdentifier(Utils.rainbowOidLookup(rainbowPublicKeyParameters.getParameters())), new DEROctetString(rainbowPublicKeyParameters.getEncoded()));
    }
}
