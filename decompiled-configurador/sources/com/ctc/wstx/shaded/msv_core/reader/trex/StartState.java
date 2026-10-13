package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/StartState.class */
public class StartState extends SequenceState {
    protected final TREXBaseReader getReader() {
        return (TREXBaseReader) this.reader;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression annealExpression(Expression exp) {
        String name = this.startTag.getAttribute("name");
        if (name != null) {
            ReferenceExp ref = getReader().grammar.namedPatterns.getOrCreate(name);
            ref.exp = exp;
            exp = ref;
        }
        getReader().grammar.exp = exp;
        return null;
    }
}
