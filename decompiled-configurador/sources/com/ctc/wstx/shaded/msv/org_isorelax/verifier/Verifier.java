package com.ctc.wstx.shaded.msv.org_isorelax.verifier;

import java.io.File;
import java.io.IOException;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/org_isorelax/verifier/Verifier.class */
public interface Verifier {
    public static final String FEATURE_HANDLER = "http://www.iso-relax.org/verifier/handler";
    public static final String FEATURE_FILTER = "http://www.iso-relax.org/verifier/filter";

    boolean isFeature(String str) throws SAXNotRecognizedException, SAXNotSupportedException;

    void setFeature(String str, boolean z) throws SAXNotRecognizedException, SAXNotSupportedException;

    Object getProperty(String str) throws SAXNotRecognizedException, SAXNotSupportedException;

    void setProperty(String str, Object obj) throws SAXNotRecognizedException, SAXNotSupportedException;

    void setErrorHandler(ErrorHandler errorHandler);

    void setEntityResolver(EntityResolver entityResolver);

    boolean verify(String str) throws SAXException, IOException;

    boolean verify(InputSource inputSource) throws SAXException, IOException;

    boolean verify(File file) throws SAXException, IOException;

    boolean verify(Node node) throws SAXException;

    VerifierHandler getVerifierHandler() throws SAXException;

    VerifierFilter getVerifierFilter() throws SAXException;
}
