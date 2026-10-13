package javax.help;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.MenuComponent;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;

/* JADX INFO: loaded from: autofirma.jar:javax/help/CSH.class */
public class CSH {
    private static java.util.Map comps;
    private static java.util.Map parents;
    private static Vector managers = new Vector();
    private static final boolean debug = false;
    static Class class$javax$help$HelpSet;
    static Class class$java$lang$String;

    /* JADX INFO: loaded from: autofirma.jar:javax/help/CSH$Manager.class */
    public interface Manager {
        HelpSet getHelpSet(Object obj, AWTEvent aWTEvent);

        String getHelpIDString(Object obj, AWTEvent aWTEvent);
    }

    public static void addManager(Manager manager) {
        managers.add(manager);
    }

    public static void addManager(int i, Manager manager) {
        managers.add(i, manager);
    }

    public static boolean removeManager(Manager manager) {
        return managers.remove(manager);
    }

    public static void removeManager(int i) {
        managers.remove(i);
    }

    public static void removeAllManagers() {
        managers.clear();
    }

    public static Manager getManager(int i) {
        return (Manager) managers.get(i);
    }

    public static Manager[] getManagers() {
        return (Manager[]) managers.toArray(new Manager[0]);
    }

    public static int getManagerCount() {
        return managers.size();
    }

