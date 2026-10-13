package org.mozilla.javascript;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Node.class */
public class Node implements Iterable<Node> {
    public static final int FUNCTION_PROP = 1;
    public static final int LOCAL_PROP = 2;
    public static final int LOCAL_BLOCK_PROP = 3;
    public static final int REGEXP_PROP = 4;
    public static final int CASEARRAY_PROP = 5;
    public static final int TARGETBLOCK_PROP = 6;
    public static final int VARIABLE_PROP = 7;
    public static final int ISNUMBER_PROP = 8;
    public static final int DIRECTCALL_PROP = 9;
    public static final int SPECIALCALL_PROP = 10;
    public static final int SKIP_INDEXES_PROP = 11;
    public static final int OBJECT_IDS_PROP = 12;
    public static final int INCRDECR_PROP = 13;
    public static final int CATCH_SCOPE_PROP = 14;
    public static final int LABEL_ID_PROP = 15;
    public static final int MEMBER_TYPE_PROP = 16;
    public static final int NAME_PROP = 17;
    public static final int CONTROL_BLOCK_PROP = 18;
    public static final int PARENTHESIZED_PROP = 19;
    public static final int GENERATOR_END_PROP = 20;
    public static final int DESTRUCTURING_ARRAY_LENGTH = 21;
    public static final int DESTRUCTURING_NAMES = 22;
    public static final int DESTRUCTURING_PARAMS = 23;
    public static final int JSDOC_PROP = 24;
    public static final int EXPRESSION_CLOSURE_PROP = 25;
    public static final int DESTRUCTURING_SHORTHAND = 26;
    public static final int ARROW_FUNCTION_PROP = 27;
    public static final int LAST_PROP = 27;
    public static final int BOTH = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int NON_SPECIALCALL = 0;
    public static final int SPECIALCALL_EVAL = 1;
    public static final int SPECIALCALL_WITH = 2;
    public static final int DECR_FLAG = 1;
    public static final int POST_FLAG = 2;
    public static final int PROPERTY_FLAG = 1;
    public static final int ATTRIBUTE_FLAG = 2;
    public static final int DESCENDANTS_FLAG = 4;
    private static final Node NOT_SET = new Node(-1);
    public static final int END_UNREACHED = 0;
    public static final int END_DROPS_OFF = 1;
    public static final int END_RETURNS = 2;
    public static final int END_RETURNS_VALUE = 4;
    public static final int END_YIELDS = 8;
    protected int type;
    protected Node next;
    protected Node first;
    protected Node last;
    protected int lineno;
    protected PropListItem propListHead;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Node$PropListItem.class */
    private static class PropListItem {
        PropListItem next;
        int type;
        int intValue;
        Object objectValue;

        private PropListItem() {
        }
    }

    public Node(int nodeType) {
        this.type = -1;
        this.lineno = -1;
        this.type = nodeType;
    }

    public Node(int nodeType, Node child) {
        this.type = -1;
        this.lineno = -1;
        this.type = nodeType;
        this.last = child;
        this.first = child;
        child.next = null;
    }

    public Node(int nodeType, Node left, Node right) {
        this.type = -1;
        this.lineno = -1;
        this.type = nodeType;
        this.first = left;
        this.last = right;
        left.next = right;
        right.next = null;
    }

    public Node(int nodeType, Node left, Node mid, Node right) {
        this.type = -1;
        this.lineno = -1;
        this.type = nodeType;
        this.first = left;
        this.last = right;
        left.next = mid;
        mid.next = right;
        right.next = null;
    }

    public Node(int nodeType, int line) {
        this.type = -1;
        this.lineno = -1;
        this.type = nodeType;
        this.lineno = line;
    }

    public Node(int nodeType, Node child, int line) {
        this(nodeType, child);
        this.lineno = line;
    }

    public Node(int nodeType, Node left, Node right, int line) {
        this(nodeType, left, right);
        this.lineno = line;
    }

    public Node(int nodeType, Node left, Node mid, Node right, int line) {
        this(nodeType, left, mid, right);
        this.lineno = line;
    }

