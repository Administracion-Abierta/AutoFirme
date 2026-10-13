package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/AttributeFeeder.class */
public class AttributeFeeder implements ExpressionVisitorExpression {
    protected final REDocumentDeclaration docDecl;
    protected final ExpressionPool pool;
    private Token token;

    public AttributeFeeder(REDocumentDeclaration docDecl) {
        this.docDecl = docDecl;
        this.pool = docDecl.pool;
    }

    public final Expression feed(Expression exp, AttributeToken token, boolean ignoreUndeclaredAttribute) {
        this.token = token;
        Expression r = exp.visit(this);
        if (r != Expression.nullSet || !ignoreUndeclaredAttribute) {
            return r;
        }
        this.token = token.createRecoveryAttToken();
        return exp.visit(this) == Expression.nullSet ? exp : Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onAttribute(AttributeExp exp) {
        return this.token.match(exp) ? Expression.epsilon : Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onChoice(ChoiceExp exp) {
        return this.pool.createChoice(exp.exp1.visit(this), exp.exp2.visit(this));
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onElement(ElementExp exp) {
        return Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onOneOrMore(OneOrMoreExp exp) {
        return this.pool.createSequence(exp.exp.visit(this), this.pool.createZeroOrMore(exp.exp));
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onMixed(MixedExp exp) {
        return this.pool.createMixed(exp.exp.visit(this));
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onList(ListExp exp) {
        return Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onEpsilon() {
        return Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onNullSet() {
        return Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onAnyString() {
        return Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onRef(ReferenceExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onSequence(SequenceExp exp) {
        return this.pool.createChoice(this.pool.createSequence(exp.exp1.visit(this), exp.exp2), this.pool.createSequence(exp.exp1, exp.exp2.visit(this)));
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onData(DataExp exp) {
        return Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onValue(ValueExp exp) {
        return Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onConcur(ConcurExp exp) {
        return this.pool.createConcur(exp.exp1.visit(this), exp.exp2.visit(this));
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onInterleave(InterleaveExp exp) {
        return this.pool.createChoice(this.pool.createInterleave(exp.exp1.visit(this), exp.exp2), this.pool.createInterleave(exp.exp1, exp.exp2.visit(this)));
    }
}
