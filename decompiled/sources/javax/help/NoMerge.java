package javax.help;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/* JADX INFO: loaded from: autofirma.jar:javax/help/NoMerge.class */
public class NoMerge extends Merge {
    private static boolean debug = false;

    public NoMerge(NavigatorView navigatorView, NavigatorView navigatorView2) {
        super(navigatorView, navigatorView2);
    }

    @Override // javax.help.Merge
    public TreeNode processMerge(TreeNode treeNode) {
        debug("start merge");
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treeNode;
        MergeHelpUtilities.mergeNodeChildren("javax.help.NoMerge", defaultMutableTreeNode);
        return defaultMutableTreeNode;
    }

    public static void mergeNodes(TreeNode treeNode, TreeNode treeNode2) {
    }

    public static void mergeNodeChildren(TreeNode treeNode) {
    }

    private static void debug(String str) {
        if (debug) {
            System.out.println(new StringBuffer().append("NoMerge :").append(str).toString());
        }
    }
}
