package com.ctc.wstx.shaded.msv_core.reader.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/LabelRefState.class */
abstract class LabelRefState extends ExpressionWithoutChildState {
    protected abstract Expression resolve(String str, String str2);

    LabelRefState() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        String label = this.startTag.getAttribute(AnnotatedPrivateKey.LABEL);
        String namespace = this.startTag.getAttribute("namespace");
        RELAXReader reader = (RELAXReader) this.reader;
        if (label == null) {
            reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, this.startTag.localName, AnnotatedPrivateKey.LABEL);
            return Expression.nullSet;
        }
        return resolve(namespace, label);
    }
}
