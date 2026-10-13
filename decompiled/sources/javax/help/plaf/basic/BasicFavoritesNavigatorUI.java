package javax.help.plaf.basic;

import com.aowagie.text.ElementTags;
import com.aowagie.text.pdf.ColumnText;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.RuleBasedCollator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;
import javax.help.BadIDException;
import javax.help.FavoritesItem;
import javax.help.FavoritesNode;
import javax.help.FavoritesView;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.InvalidHelpSetContextException;
import javax.help.JHelpFavoritesNavigator;
import javax.help.Map;
import javax.help.NavigatorView;
import javax.help.SwingHelpUtilities;
import javax.help.TreeItem;
import javax.help.event.HelpModelEvent;
import javax.help.event.HelpModelListener;
import javax.help.event.HelpSetEvent;
import javax.help.event.HelpSetListener;
import javax.help.plaf.HelpNavigatorUI;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.RepaintManager;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicFavoritesNavigatorUI.class */
public class BasicFavoritesNavigatorUI extends HelpNavigatorUI implements HelpModelListener, TreeSelectionListener, HelpSetListener, PropertyChangeListener, TreeModelListener, Serializable {
    protected JHelpFavoritesNavigator favorites;
    protected JScrollPane sp;
    protected FavoritesNode topNode;
    protected JTree tree;
    private String contentTitle;
    protected RuleBasedCollator rbc;
    protected String oldText;
    protected DefaultMutableTreeNode currentFindNode;
    protected Action addAction;
    protected Action removeAction;
    protected Action cutAction;
    protected Action pasteAction;
    protected Action copyAction;
    protected Action folderAction;
    protected JPopupMenu popup;
    private HashMap dataMap = new HashMap();
    private FavoritesNode favNode = null;
    private FavoritesNode rootNode = null;
    private Locale locale = null;
    private FavoritesNode selectedNode = null;
    private FavoritesItem selectedItem = null;
    private TreePath selectedTreePath = null;
    private Map.ID selectedID = null;
    private JMenuItem newFolderMI = null;
    private JMenuItem addMI = null;
    private JSeparator separatorMI = null;
    private JMenuItem cutMI = null;
    private JMenuItem copyMI = null;
    private JMenuItem pasteMI = null;
    private JMenuItem removeMI = null;
    private HashMap hsMap = null;
    private HashMap connections = new HashMap();
    private Vector nodeClipboard = new Vector();
    private static boolean on1dot3;
    private static boolean debug;
    static Class class$javax$help$plaf$basic$BasicFavoritesNavigatorUI;

    static {
        on1dot3 = false;
        try {
            on1dot3 = Class.forName("javax.swing.InputVerifier") != null;
        } catch (ClassNotFoundException e) {
            on1dot3 = false;
        }
        debug = false;
    }

    public static ComponentUI createUI(JComponent jComponent) {
        return new BasicFavoritesNavigatorUI((JHelpFavoritesNavigator) jComponent);
    }

    public BasicFavoritesNavigatorUI(JHelpFavoritesNavigator jHelpFavoritesNavigator) {
        ImageIcon imageIcon = getImageIcon(jHelpFavoritesNavigator.getNavigatorView());
        if (imageIcon != null) {
            setIcon(imageIcon);
        } else {
            setIcon(UIManager.getIcon("FavoritesNav.icon"));
        }
    }

    public void installUI(JComponent jComponent) {
        debug("installUI");
        this.locale = HelpUtilities.getLocale(jComponent);
        this.addAction = new AddAction(this);
        this.removeAction = new RemoveAction(this);
        this.folderAction = new FolderAction(this);
        this.favorites = (JHelpFavoritesNavigator) jComponent;
        HelpModel model = this.favorites.getModel();
        this.favorites.setLayout(new BorderLayout());
        this.favorites.addPropertyChangeListener(this);
        if (model != null) {
            model.addHelpModelListener(this);
            model.addPropertyChangeListener(this);
            HelpSet helpSet = model.getHelpSet();
            if (helpSet != null) {
                helpSet.addHelpSetListener(this);
            }
        }
        this.topNode = new FavoritesNode(new FavoritesItem("Favorites"));
        if (on1dot3) {
            this.tree = new FavoritesTree(this, this.topNode);
        } else {
            this.tree = new JTree(this.topNode);
        }
        this.tree.setEditable(true);
        this.tree.addMouseListener(new PopupListener(this));
        this.cutAction = new CutAction(this);
        this.copyAction = new CopyAction(this);
        this.pasteAction = new PasteAction(this);
        this.popup = new JPopupMenu();
        Action action = this.folderAction;
        Action action2 = this.folderAction;
        this.newFolderMI = new JMenuItem((String) action.getValue("Name"));
        this.newFolderMI.addActionListener(this.folderAction);
        this.popup.add(this.newFolderMI);
        Action action3 = this.addAction;
        Action action4 = this.addAction;
        this.addMI = new JMenuItem((String) action3.getValue("Name"));
        this.addMI.addActionListener(this.addAction);
        this.popup.add(this.addMI);
        this.separatorMI = new JSeparator();
        this.popup.add(this.separatorMI);
        Action action5 = this.cutAction;
        Action action6 = this.cutAction;
        this.cutMI = new JMenuItem((String) action5.getValue("Name"));
        this.cutMI.addActionListener(this.cutAction);
        this.cutMI.setAccelerator(KeyStroke.getKeyStroke(88, 2));
        this.popup.add(this.cutMI);
        Action action7 = this.copyAction;
        Action action8 = this.copyAction;
        this.copyMI = new JMenuItem((String) action7.getValue("Name"));
        this.copyMI.addActionListener(this.copyAction);
        this.copyMI.setAccelerator(KeyStroke.getKeyStroke(67, 2));
        this.popup.add(this.copyMI);
        Action action9 = this.pasteAction;
        Action action10 = this.pasteAction;
        this.pasteMI = new JMenuItem((String) action9.getValue("Name"));
        this.pasteMI.addActionListener(this.pasteAction);
        this.pasteMI.setEnabled(false);
        this.pasteMI.setAccelerator(KeyStroke.getKeyStroke(86, 2));
        this.popup.add(this.pasteMI);
        Action action11 = this.removeAction;
        Action action12 = this.removeAction;
        this.removeMI = new JMenuItem((String) action11.getValue("Name"));
        this.removeMI.addActionListener(this.removeAction);
        this.popup.add(this.removeMI);
        this.tree.getModel().addTreeModelListener(this);
        this.tree.addTreeSelectionListener(this);
        this.tree.getSelectionModel().addTreeSelectionListener(this);
        this.tree.setShowsRootHandles(true);
        this.tree.setRootVisible(false);
        setCellRenderer(this.favorites.getNavigatorView(), this.tree);
        this.sp = new JScrollPane();
        this.sp.getViewport().add(this.tree);
        this.favorites.add("Center", this.sp);
        reloadData();
    }

