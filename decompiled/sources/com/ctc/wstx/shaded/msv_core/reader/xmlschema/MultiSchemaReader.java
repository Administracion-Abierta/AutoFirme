package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.util.Util;
import javax.xml.transform.Source;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.LocatorImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/MultiSchemaReader.class */
public class MultiSchemaReader {
    private final XMLSchemaReader reader;
    private boolean finalized = false;

    public MultiSchemaReader(XMLSchemaReader _reader) {
        this.reader = _reader;
        this.reader.setDocumentLocator(new LocatorImpl());
    }

    public final XMLSchemaReader getReader() {
        return this.reader;
    }

    public final XMLSchemaGrammar getResult() {
        finish();
        return this.reader.getResult();
    }

    public void parse(Source source) {
        this.reader.switchSource(source, new RootIncludedSchemaState(this.reader.sfactory.schemaHead(null)));
    }

    public final void parse(String source) {
        parse(Util.getInputSource(source));
    }

    public void parse(InputSource is) {
        this.reader.switchSource(is, new RootIncludedSchemaState(this.reader.sfactory.schemaHead(null)));
    }

    public void finish() {
        if (!this.finalized) {
            this.finalized = true;
            this.reader.wrapUp();
        }
    }
}
