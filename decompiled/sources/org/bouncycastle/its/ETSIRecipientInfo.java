package org.bouncycastle.its;

import org.bouncycastle.its.operator.ETSIDataDecryptor;
import org.bouncycastle.oer.its.ieee1609dot2.AesCcmCiphertext;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedData;
import org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EciesP256EncryptedKey;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/ETSIRecipientInfo.class */
public class ETSIRecipientInfo {
    private final RecipientInfo recipientInfo;
    private final EncryptedData encryptedData;

    public ETSIRecipientInfo(EncryptedData encryptedData, RecipientInfo recipientInfo) {
        this.recipientInfo = recipientInfo;
        this.encryptedData = encryptedData;
    }

    public ETSIRecipientInfo(RecipientInfo recipientInfo) {
        this.recipientInfo = recipientInfo;
        this.encryptedData = null;
    }

    public RecipientInfo getRecipientInfo() {
        return this.recipientInfo;
    }

    public EncryptedData getEncryptedData() {
        return this.encryptedData;
    }

    public byte[] getContent(ETSIDataDecryptor eTSIDataDecryptor) {
        if (0 != this.encryptedData.getCiphertext().getChoice()) {
            throw new IllegalArgumentException("Encrypted data is no AES 128 CCM");
        }
        AesCcmCiphertext aesCcmCiphertext = AesCcmCiphertext.getInstance(this.encryptedData.getCiphertext().getSymmetricCiphertext());
        EciesP256EncryptedKey eciesP256EncryptedKey = EciesP256EncryptedKey.getInstance(PKRecipientInfo.getInstance(this.recipientInfo.getRecipientInfo()).getEncKey().getEncryptedDataEncryptionKey());
        return eTSIDataDecryptor.decrypt(Arrays.concatenate(EccP256CurvePoint.getInstance(eciesP256EncryptedKey.getV()).getEncodedPoint(), eciesP256EncryptedKey.getC().getOctets(), eciesP256EncryptedKey.getT().getOctets()), aesCcmCiphertext.getCcmCiphertext().getContent(), aesCcmCiphertext.getNonce().getOctets());
    }
}
