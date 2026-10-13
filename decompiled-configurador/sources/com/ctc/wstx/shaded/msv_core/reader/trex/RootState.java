package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/RootState.class */
public class RootState extends RootIncludedPatternState {
    private boolean simple;

    public RootState() {
        super(null);
        this.simple = false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.RootIncludedPatternState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        TREXBaseReader reader = (TREXBaseReader) this.reader;
        if (tag.localName.equals("grammar")) {
            return reader.sfactory.grammar(null, tag);
        }
        State s = super.createChildState(tag);
        if (s != null) {
            reader.grammar = reader.sfactory.createGrammar(reader.pool, null);
            this.simple = true;
        }
        return s;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.RootIncludedPatternState, com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner
    public void onEndChild(Expression exp) {
        super.onEndChild(exp);
        TREXBaseReader reader = (TREXBaseReader) this.reader;
        if (this.simple) {
            reader.grammar.exp = exp;
        }
        reader.wrapUp();
    }
}
