package org.spongycastle.cms;

import java.util.Map;
import org.spongycastle.asn1.cms.AttributeTable;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/SimpleAttributeTableGenerator.class */
public class SimpleAttributeTableGenerator implements CMSAttributeTableGenerator {
    private final AttributeTable attributes;

    public SimpleAttributeTableGenerator(AttributeTable attributes) {
        this.attributes = attributes;
    }

    @Override // org.spongycastle.cms.CMSAttributeTableGenerator
    public AttributeTable getAttributes(Map parameters) {
        return this.attributes;
    }
}
