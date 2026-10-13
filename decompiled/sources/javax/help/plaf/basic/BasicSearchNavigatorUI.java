package javax.help.plaf.basic;

import com.aowagie.text.ElementTags;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import javax.help.DefaultHelpModel;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.InvalidHelpSetContextException;
import javax.help.JHelpNavigator;
import javax.help.JHelpSearchNavigator;
import javax.help.Map;
import javax.help.NavigatorView;
import javax.help.SearchHit;
import javax.help.SearchTOCItem;
import javax.help.SearchView;
import javax.help.TextHelpModel;
import javax.help.TreeItem;
import javax.help.event.HelpModelEvent;
import javax.help.event.HelpModelListener;
import javax.help.plaf.HelpNavigatorUI;
import javax.help.search.MergingSearchEngine;
import javax.help.search.SearchEngine;
import javax.help.search.SearchEvent;
import javax.help.search.SearchItem;
import javax.help.search.SearchListener;
import javax.help.search.SearchQuery;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicSearchNavigatorUI.class */
public class BasicSearchNavigatorUI extends HelpNavigatorUI implements HelpModelListener, SearchListener, PropertyChangeListener, TreeSelectionListener, ComponentListener {
    protected JHelpSearchNavigator searchnav;
    protected JScrollPane sp;
    protected JTree tree;
    protected DefaultMutableTreeNode topNode;
    protected JTextField searchparams;
    protected boolean displayOptions;
    protected SearchEngine helpsearch;
    protected SearchQuery searchquery;
    protected DefaultMutableTreeNode lastTOCnode;
    private HelpSet newHelpSet;
    private Cursor paramCursor;
    private Cursor treeCursor;
    private static boolean debug = false;
    ActionListener searchAction = new SearchActionListener(this);
    private Cursor waitCursor = null;

    public static ComponentUI createUI(JComponent jComponent) {
        return new BasicSearchNavigatorUI((JHelpSearchNavigator) jComponent);
    }

    public BasicSearchNavigatorUI(JHelpSearchNavigator jHelpSearchNavigator) {
        ImageIcon imageIcon = getImageIcon(jHelpSearchNavigator.getNavigatorView());
        if (imageIcon != null) {
            setIcon(imageIcon);
        } else {
            setIcon(UIManager.getIcon("SearchNav.icon"));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicSearchNavigatorUI$SearchActionListener.class */
    class SearchActionListener implements ActionListener {
        private final BasicSearchNavigatorUI this$0;

        SearchActionListener(BasicSearchNavigatorUI basicSearchNavigatorUI) {
            this.this$0 = basicSearchNavigatorUI;
        }

        public synchronized void actionPerformed(ActionEvent actionEvent) {
            this.this$0.searchnav.getModel();
            try {
                if (this.this$0.paramCursor == null) {
                    this.this$0.paramCursor = this.this$0.searchparams.getCursor();
                }
                if (this.this$0.treeCursor == null) {
                    this.this$0.treeCursor = this.this$0.tree.getCursor();
                }
                if (this.this$0.waitCursor == null) {
                    this.this$0.waitCursor = Cursor.getPredefinedCursor(3);
                }
                this.this$0.searchparams.setCursor(this.this$0.waitCursor);
                this.this$0.tree.setCursor(this.this$0.waitCursor);
                if (this.this$0.helpsearch == null) {
                    BasicSearchNavigatorUI.debug("helpsearch is null");
                    this.this$0.helpsearch = this.this$0.searchnav.getSearchEngine();
                    this.this$0.searchquery = this.this$0.helpsearch.createQuery();
                    this.this$0.searchquery.addSearchListener(this.this$0);
                }
                BasicSearchNavigatorUI.debug(new StringBuffer().append("click on search w/: ").append(this.this$0.searchparams.getText()).toString());
                if (this.this$0.searchquery.isActive()) {
                    this.this$0.searchquery.stop();
                }
                this.this$0.searchquery.start(this.this$0.searchparams.getText(), this.this$0.searchnav.getLocale());
            } catch (Exception e) {
                this.this$0.searchparams.setCursor(this.this$0.paramCursor);
                this.this$0.tree.setCursor(this.this$0.treeCursor);
                e.printStackTrace();
                this.this$0.searchnav.getToolkit().beep();
                this.this$0.searchnav.getToolkit().beep();
            }
        }
    }

    public void installUI(JComponent jComponent) {
        this.searchnav = (JHelpSearchNavigator) jComponent;
        HelpModel model = this.searchnav.getModel();
        this.searchnav.setLayout(new BorderLayout());
        this.searchnav.addPropertyChangeListener(this);
        this.searchnav.addComponentListener(this);
        if (model != null) {
            model.addHelpModelListener(this);
        }
        JLabel jLabel = new JLabel(HelpUtilities.getString(HelpUtilities.getLocale(jComponent), "search.findLabel"));
        this.searchparams = new JTextField("", 20);
        jLabel.setLabelFor(this.searchparams);
        this.searchparams.addActionListener(this.searchAction);
        Component jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, 0));
        jPanel.add(jLabel);
        jPanel.add(this.searchparams);
        this.searchnav.add("North", jPanel);
        this.topNode = new DefaultMutableTreeNode();
        this.lastTOCnode = null;
        this.tree = new JTree(this.topNode);
        this.tree.getSelectionModel().addTreeSelectionListener(this);
        this.tree.setShowsRootHandles(false);
        this.tree.setRootVisible(false);
        this.sp = new JScrollPane();
        this.sp.getViewport().add(this.tree);
        this.searchnav.add("Center", this.sp);
        reloadData();
    }

