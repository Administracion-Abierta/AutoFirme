package org.spongycastle.cms;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.CMSAlgorithmProtection;
import org.spongycastle.asn1.cms.CMSAttributes;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/DefaultAuthenticatedAttributeTableGenerator.class */
public class DefaultAuthenticatedAttributeTableGenerator implements CMSAttributeTableGenerator {
    private final Hashtable table;

    public DefaultAuthenticatedAttributeTableGenerator() {
        this.table = new Hashtable();
    }

    public DefaultAuthenticatedAttributeTableGenerator(AttributeTable attributeTable) {
        if (attributeTable != null) {
            this.table = attributeTable.toHashtable();
        } else {
            this.table = new Hashtable();
        }
    }

    protected Hashtable createStandardAttributeTable(Map parameters) {
        Hashtable std = new Hashtable();
        Enumeration en = this.table.keys();
        while (en.hasMoreElements()) {
            Object key = en.nextElement();
            std.put(key, this.table.get(key));
        }
        if (!std.containsKey(CMSAttributes.contentType)) {
            ASN1ObjectIdentifier contentType = ASN1ObjectIdentifier.getInstance(parameters.get(CMSAttributeTableGenerator.CONTENT_TYPE));
            Attribute attr = new Attribute(CMSAttributes.contentType, new DERSet(contentType));
            std.put(attr.getAttrType(), attr);
        }
        if (!std.containsKey(CMSAttributes.messageDigest)) {
            byte[] messageDigest = (byte[]) parameters.get(CMSAttributeTableGenerator.DIGEST);
            Attribute attr2 = new Attribute(CMSAttributes.messageDigest, new DERSet(new DEROctetString(messageDigest)));
            std.put(attr2.getAttrType(), attr2);
        }
        if (!std.contains(CMSAttributes.cmsAlgorithmProtect)) {
            Attribute attr3 = new Attribute(CMSAttributes.cmsAlgorithmProtect, new DERSet(new CMSAlgorithmProtection((AlgorithmIdentifier) parameters.get(CMSAttributeTableGenerator.DIGEST_ALGORITHM_IDENTIFIER), 2, (AlgorithmIdentifier) parameters.get(CMSAttributeTableGenerator.MAC_ALGORITHM_IDENTIFIER))));
            std.put(attr3.getAttrType(), attr3);
        }
        return std;
    }

    @Override // org.spongycastle.cms.CMSAttributeTableGenerator
    public AttributeTable getAttributes(Map parameters) {
        return new AttributeTable(createStandardAttributeTable(parameters));
    }
}
