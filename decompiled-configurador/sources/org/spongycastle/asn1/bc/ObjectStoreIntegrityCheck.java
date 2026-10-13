package org.spongycastle.asn1.bc;

import java.io.IOException;
import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/bc/ObjectStoreIntegrityCheck.class */
public class ObjectStoreIntegrityCheck extends ASN1Object implements ASN1Choice {
    public static final int PBKD_MAC_CHECK = 0;
    private final int type;
    private final ASN1Object integrityCheck;

    public ObjectStoreIntegrityCheck(PbkdMacIntegrityCheck macIntegrityCheck) {
        this((ASN1Encodable) macIntegrityCheck);
    }

    private ObjectStoreIntegrityCheck(ASN1Encodable obj) {
        if ((obj instanceof ASN1Sequence) || (obj instanceof PbkdMacIntegrityCheck)) {
            this.type = 0;
            this.integrityCheck = PbkdMacIntegrityCheck.getInstance(obj);
            return;
        }
        throw new IllegalArgumentException("Unknown check object in integrity check.");
    }

    public static ObjectStoreIntegrityCheck getInstance(Object o) {
        if (o instanceof ObjectStoreIntegrityCheck) {
            return (ObjectStoreIntegrityCheck) o;
        }
        if (o instanceof byte[]) {
            try {
                return new ObjectStoreIntegrityCheck(ASN1Primitive.fromByteArray((byte[]) o));
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to parse integrity check details.");
            }
        }
        if (o != null) {
            return new ObjectStoreIntegrityCheck((ASN1Encodable) o);
        }
        return null;
    }

    public int getType() {
        return this.type;
    }

    public ASN1Object getIntegrityCheck() {
        return this.integrityCheck;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.integrityCheck.toASN1Primitive();
    }
}