    public void uninstallUI(JComponent jComponent) {
        HelpModel model = this.searchnav.getModel();
        this.searchnav.removeComponentListener(this);
        this.searchnav.removePropertyChangeListener(this);
        this.tree.getSelectionModel().removeTreeSelectionListener(this);
        this.searchnav.setLayout(null);
        this.searchnav.removeAll();
        if (model != null) {
            model.removeHelpModelListener(this);
        }
        this.searchnav = null;
    }

    public Dimension getPreferredSize(JComponent jComponent) {
        return new Dimension(200, 100);
    }

    public Dimension getMinimumSize(JComponent jComponent) {
        if (this.sp != null) {
            return this.sp.getLayout().minimumLayoutSize(this.sp);
        }
        return new Dimension(100, 100);
    }

    public Dimension getMaximumSize(JComponent jComponent) {
        return new Dimension(32767, 32767);
    }

    private void reloadData() {
        this.helpsearch = null;
        setCellRenderer(this.searchnav.getNavigatorView(), this.tree);
        HelpModel model = this.searchnav.getModel();
        if (model != null) {
            addSubHelpSets(model.getHelpSet());
        }
    }

    private void reloadData(HelpModel helpModel) {
        debug("reloadData using new model");
        this.helpsearch = null;
        SearchView searchView = null;
        this.newHelpSet = helpModel.getHelpSet();
        String name = ((SearchView) this.searchnav.getNavigatorView()).getName();
        NavigatorView[] navigatorViews = this.newHelpSet.getNavigatorViews();
        for (int i = 0; i < navigatorViews.length; i++) {
            if (navigatorViews[i].getName().equals(name)) {
                NavigatorView navigatorView = navigatorViews[i];
                if (navigatorView instanceof SearchView) {
                    searchView = (SearchView) navigatorView;
                    break;
                }
            }
        }
        if (searchView == null) {
            return;
        }
        this.topNode.removeAllChildren();
        this.searchnav.setSearchEngine(new MergingSearchEngine(searchView));
        setCellRenderer(searchView, this.tree);
        addSubHelpSets(this.newHelpSet);
    }

    protected void addSubHelpSets(HelpSet helpSet) {
        Enumeration helpSets = helpSet.getHelpSets();
        while (helpSets.hasMoreElements()) {
            HelpSet helpSet2 = (HelpSet) helpSets.nextElement();
            NavigatorView[] navigatorViews = helpSet2.getNavigatorViews();
            for (int i = 0; i < navigatorViews.length; i++) {
                if (this.searchnav.canMerge(navigatorViews[i])) {
                    this.searchnav.merge(navigatorViews[i]);
                }
            }
            addSubHelpSets(helpSet2);
        }
    }

    @Override // javax.help.plaf.HelpNavigatorUI
    public void merge(NavigatorView navigatorView) {
        debug(new StringBuffer().append("merging ").append(navigatorView).toString());
        String text = this.searchparams.getText();
        if (text != null && text.length() != 0) {
            this.searchAction.actionPerformed(new ActionEvent(this.searchparams, 1001, ""));
        }
    }

