package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/jarv/RELAXCoreFactoryImpl.class */
public class RELAXCoreFactoryImpl extends FactoryImpl {
    @Override // com.ctc.wstx.shaded.msv_core.verifier.jarv.FactoryImpl
    protected Grammar parse(InputSource is, GrammarReaderController controller) {
        return RELAXCoreReader.parse(is, this.factory, controller, new ExpressionPool());
    }
}
