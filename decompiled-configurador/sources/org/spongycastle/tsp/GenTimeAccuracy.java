package org.spongycastle.tsp;

import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.tsp.Accuracy;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/tsp/GenTimeAccuracy.class */
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

    private int getTimeComponent(ASN1Integer time) {
        if (time != null) {
            return time.getValue().intValue();
        }
        return 0;
    }

    public String toString() {
        return getSeconds() + "." + format(getMillis()) + format(getMicros());
    }

    private String format(int v) {
        if (v < 10) {
            return "00" + v;
        }
        if (v < 100) {
            return "0" + v;
        }
        return Integer.toString(v);
    }
}
