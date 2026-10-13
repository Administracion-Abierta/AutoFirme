package javax.help.plaf.basic;

import com.aowagie.text.ElementTags;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Enumeration;
import java.util.Vector;
import javax.help.BadIDException;
import javax.help.GlossaryView;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.IndexItem;
import javax.help.InvalidHelpSetContextException;
import javax.help.JHelpContentViewer;
import javax.help.JHelpGlossaryNavigator;
import javax.help.JHelpNavigator;
import javax.help.Map;
import javax.help.Merge;
import javax.help.MergeHelpUtilities;
import javax.help.NavigatorView;
import javax.help.TreeItem;
import javax.help.event.HelpModelEvent;
import javax.help.event.HelpModelListener;
import javax.help.plaf.HelpNavigatorUI;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.RepaintManager;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicGlossaryNavigatorUI.class */
public class BasicGlossaryNavigatorUI extends HelpNavigatorUI implements HelpModelListener, TreeSelectionListener, PropertyChangeListener, ActionListener, Serializable {
    protected JHelpGlossaryNavigator glossary;
    protected JScrollPane sp;
    protected DefaultMutableTreeNode topNode;
    protected JTree tree;
    protected JTextField searchField;
    protected RuleBasedCollator rbc;
    protected String oldText;
    protected DefaultMutableTreeNode currentFindNode;
    protected JHelpContentViewer viewer;
    private static boolean debug = false;

    public static ComponentUI createUI(JComponent jComponent) {
        return new BasicGlossaryNavigatorUI((JHelpGlossaryNavigator) jComponent);
    }

    public BasicGlossaryNavigatorUI(JHelpGlossaryNavigator jHelpGlossaryNavigator) {
        ImageIcon imageIcon = getImageIcon(jHelpGlossaryNavigator.getNavigatorView());
        if (imageIcon != null) {
            setIcon(imageIcon);
        } else {
            setIcon(UIManager.getIcon("GlossaryNav.icon"));
        }
    }

