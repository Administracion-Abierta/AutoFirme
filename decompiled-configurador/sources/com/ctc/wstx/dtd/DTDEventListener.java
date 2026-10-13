package com.ctc.wstx.dtd;

import java.net.URL;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/DTDEventListener.class */
public interface DTDEventListener {
    boolean dtdReportComments();

    void dtdProcessingInstruction(String str, String str2);

    void dtdComment(char[] cArr, int i, int i2);

    void dtdSkippedEntity(String str);

    void dtdNotationDecl(String str, String str2, String str3, URL url) throws XMLStreamException;

    void dtdUnparsedEntityDecl(String str, String str2, String str3, String str4, URL url) throws XMLStreamException;

    void attributeDecl(String str, String str2, String str3, String str4, String str5);

    void dtdElementDecl(String str, String str2);

    void dtdExternalEntityDecl(String str, String str2, String str3);

    void dtdInternalEntityDecl(String str, String str2);
}
