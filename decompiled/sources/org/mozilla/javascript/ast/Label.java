package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/Label.class */
public class Label extends Jump {
    private String name;

    public Label() {
        this.type = 131;
    }

    public Label(int pos) {
        this(pos, -1);
    }

    public Label(int pos, int len) {
        this.type = 131;
        this.position = pos;
        this.length = len;
    }

    public Label(int pos, int len, String name) {
        this(pos, len);
        setName(name);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        String name2 = name == null ? null : name.trim();
        if (name2 == null || "".equals(name2)) {
            throw new IllegalArgumentException("invalid label name");
        }
        this.name = name2;
    }

    @Override // org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        return makeIndent(depth) + this.name + ":\n";
    }

    @Override // org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        v.visit(this);
    }
}
