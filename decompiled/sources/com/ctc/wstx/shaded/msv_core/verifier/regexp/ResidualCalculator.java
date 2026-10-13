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

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/ResidualCalculator.class */
public class ResidualCalculator implements ExpressionVisitorExpression {
    protected Token token;
    protected final ExpressionPool pool;

    public ResidualCalculator(ExpressionPool pool) {
        this.pool = pool;
    }

    final Expression calcResidual(Expression exp, ElementToken token) {
        OptimizationTag ot;
        if (token.acceptedPatterns != null && token.acceptedPatterns.length == 1) {
            if (exp.verifierTag == null) {
                OptimizationTag optimizationTag = new OptimizationTag();
                ot = optimizationTag;
                exp.verifierTag = optimizationTag;
            } else {
                ot = (OptimizationTag) exp.verifierTag;
                Expression residual = (Expression) ot.simpleElementTokenResidual.get(token.acceptedPatterns[0]);
                if (residual != null) {
                    return residual;
                }
            }
            this.token = token;
            Expression residual2 = exp.visit(this);
            ot.simpleElementTokenResidual.put(token.acceptedPatterns[0], residual2);
            return residual2;
        }
        this.token = token;
        return exp.visit(this);
    }

    public final Expression calcResidual(Expression exp, Token token) {
        if (token instanceof ElementToken) {
            return calcResidual(exp, (ElementToken) token);
        }
        this.token = token;
        Expression r = exp.visit(this);
        if (token.isIgnorable()) {
            r = this.pool.createChoice(r, exp);
        }
        return r;
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
        return this.token.match(exp) ? Expression.epsilon : Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onOneOrMore(OneOrMoreExp exp) {
        return this.pool.createSequence(exp.exp.visit(this), this.pool.createZeroOrMore(exp.exp));
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onMixed(MixedExp exp) {
        return this.token.matchAnyString() ? exp : this.pool.createMixed(exp.exp.visit(this));
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
        return this.token.matchAnyString() ? Expression.anyString : Expression.nullSet;
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
        Expression r = this.pool.createSequence(exp.exp1.visit(this), exp.exp2);
        if (exp.exp1.isEpsilonReducible()) {
            return this.pool.createChoice(r, exp.exp2.visit(this));
        }
        return r;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onData(DataExp exp) {
        return this.token.match(exp) ? Expression.epsilon : Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onValue(ValueExp exp) {
        return this.token.match(exp) ? Expression.epsilon : Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onList(ListExp exp) {
        return this.token.match(exp) ? Expression.epsilon : Expression.nullSet;
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
