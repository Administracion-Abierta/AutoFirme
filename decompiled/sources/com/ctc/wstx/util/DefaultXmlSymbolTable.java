package com.ctc.wstx.util;

import com.aowagie.text.ElementTags;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/util/DefaultXmlSymbolTable.class */
public final class DefaultXmlSymbolTable {
    static final SymbolTable sInstance = new SymbolTable(true, 128);
    static final String mNsPrefixXml = sInstance.findSymbol("xml");
    static final String mNsPrefixXmlns = sInstance.findSymbol(CanonicalizerBase.XMLNS);

    static {
        sInstance.findSymbol(ElementTags.ID);
        sInstance.findSymbol("name");
        sInstance.findSymbol("xsd");
        sInstance.findSymbol("xsi");
        sInstance.findSymbol("type");
        sInstance.findSymbol("soap");
        sInstance.findSymbol("SOAP-ENC");
        sInstance.findSymbol("SOAP-ENV");
        sInstance.findSymbol("Body");
        sInstance.findSymbol("Envelope");
    }

    public static SymbolTable getInstance() {
        return sInstance.makeChild();
    }

    public static String getXmlSymbol() {
        return mNsPrefixXml;
    }

    public static String getXmlnsSymbol() {
        return mNsPrefixXmlns;
    }
}
