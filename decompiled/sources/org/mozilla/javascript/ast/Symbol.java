package org.mozilla.javascript.ast;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/Symbol.class */
public class Symbol {
    private int declType;
    private int index = -1;
    private String name;
    private Node node;
    private Scope containingTable;

    public Symbol() {
    }

    public Symbol(int declType, String name) {
        setName(name);
        setDeclType(declType);
    }

    public int getDeclType() {
        return this.declType;
    }

    public void setDeclType(int declType) {
        if (declType != 110 && declType != 88 && declType != 123 && declType != 154 && declType != 155) {
            throw new IllegalArgumentException("Invalid declType: " + declType);
        }
        this.declType = declType;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Node getNode() {
        return this.node;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Scope getContainingTable() {
        return this.containingTable;
    }

    public void setContainingTable(Scope containingTable) {
        this.containingTable = containingTable;
    }

    public String getDeclTypeName() {
        return Token.typeToName(this.declType);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Symbol (");
        result.append(getDeclTypeName());
        result.append(") name=");
        result.append(this.name);
        if (this.node != null) {
            result.append(" line=");
            result.append(this.node.getLineno());
        }
        return result.toString();
    }
}
