package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.signvalidation.SignValidity;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignOperationConfig.class */
public final class SignOperationConfig {
    private Properties extraParams;
    private List<SignValidity> signValidity;
    private String invalidSignatureText;
    private String digestAlgorithm;
    private FileType fileType = FileType.BINARY;
    private AOSigner signer = null;
    private String signatureFormatName = null;
    private File dataFile = null;
    private File signatureFile = null;
    private CryptoOperation cryptoOperation = CryptoOperation.SIGN;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignOperationConfig$CryptoOperation.class */
    public enum CryptoOperation {
        SIGN,
        COSIGN,
        COUNTERSIGN_LEAFS,
        COUNTERSIGN_TREE
    }

    public FileType getFileType() {
        return this.fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public AOSigner getSigner() {
        return this.signer;
    }

    public void setSigner(AOSigner aOSigner) {
        this.signer = aOSigner;
    }

    public Properties getExtraParams() {
        return this.extraParams;
    }

    public void setExtraParams(Properties properties) {
        this.extraParams = properties != null ? properties : new Properties();
    }

    public void addExtraParams(Properties properties) {
        if (properties == null) {
            return;
        }
        if (this.extraParams == null) {
            this.extraParams = new Properties();
        }
        this.extraParams.putAll(properties);
    }

    public void addExtraParam(String str, String str2) {
        if (str == null || str2 == null) {
            Logger.getLogger("es.gob.afirma").warning("No se pueden establecer parametros adicionales nulos");
        }
        if (this.extraParams == null) {
            this.extraParams = new Properties();
        }
        this.extraParams.put(str, str2);
    }

    public String getSignatureFormatName() {
        return this.signatureFormatName;
    }

    public void setSignatureFormatName(String str) {
        this.signatureFormatName = str;
    }

    public List<SignValidity> getSignValidity() {
        return this.signValidity;
    }

    public void setSignValidity(List<SignValidity> list) {
        this.signValidity = list;
    }

    public String getInvalidSignatureText() {
        return this.invalidSignatureText;
    }

    public void setInvalidSignatureText(String str) {
        this.invalidSignatureText = str;
    }

    public File getDataFile() {
        return this.dataFile;
    }

    public void setDataFile(File file) {
        this.dataFile = file;
    }

    public File getSignatureFile() {
        return this.signatureFile;
    }

    public void setSignatureFile(File file) {
        this.signatureFile = file;
    }

    public CryptoOperation getCryptoOperation() {
        return this.cryptoOperation;
    }

    public void setCryptoOperation(CryptoOperation cryptoOperation) {
        this.cryptoOperation = cryptoOperation;
    }

    public String getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public void setDigestAlgorithm(String str) {
        this.digestAlgorithm = str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public SignOperationConfig m750clone() {
        SignOperationConfig signOperationConfig = new SignOperationConfig();
        signOperationConfig.setFileType(this.fileType);
        signOperationConfig.setDataFile(this.dataFile);
        signOperationConfig.setSignatureFile(this.signatureFile);
        signOperationConfig.setCryptoOperation(this.cryptoOperation);
        signOperationConfig.setSigner(this.signer);
        signOperationConfig.setExtraParams(this.extraParams != null ? (Properties) this.extraParams.clone() : null);
        signOperationConfig.setSignatureFormatName(this.signatureFormatName);
        signOperationConfig.setSignValidity(this.signValidity != null ? Collections.unmodifiableList(this.signValidity) : null);
        signOperationConfig.setInvalidSignatureText(this.invalidSignatureText);
        signOperationConfig.setDigestAlgorithm(this.digestAlgorithm);
        return signOperationConfig;
    }
}
