package org.spongycastle.cms;

import java.util.Date;
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
import org.spongycastle.asn1.cms.Time;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/DefaultSignedAttributeTableGenerator.class */
public class DefaultSignedAttributeTableGenerator implements CMSAttributeTableGenerator {
    private final Hashtable table;

    public DefaultSignedAttributeTableGenerator() {
        this.table = new Hashtable();
    }

    public DefaultSignedAttributeTableGenerator(AttributeTable attributeTable) {
        if (attributeTable != null) {
            this.table = attributeTable.toHashtable();
        } else {
            this.table = new Hashtable();
        }
    }

    protected Hashtable createStandardAttributeTable(Map parameters) {
        ASN1ObjectIdentifier contentType;
        Hashtable std = copyHashTable(this.table);
        if (!std.containsKey(CMSAttributes.contentType) && (contentType = ASN1ObjectIdentifier.getInstance(parameters.get(CMSAttributeTableGenerator.CONTENT_TYPE))) != null) {
            Attribute attr = new Attribute(CMSAttributes.contentType, new DERSet(contentType));
            std.put(attr.getAttrType(), attr);
        }
        if (!std.containsKey(CMSAttributes.signingTime)) {
            Date signingTime = new Date();
            Attribute attr2 = new Attribute(CMSAttributes.signingTime, new DERSet(new Time(signingTime)));
            std.put(attr2.getAttrType(), attr2);
        }
        if (!std.containsKey(CMSAttributes.messageDigest)) {
            byte[] messageDigest = (byte[]) parameters.get(CMSAttributeTableGenerator.DIGEST);
            Attribute attr3 = new Attribute(CMSAttributes.messageDigest, new DERSet(new DEROctetString(messageDigest)));
            std.put(attr3.getAttrType(), attr3);
        }
        if (!std.contains(CMSAttributes.cmsAlgorithmProtect)) {
            Attribute attr4 = new Attribute(CMSAttributes.cmsAlgorithmProtect, new DERSet(new CMSAlgorithmProtection((AlgorithmIdentifier) parameters.get(CMSAttributeTableGenerator.DIGEST_ALGORITHM_IDENTIFIER), 1, (AlgorithmIdentifier) parameters.get(CMSAttributeTableGenerator.SIGNATURE_ALGORITHM_IDENTIFIER))));
            std.put(attr4.getAttrType(), attr4);
        }
        return std;
    }

    @Override // org.spongycastle.cms.CMSAttributeTableGenerator
    public AttributeTable getAttributes(Map parameters) {
        return new AttributeTable(createStandardAttributeTable(parameters));
    }

    private static Hashtable copyHashTable(Hashtable paramsMap) {
        Hashtable newTable = new Hashtable();
        Enumeration keys = paramsMap.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            newTable.put(key, paramsMap.get(key));
        }
        return newTable;
    }
}
