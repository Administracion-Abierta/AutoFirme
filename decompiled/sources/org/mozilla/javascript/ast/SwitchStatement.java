package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/SwitchStatement.class */
public class SwitchStatement extends Jump {
    private static final List<SwitchCase> NO_CASES = Collections.unmodifiableList(new ArrayList());
    private AstNode expression;
    private List<SwitchCase> cases;
    private int lp = -1;
    private int rp = -1;

    public SwitchStatement() {
        this.type = 115;
    }

    public SwitchStatement(int pos) {
        this.type = 115;
        this.position = pos;
    }

    public SwitchStatement(int pos, int len) {
        this.type = 115;
        this.position = pos;
        this.length = len;
    }

    public AstNode getExpression() {
        return this.expression;
    }

    public void setExpression(AstNode expression) {
        assertNotNull(expression);
        this.expression = expression;
        expression.setParent(this);
    }

    public List<SwitchCase> getCases() {
        return this.cases != null ? this.cases : NO_CASES;
    }

    public void setCases(List<SwitchCase> cases) {
        if (cases == null) {
            this.cases = null;
            return;
        }
        if (this.cases != null) {
            this.cases.clear();
        }
        for (SwitchCase sc : cases) {
            addCase(sc);
        }
    }

    public void addCase(SwitchCase switchCase) {
        assertNotNull(switchCase);
        if (this.cases == null) {
            this.cases = new ArrayList();
        }
        this.cases.add(switchCase);
        switchCase.setParent(this);
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

    @Override // org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        String pad = makeIndent(depth);
        StringBuilder sb = new StringBuilder();
        sb.append(pad);
        sb.append("switch (");
        sb.append(this.expression.toSource(0));
        sb.append(") {\n");
        if (this.cases != null) {
            for (SwitchCase sc : this.cases) {
                sb.append(sc.toSource(depth + 1));
            }
        }
        sb.append(pad);
        sb.append("}\n");
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.expression.visit(v);
            for (SwitchCase sc : getCases()) {
                sc.visit(v);
            }
        }
    }
}
