package org.spongycastle.cms;

import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/PasswordRecipient.class */
public interface PasswordRecipient extends Recipient {
    public static final int PKCS5_SCHEME2 = 0;
    public static final int PKCS5_SCHEME2_UTF8 = 1;

    byte[] calculateDerivedKey(int i, AlgorithmIdentifier algorithmIdentifier, int i2) throws CMSException;

    RecipientOperator getRecipientOperator(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, byte[] bArr, byte[] bArr2) throws CMSException;

    int getPasswordConversionScheme();

    char[] getPassword();

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/PasswordRecipient$PRF.class */
    public static final class PRF {
        public static final PRF HMacSHA1 = new PRF("HMacSHA1", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, DERNull.INSTANCE));
        public static final PRF HMacSHA224 = new PRF("HMacSHA224", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA224, DERNull.INSTANCE));
        public static final PRF HMacSHA256 = new PRF("HMacSHA256", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE));
        public static final PRF HMacSHA384 = new PRF("HMacSHA384", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA384, DERNull.INSTANCE));
        public static final PRF HMacSHA512 = new PRF("HMacSHA512", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE));
        private final String hmac;
        final AlgorithmIdentifier prfAlgID;

        private PRF(String hmac, AlgorithmIdentifier prfAlgID) {
            this.hmac = hmac;
            this.prfAlgID = prfAlgID;
        }

        public String getName() {
            return this.hmac;
        }

        public AlgorithmIdentifier getAlgorithmID() {
            return this.prfAlgID;
        }
    }
}
