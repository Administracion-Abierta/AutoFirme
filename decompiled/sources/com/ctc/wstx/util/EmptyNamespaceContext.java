package com.ctc.wstx.util;

import java.io.Writer;
import java.util.Iterator;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Namespace;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/util/EmptyNamespaceContext.class */
public final class EmptyNamespaceContext extends BaseNsContext {
    static final EmptyNamespaceContext sInstance = new EmptyNamespaceContext();

    private EmptyNamespaceContext() {
    }

    public static EmptyNamespaceContext getInstance() {
        return sInstance;
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public Iterator<Namespace> getNamespaces() {
        return DataUtil.emptyIterator();
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public void outputNamespaceDeclarations(Writer w) {
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public void outputNamespaceDeclarations(XMLStreamWriter w) {
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public String doGetNamespaceURI(String prefix) {
        return null;
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public String doGetPrefix(String nsURI) {
        return null;
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public Iterator<String> doGetPrefixes(String nsURI) {
        return DataUtil.emptyIterator();
    }
}
