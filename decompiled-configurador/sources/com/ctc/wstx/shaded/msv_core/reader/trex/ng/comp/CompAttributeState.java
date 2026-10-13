package com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.AttributeState;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/comp/CompAttributeState.class */
public class CompAttributeState extends AttributeState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.AttributeState, com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression annealExpression(Expression contentModel) {
        Expression exp = super.annealExpression(contentModel);
        String defaultValue = this.startTag.getAttribute(RELAXNGCompReader.AnnotationNamespace, "defaultValue");
        if (defaultValue != null && (exp instanceof AttributeExp)) {
            RELAXNGCompReader reader = (RELAXNGCompReader) this.reader;
            reader.addDefaultValue((AttributeExp) exp, defaultValue);
        }
        return exp;
    }
}
