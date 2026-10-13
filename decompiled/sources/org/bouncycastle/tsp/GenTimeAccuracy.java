package org.bouncycastle.tsp;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.tsp.Accuracy;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/GenTimeAccuracy.class */
public class GenTimeAccuracy {
    private Accuracy accuracy;

    public GenTimeAccuracy(Accuracy accuracy) {
        this.accuracy = accuracy;
    }

    public int getSeconds() {
        return getTimeComponent(this.accuracy.getSeconds());
    }

    public int getMillis() {
        return getTimeComponent(this.accuracy.getMillis());
    }

    public int getMicros() {
        return getTimeComponent(this.accuracy.getMicros());
    }

    private int getTimeComponent(ASN1Integer aSN1Integer) {
        if (aSN1Integer != null) {
            return aSN1Integer.intValueExact();
        }
        return 0;
    }

    public String toString() {
        return getSeconds() + "." + format(getMillis()) + format(getMicros());
    }

    private String format(int i) {
        if (i < 10) {
            return "00" + i;
        }
        return i < 100 ? "0" + i : Integer.toString(i);
    }
}
