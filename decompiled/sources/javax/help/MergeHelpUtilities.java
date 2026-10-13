package javax.help;

import java.text.Collator;
import java.util.Locale;
import javax.swing.tree.DefaultMutableTreeNode;

/* JADX INFO: loaded from: autofirma.jar:javax/help/MergeHelpUtilities.class */
public class MergeHelpUtilities {
    private static boolean debug = false;
    static Class class$javax$swing$tree$TreeNode;

    public static void mergeNodes(String str, DefaultMutableTreeNode defaultMutableTreeNode, DefaultMutableTreeNode defaultMutableTreeNode2) {
        Class<?> clsClass$;
        Class<?> clsClass$2;
        if (defaultMutableTreeNode2.isLeaf()) {
            return;
        }
        String mergeType = getMergeType(defaultMutableTreeNode);
        if (mergeType == null) {
            mergeType = str;
        }
        Class<?>[] clsArr = new Class[2];
        if (class$javax$swing$tree$TreeNode == null) {
            clsClass$ = class$("javax.swing.tree.TreeNode");
            class$javax$swing$tree$TreeNode = clsClass$;
        } else {
            clsClass$ = class$javax$swing$tree$TreeNode;
        }
        clsArr[0] = clsClass$;
        if (class$javax$swing$tree$TreeNode == null) {
            clsClass$2 = class$("javax.swing.tree.TreeNode");
            class$javax$swing$tree$TreeNode = clsClass$2;
        } else {
            clsClass$2 = class$javax$swing$tree$TreeNode;
        }
        clsArr[1] = clsClass$2;
        try {
            Class.forName(mergeType).getDeclaredMethod("mergeNodes", clsArr).invoke(null, defaultMutableTreeNode, defaultMutableTreeNode2);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(new StringBuffer().append("Could not find or execute mergeNodes for ").append(mergeType).toString());
        }
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public static void mergeNodeChildren(String str, DefaultMutableTreeNode defaultMutableTreeNode) {
        Class<?> clsClass$;
        if (defaultMutableTreeNode.isLeaf()) {
            return;
        }
        String mergeType = getMergeType(defaultMutableTreeNode);
        if (mergeType == null) {
            mergeType = str;
        }
        Class<?>[] clsArr = new Class[1];
        if (class$javax$swing$tree$TreeNode == null) {
            clsClass$ = class$("javax.swing.tree.TreeNode");
            class$javax$swing$tree$TreeNode = clsClass$;
        } else {
            clsClass$ = class$javax$swing$tree$TreeNode;
        }
        clsArr[0] = clsClass$;
        try {
            Class.forName(mergeType).getDeclaredMethod("mergeNodeChildren", clsArr).invoke(null, defaultMutableTreeNode);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(new StringBuffer().append("Could not find or execute mergeNodeChildren for ").append(mergeType).toString());
        }
    }

    private static String getMergeType(DefaultMutableTreeNode defaultMutableTreeNode) {
        TreeItem treeItem = (TreeItem) defaultMutableTreeNode.getUserObject();
        if (treeItem == null) {
            return null;
        }
        return treeItem.getMergeType();
    }

    public static String getNodeName(DefaultMutableTreeNode defaultMutableTreeNode) {
        TreeItem treeItem;
        if (defaultMutableTreeNode == null || (treeItem = (TreeItem) defaultMutableTreeNode.getUserObject()) == null) {
            return null;
        }
        return treeItem.getName();
    }

    public static DefaultMutableTreeNode getChildWithName(DefaultMutableTreeNode defaultMutableTreeNode, String str) {
        DefaultMutableTreeNode defaultMutableTreeNode2 = null;
        for (int i = 0; i < defaultMutableTreeNode.getChildCount(); i++) {
            DefaultMutableTreeNode defaultMutableTreeNode3 = (DefaultMutableTreeNode) defaultMutableTreeNode.getChildAt(i);
            if (getNodeName(defaultMutableTreeNode3).equals(str)) {
                defaultMutableTreeNode2 = defaultMutableTreeNode3;
                break;
            }
        }
        return defaultMutableTreeNode2;
    }

    public static Locale getLocale(DefaultMutableTreeNode defaultMutableTreeNode) {
        Locale locale = null;
        TreeItem treeItem = (TreeItem) defaultMutableTreeNode.getUserObject();
        if (treeItem != null) {
            locale = treeItem.getLocale();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }

    public static int compareNames(DefaultMutableTreeNode defaultMutableTreeNode, DefaultMutableTreeNode defaultMutableTreeNode2) {
        TreeItem treeItem = (TreeItem) defaultMutableTreeNode.getUserObject();
        debug(new StringBuffer().append("haveEqualName - master:").append(treeItem).toString());
        TreeItem treeItem2 = (TreeItem) defaultMutableTreeNode2.getUserObject();
        debug(new StringBuffer().append("haveEqualName - slave:").append(treeItem2).toString());
        String name = treeItem.getName();
        if (name == null) {
            name = " ";
        }
        String name2 = treeItem2.getName();
        if (name2 == null) {
            name2 = " ";
        }
        return Collator.getInstance(getLocale(defaultMutableTreeNode)).compare(name, name2);
    }

    public static boolean haveEqualID(DefaultMutableTreeNode defaultMutableTreeNode, DefaultMutableTreeNode defaultMutableTreeNode2) {
        TreeItem treeItem = (TreeItem) defaultMutableTreeNode.getUserObject();
        TreeItem treeItem2 = (TreeItem) defaultMutableTreeNode2.getUserObject();
        if (treeItem.getID() == null) {
            if (treeItem2.getID() == null) {
                return true;
            }
            return false;
        }
        if (treeItem2.getID() == null) {
            return false;
        }
        Map.ID id = treeItem.getID();
        Map.ID id2 = treeItem2.getID();
        if (id.id == null) {
            if (id2.id == null) {
                return true;
            }
            return false;
        }
        if (id2.id == null) {
            return false;
        }
        return id.id.equals(id2.id);
    }

    public static void markNodes(DefaultMutableTreeNode defaultMutableTreeNode, DefaultMutableTreeNode defaultMutableTreeNode2) {
        debug("MarkNodes");
        TreeItem treeItem = (TreeItem) defaultMutableTreeNode.getUserObject();
        TreeItem treeItem2 = (TreeItem) defaultMutableTreeNode2.getUserObject();
        HelpSet helpSet = treeItem.getHelpSet();
        HelpSet helpSet2 = treeItem2.getHelpSet();
        if (treeItem.getName() != null) {
            treeItem.setName(new StringBuffer().append(treeItem.getName()).append("(").append(helpSet.getTitle()).append(")").toString());
        } else {
            treeItem.setName(new StringBuffer().append(treeItem.getName()).append("(").append(helpSet.getTitle()).append(")").toString());
        }
        if (treeItem2.getName() != null) {
            treeItem2.setName(new StringBuffer().append(treeItem2.getName()).append("(").append(helpSet2.getTitle()).append(")").toString());
        } else {
            treeItem2.setName(new StringBuffer().append(treeItem2.getName()).append("(").append(helpSet2.getTitle()).append(")").toString());
        }
    }

    private static void debug(String str) {
        if (debug) {
            System.out.println(new StringBuffer().append("MergeHelpUtilities :").append(str).toString());
        }
    }
}
