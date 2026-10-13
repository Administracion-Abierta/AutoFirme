package javax.help;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/* JADX INFO: loaded from: autofirma.jar:javax/help/AppendMerge.class */
public class AppendMerge extends Merge {
    private static boolean debug = false;

    public AppendMerge(NavigatorView navigatorView, NavigatorView navigatorView2) {
        super(navigatorView, navigatorView2);
    }

    @Override // javax.help.Merge
    public TreeNode processMerge(TreeNode treeNode) {
        debug("start merge");
        mergeNodes(treeNode, this.slaveTopNode);
        return treeNode;
    }

    public static void mergeNodes(TreeNode treeNode, TreeNode treeNode2) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treeNode;
        DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode) treeNode2;
        debug(new StringBuffer().append("mergeNodes master=").append(MergeHelpUtilities.getNodeName(defaultMutableTreeNode)).append(" slave=").append(MergeHelpUtilities.getNodeName(defaultMutableTreeNode2)).toString());
        while (defaultMutableTreeNode2.getChildCount() > 0) {
            DefaultMutableTreeNode firstChild = defaultMutableTreeNode2.getFirstChild();
            defaultMutableTreeNode.add(firstChild);
            MergeHelpUtilities.mergeNodeChildren("javax.help.AppendMerge", firstChild);
        }
    }

    public static void mergeNodeChildren(TreeNode treeNode) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treeNode;
        debug(new StringBuffer().append("mergeNodes master=").append(MergeHelpUtilities.getNodeName(defaultMutableTreeNode)).toString());
        for (int i = 0; i < defaultMutableTreeNode.getChildCount(); i++) {
            DefaultMutableTreeNode childAt = defaultMutableTreeNode.getChildAt(i);
            if (!childAt.isLeaf()) {
                MergeHelpUtilities.mergeNodeChildren("javax.help.AppendMerge", childAt);
            }
        }
    }

    private static void debug(String str) {
        if (debug) {
            System.out.println(new StringBuffer().append("AppendMerge :").append(str).toString());
        }
    }
}
