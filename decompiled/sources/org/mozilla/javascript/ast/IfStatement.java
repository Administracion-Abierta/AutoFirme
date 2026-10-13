package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/IfStatement.class */
public class IfStatement extends AstNode {
    private AstNode condition;
    private AstNode thenPart;
    private int elsePosition;
    private AstNode elsePart;
    private AstNode elseKeyWordInlineComment;
    private int lp;
    private int rp;

    public IfStatement() {
        this.elsePosition = -1;
        this.lp = -1;
        this.rp = -1;
        this.type = 113;
    }

    public IfStatement(int pos) {
        super(pos);
        this.elsePosition = -1;
        this.lp = -1;
        this.rp = -1;
        this.type = 113;
    }

    public IfStatement(int pos, int len) {
        super(pos, len);
        this.elsePosition = -1;
        this.lp = -1;
        this.rp = -1;
        this.type = 113;
    }

    public AstNode getCondition() {
        return this.condition;
    }

    public void setCondition(AstNode condition) {
        assertNotNull(condition);
        this.condition = condition;
        condition.setParent(this);
    }

    public AstNode getThenPart() {
        return this.thenPart;
    }

    public void setThenPart(AstNode thenPart) {
        assertNotNull(thenPart);
        this.thenPart = thenPart;
        thenPart.setParent(this);
    }

    public AstNode getElsePart() {
        return this.elsePart;
    }

    public void setElsePart(AstNode elsePart) {
        this.elsePart = elsePart;
        if (elsePart != null) {
            elsePart.setParent(this);
        }
    }

    public int getElsePosition() {
        return this.elsePosition;
    }

    public void setElsePosition(int elsePosition) {
        this.elsePosition = elsePosition;
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

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        String pad = makeIndent(depth);
        StringBuilder sb = new StringBuilder(32);
        sb.append(pad);
        sb.append("if (");
        sb.append(this.condition.toSource(0));
        sb.append(") ");
        if (getInlineComment() != null) {
            sb.append("    ").append(getInlineComment().toSource()).append("\n");
        }
        if (this.thenPart.getType() != 130) {
            if (getInlineComment() == null) {
                sb.append("\n");
            }
            sb.append(makeIndent(depth + 1));
        }
        sb.append(this.thenPart.toSource(depth).trim());
        if (this.elsePart != null) {
            if (this.thenPart.getType() != 130) {
                sb.append("\n").append(pad).append("else ");
            } else {
                sb.append(" else ");
            }
            if (getElseKeyWordInlineComment() != null) {
                sb.append("    ").append(getElseKeyWordInlineComment().toSource()).append("\n");
            }
            if (this.elsePart.getType() != 130 && this.elsePart.getType() != 113) {
                if (getElseKeyWordInlineComment() == null) {
                    sb.append("\n");
                }
                sb.append(makeIndent(depth + 1));
            }
            sb.append(this.elsePart.toSource(depth).trim());
        }
        sb.append("\n");
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.condition.visit(v);
            this.thenPart.visit(v);
            if (this.elsePart != null) {
                this.elsePart.visit(v);
            }
        }
    }

    public AstNode getElseKeyWordInlineComment() {
        return this.elseKeyWordInlineComment;
    }

    public void setElseKeyWordInlineComment(AstNode elseKeyWordInlineComment) {
        this.elseKeyWordInlineComment = elseKeyWordInlineComment;
    }
}
