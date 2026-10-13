package com.ctc.wstx.shaded.msv_core.reader.trex.typed;

import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.TREXGrammarReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/typed/TypedTREXGrammarInterceptor.class */
public class TypedTREXGrammarInterceptor extends TREXGrammarReader.StateFactory {
    public static final String LABEL_NAMESPACE = "http://www.sun.com/xml/msv/trex-type";

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader.StateFactory
    public State element(State parent, StartTagInfo tag) {
        return new TypedElementState();
    }
}
