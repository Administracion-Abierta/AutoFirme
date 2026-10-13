package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/org_isorelax/dispatcher/IslandVerifier.class */
public interface IslandVerifier extends ContentHandler {
    void setDispatcher(Dispatcher dispatcher);

    ElementDecl[] endIsland() throws SAXException;

    void endChildIsland(String str, ElementDecl[] elementDeclArr) throws SAXException;
}
