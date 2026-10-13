package com.ctc.wstx.shaded.msv_core.relaxns.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/relaxns/grammar/RELAXGrammar.class */
public class RELAXGrammar implements Grammar {
    public final Map moduleMap = new HashMap();
    public Expression topLevel;
    public final ExpressionPool pool;

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Grammar
    public Expression getTopLevel() {
        return this.topLevel;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Grammar
    public ExpressionPool getPool() {
        return this.pool;
    }

    public RELAXGrammar(ExpressionPool pool) {
        this.pool = pool;
    }
}