    private static void _setHelpIDString(Object obj, String str) {
        if (obj instanceof JComponent) {
            ((JComponent) obj).putClientProperty("HelpID", str);
            return;
        }
        if ((obj instanceof Component) || (obj instanceof MenuItem)) {
            if (comps == null) {
                comps = new WeakHashMap(5);
            }
            Hashtable hashtable = (Hashtable) comps.get(obj);
            if (hashtable != null) {
                if (str != null) {
                    hashtable.put("HelpID", str);
                    return;
                }
                hashtable.remove("HelpID");
                if (hashtable.isEmpty()) {
                    comps.remove(obj);
                    return;
                }
                return;
            }
            if (str != null) {
                Hashtable hashtable2 = new Hashtable(2);
                hashtable2.put("HelpID", str);
                comps.put(obj, hashtable2);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Invalid Component");
    }

    private static String _getHelpIDString(Object obj) {
        Hashtable hashtable;
        String str = null;
        if (obj != null) {
            if (obj instanceof JComponent) {
                str = (String) ((JComponent) obj).getClientProperty("HelpID");
            } else if ((obj instanceof Component) || (obj instanceof MenuItem)) {
                if (comps != null && (hashtable = (Hashtable) comps.get(obj)) != null) {
                    str = (String) hashtable.get("HelpID");
                }
            } else {
                throw new IllegalArgumentException("Invalid Component");
            }
        }
        return str;
    }

    private static String _getHelpIDString(Object obj, AWTEvent aWTEvent) {
        if (obj != null) {
            for (Manager manager : getManagers()) {
                String helpIDString = manager.getHelpIDString(obj, aWTEvent);
                if (helpIDString != null) {
                    return helpIDString;
                }
            }
            return null;
        }
        return null;
    }

    private static Object getParent(Object obj) {
        Object parent;
        if (obj == null) {
            return null;
        }
        if (obj instanceof MenuComponent) {
            parent = ((MenuComponent) obj).getParent();
        } else if (obj instanceof JPopupMenu) {
            parent = ((JPopupMenu) obj).getInvoker();
        } else if (obj instanceof Component) {
            parent = ((Component) obj).getParent();
        } else {
            throw new IllegalArgumentException("Invalid Component");
        }
        if (parent == null && parents != null) {
            parent = parents.get(obj);
        }
        return parent;
    }

    public static void setHelpIDString(Component component, String str) {
        _setHelpIDString(component, str);
    }

    public static void setHelpIDString(MenuItem menuItem, String str) {
        _setHelpIDString(menuItem, str);
    }

    public static String getHelpIDString(Object obj, AWTEvent aWTEvent) {
        if (obj == null) {
            return null;
        }
        String str_getHelpIDString = _getHelpIDString(obj, aWTEvent);
        if (str_getHelpIDString == null) {
            str_getHelpIDString = _getHelpIDString(obj);
        }
        if (str_getHelpIDString == null) {
            str_getHelpIDString = getHelpIDString(getParent(obj), aWTEvent);
        }
        return str_getHelpIDString;
    }

    public static String getHelpIDString(Component component) {
        return getHelpIDString(component, null);
    }

    public static String getHelpIDString(MenuItem menuItem) {
        return getHelpIDString(menuItem, null);
    }

    private static void _setHelpSet(Object obj, HelpSet helpSet) {
        if (obj instanceof JComponent) {
            ((JComponent) obj).putClientProperty("HelpSet", helpSet);
            return;
        }
        if ((obj instanceof Component) || (obj instanceof MenuItem)) {
            if (comps == null) {
                comps = new WeakHashMap(5);
            }
            Hashtable hashtable = (Hashtable) comps.get(obj);
            if (hashtable != null) {
                if (helpSet != null) {
                    hashtable.put("HelpSet", helpSet);
                    return;
                }
                hashtable.remove("HelpSet");
                if (hashtable.isEmpty()) {
                    comps.remove(obj);
                    return;
                }
                return;
            }
            if (helpSet != null) {
                Hashtable hashtable2 = new Hashtable(2);
                hashtable2.put("HelpSet", helpSet);
                comps.put(obj, hashtable2);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Invalid Component");
    }

    private static HelpSet _getHelpSet(Object obj) {
        Hashtable hashtable;
        HelpSet helpSet = null;
        if (obj != null) {
            if (obj instanceof JComponent) {
                helpSet = (HelpSet) ((JComponent) obj).getClientProperty("HelpSet");
            } else if ((obj instanceof Component) || (obj instanceof MenuItem)) {
                if (comps != null && (hashtable = (Hashtable) comps.get(obj)) != null) {
                    helpSet = (HelpSet) hashtable.get("HelpSet");
                }
            } else {
                throw new IllegalArgumentException("Invalid Component");
            }
        }
        return helpSet;
    }

    private static HelpSet _getHelpSet(Object obj, AWTEvent aWTEvent) {
        HelpSet helpSet = null;
        if (obj != null) {
            for (Manager manager : getManagers()) {
                helpSet = manager.getHelpSet(obj, aWTEvent);
                if (helpSet != null) {
                    return helpSet;
                }
            }
        }
        return helpSet;
    }

    public static void setHelpSet(Component component, HelpSet helpSet) {
        _setHelpSet(component, helpSet);
    }

    public static void setHelpSet(MenuItem menuItem, HelpSet helpSet) {
        _setHelpSet(menuItem, helpSet);
    }

    public static HelpSet getHelpSet(Object obj, AWTEvent aWTEvent) {
        if (obj == null) {
            return null;
        }
        String str_getHelpIDString = _getHelpIDString(obj, aWTEvent);
        if (str_getHelpIDString == null) {
            str_getHelpIDString = _getHelpIDString(obj);
        }
        if (str_getHelpIDString != null) {
            HelpSet helpSet_getHelpSet = _getHelpSet(obj, aWTEvent);
            if (helpSet_getHelpSet == null) {
                helpSet_getHelpSet = _getHelpSet(obj);
            }
            return helpSet_getHelpSet;
        }
        return getHelpSet(getParent(obj), aWTEvent);
    }

    public static HelpSet getHelpSet(Component component) {
        return getHelpSet(component, null);
    }

    public static HelpSet getHelpSet(MenuItem menuItem) {
        return getHelpSet(menuItem, null);
    }

    public static Object trackCSEvents() {
        MouseEvent mouseEvent = getMouseEvent();
        if (mouseEvent != null) {
            return getDeepestObjectAt(mouseEvent.getSource(), mouseEvent.getX(), mouseEvent.getY());
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayHelp(HelpBroker helpBroker, HelpSet helpSet, String str, String str2, Object obj, Object obj2, AWTEvent aWTEvent) {
        Class<?> clsClass$;
        Class<?> clsClass$2;
        Class<?> clsLoadClass;
        Presentation presentation = null;
        if (helpBroker != null) {
            if (helpBroker instanceof DefaultHelpBroker) {
                ((DefaultHelpBroker) helpBroker).setActivationObject(obj2);
            }
        } else {
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
            Object[] objArr = {helpSet, str2};
            try {
                ClassLoader loader = helpSet.getLoader();
                if (loader == null) {
                    clsLoadClass = Class.forName(str);
                } else {
                    clsLoadClass = loader.loadClass(str);
                }
                presentation = (Presentation) clsLoadClass.getMethod("getPresentation", clsArr).invoke(null, objArr);
                if (presentation == null) {
                    return;
                }
                if (presentation instanceof WindowPresentation) {
                    ((WindowPresentation) presentation).setActivationObject(obj2);
                }
                if ((presentation instanceof Popup) && (obj instanceof Component)) {
                    ((Popup) presentation).setInvoker((Component) obj);
                }
            } catch (Exception e) {
                throw new RuntimeException("error invoking presentation");
            }
        }
        String helpIDString = getHelpIDString(obj, aWTEvent);
        HelpSet helpSet2 = getHelpSet(obj, aWTEvent);
        if (helpSet2 == null) {
            if (helpBroker != null) {
                helpSet2 = helpBroker.getHelpSet();
            } else {
                helpSet2 = helpSet;
            }
        }
        try {
            Map.ID idCreate = Map.ID.create(helpIDString, helpSet2);
            if (idCreate == null) {
                idCreate = helpSet2.getHomeID();
            }
            if (helpBroker != null) {
                helpBroker.setCurrentID(idCreate);
                helpBroker.setDisplayed(true);
            } else {
                presentation.setCurrentID(idCreate);
                presentation.setDisplayed(true);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static MouseEvent getMouseEvent() {
        KeyEvent keyEvent;
        try {
            if (EventQueue.isDispatchThread()) {
                EventQueue systemEventQueue = null;
                try {
                    systemEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
                } catch (Exception e) {
                    debug(e);
                }
                if (systemEventQueue == null) {
                    return null;
                }
                int i = -1;
                while (true) {
                    i++;
                    InputEvent nextEvent = systemEventQueue.getNextEvent();
                    Object source = nextEvent.getSource();
                    if (nextEvent instanceof ActiveEvent) {
                        ((ActiveEvent) nextEvent).dispatch();
                    } else if (source instanceof Component) {
                        if (nextEvent instanceof KeyEvent) {
                            keyEvent = (KeyEvent) nextEvent;
                            if (keyEvent.getKeyCode() == 3 || keyEvent.getKeyCode() == 27) {
                                break;
                            }
                            keyEvent.consume();
                        } else if (nextEvent instanceof MouseEvent) {
                            MouseEvent mouseEvent = (MouseEvent) nextEvent;
                            int id = mouseEvent.getID();
                            if ((id == 500 || id == 501 || id == 502) && SwingUtilities.isLeftMouseButton(mouseEvent)) {
                                if (id == 500 && i == 0) {
                                    dispatchEvent(nextEvent);
                                } else {
                                    mouseEvent.consume();
                                    return mouseEvent;
                                }
                            } else {
                                mouseEvent.consume();
                            }
                        } else {
                            dispatchEvent(nextEvent);
                        }
                    } else if (source instanceof MenuComponent) {
                        if (nextEvent instanceof InputEvent) {
                            nextEvent.consume();
                        }
                    } else {
                        System.err.println(new StringBuffer().append("unable to dispatch event: ").append(nextEvent).toString());
                    }
                }
                keyEvent.consume();
                return null;
            }
        } catch (InterruptedException e2) {
            debug(e2);
        }
        debug("Fall Through code");
        return null;
    }

    private static void dispatchEvent(AWTEvent aWTEvent) {
        Object source = aWTEvent.getSource();
        if (aWTEvent instanceof ActiveEvent) {
            ((ActiveEvent) aWTEvent).dispatch();
            return;
        }
        if (source instanceof Component) {
            ((Component) source).dispatchEvent(aWTEvent);
        } else if (source instanceof MenuComponent) {
            ((MenuComponent) source).dispatchEvent(aWTEvent);
        } else {
            System.err.println(new StringBuffer().append("unable to dispatch event: ").append(aWTEvent).toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object getDeepestObjectAt(Object obj, int i, int i2) {
        if (obj instanceof Container) {
            Component component = (Container) obj;
            Component componentFindComponentAt = findComponentAt(component, component.getWidth(), component.getHeight(), i, i2);
            if (componentFindComponentAt != null && componentFindComponentAt != component) {
                if (componentFindComponentAt instanceof JRootPane) {
                    JLayeredPane layeredPane = ((JRootPane) componentFindComponentAt).getLayeredPane();
                    Rectangle bounds = layeredPane.getBounds();
                    componentFindComponentAt = (Component) getDeepestObjectAt(layeredPane, i - bounds.x, i2 - bounds.y);
                }
                if (componentFindComponentAt != null) {
                    return componentFindComponentAt;
                }
            }
        }
        return obj;
    }

    private static Component findComponentAt(Container container, int i, int i2, int i3, int i4) {
        Component componentAt;
        CellRendererPane componentAt2;
        Component componentAt3;
        CellRendererPane componentAt4;
        synchronized (container.getTreeLock()) {
            if (i3 >= 0 && i3 < i && i4 >= 0 && i4 < i2) {
                if (container.isVisible() && container.isEnabled()) {
                    CellRendererPane[] components = container.getComponents();
                    int componentCount = container.getComponentCount();
                    for (int i5 = 0; i5 < componentCount; i5++) {
                        CellRendererPane cellRendererPane = components[i5];
                        Rectangle bounds = null;
                        if ((cellRendererPane instanceof CellRendererPane) && (componentAt4 = getComponentAt(cellRendererPane, i3, i4)) != null) {
                            bounds = getRectangleAt(cellRendererPane, i3, i4);
                            cellRendererPane = componentAt4;
                        }
                        if (cellRendererPane != null && !cellRendererPane.isLightweight()) {
                            if (bounds == null || bounds.width == 0 || bounds.height == 0) {
                                bounds = cellRendererPane.getBounds();
                            }
                            if (cellRendererPane instanceof Container) {
                                componentAt3 = findComponentAt((Container) cellRendererPane, bounds.width, bounds.height, i3 - bounds.x, i4 - bounds.y);
                            } else {
                                componentAt3 = cellRendererPane.getComponentAt(i3 - bounds.x, i4 - bounds.y);
                            }
                            if (componentAt3 != null && componentAt3.isVisible() && componentAt3.isEnabled()) {
                                return componentAt3;
                            }
                        }
                    }
                    for (int i6 = 0; i6 < componentCount; i6++) {
                        CellRendererPane cellRendererPane2 = components[i6];
                        Rectangle bounds2 = null;
                        if ((cellRendererPane2 instanceof CellRendererPane) && (componentAt2 = getComponentAt(cellRendererPane2, i3, i4)) != null) {
                            bounds2 = getRectangleAt(cellRendererPane2, i3, i4);
                            cellRendererPane2 = componentAt2;
                        }
                        if (cellRendererPane2 != null && cellRendererPane2.isLightweight()) {
                            if (bounds2 == null || bounds2.width == 0 || bounds2.height == 0) {
                                bounds2 = cellRendererPane2.getBounds();
                            }
                            if (cellRendererPane2 instanceof Container) {
                                componentAt = findComponentAt((Container) cellRendererPane2, bounds2.width, bounds2.height, i3 - bounds2.x, i4 - bounds2.y);
                            } else {
                                componentAt = cellRendererPane2.getComponentAt(i3 - bounds2.x, i4 - bounds2.y);
                            }
                            if (componentAt != null && componentAt.isVisible() && componentAt.isEnabled()) {
                                return componentAt;
                            }
                        }
                    }
                    return container;
                }
            }
            return null;
        }
    }

    private static Rectangle getRectangleAt(CellRendererPane cellRendererPane, int i, int i2) {
        Rectangle rectangleAt = null;
        JTable parent = cellRendererPane.getParent();
        if (parent instanceof JTable) {
            rectangleAt = getRectangleAt(parent, i, i2);
        } else if (parent instanceof JTableHeader) {
            rectangleAt = getRectangleAt((JTableHeader) parent, i, i2);
        } else if (parent instanceof JTree) {
            rectangleAt = getRectangleAt((JTree) parent, i, i2);
        } else if (parent instanceof JList) {
            rectangleAt = getRectangleAt((JList) parent, i, i2);
        }
        return rectangleAt;
    }

    private static Component getComponentAt(CellRendererPane cellRendererPane, int i, int i2) {
        Component componentAt = null;
        JTable parent = cellRendererPane.getParent();
        if (parent instanceof JTable) {
            componentAt = getComponentAt(parent, i, i2);
        } else if (parent instanceof JTableHeader) {
            componentAt = getComponentAt((JTableHeader) parent, i, i2);
        } else if (parent instanceof JTree) {
            componentAt = getComponentAt((JTree) parent, i, i2);
        } else if (parent instanceof JList) {
            componentAt = getComponentAt((JList) parent, i, i2);
        }
        if (componentAt != null) {
            if (parents == null) {
                parents = new WeakHashMap(4) { // from class: javax.help.CSH.1
                    @Override // java.util.WeakHashMap, java.util.AbstractMap, java.util.Map
                    public Object put(Object obj, Object obj2) {
                        return super.put(obj, new WeakReference(obj2));
                    }

                    @Override // java.util.WeakHashMap, java.util.AbstractMap, java.util.Map
                    public Object get(Object obj) {
                        WeakReference weakReference = (WeakReference) super.get(obj);
                        if (weakReference != null) {
                            return weakReference.get();
                        }
                        return null;
                    }
                };
            }
            parents.put(componentAt, cellRendererPane);
        }
        return componentAt;
    }

    private static Rectangle getRectangleAt(JTableHeader jTableHeader, int i, int i2) {
        Rectangle headerRect = null;
        try {
            headerRect = jTableHeader.getHeaderRect(jTableHeader.columnAtPoint(new Point(i, i2)));
        } catch (Exception e) {
        }
        return headerRect;
    }

    private static Component getComponentAt(JTableHeader jTableHeader, int i, int i2) {
        try {
            if (!jTableHeader.contains(i, i2) || !jTableHeader.isVisible() || !jTableHeader.isEnabled()) {
                return null;
            }
            TableColumnModel columnModel = jTableHeader.getColumnModel();
            int columnIndexAtX = columnModel.getColumnIndexAtX(i);
            TableColumn column = columnModel.getColumn(columnIndexAtX);
            TableCellRenderer headerRenderer = column.getHeaderRenderer();
            if (headerRenderer == null) {
                headerRenderer = jTableHeader.getDefaultRenderer();
            }
            return headerRenderer.getTableCellRendererComponent(jTableHeader.getTable(), column.getHeaderValue(), false, false, -1, columnIndexAtX);
        } catch (Exception e) {
            return null;
        }
    }

    private static Rectangle getRectangleAt(JTable jTable, int i, int i2) {
        Rectangle cellRect = null;
        try {
            Point point = new Point(i, i2);
            cellRect = jTable.getCellRect(jTable.rowAtPoint(point), jTable.columnAtPoint(point), true);
        } catch (Exception e) {
        }
        return cellRect;
    }

    private static Component getComponentAt(JTable jTable, int i, int i2) {
        try {
            if (!jTable.contains(i, i2) || !jTable.isVisible() || !jTable.isEnabled()) {
                return null;
            }
            Point point = new Point(i, i2);
            int iRowAtPoint = jTable.rowAtPoint(point);
            int iColumnAtPoint = jTable.columnAtPoint(point);
            if (jTable.isEditing() && jTable.getEditingRow() == iRowAtPoint && jTable.getEditingColumn() == iColumnAtPoint) {
                return null;
            }
            return jTable.prepareRenderer(jTable.getCellRenderer(iRowAtPoint, iColumnAtPoint), iRowAtPoint, iColumnAtPoint);
        } catch (Exception e) {
            return null;
        }
    }

    private static Rectangle getRectangleAt(JTree jTree, int i, int i2) {
        Rectangle pathBounds = null;
        try {
            pathBounds = jTree.getPathBounds(jTree.getPathForLocation(i, i2));
        } catch (Exception e) {
        }
        return pathBounds;
    }

    private static Component getComponentAt(JTree jTree, int i, int i2) {
        try {
            TreePath pathForLocation = jTree.getPathForLocation(i, i2);
            if (jTree.isEditing() && jTree.getSelectionPath() == pathForLocation) {
                return null;
            }
            int rowForPath = jTree.getRowForPath(pathForLocation);
            Object lastPathComponent = pathForLocation.getLastPathComponent();
            return jTree.getCellRenderer().getTreeCellRendererComponent(jTree, lastPathComponent, jTree.isRowSelected(rowForPath), jTree.isExpanded(pathForLocation), jTree.getModel().isLeaf(lastPathComponent), rowForPath, jTree.hasFocus() && jTree.getLeadSelectionRow() == rowForPath);
        } catch (Exception e) {
            return null;
        }
    }

    private static Rectangle getRectangleAt(JList jList, int i, int i2) {
        Rectangle cellBounds = null;
        try {
            int iLocationToIndex = jList.locationToIndex(new Point(i, i2));
            cellBounds = jList.getCellBounds(iLocationToIndex, iLocationToIndex);
        } catch (Exception e) {
        }
        return cellBounds;
    }

    private static Component getComponentAt(JList jList, int i, int i2) {
        try {
            int iLocationToIndex = jList.locationToIndex(new Point(i, i2));
            return jList.getCellRenderer().getListCellRendererComponent(jList, jList.getModel().getElementAt(iLocationToIndex), iLocationToIndex, jList.isSelectedIndex(iLocationToIndex), jList.hasFocus() && jList.getLeadSelectionIndex() == iLocationToIndex);
        } catch (Exception e) {
            return null;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/CSH$DisplayHelpFromFocus.class */
    public static class DisplayHelpFromFocus implements ActionListener {
        private HelpBroker hb;
        private HelpSet hs;
        private String presentation;
        private String presentationName;

        public DisplayHelpFromFocus(HelpBroker helpBroker) {
            this.hb = null;
            this.hs = null;
            this.presentation = null;
            this.presentationName = null;
            if (helpBroker == null) {
                throw new NullPointerException("hb");
            }
            this.hb = helpBroker;
        }

        public DisplayHelpFromFocus(HelpSet helpSet, String str, String str2) {
            this.hb = null;
            this.hs = null;
            this.presentation = null;
            this.presentationName = null;
            if (helpSet == null) {
                throw new NullPointerException("hs");
            }
            try {
                ClassLoader loader = helpSet.getLoader();
                if (loader == null) {
                    Class.forName(str);
                } else {
                    loader.loadClass(str);
                }
                this.presentation = str;
                this.presentationName = str2;
                this.hs = helpSet;
            } catch (Exception e) {
                throw new IllegalArgumentException(new StringBuffer().append(str).append("presentation  invalid").toString());
            }
        }

        public void actionPerformed(ActionEvent actionEvent) {
            Component component = (Component) actionEvent.getSource();
            if (this.hb instanceof DefaultHelpBroker) {
                ((DefaultHelpBroker) this.hb).setActivationObject(component);
            }
            Component componentFindFocusOwner = CSH.findFocusOwner(component);
            CSH.debug(new StringBuffer().append("focusOwner:").append(componentFindFocusOwner).toString());
            if (componentFindFocusOwner == null) {
                componentFindFocusOwner = component;
            }
            CSH.displayHelp(this.hb, this.hs, this.presentation, this.presentationName, componentFindFocusOwner, componentFindFocusOwner, actionEvent);
        }
    }

    private static JPopupMenu getRootPopupMenu(JPopupMenu jPopupMenu) {
        while (jPopupMenu != null && (jPopupMenu.getInvoker() instanceof JMenu) && (jPopupMenu.getInvoker().getParent() instanceof JPopupMenu)) {
            jPopupMenu = (JPopupMenu) jPopupMenu.getInvoker().getParent();
        }
        return jPopupMenu;
    }

    private static Component findFocusOwner(JPopupMenu jPopupMenu) {
        if (jPopupMenu == null) {
            return null;
        }
        synchronized (jPopupMenu.getTreeLock()) {
            if (!jPopupMenu.isVisible()) {
                return null;
            }
            JMenu jMenuFindFocusOwner = null;
            int componentCount = jPopupMenu.getComponentCount();
            for (int i = 0; i < componentCount; i++) {
                JMenu component = jPopupMenu.getComponent(i);
                if (component.hasFocus()) {
                    jMenuFindFocusOwner = component;
                    break;
                }
                if ((component instanceof JMenu) && component.isPopupMenuVisible()) {
                    jMenuFindFocusOwner = component;
                }
                if ((component instanceof JMenuItem) && ((JMenuItem) component).isArmed()) {
                    jMenuFindFocusOwner = component;
                }
            }
            if (jMenuFindFocusOwner instanceof JMenu) {
                jMenuFindFocusOwner = findFocusOwner(jMenuFindFocusOwner.getPopupMenu());
            }
            if (jMenuFindFocusOwner != null) {
                return jMenuFindFocusOwner;
            }
            return jPopupMenu;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Component findFocusOwner(Component component) {
        synchronized (component.getTreeLock()) {
            if (component instanceof JPopupMenu) {
                return findFocusOwner(getRootPopupMenu((JPopupMenu) component));
            }
            if (component.hasFocus()) {
                return component;
            }
            if (component instanceof Container) {
                int componentCount = ((Container) component).getComponentCount();
                for (int i = 0; i < componentCount; i++) {
                    Component componentFindFocusOwner = findFocusOwner(((Container) component).getComponent(i));
                    if (componentFindFocusOwner != null) {
                        return componentFindFocusOwner;
                    }
                }
            }
            return null;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/CSH$DisplayHelpAfterTracking.class */
    public static class DisplayHelpAfterTracking implements ActionListener {
        private HelpBroker hb;
        private HelpSet hs;
        private String presentation;
        private String presentationName;
        private Hashtable cursors;

        public DisplayHelpAfterTracking(HelpBroker helpBroker) {
            this.hb = null;
            this.hs = null;
            this.presentation = null;
            this.presentationName = null;
            if (helpBroker == null) {
                throw new NullPointerException("hb");
            }
            this.hb = helpBroker;
        }

        public DisplayHelpAfterTracking(HelpSet helpSet, String str, String str2) {
            this.hb = null;
            this.hs = null;
            this.presentation = null;
            this.presentationName = null;
            if (helpSet == null) {
                throw new NullPointerException("hs");
            }
            try {
                ClassLoader loader = helpSet.getLoader();
                if (loader == null) {
                    Class.forName(str);
                } else {
                    loader.loadClass(str);
                }
                this.presentation = str;
                this.presentationName = str2;
                this.hs = helpSet;
            } catch (Exception e) {
                throw new IllegalArgumentException(new StringBuffer().append(str).append("presentation  invalid").toString());
            }
        }

        public void actionPerformed(ActionEvent actionEvent) {
            SwingHelpUtilities.installUIDefaults();
            Cursor cursor = (Cursor) UIManager.get("HelpOnItemCursor");
            if (cursor == null) {
                return;
            }
            Vector topContainers = null;
            this.cursors = null;
            if (cursor != null) {
                this.cursors = new Hashtable();
                topContainers = getTopContainers(actionEvent.getSource());
                Enumeration enumerationElements = topContainers.elements();
                while (enumerationElements.hasMoreElements()) {
                    setAndStoreCursors((Container) enumerationElements.nextElement(), cursor);
                }
            }
            MouseEvent mouseEvent = CSH.getMouseEvent();
            CSH.debug(new StringBuffer().append("CSH.getMouseEvent() >>> ").append(mouseEvent).toString());
            if (mouseEvent != null) {
                Object deepestObjectAt = CSH.getDeepestObjectAt(mouseEvent.getSource(), mouseEvent.getX(), mouseEvent.getY());
                CSH.debug(new StringBuffer().append("CSH.getDeepestObjectAt() >>> ").append(deepestObjectAt).toString());
                if (deepestObjectAt != null) {
                    CSH.displayHelp(this.hb, this.hs, this.presentation, this.presentationName, deepestObjectAt, actionEvent.getSource(), mouseEvent);
                }
            }
            if (topContainers != null) {
                Enumeration enumerationElements2 = topContainers.elements();
                while (enumerationElements2.hasMoreElements()) {
                    resetAndRestoreCursors((Container) enumerationElements2.nextElement());
                }
            }
            this.cursors = null;
        }

        private static Vector getTopContainers(Object obj) {
            Vector vector = new Vector();
            Applet root = getRoot(obj);
            if (root instanceof Applet) {
                try {
                    Enumeration applets = root.getAppletContext().getApplets();
                    while (applets.hasMoreElements()) {
                        vector.add(applets.nextElement());
                    }
                } catch (NullPointerException e) {
                    vector.add(root);
                }
            }
            Frame[] frames = Frame.getFrames();
            for (int i = 0; i < frames.length; i++) {
                for (Window window : frames[i].getOwnedWindows()) {
                    vector.add(window);
                }
                if (!vector.contains(frames[i])) {
                    vector.add(frames[i]);
                }
            }
            return vector;
        }

        private static Component getRoot(Object obj) {
            Object parent = obj;
            while (true) {
                Object obj2 = parent;
                if (obj2 == null) {
                    break;
                }
                obj = obj2;
                if (obj instanceof MenuComponent) {
                    parent = ((MenuComponent) obj).getParent();
                } else {
                    if (!(obj instanceof Component) || (obj instanceof Window) || (obj instanceof Applet)) {
                        break;
                    }
                    parent = ((Component) obj).getParent();
                }
            }
            if (obj instanceof Component) {
                return (Component) obj;
            }
            return null;
        }

        private void setAndStoreCursors(Component component, Cursor cursor) {
            if (component == null) {
                return;
            }
            Cursor cursor2 = component.getCursor();
            if (cursor2 != cursor) {
                this.cursors.put(component, cursor2);
                CSH.debug(new StringBuffer().append("set cursor on ").append(component).toString());
                component.setCursor(cursor);
            }
            if (component instanceof Container) {
                for (Component component2 : ((Container) component).getComponents()) {
                    setAndStoreCursors(component2, cursor);
                }
            }
        }

        private void resetAndRestoreCursors(Component component) {
            if (component == null) {
                return;
            }
            Cursor cursor = (Cursor) this.cursors.get(component);
            if (cursor != null) {
                CSH.debug(new StringBuffer().append("restored cursor ").append(cursor).append(" on ").append(component).toString());
                component.setCursor(cursor);
            }
            if (component instanceof Container) {
                for (Component component2 : ((Container) component).getComponents()) {
                    resetAndRestoreCursors(component2);
                }
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/CSH$DisplayHelpFromSource.class */
    public static class DisplayHelpFromSource implements ActionListener {
        private HelpBroker hb;
        private HelpSet hs;
        private String presentation;
        private String presentationName;

        public DisplayHelpFromSource(HelpBroker helpBroker) {
            this.hs = null;
            this.presentation = null;
            this.presentationName = null;
            if (helpBroker == null) {
                throw new NullPointerException("hb");
            }
            this.hb = helpBroker;
        }

        public DisplayHelpFromSource(HelpSet helpSet, String str, String str2) {
            this.hs = null;
            this.presentation = null;
            this.presentationName = null;
            if (helpSet == null) {
                throw new NullPointerException("hs");
            }
            try {
                ClassLoader loader = helpSet.getLoader();
                if (loader == null) {
                    Class.forName(str);
                } else {
                    loader.loadClass(str);
                }
                this.presentation = str;
                this.presentationName = str2;
                this.hs = helpSet;
            } catch (Exception e) {
                throw new IllegalArgumentException(new StringBuffer().append(str).append("presentation  invalid").toString());
            }
        }

        public void actionPerformed(ActionEvent actionEvent) {
            Object source = actionEvent.getSource();
            CSH.displayHelp(this.hb, this.hs, this.presentation, this.presentationName, source, source, actionEvent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void debug(Object obj) {
    }
}
