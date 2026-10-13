package com.ctc.wstx.shaded.msv_core.reader.trex.classic;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.util.StringPair;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/classic/DataState.class */
public class DataState extends ExpressionWithoutChildState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        String typeName = this.startTag.getAttribute("type");
        if (typeName == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, this.startTag.qName, "type");
            return Expression.anyString;
        }
        return this.reader.pool.createData(((TREXGrammarReader) this.reader).resolveDatatype(typeName), new StringPair("", typeName));
    }
}
