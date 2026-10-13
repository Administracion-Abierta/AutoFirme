package javax.help;

import java.util.Locale;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/* JADX INFO: loaded from: autofirma.jar:javax/help/Merge.class */
public abstract class Merge {
    protected DefaultMutableTreeNode slaveTopNode;
    protected Locale locale;
    static Class class$javax$help$NavigatorView;

    public abstract TreeNode processMerge(TreeNode treeNode);

    protected Merge(NavigatorView navigatorView, NavigatorView navigatorView2) {
        try {
            if (Class.forName("javax.help.TOCView").isInstance(navigatorView2)) {
                this.slaveTopNode = ((TOCView) navigatorView2).getDataAsTree();
            }
            if (Class.forName("javax.help.IndexView").isInstance(navigatorView2)) {
                this.slaveTopNode = ((IndexView) navigatorView2).getDataAsTree();
            }
        } catch (ClassNotFoundException e) {
            System.err.println(e);
        }
        this.locale = navigatorView.getHelpSet().getLocale();
        if (this.locale == null) {
            this.locale = Locale.getDefault();
        }
    }

    public static void mergeNodes(TreeNode treeNode, TreeNode treeNode2) {
    }

    public static void mergeNodeChildren(TreeNode treeNode) {
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/Merge$DefaultMergeFactory.class */
    public static class DefaultMergeFactory {
        public static Merge getMerge(NavigatorView navigatorView, NavigatorView navigatorView2) {
            Class<?> clsClass$;
            Class<?> clsClass$2;
            Class<?> clsLoadClass;
            Merge appendMerge;
            if (navigatorView == null || navigatorView2 == null) {
                throw new NullPointerException("masterView and/or slaveView are null");
            }
            String mergeType = navigatorView.getMergeType();
            HelpSet helpSet = navigatorView.getHelpSet();
            helpSet.getLocale();
            ClassLoader loader = helpSet.getLoader();
            if (mergeType != null) {
                try {
                    Class<?>[] clsArr = new Class[2];
                    if (Merge.class$javax$help$NavigatorView == null) {
                        clsClass$ = Merge.class$("javax.help.NavigatorView");
                        Merge.class$javax$help$NavigatorView = clsClass$;
                    } else {
                        clsClass$ = Merge.class$javax$help$NavigatorView;
                    }
                    clsArr[0] = clsClass$;
                    if (Merge.class$javax$help$NavigatorView == null) {
                        clsClass$2 = Merge.class$("javax.help.NavigatorView");
                        Merge.class$javax$help$NavigatorView = clsClass$2;
                    } else {
                        clsClass$2 = Merge.class$javax$help$NavigatorView;
                    }
                    clsArr[1] = clsClass$2;
                    Object[] objArr = {navigatorView, navigatorView2};
                    if (loader == null) {
                        clsLoadClass = Class.forName(mergeType);
                    } else {
                        clsLoadClass = loader.loadClass(mergeType);
                    }
                    appendMerge = (Merge) clsLoadClass.getConstructor(clsArr).newInstance(objArr);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(new StringBuffer().append("Could not create Merge type ").append(mergeType).toString());
                }
            } else {
                appendMerge = new AppendMerge(navigatorView, navigatorView2);
            }
            return appendMerge;
        }
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }
}
