package com.ctc.wstx.shaded.msv_core.verifier.identity;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/identity/KeyValue.class */
class KeyValue {
    public final Object[] values;
    public final Locator locator;

    KeyValue(Object[] values, Locator loc) {
        this.values = values;
        if (loc != null) {
            this.locator = new LocatorImpl(loc);
        } else {
            this.locator = null;
        }
    }

    public int hashCode() {
        int code = 0;
        for (int i = 0; i < this.values.length; i++) {
            code ^= this.values[i].hashCode();
        }
        return code;
    }

    public boolean equals(Object o) {
        if (!(o instanceof KeyValue)) {
            return false;
        }
        KeyValue rhs = (KeyValue) o;
        if (this.values.length != rhs.values.length) {
            return false;
        }
        for (int i = 0; i < this.values.length; i++) {
            if (!this.values[i].equals(rhs.values[i])) {
                return false;
            }
        }
        return true;
    }
}
