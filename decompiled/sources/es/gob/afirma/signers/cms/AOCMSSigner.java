package es.gob.afirma.signers.cms;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSignInfo;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.signers.cades.CAdESExtraParams;
import es.gob.afirma.signers.pkcs7.ObtainContentSignedData;
import es.gob.afirma.signers.pkcs7.P7ContentSignerParameters;
import es.gob.afirma.signers.pkcs7.ReadNodesTree;
import es.gob.afirma.signers.pkcs7.SCChecker;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cms/AOCMSSigner.class */
public final class AOCMSSigner implements AOSigner {
    private String dataType = null;
    private final Map<String, byte[]> atrib = new HashMap();
    private final Map<String, byte[]> uatrib = new HashMap();
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        new SCChecker().checkSpongyCastle();
        Properties properties2 = properties != null ? properties : new Properties();
        String property = properties2.getProperty("precalculatedHashAlgorithm");
        byte[] bArr2 = null;
        if (property != null) {
            bArr2 = bArr;
        }
        P7ContentSignerParameters p7ContentSignerParameters = new P7ContentSignerParameters(bArr, str);
        if (this.dataType == null) {
            this.dataType = PKCSObjectIdentifiers.data.getId();
        }
        try {
            return new GenSignedData().generateSignedData(p7ContentSignerParameters, properties2.getProperty(CAdESExtraParams.MODE, "explicit").equals("explicit") || property != null, Boolean.parseBoolean(properties2.getProperty("applySystemDate", "true")), this.dataType, privateKey, certificateArr, this.atrib, this.uatrib, bArr2);
        } catch (NoSuchAlgorithmException e) {
            throw new AOException("Error en el algoritmo de firma: " + e, e);
        } catch (CertificateException e2) {
            throw new AOException("Error en el certificado de firma: " + e2, e2);
        }
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        new SCChecker().checkSpongyCastle();
        Properties properties2 = properties != null ? properties : new Properties();
        String property = properties2.getProperty("precalculatedHashAlgorithm");
        byte[] bArr3 = null;
        if (property != null) {
            bArr3 = bArr;
        }
        P7ContentSignerParameters p7ContentSignerParameters = new P7ContentSignerParameters(bArr, str);
        if (this.dataType == null) {
            this.dataType = PKCSObjectIdentifiers.data.getId();
        }
        boolean z = properties2.getProperty(CAdESExtraParams.MODE, "explicit").equals("explicit") || property != null;
        if (ValidateCMSSignedData.isCMSSignedData(bArr2)) {
            try {
                return new CoSigner().coSigner(p7ContentSignerParameters, bArr2, z, this.dataType, privateKey, certificateArr, this.atrib, this.uatrib, bArr3);
            } catch (Exception e) {
                throw new AOException("Error generando la Cofirma PKCS#7", e);
            }
        }
        throw new AOException("Los datos no se corresponden con una firma CMS valida");
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        new SCChecker().checkSpongyCastle();
        if (this.dataType == null) {
            this.dataType = PKCSObjectIdentifiers.data.getId();
        }
        if (ValidateCMSSignedData.isCMSSignedData(bArr)) {
            try {
                return new CoSigner().coSigner(str, (X509Certificate[]) certificateArr, bArr, this.dataType, privateKey, this.atrib, this.uatrib, null);
            } catch (Exception e) {
                throw new AOException("Error generando la Cofirma PKCS#7", e);
            }
        }
        throw new AOException("Los datos no se corresponden con una firma CMS valida");
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException {
        new SCChecker().checkSpongyCastle();
        P7ContentSignerParameters p7ContentSignerParameters = new P7ContentSignerParameters(bArr, str);
        if (this.dataType == null) {
            this.dataType = PKCSObjectIdentifiers.data.getId();
        }
        byte[] bArrCounterSigner = null;
        if (ValidateCMSSignedData.isCMSSignedData(bArr)) {
            try {
                if (counterSignTarget == CounterSignTarget.TREE) {
                    bArrCounterSigner = new CounterSigner().counterSigner(p7ContentSignerParameters, bArr, CounterSignTarget.TREE, new int[]{0}, privateKey, certificateArr, this.atrib, this.uatrib);
                } else if (counterSignTarget == CounterSignTarget.LEAFS) {
                    bArrCounterSigner = new CounterSigner().counterSigner(p7ContentSignerParameters, bArr, CounterSignTarget.LEAFS, new int[]{0}, privateKey, certificateArr, this.atrib, this.uatrib);
                } else if (counterSignTarget == CounterSignTarget.NODES) {
                    int[] iArr = new int[objArr.length];
                    for (int i = 0; i < objArr.length; i++) {
                        iArr[i] = ((Integer) objArr[i]).intValue();
                    }
                    bArrCounterSigner = new CounterSigner().counterSigner(p7ContentSignerParameters, bArr, CounterSignTarget.NODES, ReadNodesTree.simplyArray(iArr), privateKey, certificateArr, this.atrib, this.uatrib);
                } else if (counterSignTarget == CounterSignTarget.SIGNERS) {
                    String[] strArr = new String[objArr.length];
                    for (int i2 = 0; i2 < objArr.length; i2++) {
                        strArr[i2] = (String) objArr[i2];
                    }
                    bArrCounterSigner = new CounterSigner().counterSigner(p7ContentSignerParameters, bArr, CounterSignTarget.SIGNERS, new ReadNodesTree().readNodesFromSigners(strArr, bArr), privateKey, certificateArr, this.atrib, this.uatrib);
                }
                return bArrCounterSigner;
            } catch (Exception e) {
                throw new AOException("Error generando la Contrafirma PKCS#7", e);
            }
        }
        throw new AOException("Los datos no se corresponden con una firma CMS valida");
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) throws IOException, AOInvalidFormatException {
        new SCChecker().checkSpongyCastle();
        try {
            return new ReadNodesTree().readNodesTree(bArr, z);
        } catch (Exception e) {
            LOGGER.severe(e.toString());
            return null;
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, boolean z) throws IOException, AOInvalidFormatException {
        return getSignersStructure(bArr, null, z);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr, Properties properties) throws IOException {
        if (bArr == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        return ValidateCMSSignedData.isCMSSignedData(bArr);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) throws IOException {
        return isSign(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isValidDataFile(byte[] bArr) {
        if (bArr == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        return true;
    }

    public void addSignedAttribute(String str, byte[] bArr) {
        this.atrib.put(str, bArr);
    }

    public void addUnsignedAttribute(String str, byte[] bArr) {
        this.uatrib.put(str, bArr);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) throws IOException, AOException {
        if (bArr == null) {
            throw new IllegalArgumentException("Se han introducido datos nulos para su comprobacion");
        }
        if (!ValidateCMSSignedData.isCMSSignedData(bArr)) {
            throw new AOInvalidFormatException("Los datos introducidos no se corresponden con un objeto de firma");
        }
        return ObtainContentSignedData.obtainData(bArr);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr) throws IOException, AOException {
        return getData(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public String getSignedName(String str, String str2) {
        return str + (str2 != null ? str2 : "") + ".csig";
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws IOException, AOException {
        if (bArr == null) {
            throw new IllegalArgumentException("No se han introducido datos para analizar");
        }
        if (!isSign(bArr)) {
            throw new AOInvalidFormatException("Los datos introducidos no se corresponden con un objeto de firma");
        }
        return new AOSignInfo(AOSignConstants.SIGN_FORMAT_CMS);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) throws IOException, AOException {
        return getSignInfo(bArr, null);
    }
}
