package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/jarv/XSFactoryImpl.class */
public class XSFactoryImpl extends FactoryImpl {
    @Override // com.ctc.wstx.shaded.msv_core.verifier.jarv.FactoryImpl
    protected Grammar parse(InputSource is, GrammarReaderController controller) {
        return XMLSchemaReader.parse(is, this.factory, controller);
    }
}