    @Override // javax.help.plaf.HelpNavigatorUI
    public void remove(NavigatorView navigatorView) {
        debug(new StringBuffer().append("removing ").append(navigatorView).toString());
        if (this.searchparams.getText() != null) {
            this.searchAction.actionPerformed(new ActionEvent(this.searchparams, 1001, ""));
        }
    }

    protected void setCellRenderer(NavigatorView navigatorView, JTree jTree) {
        if (navigatorView == null) {
            return;
        }
        jTree.setCellRenderer(new BasicSearchCellRenderer(navigatorView.getHelpSet().getCombinedMap()));
    }

    @Override // javax.help.event.HelpModelListener
    public void idChanged(HelpModelEvent helpModelEvent) {
        SearchTOCItem searchTOCItem;
        Map.ID id;
        Map.ID id2 = helpModelEvent.getID();
        URL url = helpModelEvent.getURL();
        HelpModel model = this.searchnav.getModel();
        debug(new StringBuffer().append("idChanged(").append(helpModelEvent).append(")").toString());
        if (helpModelEvent.getSource() != model) {
            debug("Internal inconsistency!");
            debug(new StringBuffer().append("  ").append(helpModelEvent.getSource()).append(" != ").append(model).toString());
            throw new Error("Internal error");
        }
        TreePath selectionPath = this.tree.getSelectionPath();
        if (selectionPath != null) {
            Object lastPathComponent = selectionPath.getLastPathComponent();
            if ((lastPathComponent instanceof DefaultMutableTreeNode) && (searchTOCItem = (SearchTOCItem) ((DefaultMutableTreeNode) lastPathComponent).getUserObject()) != null && (id = searchTOCItem.getID()) != null && id.equals(id2)) {
                return;
            }
        }
        DefaultMutableTreeNode defaultMutableTreeNodeFindIDorURL = findIDorURL(this.topNode, id2, url);
        if (defaultMutableTreeNodeFindIDorURL == null) {
            debug("node didn't exist");
            this.tree.clearSelection();
        } else {
            TreePath treePath = new TreePath(defaultMutableTreeNodeFindIDorURL.getPath());
            this.tree.expandPath(treePath);
            this.tree.setSelectionPath(treePath);
            this.tree.scrollPathToVisible(treePath);
        }
    }

