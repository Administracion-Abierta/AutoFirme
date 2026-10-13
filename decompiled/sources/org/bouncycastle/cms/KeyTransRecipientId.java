package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/KeyTransRecipientId.class */
public class KeyTransRecipientId extends PKIXRecipientId {
    private KeyTransRecipientId(X509CertificateHolderSelector x509CertificateHolderSelector) {
        super(0, x509CertificateHolderSelector);
    }

    public KeyTransRecipientId(byte[] bArr) {
        super(0, null, null, bArr);
    }

    public KeyTransRecipientId(X500Name x500Name, BigInteger bigInteger) {
        super(0, x500Name, bigInteger, null);
    }

    public KeyTransRecipientId(X500Name x500Name, BigInteger bigInteger, byte[] bArr) {
        super(0, x500Name, bigInteger, bArr);
    }

    @Override // org.bouncycastle.cms.PKIXRecipientId
    public boolean equals(Object obj) {
        if (obj instanceof KeyTransRecipientId) {
            return this.baseSelector.equals(((KeyTransRecipientId) obj).baseSelector);
        }
        return false;
    }

    @Override // org.bouncycastle.cms.PKIXRecipientId, org.bouncycastle.cms.RecipientId, org.bouncycastle.util.Selector
    public Object clone() {
        return new KeyTransRecipientId(this.baseSelector);
    }

    @Override // org.bouncycastle.cms.PKIXRecipientId, org.bouncycastle.util.Selector
    public boolean match(Object obj) {
        return obj instanceof KeyTransRecipientInformation ? ((KeyTransRecipientInformation) obj).getRID().equals(this) : super.match(obj);
    }
}
