package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/ObjectProperty.class */
public class ObjectProperty extends InfixExpression {
    public void setNodeType(int nodeType) {
        if (nodeType != 104 && nodeType != 152 && nodeType != 153 && nodeType != 164) {
            throw new IllegalArgumentException("invalid node type: " + nodeType);
        }
        setType(nodeType);
    }

    public ObjectProperty() {
        this.type = 104;
    }

    public ObjectProperty(int pos) {
        super(pos);
        this.type = 104;
    }

    public ObjectProperty(int pos, int len) {
        super(pos, len);
        this.type = 104;
    }

    public void setIsGetterMethod() {
        this.type = 152;
    }

    public boolean isGetterMethod() {
        return this.type == 152;
    }

    public void setIsSetterMethod() {
        this.type = 153;
    }

    public boolean isSetterMethod() {
        return this.type == 153;
    }

    public void setIsNormalMethod() {
        this.type = 164;
    }

    public boolean isNormalMethod() {
        return this.type == 164;
    }

    public boolean isMethod() {
        return isGetterMethod() || isSetterMethod() || isNormalMethod();
    }

    @Override // org.mozilla.javascript.ast.InfixExpression, org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(makeIndent(depth + 1));
        if (isGetterMethod()) {
            sb.append("get ");
        } else if (isSetterMethod()) {
            sb.append("set ");
        }
        sb.append(this.left.toSource(getType() == 104 ? 0 : depth));
        if (this.type == 104) {
            sb.append(": ");
        }
        sb.append(this.right.toSource(getType() == 104 ? 0 : depth + 1));
        return sb.toString();
    }
}
