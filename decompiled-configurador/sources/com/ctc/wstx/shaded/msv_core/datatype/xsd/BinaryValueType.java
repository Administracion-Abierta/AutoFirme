package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/BinaryValueType.class */
class BinaryValueType implements Serializable {
    public byte[] rawData;
    private static final long serialVersionUID = -2609017982625895534L;

    public boolean equals(Object o) {
        if (o == null || o.getClass() != BinaryValueType.class) {
            return false;
        }
        BinaryValueType rhs = (BinaryValueType) o;
        if (this.rawData.length != rhs.rawData.length) {
            return false;
        }
        int len = this.rawData.length;
        for (int i = 0; i < len; i++) {
            if (this.rawData[i] != rhs.rawData[i]) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        if (this.rawData.length == 0) {
            return 293;
        }
        return this.rawData.length == 1 ? this.rawData[0] : this.rawData.length * this.rawData[0] * this.rawData[1];
    }

    public BinaryValueType(byte[] rawData) {
        this.rawData = rawData;
    }
}
