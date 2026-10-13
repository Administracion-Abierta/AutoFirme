package org.bouncycastle.its;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.its.operator.ETSIDataEncryptor;
import org.bouncycastle.oer.its.ieee1609dot2.AesCcmCiphertext;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedData;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.SymmetricCiphertext;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/ETSIEncryptedDataBuilder.class */
public class ETSIEncryptedDataBuilder {
    private final List<ETSIRecipientInfoBuilder> recipientInfoBuilders = new ArrayList();

    public void addRecipientInfoBuilder(ETSIRecipientInfoBuilder eTSIRecipientInfoBuilder) {
        this.recipientInfoBuilders.add(eTSIRecipientInfoBuilder);
    }

    public ETSIEncryptedData build(ETSIDataEncryptor eTSIDataEncryptor, byte[] bArr) {
        byte[] bArrEncrypt = eTSIDataEncryptor.encrypt(bArr);
        byte[] key = eTSIDataEncryptor.getKey();
        byte[] nonce = eTSIDataEncryptor.getNonce();
        SequenceOfRecipientInfo.Builder builder = SequenceOfRecipientInfo.builder();
        Iterator<ETSIRecipientInfoBuilder> it = this.recipientInfoBuilders.iterator();
        while (it.hasNext()) {
            builder.addRecipients(it.next().build(key));
        }
        return new ETSIEncryptedData(EncryptedData.builder().setRecipients(builder.createSequenceOfRecipientInfo()).setCiphertext(SymmetricCiphertext.aes128ccm(AesCcmCiphertext.builder().setCcmCiphertext(bArrEncrypt).setNonce(nonce).createAesCcmCiphertext())).createEncryptedData());
    }
}
