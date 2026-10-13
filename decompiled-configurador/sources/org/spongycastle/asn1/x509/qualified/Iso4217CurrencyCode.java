package org.spongycastle.asn1.x509.qualified;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator;
import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERPrintableString;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x509/qualified/Iso4217CurrencyCode.class */
public class Iso4217CurrencyCode extends ASN1Object implements ASN1Choice {
    final int ALPHABETIC_MAXSIZE = 3;
    final int NUMERIC_MINSIZE = 1;
    final int NUMERIC_MAXSIZE = Comparator.UNDECIDABLE;
    ASN1Encodable obj;
    int numeric;

    public static Iso4217CurrencyCode getInstance(Object obj) {
        if (obj == null || (obj instanceof Iso4217CurrencyCode)) {
            return (Iso4217CurrencyCode) obj;
        }
        if (obj instanceof ASN1Integer) {
            ASN1Integer numericobj = ASN1Integer.getInstance(obj);
            int numeric = numericobj.getValue().intValue();
            return new Iso4217CurrencyCode(numeric);
        }
        if (obj instanceof DERPrintableString) {
            DERPrintableString alphabetic = DERPrintableString.getInstance(obj);
            return new Iso4217CurrencyCode(alphabetic.getString());
        }
        throw new IllegalArgumentException("unknown object in getInstance");
    }

    public Iso4217CurrencyCode(int numeric) {
        if (numeric > 999 || numeric < 1) {
            throw new IllegalArgumentException("wrong size in numeric code : not in (1..999)");
        }
        this.obj = new ASN1Integer(numeric);
    }

    public Iso4217CurrencyCode(String alphabetic) {
        if (alphabetic.length() > 3) {
            throw new IllegalArgumentException("wrong size in alphabetic code : max size is 3");
        }
        this.obj = new DERPrintableString(alphabetic);
    }

    public boolean isAlphabetic() {
        return this.obj instanceof DERPrintableString;
    }

    public String getAlphabetic() {
        return ((DERPrintableString) this.obj).getString();
    }

    public int getNumeric() {
        return ((ASN1Integer) this.obj).getValue().intValue();
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.obj.toASN1Primitive();
    }
}
