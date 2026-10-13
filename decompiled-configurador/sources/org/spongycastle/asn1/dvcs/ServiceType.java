package org.spongycastle.asn1.dvcs;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1Enumerated;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1TaggedObject;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/dvcs/ServiceType.class */
public class ServiceType extends ASN1Object {
    public static final ServiceType CPD = new ServiceType(1);
    public static final ServiceType VSD = new ServiceType(2);
    public static final ServiceType VPKC = new ServiceType(3);
    public static final ServiceType CCPD = new ServiceType(4);
    private ASN1Enumerated value;

    public ServiceType(int value) {
        this.value = new ASN1Enumerated(value);
    }

    private ServiceType(ASN1Enumerated value) {
        this.value = value;
    }

    public static ServiceType getInstance(Object obj) {
        if (obj instanceof ServiceType) {
            return (ServiceType) obj;
        }
        if (obj != null) {
            return new ServiceType(ASN1Enumerated.getInstance(obj));
        }
        return null;
    }

    public static ServiceType getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Enumerated.getInstance(obj, explicit));
    }

    public BigInteger getValue() {
        return this.value.getValue();
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.value;
    }

    public String toString() {
        String str;
        int num = this.value.getValue().intValue();
        StringBuilder sbAppend = new StringBuilder().append("").append(num);
        if (num == CPD.getValue().intValue()) {
            str = "(CPD)";
        } else if (num == VSD.getValue().intValue()) {
            str = "(VSD)";
        } else if (num == VPKC.getValue().intValue()) {
            str = "(VPKC)";
        } else {
            str = num == CCPD.getValue().intValue() ? "(CCPD)" : "?";
        }
        return sbAppend.append(str).toString();
    }
}
