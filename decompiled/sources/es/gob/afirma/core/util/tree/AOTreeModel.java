package es.gob.afirma.core.util.tree;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/util/tree/AOTreeModel.class */
public final class AOTreeModel {
    private final AOTreeNode root;

    public AOTreeModel(AOTreeNode aOTreeNode) {
        this.root = aOTreeNode;
    }

    public Object getRoot() {
        return this.root;
    }

    public static Object getChild(Object obj, int i) {
        return ((AOTreeNode) obj).getChildAt(i);
    }

    public static int getChildCount(Object obj) {
        return ((AOTreeNode) obj).getChildCount();
    }
}
