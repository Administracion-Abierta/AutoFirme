package com.ctc.wstx.shaded.msv_core.reader.trex.typed;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.trex.typed.TypedElementPattern;
import com.ctc.wstx.shaded.msv_core.reader.trex.ElementState;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/typed/TypedElementState.class */
public class TypedElementState extends ElementState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.ElementState, com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression annealExpression(Expression contentModel) {
        String label = this.startTag.getAttribute(TypedTREXGrammarInterceptor.LABEL_NAMESPACE, AnnotatedPrivateKey.LABEL);
        if (label == null) {
            return super.annealExpression(contentModel);
        }
        return new TypedElementPattern(this.nameClass, contentModel, label);
    }
}