    protected void setCellRenderer(NavigatorView navigatorView, JTree jTree) {
        jTree.setCellRenderer(new BasicFavoritesCellRenderer());
    }

    public void uninstallUI(JComponent jComponent) {
        debug("uninstallUI");
        HelpModel model = this.favorites.getModel();
        this.favorites.removePropertyChangeListener(this);
        this.tree.getSelectionModel().removeTreeSelectionListener(this);
        this.favorites.setLayout(null);
        this.favorites.removeAll();
        if (model != null) {
            model.removeHelpModelListener(this);
        }
        this.favorites = null;
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
        if (this.favorites.getModel() == null) {
            return;
        }
        this.topNode.removeAllChildren();
        FavoritesView favoritesView = (FavoritesView) this.favorites.getNavigatorView();
        if (favoritesView == null) {
            return;
        }
        this.favNode = favoritesView.getDataAsTree();
        this.rootNode = this.favNode.getDeepCopy();
        classifyNode(this.favNode);
        while (this.favNode.getChildCount() > 0) {
            this.topNode.add((DefaultMutableTreeNode) this.favNode.getFirstChild());
        }
        this.tree.getModel().reload();
        setVisibility(this.topNode);
    }

    private void classifyNode(FavoritesNode favoritesNode) {
        debug("classifyNode");
        if (favoritesNode == null) {
            return;
        }
        HelpModel model = this.favorites.getModel();
        if (model == null) {
            favoritesNode.removeAllChildren();
            return;
        }
        HelpSet helpSet = model.getHelpSet();
        if (helpSet == null) {
            favoritesNode.removeAllChildren();
            return;
        }
        this.hsMap = new HashMap();
        this.hsMap.put(helpSet.getTitle(), helpSet);
        fillHelpSetTitles(helpSet);
        classifyChildren(favoritesNode);
    }

    private void fillHelpSetTitles(HelpSet helpSet) {
        Enumeration helpSets = helpSet.getHelpSets();
        while (helpSets.hasMoreElements()) {
            HelpSet helpSet2 = (HelpSet) helpSets.nextElement();
            if (helpSet2 != null) {
                this.hsMap.put(helpSet2.getTitle(), helpSet2);
                debug(new StringBuffer().append(" fill title: ").append(helpSet2.getTitle()).toString());
                fillHelpSetTitles(helpSet2);
            }
        }
    }

