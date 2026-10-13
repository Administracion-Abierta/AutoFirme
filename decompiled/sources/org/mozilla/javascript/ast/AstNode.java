package org.mozilla.javascript.ast;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/AstNode.class */
public abstract class AstNode extends Node implements Comparable<AstNode> {
    protected int position;
    protected int length;
    protected AstNode parent;
    protected AstNode inlineComment;
    private static Map<Integer, String> operatorNames = new HashMap();

    public abstract String toSource(int i);

    public abstract void visit(NodeVisitor nodeVisitor);

    static {
        operatorNames.put(52, "in");
        operatorNames.put(32, "typeof");
        operatorNames.put(53, "instanceof");
        operatorNames.put(31, "delete");
        operatorNames.put(90, ",");
        operatorNames.put(104, ":");
        operatorNames.put(105, "||");
        operatorNames.put(106, "&&");
        operatorNames.put(107, "++");
        operatorNames.put(108, "--");
        operatorNames.put(9, "|");
        operatorNames.put(10, "^");
        operatorNames.put(11, "&");
        operatorNames.put(12, "==");
        operatorNames.put(13, "!=");
        operatorNames.put(14, "<");
        operatorNames.put(16, ">");
        operatorNames.put(15, "<=");
        operatorNames.put(17, ">=");
        operatorNames.put(18, "<<");
        operatorNames.put(19, ">>");
        operatorNames.put(20, ">>>");
        operatorNames.put(21, Marker.ANY_NON_NULL_MARKER);
        operatorNames.put(22, "-");
        operatorNames.put(23, "*");
        operatorNames.put(24, "/");
        operatorNames.put(25, "%");
        operatorNames.put(26, "!");
        operatorNames.put(27, "~");
        operatorNames.put(28, Marker.ANY_NON_NULL_MARKER);
        operatorNames.put(29, "-");
        operatorNames.put(46, "===");
        operatorNames.put(47, "!==");
        operatorNames.put(91, "=");
        operatorNames.put(92, "|=");
        operatorNames.put(94, "&=");
        operatorNames.put(95, "<<=");
        operatorNames.put(96, ">>=");
        operatorNames.put(97, ">>>=");
        operatorNames.put(98, "+=");
        operatorNames.put(99, "-=");
        operatorNames.put(100, "*=");
        operatorNames.put(101, "/=");
        operatorNames.put(102, "%=");
        operatorNames.put(93, "^=");
        operatorNames.put(127, "void");
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/AstNode$PositionComparator.class */
    public static class PositionComparator implements Comparator<AstNode>, Serializable {
        private static final long serialVersionUID = 1;

        @Override // java.util.Comparator
        public int compare(AstNode n1, AstNode n2) {
            return n1.position - n2.position;
        }
    }

    public AstNode() {
        super(-1);
        this.position = -1;
        this.length = 1;
    }

    public AstNode(int pos) {
        this();
        this.position = pos;
    }

    public AstNode(int pos, int len) {
        this();
        this.position = pos;
        this.length = len;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getAbsolutePosition() {
        int pos = this.position;
        AstNode parent = this.parent;
        while (true) {
            AstNode parent2 = parent;
            if (parent2 != null) {
                pos += parent2.getPosition();
                parent = parent2.getParent();
            } else {
                return pos;
            }
        }
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setBounds(int position, int end) {
        setPosition(position);
        setLength(end - position);
    }

    public void setRelative(int parentPosition) {
        this.position -= parentPosition;
    }

    public AstNode getParent() {
        return this.parent;
    }

    public void setParent(AstNode parent) {
        if (parent == this.parent) {
            return;
        }
        if (this.parent != null) {
            setRelative(-this.parent.getAbsolutePosition());
        }
        this.parent = parent;
        if (parent != null) {
            setRelative(parent.getAbsolutePosition());
        }
    }

    public void addChild(AstNode kid) {
        assertNotNull(kid);
        int end = kid.getPosition() + kid.getLength();
        setLength(end - getPosition());
        addChildToBack(kid);
        kid.setParent(this);
    }

    public AstRoot getAstRoot() {
        AstNode parent;
        AstNode parent2 = this;
        while (true) {
            parent = parent2;
            if (parent == null || (parent instanceof AstRoot)) {
                break;
            }
            parent2 = parent.getParent();
        }
        return (AstRoot) parent;
    }

    public String toSource() {
        return toSource(0);
    }

    public String makeIndent(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    public String shortName() {
        String classname = getClass().getName();
        int last = classname.lastIndexOf(".");
        return classname.substring(last + 1);
    }

    public static String operatorToString(int op) {
        String result = operatorNames.get(Integer.valueOf(op));
        if (result == null) {
            throw new IllegalArgumentException("Invalid operator: " + op);
        }
        return result;
    }

    @Override // org.mozilla.javascript.Node
    public boolean hasSideEffects() {
        switch (getType()) {
            case -1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 30:
            case 31:
            case 35:
            case 37:
            case 38:
            case 50:
            case 51:
            case 56:
            case 57:
            case 65:
            case 69:
            case 70:
            case 71:
            case 73:
            case 82:
            case 83:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 107:
            case 108:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 130:
            case 131:
            case 132:
            case 133:
            case 135:
            case 136:
            case 140:
            case 141:
            case 142:
            case 143:
            case 154:
            case 155:
            case 159:
            case 160:
            case 166:
                return true;
            case 0:
            case 1:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 32:
            case 33:
            case 34:
            case 36:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 52:
            case 53:
            case 54:
            case 55:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 66:
            case 67:
            case 68:
            case 72:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 103:
            case 104:
            case 105:
            case 106:
            case 109:
            case 116:
            case 117:
            case 127:
            case 128:
            case 129:
            case 134:
            case 137:
            case 138:
            case 139:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 156:
            case 157:
            case 158:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            default:
                return false;
        }
    }

    protected void assertNotNull(Object arg) {
        if (arg == null) {
            throw new IllegalArgumentException("arg cannot be null");
        }
    }

    protected <T extends AstNode> void printList(List<T> items, StringBuilder sb) {
        int max = items.size();
        int count = 0;
        for (AstNode item : items) {
            sb.append(item.toSource(0));
            int i = count;
            count++;
            if (i < max - 1) {
                sb.append(", ");
            } else if (item instanceof EmptyExpression) {
                sb.append(",");
            }
        }
    }

    public static RuntimeException codeBug() throws RuntimeException {
        throw Kit.codeBug();
    }

    public FunctionNode getEnclosingFunction() {
        AstNode parent;
        AstNode parent2 = getParent();
        while (true) {
            parent = parent2;
            if (parent == null || (parent instanceof FunctionNode)) {
                break;
            }
            parent2 = parent.getParent();
        }
        return (FunctionNode) parent;
    }

    public Scope getEnclosingScope() {
        AstNode parent;
        AstNode parent2 = getParent();
        while (true) {
            parent = parent2;
            if (parent == null || (parent instanceof Scope)) {
                break;
            }
            parent2 = parent.getParent();
        }
        return (Scope) parent;
    }

    @Override // java.lang.Comparable
    public int compareTo(AstNode other) {
        if (equals(other)) {
            return 0;
        }
        int abs1 = getAbsolutePosition();
        int abs2 = other.getAbsolutePosition();
        if (abs1 < abs2) {
            return -1;
        }
        if (abs2 < abs1) {
            return 1;
        }
        int len1 = getLength();
        int len2 = other.getLength();
        if (len1 < len2) {
            return -1;
        }
        if (len2 < len1) {
            return 1;
        }
        return hashCode() - other.hashCode();
    }

    public int depth() {
        if (this.parent == null) {
            return 0;
        }
        return 1 + this.parent.depth();
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/AstNode$DebugPrintVisitor.class */
    protected static class DebugPrintVisitor implements NodeVisitor {
        private StringBuilder buffer;
        private static final int DEBUG_INDENT = 2;

        public DebugPrintVisitor(StringBuilder buf) {
            this.buffer = buf;
        }

        public String toString() {
            return this.buffer.toString();
        }

        private static String makeIndent(int depth) {
            StringBuilder sb = new StringBuilder(2 * depth);
            for (int i = 0; i < 2 * depth; i++) {
                sb.append(" ");
            }
            return sb.toString();
        }

        @Override // org.mozilla.javascript.ast.NodeVisitor
        public boolean visit(AstNode node) {
            int tt = node.getType();
            String name = Token.typeToName(tt);
            this.buffer.append(node.getAbsolutePosition()).append(TlbBase.TAB);
            this.buffer.append(makeIndent(node.depth()));
            this.buffer.append(name).append(" ");
            this.buffer.append(node.getPosition()).append(" ");
            this.buffer.append(node.getLength());
            if (tt == 39) {
                this.buffer.append(" ").append(((Name) node).getIdentifier());
            } else if (tt == 41) {
                this.buffer.append(" ").append(((StringLiteral) node).getValue(true));
            }
            this.buffer.append("\n");
            return true;
        }
    }

    @Override // org.mozilla.javascript.Node
    public int getLineno() {
        if (this.lineno != -1) {
            return this.lineno;
        }
        if (this.parent != null) {
            return this.parent.getLineno();
        }
        return -1;
    }

    public String debugPrint() {
        DebugPrintVisitor dpv = new DebugPrintVisitor(new StringBuilder(1000));
        visit(dpv);
        return dpv.toString();
    }

    public AstNode getInlineComment() {
        return this.inlineComment;
    }

    public void setInlineComment(AstNode inlineComment) {
        this.inlineComment = inlineComment;
    }
}
