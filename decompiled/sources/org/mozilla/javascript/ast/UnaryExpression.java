package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/UnaryExpression.class */
public class UnaryExpression extends AstNode {
    private AstNode operand;
    private boolean isPostfix;

    public UnaryExpression() {
    }

    public UnaryExpression(int pos) {
        super(pos);
    }

    public UnaryExpression(int pos, int len) {
        super(pos, len);
    }

    public UnaryExpression(int operator, int operatorPosition, AstNode operand) {
        this(operator, operatorPosition, operand, false);
    }

    public UnaryExpression(int operator, int operatorPosition, AstNode operand, boolean postFix) {
        assertNotNull(operand);
        int beg = postFix ? operand.getPosition() : operatorPosition;
        int end = postFix ? operatorPosition + 2 : operand.getPosition() + operand.getLength();
        setBounds(beg, end);
        setOperator(operator);
        setOperand(operand);
        this.isPostfix = postFix;
    }

    public int getOperator() {
        return this.type;
    }

    public void setOperator(int operator) {
        if (!Token.isValidToken(operator)) {
            throw new IllegalArgumentException("Invalid token: " + operator);
        }
        setType(operator);
    }

    public AstNode getOperand() {
        return this.operand;
    }

    public void setOperand(AstNode operand) {
        assertNotNull(operand);
        this.operand = operand;
        operand.setParent(this);
    }

    public boolean isPostfix() {
        return this.isPostfix;
    }

    public boolean isPrefix() {
        return !this.isPostfix;
    }

    public void setIsPostfix(boolean isPostfix) {
        this.isPostfix = isPostfix;
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(makeIndent(depth));
        int type = getType();
        if (!this.isPostfix) {
            sb.append(operatorToString(type));
            if (type == 32 || type == 31 || type == 127) {
                sb.append(" ");
            }
        }
        sb.append(this.operand.toSource());
        if (this.isPostfix) {
            sb.append(operatorToString(type));
        }
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.operand.visit(v);
        }
    }
}
