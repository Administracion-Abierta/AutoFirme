package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.TREXGrammarReader;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/jarv/TREXFactoryImpl.class */
public class TREXFactoryImpl extends FactoryImpl {
    @Override // com.ctc.wstx.shaded.msv_core.verifier.jarv.FactoryImpl
    protected Grammar parse(InputSource is, GrammarReaderController controller) {
        return TREXGrammarReader.parse(is, this.factory, controller);
    }
}
