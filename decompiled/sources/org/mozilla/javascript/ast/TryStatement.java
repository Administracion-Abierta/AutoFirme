package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/TryStatement.class */
public class TryStatement extends AstNode {
    private static final List<CatchClause> NO_CATCHES = Collections.unmodifiableList(new ArrayList());
    private AstNode tryBlock;
    private List<CatchClause> catchClauses;
    private AstNode finallyBlock;
    private int finallyPosition;

    public TryStatement() {
        this.finallyPosition = -1;
        this.type = 82;
    }

    public TryStatement(int pos) {
        super(pos);
        this.finallyPosition = -1;
        this.type = 82;
    }

    public TryStatement(int pos, int len) {
        super(pos, len);
        this.finallyPosition = -1;
        this.type = 82;
    }

    public AstNode getTryBlock() {
        return this.tryBlock;
    }

    public void setTryBlock(AstNode tryBlock) {
        assertNotNull(tryBlock);
        this.tryBlock = tryBlock;
        tryBlock.setParent(this);
    }

    public List<CatchClause> getCatchClauses() {
        return this.catchClauses != null ? this.catchClauses : NO_CATCHES;
    }

    public void setCatchClauses(List<CatchClause> catchClauses) {
        if (catchClauses == null) {
            this.catchClauses = null;
            return;
        }
        if (this.catchClauses != null) {
            this.catchClauses.clear();
        }
        for (CatchClause cc : catchClauses) {
            addCatchClause(cc);
        }
    }

    public void addCatchClause(CatchClause clause) {
        assertNotNull(clause);
        if (this.catchClauses == null) {
            this.catchClauses = new ArrayList();
        }
        this.catchClauses.add(clause);
        clause.setParent(this);
    }

    public AstNode getFinallyBlock() {
        return this.finallyBlock;
    }

    public void setFinallyBlock(AstNode finallyBlock) {
        this.finallyBlock = finallyBlock;
        if (finallyBlock != null) {
            finallyBlock.setParent(this);
        }
    }

    public int getFinallyPosition() {
        return this.finallyPosition;
    }

    public void setFinallyPosition(int finallyPosition) {
        this.finallyPosition = finallyPosition;
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder(250);
        sb.append(makeIndent(depth));
        sb.append("try ");
        if (getInlineComment() != null) {
            sb.append(getInlineComment().toSource(depth + 1)).append("\n");
        }
        sb.append(this.tryBlock.toSource(depth).trim());
        for (CatchClause cc : getCatchClauses()) {
            sb.append(cc.toSource(depth));
        }
        if (this.finallyBlock != null) {
            sb.append(" finally ");
            sb.append(this.finallyBlock.toSource(depth));
        }
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.tryBlock.visit(v);
            for (CatchClause cc : getCatchClauses()) {
                cc.visit(v);
            }
            if (this.finallyBlock != null) {
                this.finallyBlock.visit(v);
            }
        }
    }
}
