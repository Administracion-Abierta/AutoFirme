package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/QnameValueType.class */
public class QnameValueType implements Serializable {
    public final String namespaceURI;
    public final String localPart;
    private static final long serialVersionUID = 1;

    public boolean equals(Object o) {
        if (o == null || !(o instanceof QnameValueType)) {
            return false;
        }
        QnameValueType rhs = (QnameValueType) o;
        return this.namespaceURI.equals(rhs.namespaceURI) && this.localPart.equals(rhs.localPart);
    }

    public int hashCode() {
        return this.namespaceURI.hashCode() + this.localPart.hashCode();
    }

    public String toString() {
        return "{" + this.namespaceURI + "}:" + this.localPart;
    }

    public QnameValueType(String uri, String localPart) {
        this.namespaceURI = uri;
        this.localPart = localPart;
    }
}
