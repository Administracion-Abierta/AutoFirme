package com.ctc.wstx.shaded.msv_core.grammar.trex;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/trex/ElementPattern.class */
public class ElementPattern extends ElementExp {
    public final NameClass nameClass;
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ElementExp, com.ctc.wstx.shaded.msv_core.grammar.NameClassAndExpression
    public final NameClass getNameClass() {
        return this.nameClass;
    }

    public ElementPattern(NameClass nameClass, Expression contentModel) {
        super(contentModel, false);
        this.nameClass = nameClass;
    }
}
