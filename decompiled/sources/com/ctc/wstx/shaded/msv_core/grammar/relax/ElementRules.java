package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/ElementRules.class */
public class ElementRules extends ReferenceExp implements Exportable {
    public boolean exported;
    public final RELAXModule ownerModule;
    private static final long serialVersionUID = 1;

    protected ElementRules(String label, RELAXModule ownerModule) {
        super(label);
        this.exported = false;
        this.ownerModule = ownerModule;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp, com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean equals(Object o) {
        return this == o;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp, com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected boolean calcEpsilonReducibility() {
        return false;
    }

    public void addElementRule(ExpressionPool pool, ElementRule newRule) {
        newRule.parent = this;
        if (this.exp == null) {
            this.exp = newRule;
        } else {
            this.exp = pool.createChoice(this.exp, newRule);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Object visit(RELAXExpressionVisitor visitor) {
        return visitor.onElementRules(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Expression visit(RELAXExpressionVisitorExpression visitor) {
        return visitor.onElementRules(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean visit(RELAXExpressionVisitorBoolean visitor) {
        return visitor.onElementRules(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public void visit(RELAXExpressionVisitorVoid visitor) {
        visitor.onElementRules(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.Exportable
    public boolean isExported() {
        return this.exported;
    }
}
