package com.ctc.wstx.sr;

import com.ctc.wstx.ent.EntityDecl;
import javax.xml.stream.Location;
import org.codehaus.stax2.XMLStreamReader2;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sr/StreamReaderImpl.class */
public interface StreamReaderImpl extends XMLStreamReader2 {
    EntityDecl getCurrentEntityDecl();

    Object withStartElement(ElemCallback elemCallback, Location location);

    boolean isNamespaceAware();

    AttributeCollector getAttributeCollector();

    InputElementStack getInputElementStack();
}
