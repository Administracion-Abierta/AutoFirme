package com.ctc.wstx.sr;

import com.ctc.wstx.util.BaseNsContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/sr/ElemCallback.class */
public abstract class ElemCallback {
    public abstract Object withStartElement(Location location, QName qName, BaseNsContext baseNsContext, ElemAttrs elemAttrs, boolean z);
}
