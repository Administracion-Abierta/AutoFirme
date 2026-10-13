package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/CatchClause.class */
public class CatchClause extends AstNode {
    private Name varName;
    private AstNode catchCondition;
    private Block body;
    private int ifPosition;
    private int lp;
    private int rp;

    public CatchClause() {
        this.ifPosition = -1;
        this.lp = -1;
        this.rp = -1;
        this.type = 125;
    }

    public CatchClause(int pos) {
        super(pos);
        this.ifPosition = -1;
        this.lp = -1;
        this.rp = -1;
        this.type = 125;
    }

    public CatchClause(int pos, int len) {
        super(pos, len);
        this.ifPosition = -1;
        this.lp = -1;
        this.rp = -1;
        this.type = 125;
    }

    public Name getVarName() {
        return this.varName;
    }

    public void setVarName(Name varName) {
        assertNotNull(varName);
        this.varName = varName;
        varName.setParent(this);
    }

    public AstNode getCatchCondition() {
        return this.catchCondition;
    }

    public void setCatchCondition(AstNode catchCondition) {
        this.catchCondition = catchCondition;
        if (catchCondition != null) {
            catchCondition.setParent(this);
        }
    }

    public Block getBody() {
        return this.body;
    }

    public void setBody(Block body) {
        assertNotNull(body);
        this.body = body;
        body.setParent(this);
    }

    public int getLp() {
        return this.lp;
    }

    public void setLp(int lp) {
        this.lp = lp;
    }

    public int getRp() {
        return this.rp;
    }

    public void setRp(int rp) {
        this.rp = rp;
    }

    public void setParens(int lp, int rp) {
        this.lp = lp;
        this.rp = rp;
    }

    public int getIfPosition() {
        return this.ifPosition;
    }

    public void setIfPosition(int ifPosition) {
        this.ifPosition = ifPosition;
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(makeIndent(depth));
        sb.append("catch (");
        sb.append(this.varName.toSource(0));
        if (this.catchCondition != null) {
            sb.append(" if ");
            sb.append(this.catchCondition.toSource(0));
        }
        sb.append(") ");
        sb.append(this.body.toSource(0));
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.varName.visit(v);
            if (this.catchCondition != null) {
                this.catchCondition.visit(v);
            }
            this.body.visit(v);
        }
    }
}
