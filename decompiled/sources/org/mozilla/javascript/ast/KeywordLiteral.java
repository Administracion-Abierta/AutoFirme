package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/KeywordLiteral.class */
public class KeywordLiteral extends AstNode {
    public KeywordLiteral() {
    }

    public KeywordLiteral(int pos) {
        super(pos);
    }

    public KeywordLiteral(int pos, int len) {
        super(pos, len);
    }

    public KeywordLiteral(int pos, int len, int nodeType) {
        super(pos, len);
        setType(nodeType);
    }

    @Override // org.mozilla.javascript.Node
    public KeywordLiteral setType(int nodeType) {
        if (nodeType != 43 && nodeType != 42 && nodeType != 45 && nodeType != 44 && nodeType != 161) {
            throw new IllegalArgumentException("Invalid node type: " + nodeType);
        }
        this.type = nodeType;
        return this;
    }

    public boolean isBooleanLiteral() {
        return this.type == 45 || this.type == 44;
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(makeIndent(depth));
        switch (getType()) {
            case 42:
                sb.append("null");
                break;
            case 43:
                sb.append("this");
                break;
            case 44:
                sb.append("false");
                break;
            case 45:
                sb.append("true");
                break;
            case 161:
                sb.append("debugger;\n");
                break;
            default:
                throw new IllegalStateException("Invalid keyword literal type: " + getType());
        }
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        v.visit(this);
    }
}
