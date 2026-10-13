package com.ctc.wstx.shaded.msv_core.reader;

import org.xml.sax.Attributes;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/IgnoreState.class */
public class IgnoreState extends State {
    private int depth = 1;

    @Override // org.xml.sax.ContentHandler
    public final void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        this.depth++;
    }

    @Override // org.xml.sax.ContentHandler
    public final void endElement(String namespaceURI, String localName, String qName) {
        this.depth--;
        if (this.depth == 0) {
            this.reader.popState();
        }
    }

    @Override // org.xml.sax.ContentHandler
    public final void endDocument() {
        this.reader.popState();
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State, org.xml.sax.ContentHandler
    public void characters(char[] buffer, int from, int len) {
    }
}