    public static Node newNumber(double number) {
        NumberLiteral n = new NumberLiteral();
        n.setNumber(number);
        return n;
    }

    public static Node newString(String str) {
        return newString(41, str);
    }

    public static Node newString(int type, String str) {
        Name name = new Name();
        name.setIdentifier(str);
        name.setType(type);
        return name;
    }

    public int getType() {
        return this.type;
    }

    public Node setType(int type) {
        this.type = type;
        return this;
    }

    public String getJsDoc() {
        Comment comment = getJsDocNode();
        if (comment != null) {
            return comment.getValue();
        }
        return null;
    }

    public Comment getJsDocNode() {
        return (Comment) getProp(24);
    }

    public void setJsDocNode(Comment jsdocNode) {
        putProp(24, jsdocNode);
    }

    public boolean hasChildren() {
        return this.first != null;
    }

    public Node getFirstChild() {
        return this.first;
    }

    public Node getLastChild() {
        return this.last;
    }

    public Node getNext() {
        return this.next;
    }

    public Node getChildBefore(Node child) {
        if (child == this.first) {
            return null;
        }
        Node n = this.first;
        while (n.next != child) {
            n = n.next;
            if (n == null) {
                throw new RuntimeException("node is not a child");
            }
        }
        return n;
    }

    public Node getLastSibling() {
        Node node = this;
        while (true) {
            Node n = node;
            if (n.next != null) {
                node = n.next;
            } else {
                return n;
            }
        }
    }

    public void addChildToFront(Node child) {
        child.next = this.first;
        this.first = child;
        if (this.last == null) {
            this.last = child;
        }
    }

    public void addChildToBack(Node child) {
        child.next = null;
        if (this.last == null) {
            this.last = child;
            this.first = child;
        } else {
            this.last.next = child;
            this.last = child;
        }
    }

    public void addChildrenToFront(Node children) {
        Node lastSib = children.getLastSibling();
        lastSib.next = this.first;
        this.first = children;
        if (this.last == null) {
            this.last = lastSib;
        }
    }

    public void addChildrenToBack(Node children) {
        if (this.last != null) {
            this.last.next = children;
        }
        this.last = children.getLastSibling();
        if (this.first == null) {
            this.first = children;
        }
    }

    public void addChildBefore(Node newChild, Node node) {
        if (newChild.next != null) {
            throw new RuntimeException("newChild had siblings in addChildBefore");
        }
        if (this.first == node) {
            newChild.next = this.first;
            this.first = newChild;
        } else {
            Node prev = getChildBefore(node);
            addChildAfter(newChild, prev);
        }
    }

    public void addChildAfter(Node newChild, Node node) {
        if (newChild.next != null) {
            throw new RuntimeException("newChild had siblings in addChildAfter");
        }
        newChild.next = node.next;
        node.next = newChild;
        if (this.last == node) {
            this.last = newChild;
        }
    }

    public void removeChild(Node child) {
        Node prev = getChildBefore(child);
        if (prev == null) {
            this.first = this.first.next;
        } else {
            prev.next = child.next;
        }
        if (child == this.last) {
            this.last = prev;
        }
        child.next = null;
    }

    public void replaceChild(Node child, Node newChild) {
        newChild.next = child.next;
        if (child == this.first) {
            this.first = newChild;
        } else {
            Node prev = getChildBefore(child);
            prev.next = newChild;
        }
        if (child == this.last) {
            this.last = newChild;
        }
        child.next = null;
    }

    public void replaceChildAfter(Node prevChild, Node newChild) {
        Node child = prevChild.next;
        newChild.next = child.next;
        prevChild.next = newChild;
        if (child == this.last) {
            this.last = newChild;
        }
        child.next = null;
    }

