package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/org_isorelax/dispatcher/AttributesDecl.class */
public interface AttributesDecl {
    String getName();

    boolean getFeature(String str) throws SAXNotRecognizedException, SAXNotSupportedException;

    Object getProperty(String str) throws SAXNotRecognizedException, SAXNotSupportedException;
}
