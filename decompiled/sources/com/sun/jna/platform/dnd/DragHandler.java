package com.sun.jna.platform.dnd;

import com.sun.jna.Platform;
import es.gob.afirma.core.signers.AOSignConstants;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/dnd/DragHandler.class */
public abstract class DragHandler implements DragSourceListener, DragSourceMotionListener, DragGestureListener {
    public static final float DEFAULT_GHOST_ALPHA = 0.5f;
    public static final int UNKNOWN_MODIFIERS = -1;
    protected static final int MOVE = 2;
    protected static final int COPY = 1;
    protected static final int LINK = 1073741824;
    protected static final int NONE = 0;
    static final int MOVE_MASK = 64;
    static final int COPY_MASK;
    static final int LINK_MASK;
    static final int KEY_MASK = 9152;
    private static int modifiers;
    private static Transferable transferable;
    private int supportedActions;
    private boolean fixCursor = true;
    private Component dragSource;
    private GhostedDragImage ghost;
    private Point imageOffset;
    private Dimension maxGhostSize;
    private float ghostAlpha;
    private String lastAction;
    private boolean moved;
    private static final Logger LOG = Logger.getLogger(DragHandler.class.getName());
    public static final Dimension MAX_GHOST_SIZE = new Dimension(250, 250);
    public static final Transferable UNKNOWN_TRANSFERABLE = null;
    static final boolean OSX = Platform.isMac();

    protected abstract Transferable getTransferable(DragGestureEvent dragGestureEvent);

    static {
        COPY_MASK = OSX ? 512 : 128;
        LINK_MASK = OSX ? 768 : 192;
        modifiers = -1;
        transferable = UNKNOWN_TRANSFERABLE;
    }

    static int getModifiers() {
        return modifiers;
    }

    public static Transferable getTransferable(DropTargetEvent e) {
        if (e instanceof DropTargetDragEvent) {
            try {
                return ((DropTargetDragEvent) e).getTransferable();
            } catch (Exception e2) {
            }
        } else if (e instanceof DropTargetDropEvent) {
            return ((DropTargetDropEvent) e).getTransferable();
        }
        return transferable;
    }