    public void installUI(JComponent jComponent) {
        debug("installUI");
        this.glossary = (JHelpGlossaryNavigator) jComponent;
        HelpModel model = this.glossary.getModel();
        this.glossary.setLayout(new BorderLayout());
        this.glossary.addPropertyChangeListener(this);
        if (model != null) {
        }
        this.topNode = new DefaultMutableTreeNode();
        JLabel jLabel = new JLabel(HelpUtilities.getString(HelpUtilities.getLocale(jComponent), "index.findLabel"));
        this.searchField = new JTextField();
        jLabel.setLabelFor(this.searchField);
        this.searchField.addActionListener(this);
        Component jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, 0));
        jPanel.add(jLabel);
        jPanel.add(this.searchField);
        this.glossary.add("North", jPanel);
        this.tree = new JTree(this.topNode);
        this.tree.getSelectionModel().addTreeSelectionListener(this);
        this.tree.setShowsRootHandles(true);
        this.tree.setRootVisible(false);
        setCellRenderer(this.glossary.getNavigatorView(), this.tree);
        this.sp = new JScrollPane();
        this.sp.getViewport().add(this.tree);
        Component jSplitPane = new JSplitPane(0, false);
        jSplitPane.setOneTouchExpandable(true);
        jSplitPane.setTopComponent(this.sp);
        this.viewer = new JHelpContentViewer(model.getHelpSet());
        this.viewer.setSynch(false);
        jSplitPane.setBottomComponent(this.viewer);
        this.glossary.add("Center", jSplitPane);
        jSplitPane.setDividerLocation(180);
        reloadData();
    }

    protected void setCellRenderer(NavigatorView navigatorView, JTree jTree) {
        jTree.setCellRenderer(new BasicIndexCellRenderer());
    }

    public void uninstallUI(JComponent jComponent) {
        debug("uninstallUI");
        HelpModel model = this.glossary.getModel();
        this.glossary.removePropertyChangeListener(this);
        this.tree.getSelectionModel().removeTreeSelectionListener(this);
        this.glossary.setLayout(null);
        this.glossary.removeAll();
        if (model != null) {
            model.removeHelpModelListener(this);
        }
        this.glossary = null;
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
        loadData((GlossaryView) this.glossary.getNavigatorView());
    }

    private void loadData(GlossaryView glossaryView) {
        if (glossaryView == null) {
            return;
        }
        this.topNode.removeAllChildren();
        String mergeType = glossaryView.getMergeType();
        glossaryView.getHelpSet().getLocale();
        DefaultMutableTreeNode dataAsTree = glossaryView.getDataAsTree();
        MergeHelpUtilities.mergeNodeChildren(mergeType, dataAsTree);
        while (dataAsTree.getChildCount() > 0) {
            this.topNode.add(dataAsTree.getFirstChild());
        }
        addSubHelpSets(glossaryView.getHelpSet());
        this.tree.getModel().reload();
        setVisibility(this.topNode);
    }

    private void reloadData(HelpModel helpModel) {
        debug("reloadData in using new model");
        GlossaryView glossaryView = null;
        HelpSet helpSet = helpModel.getHelpSet();
        String name = ((GlossaryView) this.glossary.getNavigatorView()).getName();
        NavigatorView[] navigatorViews = helpSet.getNavigatorViews();
        for (int i = 0; i < navigatorViews.length; i++) {
            if (navigatorViews[i].getName().equals(name)) {
                NavigatorView navigatorView = navigatorViews[i];
                if (navigatorView instanceof GlossaryView) {
                    glossaryView = (GlossaryView) navigatorView;
                    break;
                }
            }
        }
        loadData(glossaryView);
    }

    protected void addSubHelpSets(HelpSet helpSet) {
        debug("addSubHelpSets");
        Enumeration helpSets = helpSet.getHelpSets();
        while (helpSets.hasMoreElements()) {
            HelpSet helpSet2 = (HelpSet) helpSets.nextElement();
            NavigatorView[] navigatorViews = helpSet2.getNavigatorViews();
            for (int i = 0; i < navigatorViews.length; i++) {
                if (this.glossary.canMerge(navigatorViews[i])) {
                    merge(navigatorViews[i]);
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
                IndexItem indexItem = (IndexItem) defaultMutableTreeNode.getUserObject();
                if (indexItem == null) {
                    debug("indexItem is null");
                } else {
                    Map.ID id = indexItem.getID();
                    if (id != null) {
                        debug(new StringBuffer().append("id name :").append(id.id).toString());
                        debug(new StringBuffer().append("target :").append(str).toString());
                        try {
                            if (id.equals(Map.ID.create(str, this.glossary.getModel().getHelpSet()))) {
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
        Merge merge = Merge.DefaultMergeFactory.getMerge(this.glossary.getNavigatorView(), navigatorView);
        if (merge != null) {
            merge.processMerge(this.topNode);
        }
    }

    @Override // javax.help.plaf.HelpNavigatorUI
    public void merge(NavigatorView navigatorView) {
        debug("merging data");
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
            IndexItem indexItem = (IndexItem) defaultMutableTreeNode2.getUserObject();
            HelpSet helpSet2 = indexItem.getHelpSet();
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
                        indexItem.setHelpSet(((IndexItem) defaultMutableTreeNode2.getFirstChild().getUserObject()).getHelpSet());
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
        IndexItem indexItem = (IndexItem) defaultMutableTreeNode.getUserObject();
        if (defaultMutableTreeNode == this.topNode || (indexItem != null && indexItem.getExpansionType() != 0)) {
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
    public void idChanged(HelpModelEvent helpModelEvent) {
        IndexItem indexItem;
        Map.ID id;
        Map.ID id2 = helpModelEvent.getID();
        HelpModel model = this.glossary.getModel();
        debug(new StringBuffer().append("idChanged(").append(helpModelEvent).append(")").toString());
        if (helpModelEvent.getSource() != model) {
            System.err.println("Internal inconsistency!");
            System.err.println(new StringBuffer().append("  ").append(helpModelEvent.getSource()).append(" != ").append(model).toString());
            throw new Error("Internal error");
        }
        if (id2 == null) {
        }
        TreePath selectionPath = this.tree.getSelectionPath();
        if (selectionPath != null) {
            Object lastPathComponent = selectionPath.getLastPathComponent();
            if ((lastPathComponent instanceof DefaultMutableTreeNode) && (indexItem = (IndexItem) ((DefaultMutableTreeNode) lastPathComponent).getUserObject()) != null && (id = indexItem.getID()) != null && id.equals(id2)) {
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
        IndexItem indexItem = (IndexItem) defaultMutableTreeNode.getUserObject();
        if (indexItem != null) {
            Map.ID id2 = indexItem.getID();
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

    protected JHelpContentViewer getContentViewer() {
        return this.viewer;
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
        return this.glossary;
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        IndexItem indexItem;
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
        if (treeItemArr != null && treeItemArr.length == 1 && (indexItem = (IndexItem) treeItemArr[0]) != null && indexItem.getID() != null) {
            try {
                getContentViewer().getModel().setCurrentID(indexItem.getID(), indexItem.getName(), helpNavigator);
            } catch (InvalidHelpSetContextException e) {
                System.err.println(new StringBuffer().append("BadID: ").append(indexItem.getID()).toString());
            }
        }
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        debug(new StringBuffer().append("propertyChange: ").append(propertyChangeEvent.getSource()).append(" ").append(propertyChangeEvent.getPropertyName()).toString());
        if (propertyChangeEvent.getSource() == this.glossary) {
            String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName.equals("helpModel")) {
                debug("model changed");
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

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.searchField) {
            this.rbc = (RuleBasedCollator) Collator.getInstance(this.glossary.getLocale());
            String text = this.searchField.getText();
            if (text != null) {
                text = text.toLowerCase();
            }
            if (this.oldText != null && text.compareTo(this.oldText) != 0) {
                this.currentFindNode = null;
            }
            this.oldText = text;
            DefaultMutableTreeNode defaultMutableTreeNodeSearchName = searchName(this.topNode, text);
            if (defaultMutableTreeNodeSearchName == null) {
                this.currentFindNode = null;
                this.glossary.getToolkit().beep();
                return;
            }
            this.currentFindNode = defaultMutableTreeNodeSearchName;
            TreePath treePath = new TreePath(defaultMutableTreeNodeSearchName.getPath());
            this.tree.scrollPathToVisible(treePath);
            this.tree.expandPath(treePath);
            this.tree.setSelectionPath(treePath);
        }
    }

    private DefaultMutableTreeNode searchName(DefaultMutableTreeNode defaultMutableTreeNode, String str) {
        String name;
        if (this.currentFindNode == null) {
            IndexItem indexItem = (IndexItem) defaultMutableTreeNode.getUserObject();
            if (indexItem != null && (name = indexItem.getName()) != null && name.toLowerCase().startsWith(str)) {
                return defaultMutableTreeNode;
            }
        } else if (this.currentFindNode == defaultMutableTreeNode) {
            this.currentFindNode = null;
        }
        int childCount = defaultMutableTreeNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode defaultMutableTreeNodeSearchName = searchName((DefaultMutableTreeNode) defaultMutableTreeNode.getChildAt(i), str);
            if (defaultMutableTreeNodeSearchName != null) {
                return defaultMutableTreeNodeSearchName;
            }
        }
        return null;
    }

    private static void debug(String str) {
        if (debug) {
            System.out.println(new StringBuffer().append("BasicGlossaryNavigatorUI: ").append(str).toString());
        }
    }
}
