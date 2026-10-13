package javax.help;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/* JADX INFO: loaded from: autofirma.jar:javax/help/SortMerge.class */
public class SortMerge extends Merge {
    private static boolean debug = false;

    public SortMerge(NavigatorView navigatorView, NavigatorView navigatorView2) {
        super(navigatorView, navigatorView2);
    }

    @Override // javax.help.Merge
    public TreeNode processMerge(TreeNode treeNode) {
        debug("processMerge started");
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treeNode;
        if (defaultMutableTreeNode.equals(this.slaveTopNode)) {
            return defaultMutableTreeNode;
        }
        if (this.slaveTopNode.getChildCount() == 0) {
            return defaultMutableTreeNode;
        }
        if (defaultMutableTreeNode.getChildCount() == 0) {
            MergeHelpUtilities.mergeNodeChildren("javax.help.SortMerge", this.slaveTopNode);
            while (this.slaveTopNode.getChildCount() > 0) {
                defaultMutableTreeNode.add(this.slaveTopNode.getFirstChild());
            }
            return defaultMutableTreeNode;
        }
        mergeNodes(defaultMutableTreeNode, this.slaveTopNode);
        debug("process merge ended");
        return defaultMutableTreeNode;
    }

    public static void mergeNodes(TreeNode treeNode, TreeNode treeNode2) {
        debug("mergeNodes started");
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treeNode;
        DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode) treeNode2;
        sortNode(defaultMutableTreeNode2, MergeHelpUtilities.getLocale(defaultMutableTreeNode2));
        int childCount = defaultMutableTreeNode.getChildCount();
        int i = 0;
        DefaultMutableTreeNode defaultMutableTreeNode3 = null;
        if (childCount > 0) {
            defaultMutableTreeNode3 = (DefaultMutableTreeNode) defaultMutableTreeNode.getChildAt(0);
        }
        while (defaultMutableTreeNode2.getChildCount() > 0 && defaultMutableTreeNode3 != null) {
            DefaultMutableTreeNode firstChild = defaultMutableTreeNode2.getFirstChild();
            int iCompareNames = MergeHelpUtilities.compareNames(defaultMutableTreeNode3, firstChild);
            if (iCompareNames < 0) {
                i++;
                if (i >= childCount) {
                    break;
                } else {
                    defaultMutableTreeNode3 = (DefaultMutableTreeNode) defaultMutableTreeNode.getChildAt(i);
                }
            } else if (iCompareNames > 0) {
                defaultMutableTreeNode.add(firstChild);
                MergeHelpUtilities.mergeNodeChildren("javax.help.SortMerge", firstChild);
            } else if (MergeHelpUtilities.haveEqualID(defaultMutableTreeNode3, firstChild)) {
                MergeHelpUtilities.mergeNodes("javax.help.SortMerge", defaultMutableTreeNode3, firstChild);
                firstChild.removeFromParent();
            } else {
                MergeHelpUtilities.markNodes(defaultMutableTreeNode3, firstChild);
                defaultMutableTreeNode.add(firstChild);
                MergeHelpUtilities.mergeNodeChildren("javax.help.SortMerge", firstChild);
            }
        }
        while (defaultMutableTreeNode2.getChildCount() > 0) {
            DefaultMutableTreeNode firstChild2 = defaultMutableTreeNode2.getFirstChild();
            defaultMutableTreeNode.add(firstChild2);
            MergeHelpUtilities.mergeNodeChildren("javax.help.SortMerge", firstChild2);
        }
        mergeNodeChildren(defaultMutableTreeNode);
        debug("mergeNode ended");
    }

    public static void mergeNodeChildren(TreeNode treeNode) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treeNode;
        debug(new StringBuffer().append("mergeNodeChildren master=").append(MergeHelpUtilities.getNodeName(defaultMutableTreeNode)).toString());
        sortNode(defaultMutableTreeNode, MergeHelpUtilities.getLocale(defaultMutableTreeNode));
        for (int i = 0; i < defaultMutableTreeNode.getChildCount(); i++) {
            DefaultMutableTreeNode childAt = defaultMutableTreeNode.getChildAt(i);
            if (!childAt.isLeaf()) {
                MergeHelpUtilities.mergeNodeChildren("javax.help.SortMerge", childAt);
            }
        }
    }

    public static void sortNode(DefaultMutableTreeNode defaultMutableTreeNode, Locale locale) {
        debug("sortNode");
        if (locale == null) {
            locale = Locale.getDefault();
        }
        int childCount = defaultMutableTreeNode.getChildCount();
        DefaultMutableTreeNode defaultMutableTreeNode2 = new DefaultMutableTreeNode();
        Collator collator = Collator.getInstance(locale);
        CollationKey[] collationKeyArr = new CollationKey[childCount];
        for (int i = 0; i < childCount; i++) {
            String nodeName = MergeHelpUtilities.getNodeName(defaultMutableTreeNode.getChildAt(i));
            debug(new StringBuffer().append("String , i:").append(nodeName).append(" , ").append(i).toString());
            collationKeyArr[i] = collator.getCollationKey(nodeName);
        }
        Arrays.sort(collationKeyArr);
        for (int i2 = 0; i2 < childCount; i2++) {
            DefaultMutableTreeNode childWithName = MergeHelpUtilities.getChildWithName(defaultMutableTreeNode, collationKeyArr[i2].getSourceString());
            if (childWithName != null) {
                defaultMutableTreeNode2.add(childWithName);
            }
        }
        while (defaultMutableTreeNode2.getChildCount() > 0) {
            defaultMutableTreeNode.add(defaultMutableTreeNode2.getFirstChild());
        }
        debug("end sortNode");
    }

    private static void debug(String str) {
        if (debug) {
            System.out.println(new StringBuffer().append("SortMerge :").append(str).toString());
        }
    }
}