    protected DragHandler(Component dragSource, int actions) {
        this.maxGhostSize = MAX_GHOST_SIZE;
        this.ghostAlpha = 0.5f;
        this.dragSource = dragSource;
        this.supportedActions = actions;
        try {
            String alpha = System.getProperty("DragHandler.alpha");
            if (alpha != null) {
                try {
                    this.ghostAlpha = Float.parseFloat(alpha);
                } catch (NumberFormatException e) {
                }
            }
            String max = System.getProperty("DragHandler.maxDragImageSize");
            if (max != null) {
                String[] size = max.split("x");
                if (size.length == 2) {
                    try {
                        this.maxGhostSize = new Dimension(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
                    } catch (NumberFormatException e2) {
                    }
                }
            }
        } catch (SecurityException e3) {
        }
        disableSwingDragSupport(dragSource);
        DragSource src = DragSource.getDefaultDragSource();
        src.createDefaultDragGestureRecognizer(dragSource, this.supportedActions, this);
    }

    private void disableSwingDragSupport(Component comp) {
        if (comp instanceof JTree) {
            ((JTree) comp).setDragEnabled(false);
            return;
        }
        if (comp instanceof JList) {
            ((JList) comp).setDragEnabled(false);
            return;
        }
        if (comp instanceof JTable) {
            ((JTable) comp).setDragEnabled(false);
            return;
        }
        if (comp instanceof JTextComponent) {
            ((JTextComponent) comp).setDragEnabled(false);
        } else if (comp instanceof JColorChooser) {
            ((JColorChooser) comp).setDragEnabled(false);
        } else if (comp instanceof JFileChooser) {
            ((JFileChooser) comp).setDragEnabled(false);
        }
    }

    protected boolean canDrag(DragGestureEvent e) {
        int mods = e.getTriggerEvent().getModifiersEx() & KEY_MASK;
        if (mods == 64) {
            return (this.supportedActions & 2) != 0;
        }
        if (mods == COPY_MASK) {
            return (this.supportedActions & 1) != 0;
        }
        return (mods == LINK_MASK && (this.supportedActions & 1073741824) == 0) ? false : true;
    }

    protected void setModifiers(int mods) {
        modifiers = mods;
    }

    protected Icon getDragIcon(DragGestureEvent e, Point srcOffset) {
        return null;
    }

    protected void dragStarted(DragGestureEvent e) {
    }

    public void dragGestureRecognized(DragGestureEvent e) {
        if ((e.getDragAction() & this.supportedActions) != 0 && canDrag(e)) {
            setModifiers(e.getTriggerEvent().getModifiersEx() & KEY_MASK);
            Transferable transferable2 = getTransferable(e);
            if (transferable2 == null) {
                return;
            }
            try {
                Point srcOffset = new Point(0, 0);
                Icon icon = getDragIcon(e, srcOffset);
                Point origin = e.getDragOrigin();
                this.imageOffset = new Point(srcOffset.x - origin.x, srcOffset.y - origin.y);
                Icon dragIcon = scaleDragIcon(icon, this.imageOffset);
                if (dragIcon != null && DragSource.isDragImageSupported()) {
                    GraphicsConfiguration gc = e.getComponent().getGraphicsConfiguration();
                    e.startDrag((Cursor) null, createDragImage(gc, dragIcon), this.imageOffset, transferable2, this);
                } else {
                    if (dragIcon != null) {
                        Point screen = this.dragSource.getLocationOnScreen();
                        screen.translate(origin.x, origin.y);
                        Point cursorOffset = new Point(-this.imageOffset.x, -this.imageOffset.y);
                        this.ghost = new GhostedDragImage(this.dragSource, dragIcon, getImageLocation(screen), cursorOffset);
                        this.ghost.setAlpha(this.ghostAlpha);
                    }
                    e.startDrag((Cursor) null, transferable2, this);
                }
                dragStarted(e);
                this.moved = false;
                e.getDragSource().addDragSourceMotionListener(this);
                transferable = transferable2;
            } catch (InvalidDnDOperationException e2) {
                if (this.ghost != null) {
                    this.ghost.dispose();
                    this.ghost = null;
                }
            }
        }
    }

    protected Icon scaleDragIcon(Icon icon, Point imageOffset) {
        return icon;
    }

    protected Image createDragImage(GraphicsConfiguration gc, Icon icon) {
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        BufferedImage image = gc.createCompatibleImage(w, h, 3);
        Graphics2D g = image.getGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, w, h);
        g.setComposite(AlphaComposite.getInstance(2, this.ghostAlpha));
        icon.paintIcon(this.dragSource, g, 0, 0);
        g.dispose();
        return image;
    }

    private int reduce(int actions) {
        if ((actions & 2) != 0 && actions != 2) {
            return 2;
        }
        if ((actions & 1) != 0 && actions != 1) {
            return 1;
        }
        return actions;
    }

    protected Cursor getCursorForAction(int actualAction) {
        switch (actualAction) {
            case 1:
                return DragSource.DefaultCopyDrop;
            case 2:
                return DragSource.DefaultMoveDrop;
            case 1073741824:
                return DragSource.DefaultLinkDrop;
            default:
                return DragSource.DefaultMoveNoDrop;
        }
    }

    protected int getAcceptableDropAction(int targetActions) {
        return reduce(this.supportedActions & targetActions);
    }

    protected int getDropAction(DragSourceEvent ev) {
        if (ev instanceof DragSourceDragEvent) {
            DragSourceDragEvent e = (DragSourceDragEvent) ev;
            return e.getDropAction();
        }
        if (ev instanceof DragSourceDropEvent) {
            return ((DragSourceDropEvent) ev).getDropAction();
        }
        return 0;
    }

