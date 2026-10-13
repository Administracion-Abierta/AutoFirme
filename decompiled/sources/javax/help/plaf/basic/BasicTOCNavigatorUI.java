package javax.help.plaf.basic;

import com.aowagie.text.ElementTags;
import com.sun.java.help.impl.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import javax.help.BadIDException;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.InvalidHelpSetContextException;
import javax.help.JHelpNavigator;
import javax.help.JHelpTOCNavigator;
import javax.help.Map;
import javax.help.Merge;
import javax.help.MergeHelpUtilities;
import javax.help.NavigatorView;
import javax.help.Popup;
import javax.help.Presentation;
import javax.help.TOCItem;
import javax.help.TOCView;
import javax.help.TreeItem;
import javax.help.WindowPresentation;
import javax.help.event.HelpModelEvent;
import javax.help.event.HelpModelListener;
import javax.help.plaf.HelpNavigatorUI;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.RepaintManager;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicTOCNavigatorUI.class */
public class BasicTOCNavigatorUI extends HelpNavigatorUI implements HelpModelListener, TreeSelectionListener, PropertyChangeListener, ComponentListener, Serializable {
    protected JHelpTOCNavigator toc;
    protected JScrollPane sp;
    protected DefaultMutableTreeNode topNode;
    protected JTree tree;
    private boolean inInstallUI = false;
    private SwingWorker worker = null;
    protected static final boolean debug = false;
    static Class class$javax$help$HelpSet;
    static Class class$java$lang$String;

    public static ComponentUI createUI(JComponent jComponent) {
        return new BasicTOCNavigatorUI((JHelpTOCNavigator) jComponent);
    }

    public BasicTOCNavigatorUI(JHelpTOCNavigator jHelpTOCNavigator) {
        debug(new StringBuffer().append(this).append(" ").append("CreateUI - sort of").toString());
        ImageIcon imageIcon = getImageIcon(jHelpTOCNavigator.getNavigatorView());
        if (imageIcon != null) {
            setIcon(imageIcon);
        } else {
            setIcon(UIManager.getIcon("TOCNav.icon"));
        }
    }

    public void installUI(JComponent jComponent) {
        debug(new StringBuffer().append(this).append(" ").append("installUI").toString());
        this.inInstallUI = true;
        this.toc = (JHelpTOCNavigator) jComponent;
        HelpModel model = this.toc.getModel();
        this.toc.setLayout(new BorderLayout());
        this.toc.addPropertyChangeListener(this);
        this.toc.addComponentListener(this);
        if (model != null) {
            model.addHelpModelListener(this);
        }
        this.topNode = new DefaultMutableTreeNode();
        this.tree = new JTree(this.topNode);
        this.tree.getSelectionModel().addTreeSelectionListener(this);
        this.tree.setShowsRootHandles(false);
        this.tree.setRootVisible(false);
        setCellRenderer(this.toc.getNavigatorView(), this.tree);
        this.sp = new JScrollPane();
        this.sp.getViewport().add(this.tree);
        this.toc.add("Center", this.sp);
        reloadData();
        this.inInstallUI = false;
    }

    protected void setCellRenderer(NavigatorView navigatorView, JTree jTree) {
        jTree.setCellRenderer(new BasicTOCCellRenderer(navigatorView.getHelpSet().getCombinedMap(), (TOCView) navigatorView));
    }

    public void uninstallUI(JComponent jComponent) {
        debug(new StringBuffer().append(this).append(" ").append("unistallUI").toString());
        HelpModel model = this.toc.getModel();
        this.toc.removeComponentListener(this);
        this.toc.removePropertyChangeListener(this);
        this.tree.getSelectionModel().removeTreeSelectionListener(this);
        this.toc.setLayout(null);
        this.toc.removeAll();
        if (model != null) {
            model.removeHelpModelListener(this);
        }
        this.toc = null;
    }

    public Dimension getPreferredSize(JComponent jComponent) {
        return new Dimension(200, 100);
    }

    public Dimension getMinimumSize(JComponent jComponent) {
        return new Dimension(100, 100);
    }

    public Dimension getMaximumSize(JComponent jComponent) {
        return new Dimension(32767, 32767);
    }

