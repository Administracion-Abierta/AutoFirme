package es.gob.afirma.core.util.tree;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/util/tree/AOTreeNode.class */
public final class AOTreeNode {
    private AOTreeNode parent = null;
    private List<AOTreeNode> children;
    private transient Object userObject;

    public AOTreeNode(Object obj) {
        this.userObject = obj;
    }

    private void insert(AOTreeNode aOTreeNode, int i) {
        if (aOTreeNode == null) {
            throw new IllegalArgumentException("EL nuevo hijo es nulo");
        }
        if (isNodeAncestor(aOTreeNode)) {
            throw new IllegalArgumentException("El nuevo hijo es ya un ancestro");
        }
        AOTreeNode parent = aOTreeNode.getParent();
        if (parent != null) {
            parent.remove(aOTreeNode);
        }
        aOTreeNode.setParent(this);
        if (this.children == null) {
            this.children = new ArrayList();
        }
        this.children.add(i, aOTreeNode);
    }

    private void remove(int i) {
        AOTreeNode childAt = getChildAt(i);
        this.children.remove(i);
        childAt.setParent(null);
    }

    public void setParent(AOTreeNode aOTreeNode) {
        this.parent = aOTreeNode;
    }

    public AOTreeNode getParent() {
        return this.parent;
    }

    public AOTreeNode getChildAt(int i) {
        if (this.children == null) {
            throw new ArrayIndexOutOfBoundsException("El nodo no tiene hijos");
        }
        return this.children.get(i);
    }

    public int getChildCount() {
        if (this.children == null) {
            return 0;
        }
        return this.children.size();
    }

    private int getIndex(AOTreeNode aOTreeNode) {
        if (aOTreeNode == null) {
            throw new IllegalArgumentException("Argumento nulo");
        }
        if (!isNodeChild(aOTreeNode)) {
            return -1;
        }
        return this.children.indexOf(aOTreeNode);
    }

    public Object getUserObject() {
        return this.userObject;
    }

    private void remove(AOTreeNode aOTreeNode) {
        if (aOTreeNode == null) {
            throw new IllegalArgumentException("Argumento nulo");
        }
        if (!isNodeChild(aOTreeNode)) {
            throw new IllegalArgumentException("El argumento no es un hijo");
        }
        remove(getIndex(aOTreeNode));
    }

    public void add(AOTreeNode aOTreeNode) {
        if (aOTreeNode != null && aOTreeNode.getParent() == this) {
            insert(aOTreeNode, getChildCount() - 1);
        } else {
            insert(aOTreeNode, getChildCount());
        }
    }

    private boolean isNodeAncestor(AOTreeNode aOTreeNode) {
        if (aOTreeNode == null) {
            return false;
        }
        AOTreeNode parent = this;
        while (!parent.equals(aOTreeNode)) {
            parent = parent.getParent();
            if (parent == null) {
                return false;
            }
        }
        return true;
    }

    private boolean isNodeChild(AOTreeNode aOTreeNode) {
        boolean z;
        if (aOTreeNode == null || getChildCount() == 0) {
            z = false;
        } else {
            z = aOTreeNode.getParent() == this;
        }
        return z;
    }

    public boolean isLeaf() {
        return getChildCount() == 0;
    }

    public String toString() {
        if (this.userObject == null) {
            return "null";
        }
        return this.userObject.toString();
    }
}
