package com.ctc.wstx.shaded.msv_core.writer;

import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/writer/GrammarWriter.class */
public interface GrammarWriter {
    void setDocumentHandler(DocumentHandler documentHandler);

    void write(Grammar grammar) throws UnsupportedOperationException, SAXException;
}
