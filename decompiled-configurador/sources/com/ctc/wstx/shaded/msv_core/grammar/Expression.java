package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.util.RefExpRemover;
import java.io.Serializable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/Expression.class */
public abstract class Expression implements Serializable {
    private Boolean epsilonReducibility;
    private Expression expandedExp;
    public transient Object verifierTag;
    private transient int cachedHashCode;
    public static final Expression epsilon = new EpsilonExpression();
    public static final Expression nullSet = new NullSetExpression();
    public static final Expression anyString = new AnyStringExpression();
    private static final long serialVersionUID = -569561418606215601L;

    protected abstract boolean calcEpsilonReducibility();

    public abstract Object visit(ExpressionVisitor expressionVisitor);

    public abstract Expression visit(ExpressionVisitorExpression expressionVisitorExpression);

    public abstract boolean visit(ExpressionVisitorBoolean expressionVisitorBoolean);

    public abstract void visit(ExpressionVisitorVoid expressionVisitorVoid);

    protected abstract int calcHashCode();

    public abstract boolean equals(Object obj);

    public boolean isEpsilonReducible() {
        if (this.epsilonReducibility == null) {
            this.epsilonReducibility = calcEpsilonReducibility() ? Boolean.TRUE : Boolean.FALSE;
        }
        return this.epsilonReducibility.booleanValue();
    }

    public Expression getExpandedExp(ExpressionPool pool) {
        if (this.expandedExp == null) {
            this.expandedExp = visit(new RefExpRemover(pool, false));
        }
        return this.expandedExp;
    }

    public final Expression peelOccurence() {
        if (this instanceof ChoiceExp) {
            ChoiceExp cexp = (ChoiceExp) this;
            if (cexp.exp1 == epsilon) {
                return cexp.exp2.peelOccurence();
            }
            if (cexp.exp2 == epsilon) {
                return cexp.exp1.peelOccurence();
            }
        }
        if (this instanceof OneOrMoreExp) {
            return ((OneOrMoreExp) this).exp.peelOccurence();
        }
        return this;
    }

    protected Expression(int hashCode) {
        this.expandedExp = null;
        this.verifierTag = null;
        setHashCode(hashCode);
    }

    protected Expression() {
        this.expandedExp = null;
        this.verifierTag = null;
        this.cachedHashCode = System.identityHashCode(this);
    }

    public Object visit(RELAXExpressionVisitor visitor) {
        return visit((ExpressionVisitor) visitor);
    }

    public Expression visit(RELAXExpressionVisitorExpression visitor) {
        return visit((ExpressionVisitorExpression) visitor);
    }

    public boolean visit(RELAXExpressionVisitorBoolean visitor) {
        return visit((ExpressionVisitorBoolean) visitor);
    }

    public void visit(RELAXExpressionVisitorVoid visitor) {
        visit((ExpressionVisitorVoid) visitor);
    }

    public final int hashCode() {
        return this.cachedHashCode;
    }

    private final void setHashCode(int hashCode) {
        this.cachedHashCode = hashCode ^ getClass().hashCode();
    }

    protected static int hashCode(Object o1, Object o2, int hashKey) {
        return o1.hashCode() + o2.hashCode() + hashKey;
    }

    protected static int hashCode(Object o, int hashKey) {
        return o.hashCode() + hashKey;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/Expression$EpsilonExpression.class */
    private static class EpsilonExpression extends Expression {
        private static final long serialVersionUID = 1;

        EpsilonExpression() {
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        protected final int calcHashCode() {
            return System.identityHashCode(this);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public Object visit(ExpressionVisitor visitor) {
            return visitor.onEpsilon();
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public Expression visit(ExpressionVisitorExpression visitor) {
            return visitor.onEpsilon();
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public boolean visit(ExpressionVisitorBoolean visitor) {
            return visitor.onEpsilon();
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public void visit(ExpressionVisitorVoid visitor) {
            visitor.onEpsilon();
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        protected boolean calcEpsilonReducibility() {
            return true;
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public boolean equals(Object o) {
            return this == o;
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        protected Object readResolve() {
            return Expression.epsilon;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/Expression$NullSetExpression.class */
    private static class NullSetExpression extends Expression {
        private static final long serialVersionUID = 1;

        NullSetExpression() {
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        protected final int calcHashCode() {
            return System.identityHashCode(this);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public Object visit(ExpressionVisitor visitor) {
            return visitor.onNullSet();
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public Expression visit(ExpressionVisitorExpression visitor) {
            return visitor.onNullSet();
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public boolean visit(ExpressionVisitorBoolean visitor) {
            return visitor.onNullSet();
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public void visit(ExpressionVisitorVoid visitor) {
            visitor.onNullSet();
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        protected boolean calcEpsilonReducibility() {
            return false;
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public boolean equals(Object o) {
            return this == o;
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        protected Object readResolve() {
            return Expression.nullSet;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/Expression$AnyStringExpression.class */
    private static class AnyStringExpression extends Expression {
        private static final long serialVersionUID = 1;

        AnyStringExpression() {
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        protected final int calcHashCode() {
            return System.identityHashCode(this);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public Object visit(ExpressionVisitor visitor) {
            return visitor.onAnyString();
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public Expression visit(ExpressionVisitorExpression visitor) {
            return visitor.onAnyString();
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public boolean visit(ExpressionVisitorBoolean visitor) {
            return visitor.onAnyString();
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public void visit(ExpressionVisitorVoid visitor) {
            visitor.onAnyString();
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        protected boolean calcEpsilonReducibility() {
            return true;
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        public boolean equals(Object o) {
            return this == o;
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
        protected Object readResolve() {
            return Expression.anyString;
        }
    }

    protected Object readResolve() {
        setHashCode(calcHashCode());
        return this;
    }
}
