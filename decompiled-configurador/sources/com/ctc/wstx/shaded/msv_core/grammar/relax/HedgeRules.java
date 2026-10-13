package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/HedgeRules.class */
public class HedgeRules extends ReferenceExp implements Exportable {
    public boolean exported;
    public final RELAXModule ownerModule;
    private static final long serialVersionUID = 1;

    protected HedgeRules(String label, RELAXModule ownerModule) {
        super(label);
        this.exported = false;
        this.ownerModule = ownerModule;
    }

    public void addHedge(Expression exp, ExpressionPool pool) {
        if (this.exp != null) {
            this.exp = pool.createChoice(this.exp, exp);
        } else {
            this.exp = exp;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp, com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean equals(Object o) {
        return this == o;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Object visit(RELAXExpressionVisitor visitor) {
        return visitor.onHedgeRules(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Expression visit(RELAXExpressionVisitorExpression visitor) {
        return visitor.onHedgeRules(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean visit(RELAXExpressionVisitorBoolean visitor) {
        return visitor.onHedgeRules(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public void visit(RELAXExpressionVisitorVoid visitor) {
        visitor.onHedgeRules(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.Exportable
    public boolean isExported() {
        return this.exported;
    }
}
