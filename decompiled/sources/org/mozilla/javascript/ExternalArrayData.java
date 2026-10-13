package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ExternalArrayData.class */
public interface ExternalArrayData {
    Object getArrayElement(int i);

    void setArrayElement(int i, Object obj);

    int getArrayLength();
}
