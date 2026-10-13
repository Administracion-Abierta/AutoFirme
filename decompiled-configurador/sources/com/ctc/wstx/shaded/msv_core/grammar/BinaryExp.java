package com.ctc.wstx.shaded.msv_core.grammar;

import java.util.Iterator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/BinaryExp.class */
public abstract class BinaryExp extends Expression {
    public final Expression exp1;
    public final Expression exp2;
    private static final long serialVersionUID = 1;

    public BinaryExp(Expression left, Expression right) {
        super(left.hashCode() + right.hashCode());
        this.exp1 = left;
        this.exp2 = right;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected final int calcHashCode() {
        return this.exp1.hashCode() + this.exp2.hashCode();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) {
            return false;
        }
        BinaryExp rhs = (BinaryExp) o;
        return rhs.exp1 == this.exp1 && rhs.exp2 == this.exp2;
    }

    public Expression[] getChildren() {
        int cnt = 1;
        Expression expression = this;
        while (true) {
            Expression exp = expression;
            if (exp.getClass() != getClass()) {
                break;
            }
            cnt++;
            expression = ((BinaryExp) exp).exp1;
        }
        Expression[] r = new Expression[cnt];
        Expression expression2 = this;
        while (true) {
            Expression exp2 = expression2;
            if (exp2.getClass() == getClass()) {
                cnt--;
                r[cnt] = ((BinaryExp) exp2).exp2;
                expression2 = ((BinaryExp) exp2).exp1;
            } else {
                r[0] = exp2;
                return r;
            }
        }
    }

    public Iterator children() {
        final Expression[] items = getChildren();
        return new Iterator() { // from class: com.ctc.wstx.shaded.msv_core.grammar.BinaryExp.1
            private int idx = 0;

            @Override // java.util.Iterator
            public Object next() {
                Expression[] expressionArr = items;
                int i = this.idx;
                this.idx = i + 1;
                return expressionArr[i];
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.idx != items.length;
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
