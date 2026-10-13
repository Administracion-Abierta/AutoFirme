package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/Name.class */
public class Name extends AstNode {
    private String identifier;
    private Scope scope;

    public Name() {
        this.type = 39;
    }

    public Name(int pos) {
        super(pos);
        this.type = 39;
    }

    public Name(int pos, int len) {
        super(pos, len);
        this.type = 39;
    }

    public Name(int pos, int len, String name) {
        super(pos, len);
        this.type = 39;
        setIdentifier(name);
    }

    public Name(int pos, String name) {
        super(pos);
        this.type = 39;
        setIdentifier(name);
        setLength(name.length());
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        assertNotNull(identifier);
        this.identifier = identifier;
        setLength(identifier.length());
    }

    @Override // org.mozilla.javascript.Node
    public void setScope(Scope s) {
        this.scope = s;
    }

    @Override // org.mozilla.javascript.Node
    public Scope getScope() {
        return this.scope;
    }

    public Scope getDefiningScope() {
        Scope enclosing = getEnclosingScope();
        String name = getIdentifier();
        if (enclosing == null) {
            return null;
        }
        return enclosing.getDefiningScope(name);
    }

    public boolean isLocalName() {
        Scope scope = getDefiningScope();
        return (scope == null || scope.getParentScope() == null) ? false : true;
    }

    public int length() {
        if (this.identifier == null) {
            return 0;
        }
        return this.identifier.length();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        return makeIndent(depth) + (this.identifier == null ? "<null>" : this.identifier);
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        v.visit(this);
    }
}