    protected JHelpNavigator getHelpNavigator() {
        return this.searchnav;
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        SearchTOCItem searchTOCItem;
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
        if (treeItemArr != null && treeItemArr.length == 1 && (searchTOCItem = (SearchTOCItem) treeItemArr[0]) != null) {
            if (searchTOCItem.getID() != null) {
                try {
                    model.setCurrentID(searchTOCItem.getID(), searchTOCItem.getName(), helpNavigator);
                } catch (InvalidHelpSetContextException e) {
                    System.err.println(new StringBuffer().append("BadID: ").append(searchTOCItem.getID()).toString());
                    return;
                }
            } else if (searchTOCItem.getURL() != null) {
                model.setCurrentURL(searchTOCItem.getURL(), searchTOCItem.getName(), helpNavigator);
            } else {
                return;
            }
            if (model instanceof TextHelpModel) {
                DefaultHelpModel.DefaultHighlight[] defaultHighlightArr = new DefaultHelpModel.DefaultHighlight[searchTOCItem.hitCount()];
                int i2 = 0;
                Enumeration searchHits = searchTOCItem.getSearchHits();
                while (searchHits.hasMoreElements()) {
                    SearchHit searchHit = (SearchHit) searchHits.nextElement();
                    defaultHighlightArr[i2] = new DefaultHelpModel.DefaultHighlight(searchHit.getBegin(), searchHit.getEnd());
                    i2++;
                }
                ((TextHelpModel) model).setHighlights(defaultHighlightArr);
            }
        }
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        debug(new StringBuffer().append(this).append(" ").append("propertyChange: ").append(propertyChangeEvent.getSource()).append(" ").append(propertyChangeEvent.getPropertyName()).toString());
        if (propertyChangeEvent.getSource() == this.searchnav) {
            String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName.equals("helpModel")) {
                reloadData((HelpModel) propertyChangeEvent.getNewValue());
                return;
            }
            if (propertyName.equals(ElementTags.FONT)) {
                debug("Font change");
                Font font = (Font) propertyChangeEvent.getNewValue();
                this.searchparams.setFont(font);
                RepaintManager.currentManager(this.searchparams).markCompletelyDirty(this.searchparams);
                this.tree.setFont(font);
                RepaintManager.currentManager(this.tree).markCompletelyDirty(this.tree);
            }
        }
    }

    public void componentResized(ComponentEvent componentEvent) {
    }

    public void componentMoved(ComponentEvent componentEvent) {
    }

    public void componentShown(ComponentEvent componentEvent) {
        this.searchparams.selectAll();
        this.searchparams.requestFocus();
    }

    public void componentHidden(ComponentEvent componentEvent) {
    }

    private DefaultMutableTreeNode findIDorURL(DefaultMutableTreeNode defaultMutableTreeNode, Map.ID id, URL url) {
        SearchTOCItem searchTOCItem = (SearchTOCItem) defaultMutableTreeNode.getUserObject();
        if (searchTOCItem != null) {
            Map.ID id2 = searchTOCItem.getID();
            if (id2 != null && id != null && id2.equals(id)) {
                return defaultMutableTreeNode;
            }
            URL url2 = searchTOCItem.getURL();
            if (url2 != null && url != null && url.sameFile(url2)) {
                return defaultMutableTreeNode;
            }
        }
        int childCount = defaultMutableTreeNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode defaultMutableTreeNodeFindIDorURL = findIDorURL((DefaultMutableTreeNode) defaultMutableTreeNode.getChildAt(i), id, url);
            if (defaultMutableTreeNodeFindIDorURL != null) {
                return defaultMutableTreeNodeFindIDorURL;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reorder(Vector vector) {
        debug("reorder nodes");
        this.topNode.removeAllChildren();
        MutableTreeNode[] mutableTreeNodeArr = new DefaultMutableTreeNode[vector.size()];
        vector.copyInto(mutableTreeNodeArr);
        quickSort(mutableTreeNodeArr, 0, mutableTreeNodeArr.length - 1);
        for (MutableTreeNode mutableTreeNode : mutableTreeNodeArr) {
            this.topNode.add(mutableTreeNode);
        }
        this.tree.getModel().reload();
        this.tree.invalidate();
        this.tree.repaint();
    }

    void quickSort(DefaultMutableTreeNode[] defaultMutableTreeNodeArr, int i, int i2) {
        int i3 = i;
        int i4 = i2;
        if (i2 > i) {
            int i5 = (i + i2) / 2;
            while (i3 <= i4) {
                while (i3 < i2 && compare(defaultMutableTreeNodeArr[i3], defaultMutableTreeNodeArr[i5]) > 0) {
                    i3++;
                }
                while (i4 > i && compare(defaultMutableTreeNodeArr[i4], defaultMutableTreeNodeArr[i5]) < 0) {
                    i4--;
                }
                if (i3 <= i4) {
                    swap(defaultMutableTreeNodeArr, i3, i4);
                    i3++;
                    i4--;
                }
            }
            if (i < i4) {
                quickSort(defaultMutableTreeNodeArr, i, i4);
            }
            if (i3 < i2) {
                quickSort(defaultMutableTreeNodeArr, i3, i2);
            }
        }
    }

    private void swap(DefaultMutableTreeNode[] defaultMutableTreeNodeArr, int i, int i2) {
        DefaultMutableTreeNode defaultMutableTreeNode = defaultMutableTreeNodeArr[i];
        defaultMutableTreeNodeArr[i] = defaultMutableTreeNodeArr[i2];
        defaultMutableTreeNodeArr[i2] = defaultMutableTreeNode;
    }

    private int compare(DefaultMutableTreeNode defaultMutableTreeNode, DefaultMutableTreeNode defaultMutableTreeNode2) {
        SearchTOCItem searchTOCItem = (SearchTOCItem) defaultMutableTreeNode.getUserObject();
        double confidence = searchTOCItem.getConfidence();
        int iHitCount = searchTOCItem.hitCount();
        SearchTOCItem searchTOCItem2 = (SearchTOCItem) defaultMutableTreeNode2.getUserObject();
        double confidence2 = searchTOCItem2.getConfidence();
        int iHitCount2 = searchTOCItem2.hitCount();
        if (confidence > confidence2) {
            return -1;
        }
        if (confidence < confidence2) {
            return 1;
        }
        if (iHitCount < iHitCount2) {
            return -1;
        }
        if (iHitCount > iHitCount2) {
            return 1;
        }
        return 0;
    }

    @Override // javax.help.search.SearchListener
    public synchronized void itemsFound(SearchEvent searchEvent) {
        SwingUtilities.invokeLater(new SearchItemsFound(this, searchEvent));
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicSearchNavigatorUI$SearchItemsFound.class */
    class SearchItemsFound implements Runnable {
        SearchEvent e;
        private final BasicSearchNavigatorUI this$0;

        SearchItemsFound(BasicSearchNavigatorUI basicSearchNavigatorUI, SearchEvent searchEvent) {
            this.this$0 = basicSearchNavigatorUI;
            this.e = searchEvent;
        }

        @Override // java.lang.Runnable
        public void run() {
            Vector vector = new Vector();
            Enumeration enumerationChildren = this.this$0.topNode.children();
            while (enumerationChildren.hasMoreElements()) {
                vector.addElement((DefaultMutableTreeNode) enumerationChildren.nextElement());
            }
            BasicSearchNavigatorUI.debug("items found");
            HelpSet helpSet = this.this$0.searchnav.getModel().getHelpSet();
            BasicSearchNavigatorUI.debug(new StringBuffer().append("hs:").append(helpSet.toString()).toString());
            helpSet.getCombinedMap();
            Enumeration searchItems = this.e.getSearchItems();
            while (searchItems.hasMoreElements()) {
                SearchItem searchItem = (SearchItem) searchItems.nextElement();
                BasicSearchNavigatorUI.debug(new StringBuffer().append("  item: ").append(searchItem).toString());
                try {
                    URL url = new URL(searchItem.getBase(), searchItem.getFilename());
                    boolean z = false;
                    Enumeration enumerationElements = vector.elements();
                    while (enumerationElements.hasMoreElements()) {
                        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) enumerationElements.nextElement();
                        URL url2 = ((SearchTOCItem) defaultMutableTreeNode.getUserObject()).getURL();
                        if (url2 != null && url != null && url.sameFile(url2)) {
                            ((SearchTOCItem) defaultMutableTreeNode.getUserObject()).addSearchHit(new SearchHit(searchItem.getConfidence(), searchItem.getBegin(), searchItem.getEnd()));
                            z = true;
                            break;
                        }
                    }
                    if (!z) {
                        vector.addElement(new DefaultMutableTreeNode(new SearchTOCItem(searchItem)));
                    }
                } catch (MalformedURLException e) {
                    System.err.println(new StringBuffer().append("Failed to create URL from ").append(searchItem.getBase()).append("|").append(searchItem.getFilename()).toString());
                }
            }
            this.this$0.reorder(vector);
            this.this$0.tree.getModel().reload();
        }
    }

    @Override // javax.help.search.SearchListener
    public synchronized void searchStarted(SearchEvent searchEvent) {
        debug("search Started");
        SwingUtilities.invokeLater(new Runnable(this) { // from class: javax.help.plaf.basic.BasicSearchNavigatorUI.1
            private final BasicSearchNavigatorUI this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.tree.getSelectionModel().clearSelection();
                this.this$0.topNode.removeAllChildren();
                this.this$0.tree.getModel().reload();
                this.this$0.tree.invalidate();
                this.this$0.tree.repaint();
            }
        });
    }

    @Override // javax.help.search.SearchListener
    public synchronized void searchFinished(SearchEvent searchEvent) {
        SwingUtilities.invokeLater(new Runnable(this) { // from class: javax.help.plaf.basic.BasicSearchNavigatorUI.2
            private final BasicSearchNavigatorUI this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                TreeSelectionModel selectionModel = this.this$0.tree.getSelectionModel();
                if (this.this$0.lastTOCnode == null && this.this$0.topNode.getChildCount() > 0) {
                    DefaultMutableTreeNode firstChild = this.this$0.topNode.getFirstChild();
                    if (firstChild != null) {
                        selectionModel.clearSelection();
                        selectionModel.setSelectionPath(new TreePath(firstChild.getPath()));
                    }
                } else {
                    this.this$0.searchnav.getToolkit().beep();
                }
                this.this$0.searchparams.setCursor(this.this$0.paramCursor);
                this.this$0.tree.setCursor(this.this$0.treeCursor);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void debug(String str) {
        if (debug) {
            System.out.println(new StringBuffer().append("BasicSearchNavigatorUI: ").append(str).toString());
        }
    }
}
