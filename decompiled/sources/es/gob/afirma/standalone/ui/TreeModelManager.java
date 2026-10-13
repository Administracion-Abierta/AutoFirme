package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.core.util.tree.AOTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/TreeModelManager.class */
public final class TreeModelManager {
    private final AOTreeModel tree;

    public TreeModelManager(AOTreeModel aOTreeModel) {
        this.tree = aOTreeModel;
    }

    public DefaultMutableTreeNode getSwingTree() {
        DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode();
        copyBranch((AOTreeNode) this.tree.getRoot(), defaultMutableTreeNode);
        return defaultMutableTreeNode;
    }

    private DefaultMutableTreeNode copyBranch(AOTreeNode aOTreeNode, DefaultMutableTreeNode defaultMutableTreeNode) {
        defaultMutableTreeNode.setUserObject(aOTreeNode.getUserObject());
        for (int i = 0; i < aOTreeNode.getChildCount(); i++) {
            DefaultMutableTreeNode defaultMutableTreeNode2 = new DefaultMutableTreeNode();
            copyBranch(aOTreeNode.getChildAt(i), defaultMutableTreeNode2);
            defaultMutableTreeNode.add(defaultMutableTreeNode2);
        }
        return defaultMutableTreeNode;
    }
}
