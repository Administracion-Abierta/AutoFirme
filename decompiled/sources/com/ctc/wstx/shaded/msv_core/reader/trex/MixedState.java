package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/MixedState.class */
public class MixedState extends SequenceState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression annealExpression(Expression exp) {
        return this.reader.pool.createMixed(exp);
    }
}
