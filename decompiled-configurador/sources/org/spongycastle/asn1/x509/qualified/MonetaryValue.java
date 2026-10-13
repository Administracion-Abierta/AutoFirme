package org.spongycastle.asn1.x509.qualified;

import java.math.BigInteger;
import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x509/qualified/MonetaryValue.class */
public class MonetaryValue extends ASN1Object {
    private Iso4217CurrencyCode currency;
    private ASN1Integer amount;
    private ASN1Integer exponent;

    public static MonetaryValue getInstance(Object obj) {
        if (obj instanceof MonetaryValue) {
            return (MonetaryValue) obj;
        }
        if (obj != null) {
            return new MonetaryValue(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private MonetaryValue(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.currency = Iso4217CurrencyCode.getInstance(e.nextElement());
        this.amount = ASN1Integer.getInstance(e.nextElement());
        this.exponent = ASN1Integer.getInstance(e.nextElement());
    }

    public MonetaryValue(Iso4217CurrencyCode currency, int amount, int exponent) {
        this.currency = currency;
        this.amount = new ASN1Integer(amount);
        this.exponent = new ASN1Integer(exponent);
    }

    public Iso4217CurrencyCode getCurrency() {
        return this.currency;
    }

    public BigInteger getAmount() {
        return this.amount.getValue();
    }

    public BigInteger getExponent() {
        return this.exponent.getValue();
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector seq = new ASN1EncodableVector();
        seq.add(this.currency);
        seq.add(this.amount);
        seq.add(this.exponent);
        return new DERSequence(seq);
    }
}