    private void classifyChildren(FavoritesNode favoritesNode) {
        debug(new StringBuffer().append("classifyChildren: ").append(favoritesNode).toString());
        if (favoritesNode == null) {
            return;
        }
        boolean z = true;
        Enumeration enumerationPreorderEnumeration = favoritesNode.preorderEnumeration();
        while (enumerationPreorderEnumeration.hasMoreElements()) {
            FavoritesNode favoritesNode2 = (FavoritesNode) enumerationPreorderEnumeration.nextElement();
            if (z) {
                z = false;
            } else {
                FavoritesNode deepCopy = favoritesNode2.getDeepCopy();
                this.connections.put(favoritesNode2, deepCopy);
                FavoritesNode favoritesNode3 = (FavoritesNode) this.connections.get(favoritesNode2.getParent());
                if (favoritesNode3 == null) {
                    favoritesNode3 = this.rootNode;
                }
                favoritesNode3.add(deepCopy);
                FavoritesItem favoritesItem = (FavoritesItem) favoritesNode2.getUserObject();
                debug(new StringBuffer().append("classify item: ").append(favoritesItem).toString());
                if (favoritesItem == null) {
                    debug("item is null : fillDataMap");
                } else {
                    String target = favoritesItem.getTarget();
                    String helpSetTitle = favoritesItem.getHelpSetTitle();
                    if (!this.hsMap.containsKey(helpSetTitle) && favoritesNode2.getVisibleChildCount() == 0) {
                        if (favoritesItem.emptyInitState() && favoritesItem.isFolder()) {
                            debug("empty init state");
                        } else {
                            favoritesItem.setVisible(false);
                        }
                    } else if (target == null) {
                        debug("target is null:fillDataMap");
                    } else {
                        try {
                            Map.ID idCreate = Map.ID.create(target, (HelpSet) this.hsMap.get(helpSetTitle));
                            debug(new StringBuffer().append("put to the dataMap: ").append(favoritesItem).toString());
                            this.dataMap.put(favoritesItem, idCreate);
                        } catch (BadIDException e) {
                            debug(e.getMessage());
                        }
                    }
                }
            }
        }
        Vector vector = new Vector();
        Enumeration enumerationBreadthFirstEnumeration = favoritesNode.breadthFirstEnumeration();
        while (enumerationBreadthFirstEnumeration.hasMoreElements()) {
            FavoritesNode favoritesNode4 = (FavoritesNode) enumerationBreadthFirstEnumeration.nextElement();
            if (!favoritesNode4.isVisible()) {
                debug(new StringBuffer().append("remove node:").append((FavoritesItem) favoritesNode4.getUserObject()).toString());
                vector.addElement(favoritesNode4);
            }
        }
        for (int i = 0; i < vector.size(); i++) {
            debug(new StringBuffer().append("removing ").append(vector.elementAt(i)).toString());
            try {
                favoritesNode.remove((DefaultMutableTreeNode) vector.elementAt(i));
            } catch (IllegalArgumentException e2) {
            }
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
                FavoritesItem favoritesItem = (FavoritesItem) defaultMutableTreeNode.getUserObject();
                if (favoritesItem == null) {
                    debug("favoritesItem is null");
                } else {
                    Map.ID id = (Map.ID) this.dataMap.get(favoritesItem);
                    if (id != null) {
                        debug(new StringBuffer().append("id name :").append(id.id).toString());
                        debug(new StringBuffer().append("target :").append(str).toString());
                        try {
                            if (id.equals(Map.ID.create(str, this.favorites.getModel().getHelpSet()))) {
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

    private void setVisibility(DefaultMutableTreeNode defaultMutableTreeNode) {
        this.tree.expandPath(new TreePath(defaultMutableTreeNode.getPath()));
        if (!defaultMutableTreeNode.isLeaf()) {
            int childCount = defaultMutableTreeNode.getChildCount();
            for (int i = 0; i < childCount; i++) {
                setVisibility((DefaultMutableTreeNode) defaultMutableTreeNode.getChildAt(i));
            }
        }
    }

    @Override // javax.help.event.HelpModelListener
    public void idChanged(HelpModelEvent helpModelEvent) {
        debug(new StringBuffer().append("idChanged(").append(helpModelEvent).append(")").toString());
        Map.ID id = helpModelEvent.getID();
        this.contentTitle = helpModelEvent.getHistoryName();
        URL url = helpModelEvent.getURL();
        String externalForm = null;
        if (url != null) {
            externalForm = url.toExternalForm();
        }
        Map.ID id2 = null;
        String uRLSpec = null;
        String name = null;
        HelpModel model = this.favorites.getModel();
        if (helpModelEvent.getSource() != model) {
            debug("Internal inconsistency!");
            debug(new StringBuffer().append("  ").append(helpModelEvent.getSource()).append(" != ").append(model).toString());
            throw new Error("Internal error");
        }
        TreePath selectionPath = this.tree.getSelectionPath();
        if (selectionPath != null) {
            Object lastPathComponent = selectionPath.getLastPathComponent();
            if (lastPathComponent instanceof DefaultMutableTreeNode) {
                FavoritesItem favoritesItem = (FavoritesItem) ((DefaultMutableTreeNode) lastPathComponent).getUserObject();
                if (favoritesItem != null) {
                    id2 = (Map.ID) this.dataMap.get(favoritesItem);
                    uRLSpec = favoritesItem.getURLSpec();
                    name = favoritesItem.getName();
                }
                if (name != null && name.equals(this.contentTitle)) {
                    if (id2 != null && id2.equals(id)) {
                        return;
                    }
                    if (uRLSpec != null && uRLSpec.equals(externalForm)) {
                        return;
                    }
                }
            }
        }
        DefaultMutableTreeNode defaultMutableTreeNodeFindID = findID(this.topNode, id);
        if (defaultMutableTreeNodeFindID == null) {
            defaultMutableTreeNodeFindID = findURL(this.topNode, externalForm);
        }
        if (defaultMutableTreeNodeFindID == null) {
            this.tree.clearSelection();
            return;
        }
        TreePath treePath = new TreePath(defaultMutableTreeNodeFindID.getPath());
        this.tree.expandPath(treePath);
        this.tree.setSelectionPath(treePath);
        this.tree.scrollPathToVisible(treePath);
    }

    private DefaultMutableTreeNode findID(DefaultMutableTreeNode defaultMutableTreeNode, Map.ID id) {
        debug(new StringBuffer().append("findID: (").append(id).append(")").toString());
        debug(new StringBuffer().append("  node: ").append(defaultMutableTreeNode).toString());
        if (id == null) {
            return null;
        }
        Map.ID id2 = (Map.ID) this.dataMap.get((FavoritesItem) defaultMutableTreeNode.getUserObject());
        if (id2 != null) {
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

    private DefaultMutableTreeNode findURL(DefaultMutableTreeNode defaultMutableTreeNode, String str) {
        debug(new StringBuffer().append(" findURL: ").append(str).toString());
        if (str == null) {
            return null;
        }
        Enumeration enumerationChildren = defaultMutableTreeNode.children();
        while (enumerationChildren.hasMoreElements()) {
            DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode) enumerationChildren.nextElement();
            FavoritesItem favoritesItem = (FavoritesItem) defaultMutableTreeNode2.getUserObject();
            favoritesItem.getName();
            if (str.equals(favoritesItem.getURLSpec())) {
                return defaultMutableTreeNode2;
            }
            findURL(defaultMutableTreeNode2, str);
        }
        return null;
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        FavoritesItem favoritesItem;
        this.selectedTreePath = treeSelectionEvent.getNewLeadSelectionPath();
        if (this.selectedTreePath == null) {
            this.selectedNode = null;
            return;
        }
        this.selectedNode = (FavoritesNode) this.selectedTreePath.getLastPathComponent();
        if (this.selectedNode != null) {
            this.selectedItem = (FavoritesItem) this.selectedNode.getUserObject();
            this.selectedID = (Map.ID) this.dataMap.get(this.selectedItem);
        }
        HelpModel model = this.favorites.getModel();
        HelpSet helpSet = model.getHelpSet();
        debug(new StringBuffer().append("ValueChanged: ").append(treeSelectionEvent).toString());
        debug(new StringBuffer().append("  model: ").append(model).toString());
        if (model == null) {
            return;
        }
        TreePath[] selectionPaths = this.tree.getSelectionPaths();
        if (selectionPaths != null) {
            this.removeAction.setEnabled(true);
            TreeItem[] treeItemArr = new TreeItem[selectionPaths.length];
            for (int i = 0; i < selectionPaths.length; i++) {
                if (selectionPaths[i] != null) {
                    FavoritesItem favoritesItem2 = (FavoritesItem) ((DefaultMutableTreeNode) selectionPaths[i].getLastPathComponent()).getUserObject();
                    try {
                        if (favoritesItem2.getTarget() != null) {
                            favoritesItem2.setID(Map.ID.create(favoritesItem2.getTarget(), helpSet));
                        }
                    } catch (BadIDException e) {
                    }
                    treeItemArr[i] = favoritesItem2;
                }
            }
            this.favorites.setSelectedItems(treeItemArr);
            if (treeItemArr != null && treeItemArr.length == 1 && (favoritesItem = (FavoritesItem) treeItemArr[0]) != null) {
                Map.ID id = (Map.ID) this.dataMap.get(favoritesItem);
                if (id != null) {
                    debug(new StringBuffer().append("itemID: ").append(id).toString());
                    try {
                        model.setCurrentID(id, favoritesItem.getName(), this.favorites);
                        return;
                    } catch (InvalidHelpSetContextException e2) {
                        System.err.println(new StringBuffer().append("BadID: ").append(favoritesItem.getID()).toString());
                        return;
                    }
                }
                if (favoritesItem.getURLSpec() != null) {
                    try {
                        model.setCurrentURL(new URL(favoritesItem.getURLSpec()), favoritesItem.getName(), this.favorites);
                        return;
                    } catch (MalformedURLException e3) {
                        System.err.println(e3);
                        return;
                    }
                }
                return;
            }
            return;
        }
        this.removeAction.setEnabled(false);
        this.pasteAction.setEnabled(false);
        this.pasteMI.setEnabled(false);
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        debug(new StringBuffer().append("propertyChange: ").append(propertyChangeEvent.getSource()).append(" ").append(propertyChangeEvent.getPropertyName()).toString());
        if (propertyChangeEvent.getSource() == this.favorites) {
            String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName.equals("helpModel")) {
                debug("model changed");
                reloadData();
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
                return;
            } else if (propertyName.equals("collapse")) {
                debug("Collapse change");
                collapse((String) propertyChangeEvent.getNewValue());
                return;
            } else {
                if (propertyName.equals("navigatorChange")) {
                    debug("Navigator change");
                    this.tree.clearSelection();
                    return;
                }
                return;
            }
        }
        if (this.favorites != null && propertyChangeEvent.getSource() == this.favorites.getModel() && propertyChangeEvent.getPropertyName().equals("helpSet")) {
            reloadData();
        }
    }

    @Override // javax.help.event.HelpSetListener
    public void helpSetAdded(HelpSetEvent helpSetEvent) {
        debug("HelpSet added");
        reloadData();
    }

    @Override // javax.help.event.HelpSetListener
    public void helpSetRemoved(HelpSetEvent helpSetEvent) {
        debug("HelpSet removed");
        reloadData();
    }

    public void saveFavorites() {
        ((FavoritesView) this.favorites.getNavigatorView()).saveFavorites(this.rootNode);
    }

    public void treeStructureChanged(TreeModelEvent treeModelEvent) {
        debug("tree structure changed");
    }

    public void treeNodesInserted(TreeModelEvent treeModelEvent) {
        debug("node inserted");
        int index = -1;
        FavoritesNode favoritesNode = (FavoritesNode) treeModelEvent.getTreePath().getLastPathComponent();
        Object[] children = treeModelEvent.getChildren();
        int[] childIndices = treeModelEvent.getChildIndices();
        int i = childIndices[0];
        debug(new StringBuffer().append("index first ").append(i).toString());
        int length = (childIndices.length + i) - 1;
        FavoritesNode favoritesNode2 = (FavoritesNode) this.connections.get(favoritesNode);
        if (favoritesNode2 == null) {
            favoritesNode2 = this.rootNode;
        }
        debug(new StringBuffer().append("root parent ").append(favoritesNode2).toString());
        if (i == 0) {
            if (favoritesNode2.getChildCount() == 0) {
                index = 0;
            } else {
                Enumeration enumerationChildren = favoritesNode2.children();
                while (enumerationChildren.hasMoreElements()) {
                    FavoritesNode favoritesNode3 = (FavoritesNode) enumerationChildren.nextElement();
                    if (favoritesNode3.isVisible()) {
                        debug(new StringBuffer().append("is visible : ").append(favoritesNode3).toString());
                        index = favoritesNode2.getIndex(favoritesNode3);
                        break;
                    }
                }
            }
            if (index >= 0) {
                for (int length2 = children.length - 1; length2 >= 0; length2--) {
                    FavoritesNode deepCopy = ((FavoritesNode) children[length2]).getDeepCopy();
                    favoritesNode2.insert(deepCopy, index);
                    this.connections.put((FavoritesNode) children[length2], deepCopy);
                }
                return;
            }
            return;
        }
        if (i > 0) {
            int index2 = favoritesNode2.getIndex((FavoritesNode) this.connections.get(favoritesNode.getChildAt(i - 1))) + 1;
            for (int length3 = children.length - 1; length3 >= 0; length3--) {
                FavoritesNode deepCopy2 = ((FavoritesNode) children[length3]).getDeepCopy();
                favoritesNode2.insert(deepCopy2, index2);
                this.connections.put((FavoritesNode) children[length3], deepCopy2);
            }
        }
    }

    public void treeNodesRemoved(TreeModelEvent treeModelEvent) {
        debug("nodes removed");
        for (Object obj : treeModelEvent.getChildren()) {
            FavoritesNode favoritesNode = (FavoritesNode) this.connections.get((FavoritesNode) obj);
            if (favoritesNode != null) {
                favoritesNode.removeFromParent();
            }
        }
    }

    public void treeNodesChanged(TreeModelEvent treeModelEvent) {
        debug("node changed");
        Object cellEditorValue = this.tree.getCellEditor().getCellEditorValue();
        if ((cellEditorValue instanceof String) && this.selectedItem != null) {
            debug("new name");
            Map.ID id = (Map.ID) this.dataMap.get(this.selectedItem);
            this.dataMap.remove(this.selectedItem);
            FavoritesNode favoritesNode = (FavoritesNode) this.connections.get(getSelectedNode());
            this.selectedItem.setName((String) cellEditorValue);
            this.selectedNode.setUserObject(this.selectedItem);
            if (favoritesNode != null) {
                ((FavoritesItem) favoritesNode.getUserObject()).setName((String) cellEditorValue);
            }
            this.dataMap.put(this.selectedItem, id);
            saveFavorites();
        }
    }

    public FavoritesNode getSelectedNode() {
        return this.selectedNode;
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicFavoritesNavigatorUI$AddAction.class */
    public class AddAction extends AbstractAction {
        private final BasicFavoritesNavigatorUI this$0;

        /* JADX WARN: Illegal instructions before constructor call */
        public AddAction(BasicFavoritesNavigatorUI basicFavoritesNavigatorUI) {
            Class clsClass$;
            String string = HelpUtilities.getString(basicFavoritesNavigatorUI.locale, "favorites.add");
            if (BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI == null) {
                clsClass$ = BasicFavoritesNavigatorUI.class$("javax.help.plaf.basic.BasicFavoritesNavigatorUI");
                BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI = clsClass$;
            } else {
                clsClass$ = BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI;
            }
            super(string, SwingHelpUtilities.getImageIcon(clsClass$, "images/addToFav.gif"));
            this.this$0 = basicFavoritesNavigatorUI;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            BasicFavoritesNavigatorUI.debug("add");
            String str = null;
            String helpSetTitle = null;
            HelpModel model = this.this$0.favorites.getModel();
            HelpSet helpSet = model.getHelpSet();
            Map.ID currentID = model.getCurrentID();
            URL currentURL = model.getCurrentURL();
            if (currentID != null) {
                str = currentID.id;
                helpSetTitle = currentID.hs.getTitle();
            }
            if (helpSetTitle == null) {
                helpSetTitle = getHelpSetTitle(helpSet, currentURL);
                if (helpSetTitle == null) {
                    helpSetTitle = helpSet.getTitle();
                }
            }
            String externalForm = null;
            if (str == null) {
                externalForm = currentURL.toExternalForm();
            }
            FavoritesItem favoritesItem = new FavoritesItem(this.this$0.contentTitle, str, externalForm, helpSetTitle, Locale.getDefault());
            this.this$0.dataMap.put(favoritesItem, currentID);
            FavoritesNode favoritesNode = new FavoritesNode(favoritesItem);
            this.this$0.tree.getModel().insertNodeInto(favoritesNode, this.this$0.topNode, this.this$0.topNode.getChildCount());
            TreePath treePath = new TreePath(favoritesNode.getPath());
            this.this$0.tree.expandPath(treePath);
            this.this$0.tree.setSelectionPath(treePath);
            this.this$0.tree.scrollPathToVisible(treePath);
            this.this$0.saveFavorites();
        }

        private String getHelpSetTitle(HelpSet helpSet, URL url) {
            if (url.toExternalForm().startsWith(helpSet.getHelpSetURL().toExternalForm())) {
                return helpSet.getTitle();
            }
            Enumeration helpSets = helpSet.getHelpSets();
            String helpSetTitle = null;
            while (helpSets.hasMoreElements()) {
                helpSetTitle = getHelpSetTitle((HelpSet) helpSets.nextElement(), url);
                if (helpSetTitle != null) {
                    break;
                }
            }
            return helpSetTitle;
        }
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    @Override // javax.help.plaf.HelpNavigatorUI
    public Action getAddAction() {
        return this.addAction;
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicFavoritesNavigatorUI$RemoveAction.class */
    public class RemoveAction extends AbstractAction {
        private final BasicFavoritesNavigatorUI this$0;

        /* JADX WARN: Illegal instructions before constructor call */
        public RemoveAction(BasicFavoritesNavigatorUI basicFavoritesNavigatorUI) {
            Class clsClass$;
            String string = HelpUtilities.getString(basicFavoritesNavigatorUI.locale, "favorites.remove");
            if (BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI == null) {
                clsClass$ = BasicFavoritesNavigatorUI.class$("javax.help.plaf.basic.BasicFavoritesNavigatorUI");
                BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI = clsClass$;
            } else {
                clsClass$ = BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI;
            }
            super(string, SwingHelpUtilities.getImageIcon(clsClass$, "images/remove.gif"));
            this.this$0 = basicFavoritesNavigatorUI;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            BasicFavoritesNavigatorUI.debug("remove");
            DefaultTreeModel model = this.this$0.tree.getModel();
            TreePath[] selectionPaths = this.this$0.tree.getSelectionPaths();
            for (int i = 0; i < selectionPaths.length; i++) {
                if (selectionPaths[i] != null) {
                    DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) selectionPaths[i].getLastPathComponent();
                    model.removeNodeFromParent(defaultMutableTreeNode);
                    if (defaultMutableTreeNode != null) {
                        this.this$0.dataMap.remove((FavoritesItem) defaultMutableTreeNode.getUserObject());
                    }
                }
                this.this$0.saveFavorites();
            }
        }
    }

    public Action getRemoveAction() {
        return this.removeAction;
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicFavoritesNavigatorUI$FolderAction.class */
    public class FolderAction extends AbstractAction {
        private final BasicFavoritesNavigatorUI this$0;

        /* JADX WARN: Illegal instructions before constructor call */
        public FolderAction(BasicFavoritesNavigatorUI basicFavoritesNavigatorUI) {
            Class clsClass$;
            String string = HelpUtilities.getString(basicFavoritesNavigatorUI.locale, "favorites.folder");
            if (BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI == null) {
                clsClass$ = BasicFavoritesNavigatorUI.class$("javax.help.plaf.basic.BasicFavoritesNavigatorUI");
                BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI = clsClass$;
            } else {
                clsClass$ = BasicFavoritesNavigatorUI.class$javax$help$plaf$basic$BasicFavoritesNavigatorUI;
            }
            super(string, SwingHelpUtilities.getImageIcon(clsClass$, "images/folder.gif"));
            this.this$0 = basicFavoritesNavigatorUI;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            FavoritesNode parent;
            FavoritesItem favoritesItem = new FavoritesItem(HelpUtilities.getString(this.this$0.locale, "favorites.folder"));
            favoritesItem.setAsFolder();
            FavoritesNode favoritesNode = new FavoritesNode(favoritesItem);
            TreePath selectionPath = this.this$0.tree.getSelectionPath();
            if (selectionPath == null) {
                parent = this.this$0.topNode;
            } else {
                parent = ((FavoritesNode) selectionPath.getLastPathComponent()).getParent();
            }
            this.this$0.tree.getModel().insertNodeInto(favoritesNode, parent, parent.getChildCount());
            TreePath treePath = new TreePath(favoritesNode.getPath());
            this.this$0.tree.expandPath(treePath);
            this.this$0.tree.setSelectionPath(treePath);
            this.this$0.tree.scrollPathToVisible(treePath);
            this.this$0.saveFavorites();
        }
    }

    public Action getFolderAction() {
        return this.folderAction;
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicFavoritesNavigatorUI$CutAction.class */
    public class CutAction extends AbstractAction {
        private final BasicFavoritesNavigatorUI this$0;

        public CutAction(BasicFavoritesNavigatorUI basicFavoritesNavigatorUI) {
            super(HelpUtilities.getString(basicFavoritesNavigatorUI.locale, "favorites.cut"));
            this.this$0 = basicFavoritesNavigatorUI;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            BasicFavoritesNavigatorUI.debug("cut");
            this.this$0.nodeClipboard.removeAllElements();
            DefaultTreeModel model = this.this$0.tree.getModel();
            TreePath[] selectionPaths = this.this$0.tree.getSelectionPaths();
            for (int i = 0; i < selectionPaths.length; i++) {
                if (selectionPaths[i] != null) {
                    DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) selectionPaths[i].getLastPathComponent();
                    if (defaultMutableTreeNode != null) {
                        this.this$0.nodeClipboard.add(defaultMutableTreeNode);
                    }
                    model.removeNodeFromParent(defaultMutableTreeNode);
                }
            }
            this.this$0.saveFavorites();
            this.this$0.pasteMI.setEnabled(true);
        }
    }

    public Action getCutAction() {
        return this.cutAction;
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicFavoritesNavigatorUI$PasteAction.class */
    public class PasteAction extends AbstractAction {
        private final BasicFavoritesNavigatorUI this$0;

        public PasteAction(BasicFavoritesNavigatorUI basicFavoritesNavigatorUI) {
            super(HelpUtilities.getString(basicFavoritesNavigatorUI.locale, "favorites.paste"));
            this.this$0 = basicFavoritesNavigatorUI;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            BasicFavoritesNavigatorUI.debug("paste");
            DefaultTreeModel model = this.this$0.tree.getModel();
            FavoritesNode favoritesNode = (FavoritesNode) this.this$0.tree.getSelectionPath().getLastPathComponent();
            if (favoritesNode != null) {
                if (favoritesNode.getAllowsChildren()) {
                    Enumeration enumerationElements = this.this$0.nodeClipboard.elements();
                    while (enumerationElements.hasMoreElements()) {
                        model.insertNodeInto((DefaultMutableTreeNode) enumerationElements.nextElement(), favoritesNode, favoritesNode.getChildCount());
                    }
                } else {
                    DefaultMutableTreeNode parent = favoritesNode.getParent();
                    if (parent == null) {
                        return;
                    }
                    int index = parent.getIndex(favoritesNode);
                    Enumeration enumerationElements2 = this.this$0.nodeClipboard.elements();
                    while (enumerationElements2.hasMoreElements()) {
                        model.insertNodeInto((DefaultMutableTreeNode) enumerationElements2.nextElement(), parent, index);
                        index++;
                    }
                }
                this.this$0.saveFavorites();
            }
        }
    }

    public Action getPasteAction() {
        return this.pasteAction;
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicFavoritesNavigatorUI$CopyAction.class */
    public class CopyAction extends AbstractAction {
        private final BasicFavoritesNavigatorUI this$0;

        public CopyAction(BasicFavoritesNavigatorUI basicFavoritesNavigatorUI) {
            super(HelpUtilities.getString(basicFavoritesNavigatorUI.locale, "favorites.copy"));
            this.this$0 = basicFavoritesNavigatorUI;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            DefaultMutableTreeNode defaultMutableTreeNode;
            BasicFavoritesNavigatorUI.debug("paste");
            this.this$0.nodeClipboard.removeAllElements();
            this.this$0.tree.getModel();
            TreePath[] selectionPaths = this.this$0.tree.getSelectionPaths();
            for (int i = 0; i < selectionPaths.length; i++) {
                if (selectionPaths[i] != null && (defaultMutableTreeNode = (DefaultMutableTreeNode) selectionPaths[i].getLastPathComponent()) != null) {
                    this.this$0.nodeClipboard.add(((FavoritesNode) defaultMutableTreeNode).getDeepCopy());
                }
            }
            this.this$0.saveFavorites();
            this.this$0.pasteMI.setEnabled(true);
        }
    }

    public Action getCopyAction() {
        return this.copyAction;
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicFavoritesNavigatorUI$PopupListener.class */
    public class PopupListener extends MouseAdapter {
        private final BasicFavoritesNavigatorUI this$0;

        public PopupListener(BasicFavoritesNavigatorUI basicFavoritesNavigatorUI) {
            this.this$0 = basicFavoritesNavigatorUI;
        }

        public void mousePressed(MouseEvent mouseEvent) {
            maybeShowPopup(mouseEvent);
        }

        public void mouseReleased(MouseEvent mouseEvent) {
            maybeShowPopup(mouseEvent);
        }

        private void maybeShowPopup(MouseEvent mouseEvent) {
            TreePath selectionPath = this.this$0.tree.getSelectionPath();
            TreePath pathForLocation = this.this$0.tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
            if (mouseEvent.isPopupTrigger()) {
                if (selectionPath == null || !selectionPath.equals(pathForLocation)) {
                    this.this$0.separatorMI.setVisible(false);
                    this.this$0.cutMI.setVisible(false);
                    this.this$0.copyMI.setVisible(false);
                    this.this$0.pasteMI.setVisible(false);
                    this.this$0.removeMI.setVisible(false);
                } else {
                    this.this$0.separatorMI.setVisible(true);
                    this.this$0.cutMI.setVisible(true);
                    this.this$0.copyMI.setVisible(true);
                    this.this$0.pasteMI.setVisible(true);
                    this.this$0.removeMI.setVisible(true);
                }
                this.this$0.popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicFavoritesNavigatorUI$FavoritesTree.class */
    public class FavoritesTree extends JTree implements DragGestureListener, DropTargetListener, DragSourceListener {
        protected Map.ID selectedID;
        private DragSource dragSource;
        private DragSourceContext dragSourceContext;
        private Point cursorLocation;
        private TreePath pathSource;
        private BufferedImage ghostImage;
        private Point offset;
        private Point ptLast;
        private Rectangle2D ghostRect;
        private Map.ID hashCandidate;
        private Cursor dndCursor;
        private final BasicFavoritesNavigatorUI this$0;

        public FavoritesTree(BasicFavoritesNavigatorUI basicFavoritesNavigatorUI, FavoritesNode favoritesNode) {
            super(favoritesNode);
            this.this$0 = basicFavoritesNavigatorUI;
            this.selectedID = null;
            this.dragSource = null;
            this.dragSourceContext = null;
            this.cursorLocation = null;
            this.offset = new Point();
            this.ptLast = new Point();
            this.ghostRect = new Rectangle2D.Float();
            setEditable(true);
            this.dragSource = DragSource.getDefaultDragSource();
            DragGestureRecognizer dragGestureRecognizerCreateDefaultDragGestureRecognizer = this.dragSource.createDefaultDragGestureRecognizer(this, 3, this);
            dragGestureRecognizerCreateDefaultDragGestureRecognizer.setSourceActions(dragGestureRecognizerCreateDefaultDragGestureRecognizer.getSourceActions() & (-5));
            new DropTarget(this, this);
            if (getToolkit().getBestCursorSize(16, 16).equals(new Dimension(64, 64))) {
                this.dndCursor = (Cursor) UIManager.get("HelpDnDCursor");
            }
            if (this.dndCursor == null) {
                BasicFavoritesNavigatorUI.debug("cursor is null");
            }
            putClientProperty("JTree.lineStyle", "None");
        }

        public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
            FavoritesNode selectedNode = this.this$0.getSelectedNode();
            if (selectedNode != null) {
                this.ghostImage = createGhostImage(dragGestureEvent);
                Transferable transferable = (Transferable) selectedNode.getUserObject();
                this.hashCandidate = (Map.ID) this.this$0.dataMap.get((FavoritesItem) transferable);
                Cursor cursor = DragSource.DefaultCopyDrop;
                if (dragGestureEvent.getDragAction() == 2) {
                    BasicFavoritesNavigatorUI.debug("action move");
                    Cursor cursor2 = DragSource.DefaultMoveDrop;
                }
                this.dragSource.startDrag(dragGestureEvent, this.dndCursor, this.ghostImage, new Point(5, 5), transferable, this);
            }
        }

        private BufferedImage createGhostImage(DragGestureEvent dragGestureEvent) {
            BasicFavoritesNavigatorUI.debug("createGhostImage");
            Point dragOrigin = dragGestureEvent.getDragOrigin();
            TreePath pathForLocation = getPathForLocation(dragOrigin.x, dragOrigin.y);
            if (pathForLocation == null) {
                return null;
            }
            Rectangle pathBounds = getPathBounds(pathForLocation);
            this.offset.setLocation(dragOrigin.x - pathBounds.x, dragOrigin.y - pathBounds.y);
            JLabel treeCellRendererComponent = getCellRenderer().getTreeCellRendererComponent(this, pathForLocation.getLastPathComponent(), false, isExpanded(pathForLocation), getModel().isLeaf(pathForLocation.getLastPathComponent()), 0, false);
            treeCellRendererComponent.setSize((int) pathBounds.getWidth(), (int) pathBounds.getHeight());
            BufferedImage bufferedImage = new BufferedImage((int) pathBounds.getWidth(), (int) pathBounds.getHeight(), 3);
            Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();
            graphics2DCreateGraphics.setComposite(AlphaComposite.getInstance(2, 0.5f));
            treeCellRendererComponent.paint(graphics2DCreateGraphics);
            Icon icon = treeCellRendererComponent.getIcon();
            int iconWidth = icon == null ? 0 : icon.getIconWidth() + treeCellRendererComponent.getIconTextGap();
            graphics2DCreateGraphics.setComposite(AlphaComposite.getInstance(4, 0.5f));
            graphics2DCreateGraphics.setPaint(new GradientPaint(iconWidth, ColumnText.GLOBAL_SPACE_CHAR_RATIO, SystemColor.controlShadow, getWidth(), ColumnText.GLOBAL_SPACE_CHAR_RATIO, new Color(255, 255, 255, 0)));
            graphics2DCreateGraphics.fillRect(iconWidth, 0, getWidth(), bufferedImage.getHeight());
            graphics2DCreateGraphics.dispose();
            return bufferedImage;
        }

        public void dragDropEnd(DragSourceDropEvent dragSourceDropEvent) {
            BasicFavoritesNavigatorUI.debug("dragDropEnd");
        }

        public void dragEnter(DragSourceDragEvent dragSourceDragEvent) {
            BasicFavoritesNavigatorUI.debug("dragEnter");
            setCursor(dragSourceDragEvent);
        }

        public void dragOver(DragSourceDragEvent dragSourceDragEvent) {
            BasicFavoritesNavigatorUI.debug("drag over");
            setCursor(dragSourceDragEvent);
        }

        public void dropActionChanged(DragSourceDragEvent dragSourceDragEvent) {
            BasicFavoritesNavigatorUI.debug("dropActionChanged");
            setCursor(dragSourceDragEvent);
        }

        public void dragExit(DragSourceEvent dragSourceEvent) {
            BasicFavoritesNavigatorUI.debug("dragExit");
        }

        private void setCursor(DragSourceDragEvent dragSourceDragEvent) {
            if (this.cursorLocation == null) {
                return;
            }
            TreePath pathForLocation = getPathForLocation(this.cursorLocation.x, this.cursorLocation.y);
            DragSourceContext dragSourceContext = dragSourceDragEvent.getDragSourceContext();
            if (testDropTarget(pathForLocation, this.this$0.selectedTreePath) == null) {
                dragSourceContext.setCursor(DragSource.DefaultCopyDrop);
            } else {
                dragSourceContext.setCursor(DragSource.DefaultCopyNoDrop);
            }
        }

        public void drop(DropTargetDropEvent dropTargetDropEvent) {
            BasicFavoritesNavigatorUI.debug("drop");
            try {
                Transferable transferable = dropTargetDropEvent.getTransferable();
                if (!transferable.isDataFlavorSupported(FavoritesItem.FAVORITES_FLAVOR)) {
                    BasicFavoritesNavigatorUI.debug("drop rejected not data flavor");
                    dropTargetDropEvent.rejectDrop();
                }
                FavoritesItem favoritesItem = (FavoritesItem) transferable.getTransferData(FavoritesItem.FAVORITES_FLAVOR);
                Point location = dropTargetDropEvent.getLocation();
                TreePath pathForLocation = getPathForLocation(location.x, location.y);
                String strTestDropTarget = testDropTarget(pathForLocation, this.this$0.selectedTreePath);
                if (strTestDropTarget != null) {
                    dropTargetDropEvent.rejectDrop();
                    BasicFavoritesNavigatorUI.debug(new StringBuffer().append("Error : ").append(strTestDropTarget).toString());
                    return;
                }
                FavoritesNode favoritesNode = (FavoritesNode) pathForLocation.getLastPathComponent();
                BasicFavoritesNavigatorUI.debug(new StringBuffer().append("new parent: ").append(favoritesNode).toString());
                FavoritesNode parent = this.this$0.getSelectedNode().getParent();
                FavoritesNode selectedNode = this.this$0.getSelectedNode();
                FavoritesNode deepCopy = selectedNode.getDeepCopy();
                boolean z = dropTargetDropEvent.getDropAction() == 1;
                BasicFavoritesNavigatorUI.debug(new StringBuffer().append("copy action: ").append(z).toString());
                BasicFavoritesNavigatorUI.debug(new StringBuffer().append("new child: ").append(new FavoritesNode(favoritesItem)).toString());
                if (!z) {
                    try {
                        FavoritesNode favoritesNode2 = (FavoritesNode) this.this$0.connections.get(this.this$0.getSelectedNode());
                        if (favoritesNode2 != null) {
                            favoritesNode2.removeFromParent();
                        }
                        parent.remove(this.this$0.getSelectedNode());
                    } catch (IllegalStateException e) {
                        BasicFavoritesNavigatorUI.debug("drop ejected");
                        dropTargetDropEvent.rejectDrop();
                    }
                }
                DefaultTreeModel model = getModel();
                if (!favoritesNode.getAllowsChildren()) {
                    DefaultMutableTreeNode parent2 = favoritesNode.getParent();
                    if (parent2 != null) {
                        model.insertNodeInto(deepCopy, parent2, parent2.getIndex(favoritesNode) + 1);
                    }
                } else {
                    model.insertNodeInto(deepCopy, favoritesNode, favoritesNode.getChildCount());
                }
                if (z) {
                    dropTargetDropEvent.acceptDrop(1);
                } else {
                    dropTargetDropEvent.acceptDrop(2);
                }
                dropTargetDropEvent.getDropTargetContext().dropComplete(true);
                this.this$0.dataMap.put((FavoritesItem) deepCopy.getUserObject(), this.hashCandidate);
                DefaultTreeModel model2 = getModel();
                model2.reload(parent);
                model2.reload(favoritesNode);
                expandPath(new TreePath(favoritesNode.getPath()));
                this.this$0.saveFavorites();
            } catch (IOException e2) {
                dropTargetDropEvent.rejectDrop();
                BasicFavoritesNavigatorUI.debug(new StringBuffer().append("drop rejected").append(e2).toString());
            } catch (UnsupportedFlavorException e3) {
                dropTargetDropEvent.rejectDrop();
                BasicFavoritesNavigatorUI.debug(new StringBuffer().append("drop rejected: ").append(e3).toString());
            }
        }

        public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
        }

        public void dragExit(DropTargetEvent dropTargetEvent) {
            if (!DragSource.isDragImageSupported()) {
                repaint(this.ghostRect.getBounds());
            }
        }

        public void dragOver(DropTargetDragEvent dropTargetDragEvent) {
            Point location = dropTargetDragEvent.getLocation();
            if (location.equals(this.ptLast)) {
                return;
            }
            this.ptLast = location;
            Point location2 = dropTargetDragEvent.getLocation();
            TreePath pathForLocation = getPathForLocation(location2.x, location2.y);
            Graphics2D graphics = getGraphics();
            if (testDropTarget(pathForLocation, this.this$0.selectedTreePath) == null) {
                dropTargetDragEvent.acceptDrag(3);
                if (!DragSource.isDragImageSupported()) {
                    paintImmediately(this.ghostRect.getBounds());
                    this.ghostRect.setRect(location.x - this.offset.x, location.y - this.offset.y, this.ghostImage.getWidth(), this.ghostImage.getHeight());
                    graphics.drawImage(this.ghostImage, AffineTransform.getTranslateInstance(this.ghostRect.getX(), this.ghostRect.getY()), (ImageObserver) null);
                    return;
                }
                return;
            }
            dropTargetDragEvent.rejectDrag();
        }

        public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {
        }

        private String testDropTarget(TreePath treePath, TreePath treePath2) {
            if (treePath == null) {
                return "Invalid drop location.";
            }
            if (treePath.equals(treePath2)) {
                return "Destination cannot be same as source";
            }
            if (treePath2.isDescendant(treePath)) {
                return "Destination node cannot be a descendant.";
            }
            if (treePath2.getParentPath().equals(treePath)) {
                return "Destination node cannot be a parent.";
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void debug(String str) {
        if (debug) {
            System.out.println(new StringBuffer().append("BasicFavoritesNavigatorUI: ").append(str).toString());
        }
    }
}