    protected int adjustDropAction(DragSourceEvent ev) {
        int action = getDropAction(ev);
        if (ev instanceof DragSourceDragEvent) {
            DragSourceDragEvent e = (DragSourceDragEvent) ev;
            if (action == 0) {
                int mods = e.getGestureModifiersEx() & KEY_MASK;
                if (mods == 0) {
                    action = getAcceptableDropAction(e.getTargetActions());
                }
            }
        }
        return action;
    }

    protected void updateCursor(DragSourceEvent ev) {
        if (!this.fixCursor) {
            return;
        }
        Cursor cursor = getCursorForAction(adjustDropAction(ev));
        ev.getDragSourceContext().setCursor(cursor);
    }

    static String actionString(int action) {
        switch (action) {
            case 1:
                return "COPY";
            case 2:
                return "MOVE";
            case 3:
                return "MOVE|COPY";
            case 1073741824:
                return "LINK";
            case 1073741825:
                return "COPY|LINK";
            case 1073741826:
                return "MOVE|LINK";
            case 1073741827:
                return "MOVE|COPY|LINK";
            default:
                return AOSignConstants.SIGN_FORMAT_PKCS1;
        }
    }

    private void describe(String type, DragSourceEvent e) {
        if (LOG.isLoggable(Level.FINE)) {
            StringBuilder msgBuilder = new StringBuilder();
            msgBuilder.append("drag: ");
            msgBuilder.append(type);
            DragSourceContext ds = e.getDragSourceContext();
            if (e instanceof DragSourceDragEvent) {
                DragSourceDragEvent ev = (DragSourceDragEvent) e;
                msgBuilder.append(": src=");
                msgBuilder.append(actionString(ds.getSourceActions()));
                msgBuilder.append(" usr=");
                msgBuilder.append(actionString(ev.getUserAction()));
                msgBuilder.append(" tgt=");
                msgBuilder.append(actionString(ev.getTargetActions()));
                msgBuilder.append(" act=");
                msgBuilder.append(actionString(ev.getDropAction()));
                msgBuilder.append(" mods=");
                msgBuilder.append(ev.getGestureModifiersEx());
            } else {
                msgBuilder.append(": e=");
                msgBuilder.append(e);
            }
            String msg = msgBuilder.toString();
            if (!msg.equals(this.lastAction)) {
                LOG.log(Level.FINE, msg);
                this.lastAction = msg;
            }
        }
    }

    public void dragDropEnd(DragSourceDropEvent e) {
        describe("end", e);
        setModifiers(-1);
        transferable = UNKNOWN_TRANSFERABLE;
        if (this.ghost != null) {
            if (e.getDropSuccess()) {
                this.ghost.dispose();
            } else {
                this.ghost.returnToOrigin();
            }
            this.ghost = null;
        }
        DragSource src = e.getDragSourceContext().getDragSource();
        src.removeDragSourceMotionListener(this);
        this.moved = false;
    }

    private Point getImageLocation(Point where) {
        where.translate(this.imageOffset.x, this.imageOffset.y);
        return where;
    }

    public void dragEnter(DragSourceDragEvent e) {
        describe("enter", e);
        if (this.ghost != null) {
            this.ghost.move(getImageLocation(e.getLocation()));
        }
        updateCursor(e);
    }

    public void dragMouseMoved(DragSourceDragEvent e) {
        describe("move", e);
        if (this.ghost != null) {
            this.ghost.move(getImageLocation(e.getLocation()));
        }
        if (this.moved) {
            updateCursor(e);
        }
        this.moved = true;
    }

    public void dragOver(DragSourceDragEvent e) {
        describe("over", e);
        if (this.ghost != null) {
            this.ghost.move(getImageLocation(e.getLocation()));
        }
        updateCursor(e);
    }

    public void dragExit(DragSourceEvent e) {
        describe("exit", e);
    }

    public void dropActionChanged(DragSourceDragEvent e) {
        describe("change", e);
        setModifiers(e.getGestureModifiersEx() & KEY_MASK);
        if (this.ghost != null) {
            this.ghost.move(getImageLocation(e.getLocation()));
        }
        updateCursor(e);
    }
}
