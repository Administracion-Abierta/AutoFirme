package org.spongycastle.cms.bc;

import org.spongycastle.asn1.cms.KEKIdentifier;
import org.spongycastle.cms.KEKRecipientInfoGenerator;
import org.spongycastle.operator.bc.BcSymmetricKeyWrapper;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/bc/BcKEKRecipientInfoGenerator.class */
public class BcKEKRecipientInfoGenerator extends KEKRecipientInfoGenerator {
    public BcKEKRecipientInfoGenerator(KEKIdentifier kekIdentifier, BcSymmetricKeyWrapper kekWrapper) {
        super(kekIdentifier, kekWrapper);
    }

    public BcKEKRecipientInfoGenerator(byte[] keyIdentifier, BcSymmetricKeyWrapper kekWrapper) {
        this(new KEKIdentifier(keyIdentifier, null, null), kekWrapper);
    }
}
