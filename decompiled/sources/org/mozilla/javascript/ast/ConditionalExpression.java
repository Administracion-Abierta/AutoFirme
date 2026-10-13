package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/ConditionalExpression.class */
public class ConditionalExpression extends AstNode {
    private AstNode testExpression;
    private AstNode trueExpression;
    private AstNode falseExpression;
    private int questionMarkPosition;
    private int colonPosition;

    public ConditionalExpression() {
        this.questionMarkPosition = -1;
        this.colonPosition = -1;
        this.type = 103;
    }

    public ConditionalExpression(int pos) {
        super(pos);
        this.questionMarkPosition = -1;
        this.colonPosition = -1;
        this.type = 103;
    }

    public ConditionalExpression(int pos, int len) {
        super(pos, len);
        this.questionMarkPosition = -1;
        this.colonPosition = -1;
        this.type = 103;
    }

    public AstNode getTestExpression() {
        return this.testExpression;
    }

    public void setTestExpression(AstNode testExpression) {
        assertNotNull(testExpression);
        this.testExpression = testExpression;
        testExpression.setParent(this);
    }

    public AstNode getTrueExpression() {
        return this.trueExpression;
    }

    public void setTrueExpression(AstNode trueExpression) {
        assertNotNull(trueExpression);
        this.trueExpression = trueExpression;
        trueExpression.setParent(this);
    }

    public AstNode getFalseExpression() {
        return this.falseExpression;
    }

    public void setFalseExpression(AstNode falseExpression) {
        assertNotNull(falseExpression);
        this.falseExpression = falseExpression;
        falseExpression.setParent(this);
    }

    public int getQuestionMarkPosition() {
        return this.questionMarkPosition;
    }

    public void setQuestionMarkPosition(int questionMarkPosition) {
        this.questionMarkPosition = questionMarkPosition;
    }

    public int getColonPosition() {
        return this.colonPosition;
    }

    public void setColonPosition(int colonPosition) {
        this.colonPosition = colonPosition;
    }

    @Override // org.mozilla.javascript.ast.AstNode, org.mozilla.javascript.Node
    public boolean hasSideEffects() {
        if (this.testExpression == null || this.trueExpression == null || this.falseExpression == null) {
            codeBug();
        }
        return this.trueExpression.hasSideEffects() && this.falseExpression.hasSideEffects();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        return makeIndent(depth) + this.testExpression.toSource(depth) + " ? " + this.trueExpression.toSource(0) + " : " + this.falseExpression.toSource(0);
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.testExpression.visit(v);
            this.trueExpression.visit(v);
            this.falseExpression.visit(v);
        }
    }
}
