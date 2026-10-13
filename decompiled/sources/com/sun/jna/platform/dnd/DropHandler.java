package com.sun.jna.platform.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/dnd/DropHandler.class */
public abstract class DropHandler implements DropTargetListener {
    private static final Logger LOG = Logger.getLogger(DropHandler.class.getName());
    private int acceptedActions;
    private List<DataFlavor> acceptedFlavors;
    private DropTarget dropTarget;
    private boolean active;
    private DropTargetPainter painter;
    private String lastAction;

    protected abstract void drop(DropTargetDropEvent dropTargetDropEvent, int i) throws UnsupportedFlavorException, IOException;

    public DropHandler(Component c, int acceptedActions) {
        this(c, acceptedActions, new DataFlavor[0]);
    }

    public DropHandler(Component c, int acceptedActions, DataFlavor[] acceptedFlavors) {
        this(c, acceptedActions, acceptedFlavors, null);
    }

    public DropHandler(Component c, int acceptedActions, DataFlavor[] acceptedFlavors, DropTargetPainter painter) {
        this.active = true;
        this.acceptedActions = acceptedActions;
        this.acceptedFlavors = Arrays.asList(acceptedFlavors);
        this.painter = painter;
        this.dropTarget = new DropTarget(c, acceptedActions, this, this.active);
    }

    protected DropTarget getDropTarget() {
        return this.dropTarget;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (this.dropTarget != null) {
            this.dropTarget.setActive(active);
        }
    }

    protected int getDropActionsForFlavors(DataFlavor[] dataFlavors) {
        return this.acceptedActions;
    }

    protected int getDropAction(DropTargetEvent e) {
        int currentAction = 0;
        int sourceActions = 0;
        Point location = null;
        DataFlavor[] flavors = new DataFlavor[0];
        if (e instanceof DropTargetDragEvent) {
            DropTargetDragEvent ev = (DropTargetDragEvent) e;
            currentAction = ev.getDropAction();
            sourceActions = ev.getSourceActions();
            flavors = ev.getCurrentDataFlavors();
            location = ev.getLocation();
        } else if (e instanceof DropTargetDropEvent) {
            DropTargetDropEvent ev2 = (DropTargetDropEvent) e;
            currentAction = ev2.getDropAction();
            sourceActions = ev2.getSourceActions();
            flavors = ev2.getCurrentDataFlavors();
            location = ev2.getLocation();
        }
        if (isSupported(flavors)) {
            int availableActions = getDropActionsForFlavors(flavors);
            int currentAction2 = getDropAction(e, currentAction, sourceActions, availableActions);
            if (currentAction2 != 0 && canDrop(e, currentAction2, location)) {
                return currentAction2;
            }
            return 0;
        }
        return 0;
    }

    protected int getDropAction(DropTargetEvent e, int currentAction, int sourceActions, int acceptedActions) {
        int action;
        boolean modifiersActive = modifiersActive(currentAction);
        if ((currentAction & acceptedActions) == 0 && !modifiersActive) {
            currentAction = acceptedActions & sourceActions;
        } else if (modifiersActive && (action = currentAction & acceptedActions & sourceActions) != currentAction) {
            currentAction = action;
        }
        return currentAction;
    }

    protected boolean modifiersActive(int dropAction) {
        int mods = DragHandler.getModifiers();
        if (mods != -1) {
            return mods != 0;
        }
        if (dropAction == 1073741824 || dropAction == 1) {
            return true;
        }
        return false;
    }

    private void describe(String type, DropTargetEvent e) {
        if (LOG.isLoggable(Level.FINE)) {
            StringBuilder msgBuilder = new StringBuilder();
            msgBuilder.append("drop: ");
            msgBuilder.append(type);
            if (e instanceof DropTargetDragEvent) {
                DropTargetContext dtc = e.getDropTargetContext();
                DropTarget dt = dtc.getDropTarget();
                DropTargetDragEvent ev = (DropTargetDragEvent) e;
                msgBuilder.append(": src=");
                msgBuilder.append(DragHandler.actionString(ev.getSourceActions()));
                msgBuilder.append(" tgt=");
                msgBuilder.append(DragHandler.actionString(dt.getDefaultActions()));
                msgBuilder.append(" act=");
                msgBuilder.append(DragHandler.actionString(ev.getDropAction()));
            } else if (e instanceof DropTargetDropEvent) {
                DropTargetContext dtc2 = e.getDropTargetContext();
                DropTarget dt2 = dtc2.getDropTarget();
                DropTargetDropEvent ev2 = (DropTargetDropEvent) e;
                msgBuilder.append(": src=");
                msgBuilder.append(DragHandler.actionString(ev2.getSourceActions()));
                msgBuilder.append(" tgt=");
                msgBuilder.append(DragHandler.actionString(dt2.getDefaultActions()));
                msgBuilder.append(" act=");
                msgBuilder.append(DragHandler.actionString(ev2.getDropAction()));
            }
            String msg = msgBuilder.toString();
            if (!msg.equals(this.lastAction)) {
                LOG.log(Level.FINE, msg);
                this.lastAction = msg;
            }
        }
    }

    protected int acceptOrReject(DropTargetDragEvent e) {
        int action = getDropAction(e);
        if (action != 0) {
            e.acceptDrag(action);
        } else {
            e.rejectDrag();
        }
        return action;
    }

    public void dragEnter(DropTargetDragEvent e) {
        describe("enter(tgt)", e);
        int action = acceptOrReject(e);
        paintDropTarget(e, action, e.getLocation());
    }

    public void dragOver(DropTargetDragEvent e) {
        describe("over(tgt)", e);
        int action = acceptOrReject(e);
        paintDropTarget(e, action, e.getLocation());
    }

    public void dragExit(DropTargetEvent e) {
        describe("exit(tgt)", e);
        paintDropTarget(e, 0, null);
    }

    public void dropActionChanged(DropTargetDragEvent e) {
        describe("change(tgt)", e);
        int action = acceptOrReject(e);
        paintDropTarget(e, action, e.getLocation());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.awt.datatransfer.UnsupportedFlavorException */
    public void drop(DropTargetDropEvent e) throws UnsupportedFlavorException {
        describe("drop(tgt)", e);
        int action = getDropAction(e);
        if (action != 0) {
            e.acceptDrop(action);
            try {
                drop(e, action);
                e.dropComplete(true);
            } catch (Exception e2) {
                e.dropComplete(false);
            }
        } else {
            e.rejectDrop();
        }
        paintDropTarget(e, 0, e.getLocation());
    }

    protected boolean isSupported(DataFlavor[] flavors) {
        Set<DataFlavor> set = new HashSet<>(Arrays.asList(flavors));
        set.retainAll(this.acceptedFlavors);
        return !set.isEmpty();
    }

    protected void paintDropTarget(DropTargetEvent e, int action, Point location) {
        if (this.painter != null) {
            this.painter.paintDropTarget(e, action, location);
        }
    }

    protected boolean canDrop(DropTargetEvent e, int action, Point location) {
        return true;
    }
}