    private void reloadData() {
        debug("reloadData");
        TOCView tOCView = (TOCView) this.toc.getNavigatorView();
        if (this.worker != null) {
            this.worker.interrupt();
        }
        this.worker = new NavSwingWorker(this, tOCView);
        this.worker.start(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized Object loadData(TOCView tOCView) {
        if (tOCView == null) {
            return Boolean.FALSE;
        }
        this.topNode.removeAllChildren();
        String mergeType = tOCView.getMergeType();
        tOCView.getHelpSet().getLocale();
        DefaultMutableTreeNode dataAsTree = tOCView.getDataAsTree();
        MergeHelpUtilities.mergeNodeChildren(mergeType, dataAsTree);
        while (dataAsTree.getChildCount() > 0) {
            this.topNode.add(dataAsTree.getFirstChild());
        }
        addSubHelpSets(tOCView.getHelpSet());
        return Boolean.TRUE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void presentData() {
        Map.ID currentID;
        this.tree.getModel().reload();
        setVisibility(this.topNode);
        if (this.toc.getModel() != null && (currentID = this.toc.getModel().getCurrentID()) != null) {
            selectNode(findID(this.topNode, currentID));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicTOCNavigatorUI$NavSwingWorker.class */
    private class NavSwingWorker extends SwingWorker {
        TOCView view;
        private final BasicTOCNavigatorUI this$0;

        public NavSwingWorker(BasicTOCNavigatorUI basicTOCNavigatorUI, TOCView tOCView) {
            this.this$0 = basicTOCNavigatorUI;
            this.view = tOCView;
        }

        @Override // com.sun.java.help.impl.SwingWorker
        public Object construct() {
            return this.this$0.loadData(this.view);
        }

        @Override // com.sun.java.help.impl.SwingWorker
        public void finished() {
            if (((Boolean) get()) == Boolean.TRUE) {
                this.this$0.presentData();
            }
        }
    }

    private void reloadData(HelpModel helpModel) {
        debug("reloadData using new model");
        TOCView tOCView = null;
        HelpSet helpSet = helpModel.getHelpSet();
        String name = ((TOCView) this.toc.getNavigatorView()).getName();
        NavigatorView[] navigatorViews = helpSet.getNavigatorViews();
        for (int i = 0; i < navigatorViews.length; i++) {
            if (navigatorViews[i].getName().equals(name)) {
                NavigatorView navigatorView = navigatorViews[i];
                if (navigatorView instanceof TOCView) {
                    tOCView = (TOCView) navigatorView;
                    break;
                }
            }
        }
        if (this.worker != null) {
            this.worker.interrupt();
        }
        this.worker = new NavSwingWorker(this, tOCView);
        this.worker.start(1);
    }

    protected void addSubHelpSets(HelpSet helpSet) {
        debug("addSubHelpSets");
        Enumeration helpSets = helpSet.getHelpSets();
        while (helpSets.hasMoreElements()) {
            HelpSet helpSet2 = (HelpSet) helpSets.nextElement();
            NavigatorView[] navigatorViews = helpSet2.getNavigatorViews();
            for (int i = 0; i < navigatorViews.length; i++) {
                if (this.toc.canMerge(navigatorViews[i])) {
                    doMerge(navigatorViews[i]);
                }
            }
            addSubHelpSets(helpSet2);
        }
    }

    private void expand(String str) {
        debug("expand called");
        Enumeration enumerationElements = findNodes(str).elements();
        while (enumerationElements.hasMoreElements()) {
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) enumerationElements.nextElement();
            debug(new StringBuffer().append("expandPath :").append(defaultMutableTreeNode).toString());
            if (defaultMutableTreeNode.getChildCount() > 0) {
                this.tree.makeVisible(new TreePath(defaultMutableTreeNode.getFirstChild().getPath()));
            } else {
                this.tree.makeVisible(new TreePath(defaultMutableTreeNode.getPath()));
            }
        }
    }

    private Vector findNodes(String str) {
        Enumeration enumerationPreorderEnumeration = this.topNode.preorderEnumeration();
        Vector vector = new Vector();
        while (enumerationPreorderEnumeration.hasMoreElements()) {
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) enumerationPreorderEnumeration.nextElement();
            debug(new StringBuffer().append(" node :").append(defaultMutableTreeNode.toString()).toString());
            if (defaultMutableTreeNode != null) {
                TOCItem tOCItem = (TOCItem) defaultMutableTreeNode.getUserObject();
                if (tOCItem == null) {
                    debug("tocItem is null");
                } else {
                    Map.ID id = tOCItem.getID();
                    if (id != null) {
                        debug(new StringBuffer().append("id name :").append(id.id).toString());
                        debug(new StringBuffer().append("target :").append(str).toString());
                        try {
                            if (id.equals(Map.ID.create(str, this.toc.getModel().getHelpSet()))) {
                                vector.addElement(defaultMutableTreeNode);
                            }
                        } catch (BadIDException e) {
                            System.err.println(new StringBuffer().append("Not valid ID :").append(str).toString());
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        return vector;
    }

    private void collapse(String str) {
        Enumeration enumerationElements = findNodes(str).elements();
        debug("collapse called");
        while (enumerationElements.hasMoreElements()) {
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) enumerationElements.nextElement();
            if (defaultMutableTreeNode.getChildCount() > 0) {
                TreePath treePath = new TreePath(defaultMutableTreeNode.getPath());
                this.tree.collapsePath(treePath);
                this.tree.collapseRow(this.tree.getRowForPath(treePath));
            } else {
                this.tree.collapseRow(this.tree.getRowForPath(new TreePath(defaultMutableTreeNode.getParent().getPath())));
            }
        }
    }

    public void doMerge(NavigatorView navigatorView) {
        debug("merging data");
        Merge merge = Merge.DefaultMergeFactory.getMerge(this.toc.getNavigatorView(), navigatorView);
        if (merge != null) {
            merge.processMerge(this.topNode);
        }
    }

    @Override // javax.help.plaf.HelpNavigatorUI
    public void merge(NavigatorView navigatorView) {
        debug(new StringBuffer().append("merging ").append(navigatorView).toString());
        doMerge(navigatorView);
        this.tree.getModel().reload();
        setVisibility(this.topNode);
    }

    @Override // javax.help.plaf.HelpNavigatorUI
    public void remove(NavigatorView navigatorView) {
        debug(new StringBuffer().append("removing ").append(navigatorView).toString());
        remove(this.topNode, navigatorView.getHelpSet());
        this.tree.getModel().reload();
        setVisibility(this.topNode);
    }

    private void remove(DefaultMutableTreeNode defaultMutableTreeNode, HelpSet helpSet) {
        debug(new StringBuffer().append("remove(").append(defaultMutableTreeNode).append(", ").append(helpSet).append(")").toString());
        Vector vector = new Vector();
        Enumeration enumerationChildren = defaultMutableTreeNode.children();
        while (enumerationChildren.hasMoreElements()) {
            DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode) enumerationChildren.nextElement();
            debug(new StringBuffer().append("  considering ").append(defaultMutableTreeNode2).toString());
            TOCItem tOCItem = (TOCItem) defaultMutableTreeNode2.getUserObject();
            HelpSet helpSet2 = tOCItem.getHelpSet();
            debug(new StringBuffer().append("chs=").append(helpSet2).append(" hs.contains(chs)=").append(helpSet.contains(helpSet2)).toString());
            if (helpSet2 != null && helpSet.contains(helpSet2)) {
                if (defaultMutableTreeNode2.isLeaf()) {
                    debug(new StringBuffer().append("  tagging for removal: ").append(defaultMutableTreeNode2).toString());
                    vector.addElement(defaultMutableTreeNode2);
                } else {
                    remove(defaultMutableTreeNode2, helpSet);
                    if (defaultMutableTreeNode2.isLeaf()) {
                        debug(new StringBuffer().append("  tagging for removal: ").append(defaultMutableTreeNode2).toString());
                        vector.addElement(defaultMutableTreeNode2);
                    } else {
                        tOCItem.setHelpSet(((TOCItem) defaultMutableTreeNode2.getFirstChild().getUserObject()).getHelpSet());
                        debug(new StringBuffer().append("  orphaned children - changing hs: ").append(defaultMutableTreeNode2).toString());
                    }
                }
            } else {
                remove(defaultMutableTreeNode2, helpSet);
            }
        }
        for (int i = 0; i < vector.size(); i++) {
            debug(new StringBuffer().append("  removing ").append(vector.elementAt(i)).toString());
            defaultMutableTreeNode.remove((DefaultMutableTreeNode) vector.elementAt(i));
        }
    }

    private void setVisibility(DefaultMutableTreeNode defaultMutableTreeNode) {
        TOCItem tOCItem = (TOCItem) defaultMutableTreeNode.getUserObject();
        int expansionType = -1;
        if (tOCItem != null) {
            expansionType = tOCItem.getExpansionType();
        }
        int pathCount = new TreePath(defaultMutableTreeNode.getPath()).getPathCount();
        if (defaultMutableTreeNode == this.topNode || ((pathCount <= 2 && expansionType == -1) || expansionType == 1)) {
            this.tree.expandPath(new TreePath(defaultMutableTreeNode.getPath()));
            if (!defaultMutableTreeNode.isLeaf()) {
                int childCount = defaultMutableTreeNode.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    setVisibility((DefaultMutableTreeNode) defaultMutableTreeNode.getChildAt(i));
                }
            }
        }
    }

    @Override // javax.help.event.HelpModelListener
    public synchronized void idChanged(HelpModelEvent helpModelEvent) {
        TOCItem tOCItem;
        Map.ID id;
        Map.ID id2 = helpModelEvent.getID();
        HelpModel model = this.toc.getModel();
        debug(new StringBuffer().append("idChanged(").append(helpModelEvent).append(")").toString());
        if (helpModelEvent.getSource() != model) {
            debug("Internal inconsistency!");
            debug(new StringBuffer().append("  ").append(helpModelEvent.getSource()).append(" != ").append(model).toString());
            throw new Error("Internal error");
        }
        if (id2 == null) {
            id2 = model.getHelpSet().getCombinedMap().getClosestID(helpModelEvent.getURL());
        }
        TreePath selectionPath = this.tree.getSelectionPath();
        if (selectionPath != null) {
            Object lastPathComponent = selectionPath.getLastPathComponent();
            if ((lastPathComponent instanceof DefaultMutableTreeNode) && (tOCItem = (TOCItem) ((DefaultMutableTreeNode) lastPathComponent).getUserObject()) != null && (id = tOCItem.getID()) != null && id.equals(id2)) {
                return;
            }
        }
        selectNode(findID(this.topNode, id2));
    }

    private DefaultMutableTreeNode findID(DefaultMutableTreeNode defaultMutableTreeNode, Map.ID id) {
        debug(new StringBuffer().append("findID: (").append(id).append(")").toString());
        debug(new StringBuffer().append("  node: ").append(defaultMutableTreeNode).toString());
        if (id == null) {
            return null;
        }
        TOCItem tOCItem = (TOCItem) defaultMutableTreeNode.getUserObject();
        if (tOCItem != null) {
            Map.ID id2 = tOCItem.getID();
            debug(new StringBuffer().append("  testID: ").append(id2).toString());
            if (id2 != null && id2.equals(id)) {
                return defaultMutableTreeNode;
            }
        }
        int childCount = defaultMutableTreeNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode defaultMutableTreeNodeFindID = findID((DefaultMutableTreeNode) defaultMutableTreeNode.getChildAt(i), id);
            if (defaultMutableTreeNodeFindID != null) {
                return defaultMutableTreeNodeFindID;
            }
        }
        return null;
    }

    private void selectNode(DefaultMutableTreeNode defaultMutableTreeNode) {
        if (defaultMutableTreeNode == null) {
            this.tree.clearSelection();
            return;
        }
        TreePath treePath = new TreePath(defaultMutableTreeNode.getPath());
        this.tree.expandPath(treePath);
        this.tree.setSelectionPath(treePath);
        this.tree.scrollPathToVisible(treePath);
    }

    protected JHelpNavigator getHelpNavigator() {
        return this.toc;
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        TOCItem tOCItem;
        Class<?> clsClass$;
        Class<?> clsClass$2;
        Class<?> clsLoadClass;
        JHelpNavigator helpNavigator = getHelpNavigator();
        HelpModel model = helpNavigator.getModel();
        debug(new StringBuffer().append("ValueChanged: ").append(treeSelectionEvent).toString());
        debug(new StringBuffer().append("  model: ").append(model).toString());
        TreeItem[] treeItemArr = null;
        TreePath[] selectionPaths = this.tree.getSelectionPaths();
        if (selectionPaths != null) {
            treeItemArr = new TreeItem[selectionPaths.length];
            for (int i = 0; i < selectionPaths.length; i++) {
                if (selectionPaths[i] != null) {
                    treeItemArr[i] = (TreeItem) ((DefaultMutableTreeNode) selectionPaths[i].getLastPathComponent()).getUserObject();
                }
            }
        }
        helpNavigator.setSelectedItems(treeItemArr);
        if (treeItemArr != null && treeItemArr.length == 1 && (tOCItem = (TOCItem) treeItemArr[0]) != null && tOCItem.getID() != null) {
            String presentation = tOCItem.getPresentation();
            if (presentation == null) {
                try {
                    model.setCurrentID(tOCItem.getID(), tOCItem.getName(), helpNavigator);
                    return;
                } catch (InvalidHelpSetContextException e) {
                    System.err.println(new StringBuffer().append("BadID: ").append(tOCItem.getID()).toString());
                    return;
                }
            }
            if (this.inInstallUI) {
                this.tree.clearSelection();
                return;
            }
            HelpSet helpSet = model.getHelpSet();
            Class<?>[] clsArr = new Class[2];
            if (class$javax$help$HelpSet == null) {
                clsClass$ = class$("javax.help.HelpSet");
                class$javax$help$HelpSet = clsClass$;
            } else {
                clsClass$ = class$javax$help$HelpSet;
            }
            clsArr[0] = clsClass$;
            if (class$java$lang$String == null) {
                clsClass$2 = class$("java.lang.String");
                class$java$lang$String = clsClass$2;
            } else {
                clsClass$2 = class$java$lang$String;
            }
            clsArr[1] = clsClass$2;
            Object[] objArr = {helpSet, tOCItem.getPresentationName()};
            try {
                ClassLoader loader = helpSet.getLoader();
                if (loader == null) {
                    clsLoadClass = Class.forName(presentation);
                } else {
                    clsLoadClass = loader.loadClass(presentation);
                }
                Presentation presentation2 = (Presentation) clsLoadClass.getMethod("getPresentation", clsArr).invoke(null, objArr);
                if (presentation2 == null) {
                    return;
                }
                if (presentation2 instanceof WindowPresentation) {
                    ((WindowPresentation) presentation2).setActivationObject(this.tree);
                }
                if (presentation2 instanceof Popup) {
                    ((Popup) presentation2).setInvokerInternalBounds(this.tree.getPathBounds(selectionPaths[0]));
                    ((Popup) presentation2).setInvoker(this.tree);
                }
                try {
                    presentation2.setCurrentID(tOCItem.getID());
                    presentation2.setDisplayed(true);
                } catch (InvalidHelpSetContextException e2) {
                    System.err.println(new StringBuffer().append("BadID: ").append(tOCItem.getID()).toString());
                }
            } catch (Exception e3) {
                throw new RuntimeException("error invoking presentation");
            }
        }
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        debug(new StringBuffer().append(this).append(" ").append("propertyChange: ").append(propertyChangeEvent.getSource()).append(" ").append(propertyChangeEvent.getPropertyName()).toString());
        if (propertyChangeEvent.getSource() == this.toc) {
            String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName.equals("helpModel")) {
                reloadData((HelpModel) propertyChangeEvent.getNewValue());
                return;
            }
            if (propertyName.equals(ElementTags.FONT)) {
                debug("Font change");
                this.tree.setFont((Font) propertyChangeEvent.getNewValue());
                RepaintManager.currentManager(this.tree).markCompletelyDirty(this.tree);
                return;
            }
            if (propertyName.equals("expand")) {
                debug("Expand change");
                expand((String) propertyChangeEvent.getNewValue());
            } else if (propertyName.equals("collapse")) {
                debug("Collapse change");
                collapse((String) propertyChangeEvent.getNewValue());
            }
        }
    }

    public void componentResized(ComponentEvent componentEvent) {
    }

    public void componentMoved(ComponentEvent componentEvent) {
    }

    public void componentShown(ComponentEvent componentEvent) {
        this.tree.requestFocus();
    }

    public void componentHidden(ComponentEvent componentEvent) {
    }

    protected static void debug(String str) {
    }
}
