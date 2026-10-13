package javax.help;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/* JADX INFO: loaded from: autofirma.jar:javax/help/UniteAppendMerge.class */
public class UniteAppendMerge extends Merge {
    private static boolean debug = false;

    public UniteAppendMerge(NavigatorView navigatorView, NavigatorView navigatorView2) {
        super(navigatorView, navigatorView2);
    }

    @Override // javax.help.Merge
    public TreeNode processMerge(TreeNode treeNode) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treeNode;
        if (defaultMutableTreeNode.equals(this.slaveTopNode)) {
            return defaultMutableTreeNode;
        }
        if (this.slaveTopNode.getChildCount() == 0) {
            return defaultMutableTreeNode;
        }
        mergeNodes(defaultMutableTreeNode, this.slaveTopNode);
        return defaultMutableTreeNode;
    }

    public static void mergeNodes(TreeNode treeNode, TreeNode treeNode2) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treeNode;
        DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode) treeNode2;
        debug(new StringBuffer().append("mergeNodes master=").append(MergeHelpUtilities.getNodeName(defaultMutableTreeNode)).append(" slave=").append(MergeHelpUtilities.getNodeName(defaultMutableTreeNode2)).toString());
        int childCount = defaultMutableTreeNode.getChildCount();
        while (defaultMutableTreeNode2.getChildCount() > 0) {
            DefaultMutableTreeNode firstChild = defaultMutableTreeNode2.getFirstChild();
            for (int i = 0; i < childCount; i++) {
                DefaultMutableTreeNode childAt = defaultMutableTreeNode.getChildAt(i);
                if (MergeHelpUtilities.compareNames(childAt, firstChild) == 0) {
                    if (MergeHelpUtilities.haveEqualID(childAt, firstChild)) {
                        MergeHelpUtilities.mergeNodes("javax.help.UniteAppendMerge", childAt, firstChild);
                        firstChild.removeFromParent();
                        firstChild = null;
                        break;
                    } else {
                        MergeHelpUtilities.markNodes(childAt, firstChild);
                        defaultMutableTreeNode.add(firstChild);
                        MergeHelpUtilities.mergeNodeChildren("javax.help.UniteAppendMerge", firstChild);
                        firstChild = null;
                        break;
                    }
                }
            }
            if (firstChild != null) {
                defaultMutableTreeNode.add(firstChild);
                MergeHelpUtilities.mergeNodeChildren("javax.help.UniteAppendMerge", firstChild);
            }
        }
        defaultMutableTreeNode2.removeFromParent();
    }

    public static void mergeNodeChildren(TreeNode treeNode) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treeNode;
        debug(new StringBuffer().append("mergeNodes master=").append(MergeHelpUtilities.getNodeName(defaultMutableTreeNode)).toString());
        for (int i = 0; i < defaultMutableTreeNode.getChildCount(); i++) {
            DefaultMutableTreeNode childAt = defaultMutableTreeNode.getChildAt(i);
            if (!childAt.isLeaf()) {
                MergeHelpUtilities.mergeNodeChildren("javax.help.UniteAppendMerge", childAt);
            }
        }
    }

    private static void debug(String str) {
        if (debug) {
            System.out.println(new StringBuffer().append("UniteAppendMerge :").append(str).toString());
        }
    }
}