    public void removeChildren() {
        this.last = null;
        this.first = null;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Node$NodeIterator.class */
    public class NodeIterator implements Iterator<Node> {
        private Node cursor;
        private Node prev2;
        private Node prev = Node.NOT_SET;
        private boolean removed = false;

        public NodeIterator() {
            this.cursor = Node.this.first;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.cursor != null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Node next() {
            if (this.cursor == null) {
                throw new NoSuchElementException();
            }
            this.removed = false;
            this.prev2 = this.prev;
            this.prev = this.cursor;
            this.cursor = this.cursor.next;
            return this.prev;
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.prev == Node.NOT_SET) {
                throw new IllegalStateException("next() has not been called");
            }
            if (this.removed) {
                throw new IllegalStateException("remove() already called for current element");
            }
            if (this.prev == Node.this.first) {
                Node.this.first = this.prev.next;
            } else if (this.prev == Node.this.last) {
                this.prev2.next = null;
                Node.this.last = this.prev2;
            } else {
                this.prev2.next = this.cursor;
            }
        }
    }

    @Override // java.lang.Iterable
    public Iterator<Node> iterator() {
        return new NodeIterator();
    }

    private static final String propToString(int propType) {
        return null;
    }

    private PropListItem lookupProperty(int propType) {
        PropListItem x;
        PropListItem propListItem = this.propListHead;
        while (true) {
            x = propListItem;
            if (x == null || propType == x.type) {
                break;
            }
            propListItem = x.next;
        }
        return x;
    }

    private PropListItem ensureProperty(int propType) {
        PropListItem item = lookupProperty(propType);
        if (item == null) {
            item = new PropListItem();
            item.type = propType;
            item.next = this.propListHead;
            this.propListHead = item;
        }
        return item;
    }

    public void removeProp(int propType) {
        PropListItem x = this.propListHead;
        if (x != null) {
            PropListItem prev = null;
            while (x.type != propType) {
                prev = x;
                x = x.next;
                if (x == null) {
                    return;
                }
            }
            if (prev == null) {
                this.propListHead = x.next;
            } else {
                prev.next = x.next;
            }
        }
    }

    public Object getProp(int propType) {
        PropListItem item = lookupProperty(propType);
        if (item == null) {
            return null;
        }
        return item.objectValue;
    }

    public int getIntProp(int propType, int defaultValue) {
        PropListItem item = lookupProperty(propType);
        return item == null ? defaultValue : item.intValue;
    }

    public int getExistingIntProp(int propType) {
        PropListItem item = lookupProperty(propType);
        if (item == null) {
            Kit.codeBug();
        }
        return item.intValue;
    }

    public void putProp(int propType, Object prop) {
        if (prop == null) {
            removeProp(propType);
        } else {
            PropListItem item = ensureProperty(propType);
            item.objectValue = prop;
        }
    }

    public void putIntProp(int propType, int prop) {
        PropListItem item = ensureProperty(propType);
        item.intValue = prop;
    }

    public int getLineno() {
        return this.lineno;
    }

    public void setLineno(int lineno) {
        this.lineno = lineno;
    }

    public final double getDouble() {
        return ((NumberLiteral) this).getNumber();
    }

    public final void setDouble(double number) {
        ((NumberLiteral) this).setNumber(number);
    }

    public final String getString() {
        return ((Name) this).getIdentifier();
    }

    public final void setString(String s) {
        if (s == null) {
            Kit.codeBug();
        }
        ((Name) this).setIdentifier(s);
    }

    public Scope getScope() {
        return ((Name) this).getScope();
    }

    public void setScope(Scope s) {
        if (s == null) {
            Kit.codeBug();
        }
        if (!(this instanceof Name)) {
            throw Kit.codeBug();
        }
        ((Name) this).setScope(s);
    }

    public static Node newTarget() {
        return new Node(132);
    }

    public final int labelId() {
        if (this.type != 132 && this.type != 73 && this.type != 166) {
            Kit.codeBug();
        }
        return getIntProp(15, -1);
    }

    public void labelId(int labelId) {
        if (this.type != 132 && this.type != 73 && this.type != 166) {
            Kit.codeBug();
        }
        putIntProp(15, labelId);
    }

    public boolean hasConsistentReturnUsage() {
        int n = endCheck();
        return (n & 4) == 0 || (n & 11) == 0;
    }

    private int endCheckIf() {
        int rv;
        Node th = this.next;
        Node el = ((Jump) this).target;
        int rv2 = th.endCheck();
        if (el != null) {
            rv = rv2 | el.endCheck();
        } else {
            rv = rv2 | 1;
        }
        return rv;
    }

    private int endCheckSwitch() {
        return 0;
    }

    private int endCheckTry() {
        return 0;
    }

    private int endCheckLoop() {
        Node n;
        Node node = this.first;
        while (true) {
            n = node;
            if (n.next == this.last) {
                break;
            }
            node = n.next;
        }
        if (n.type != 6) {
            return 1;
        }
        int rv = ((Jump) n).target.next.endCheck();
        if (n.first.type == 45) {
            rv &= -2;
        }
        return rv | getIntProp(18, 0);
    }

    private int endCheckBlock() {
        int rv = 1;
        Node node = this.first;
        while (true) {
            Node n = node;
            if ((rv & 1) == 0 || n == null) {
                break;
            }
            rv = (rv & (-2)) | n.endCheck();
            node = n.next;
        }
        return rv;
    }

    private int endCheckLabel() {
        int rv = this.next.endCheck();
        return rv | getIntProp(18, 0);
    }

    private int endCheckBreak() {
        Node n = ((Jump) this).getJumpStatement();
        n.putIntProp(18, 1);
        return 0;
    }

    private int endCheck() {
        switch (this.type) {
            case 4:
                if (this.first != null) {
                    return 4;
                }
                return 2;
            case 50:
            case 122:
                return 0;
            case 73:
            case 166:
                return 8;
            case 121:
                return endCheckBreak();
            case 130:
            case 142:
                if (this.first == null) {
                    return 1;
                }
                switch (this.first.type) {
                    case 7:
                        return this.first.endCheckIf();
                    case 82:
                        return this.first.endCheckTry();
                    case 115:
                        return this.first.endCheckSwitch();
                    case 131:
                        return this.first.endCheckLabel();
                    default:
                        return endCheckBlock();
                }
            case 132:
                if (this.next != null) {
                    return this.next.endCheck();
                }
                return 1;
            case 133:
                return endCheckLoop();
            case 134:
                if (this.first != null) {
                    return this.first.endCheck();
                }
                return 1;
            default:
                return 1;
        }
    }

    public boolean hasSideEffects() {
        switch (this.type) {
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
            case 104:
            case 109:
            case 110:
            case 111:
            case 112:
            case 116:
            case 117:
            case 127:
            case 128:
            case 129:
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
            case 90:
            case 134:
                if (this.last != null) {
                    return this.last.hasSideEffects();
                }
                return true;
            case 103:
                if (this.first == null || this.first.next == null || this.first.next.next == null) {
                    Kit.codeBug();
                }
                return this.first.next.hasSideEffects() && this.first.next.next.hasSideEffects();
            case 105:
            case 106:
                if (this.first == null || this.last == null) {
                    Kit.codeBug();
                }
                return this.first.hasSideEffects() || this.last.hasSideEffects();
        }
    }

    public void resetTargets() {
        if (this.type == 126) {
            resetTargets_r();
        } else {
            Kit.codeBug();
        }
    }

    private void resetTargets_r() {
        if (this.type == 132 || this.type == 73 || this.type == 166) {
            labelId(-1);
        }
        Node node = this.first;
        while (true) {
            Node child = node;
            if (child != null) {
                child.resetTargets_r();
                node = child.next;
            } else {
                return;
            }
        }
    }

    public String toString() {
        return String.valueOf(this.type);
    }

    private void toString(ObjToIntMap printIds, StringBuilder sb) {
    }

    public String toStringTree(ScriptNode treeTop) {
        return null;
    }

    private static void toStringTreeHelper(ScriptNode treeTop, Node n, ObjToIntMap printIds, int level, StringBuilder sb) {
    }

    private static void generatePrintIds(Node n, ObjToIntMap map) {
    }

    private static void appendPrintId(Node n, ObjToIntMap printIds, StringBuilder sb) {
    }
}
