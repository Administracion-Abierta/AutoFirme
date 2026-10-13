package com.ctc.wstx.shaded.msv_core.grammar.relaxng;

import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/relaxng/RELAXNGGrammar.class */
public class RELAXNGGrammar extends TREXGrammar {
    public boolean isIDcompatible;
    public boolean isDefaultAttributeValueCompatible;
    public boolean isAnnotationCompatible;
    private static final long serialVersionUID = 1;

    public RELAXNGGrammar(ExpressionPool pool, TREXGrammar parentGrammar) {
        super(pool, parentGrammar);
        this.isIDcompatible = true;
        this.isDefaultAttributeValueCompatible = true;
        this.isAnnotationCompatible = true;
    }

    public RELAXNGGrammar(ExpressionPool pool) {
        super(pool);
        this.isIDcompatible = true;
        this.isDefaultAttributeValueCompatible = true;
        this.isAnnotationCompatible = true;
    }

    public RELAXNGGrammar() {
        this.isIDcompatible = true;
        this.isDefaultAttributeValueCompatible = true;
        this.isAnnotationCompatible = true;
    }
}
