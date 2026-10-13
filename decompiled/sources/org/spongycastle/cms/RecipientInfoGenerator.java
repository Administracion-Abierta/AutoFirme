package org.spongycastle.cms;

import org.spongycastle.asn1.cms.RecipientInfo;
import org.spongycastle.operator.GenericKey;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/RecipientInfoGenerator.class */
public interface RecipientInfoGenerator {
    RecipientInfo generate(GenericKey genericKey) throws CMSException;
}
