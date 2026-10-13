package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/InfixExpression.class */
public class InfixExpression extends AstNode {
    protected AstNode left;
    protected AstNode right;
    protected int operatorPosition;

    public InfixExpression() {
        this.operatorPosition = -1;
    }

    public InfixExpression(int pos) {
        super(pos);
        this.operatorPosition = -1;
    }

    public InfixExpression(int pos, int len) {
        super(pos, len);
        this.operatorPosition = -1;
    }

    public InfixExpression(int pos, int len, AstNode left, AstNode right) {
        super(pos, len);
        this.operatorPosition = -1;
        setLeft(left);
        setRight(right);
    }

    public InfixExpression(AstNode left, AstNode right) {
        this.operatorPosition = -1;
        setLeftAndRight(left, right);
    }

    public InfixExpression(int operator, AstNode left, AstNode right, int operatorPos) {
        this.operatorPosition = -1;
        setType(operator);
        setOperatorPosition(operatorPos - left.getPosition());
        setLeftAndRight(left, right);
    }

    public void setLeftAndRight(AstNode left, AstNode right) {
        assertNotNull(left);
        assertNotNull(right);
        int beg = left.getPosition();
        int end = right.getPosition() + right.getLength();
        setBounds(beg, end);
        setLeft(left);
        setRight(right);
    }

    public int getOperator() {
        return getType();
    }

    public void setOperator(int operator) {
        if (!Token.isValidToken(operator)) {
            throw new IllegalArgumentException("Invalid token: " + operator);
        }
        setType(operator);
    }

    public AstNode getLeft() {
        return this.left;
    }

    public void setLeft(AstNode left) {
        assertNotNull(left);
        this.left = left;
        setLineno(left.getLineno());
        left.setParent(this);
    }

    public AstNode getRight() {
        return this.right;
    }

    public void setRight(AstNode right) {
        assertNotNull(right);
        this.right = right;
        right.setParent(this);
    }

    public int getOperatorPosition() {
        return this.operatorPosition;
    }

    public void setOperatorPosition(int operatorPosition) {
        this.operatorPosition = operatorPosition;
    }

    @Override // org.mozilla.javascript.ast.AstNode, org.mozilla.javascript.Node
    public boolean hasSideEffects() {
        switch (getType()) {
            case 90:
                return this.right != null && this.right.hasSideEffects();
            case 105:
            case 106:
                return (this.left != null && this.left.hasSideEffects()) || (this.right != null && this.right.hasSideEffects());
            default:
                return super.hasSideEffects();
        }
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        return makeIndent(depth) + this.left.toSource() + " " + operatorToString(getType()) + " " + this.right.toSource();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.left.visit(v);
            this.right.visit(v);
        }
    }
}
