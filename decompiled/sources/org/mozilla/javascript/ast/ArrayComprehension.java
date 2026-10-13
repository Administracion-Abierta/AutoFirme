package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/ArrayComprehension.class */
public class ArrayComprehension extends Scope {
    private AstNode result;
    private List<ArrayComprehensionLoop> loops;
    private AstNode filter;
    private int ifPosition;
    private int lp;
    private int rp;

    public ArrayComprehension() {
        this.loops = new ArrayList();
        this.ifPosition = -1;
        this.lp = -1;
        this.rp = -1;
        this.type = 158;
    }

    public ArrayComprehension(int pos) {
        super(pos);
        this.loops = new ArrayList();
        this.ifPosition = -1;
        this.lp = -1;
        this.rp = -1;
        this.type = 158;
    }

    public ArrayComprehension(int pos, int len) {
        super(pos, len);
        this.loops = new ArrayList();
        this.ifPosition = -1;
        this.lp = -1;
        this.rp = -1;
        this.type = 158;
    }

    public AstNode getResult() {
        return this.result;
    }

    public void setResult(AstNode result) {
        assertNotNull(result);
        this.result = result;
        result.setParent(this);
    }

    public List<ArrayComprehensionLoop> getLoops() {
        return this.loops;
    }

    public void setLoops(List<ArrayComprehensionLoop> loops) {
        assertNotNull(loops);
        this.loops.clear();
        for (ArrayComprehensionLoop acl : loops) {
            addLoop(acl);
        }
    }

    public void addLoop(ArrayComprehensionLoop acl) {
        assertNotNull(acl);
        this.loops.add(acl);
        acl.setParent(this);
    }

    public AstNode getFilter() {
        return this.filter;
    }

    public void setFilter(AstNode filter) {
        this.filter = filter;
        if (filter != null) {
            filter.setParent(this);
        }
    }

    public int getIfPosition() {
        return this.ifPosition;
    }

    public void setIfPosition(int ifPosition) {
        this.ifPosition = ifPosition;
    }

    public int getFilterLp() {
        return this.lp;
    }

    public void setFilterLp(int lp) {
        this.lp = lp;
    }

    public int getFilterRp() {
        return this.rp;
    }

    public void setFilterRp(int rp) {
        this.rp = rp;
    }

    @Override // org.mozilla.javascript.ast.Scope, org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder(250);
        sb.append("[");
        sb.append(this.result.toSource(0));
        for (ArrayComprehensionLoop loop : this.loops) {
            sb.append(loop.toSource(0));
        }
        if (this.filter != null) {
            sb.append(" if (");
            sb.append(this.filter.toSource(0));
            sb.append(")");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.Scope, org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (!v.visit(this)) {
            return;
        }
        this.result.visit(v);
        for (ArrayComprehensionLoop loop : this.loops) {
            loop.visit(v);
        }
        if (this.filter != null) {
            this.filter.visit(v);
        }
    }
}
