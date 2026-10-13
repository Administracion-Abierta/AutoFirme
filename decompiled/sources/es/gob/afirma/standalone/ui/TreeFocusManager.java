package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.signers.AOSimpleSignInfo;
import java.awt.Cursor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/TreeFocusManager.class */
final class TreeFocusManager extends KeyAdapter implements FocusListener, MouseMotionListener, MouseListener {
    private final JTree tree;
    private final TreeFocusManagerAction focusAction;
    private TreePath selectedPath;
    private static final Cursor DEFAULT_CURSOR = new Cursor(0);
    private static final Cursor HAND_CURSOR = new Cursor(12);

    TreeFocusManager(JTree jTree, TreeFocusManagerAction treeFocusManagerAction) {
        this.tree = jTree;
        this.focusAction = treeFocusManagerAction;
        this.selectedPath = this.tree.getPathForRow(0);
    }

    public void focusGained(FocusEvent focusEvent) {
        if (this.tree.getRowCount() > 0) {
            this.tree.setSelectionPath(this.selectedPath);
            this.tree.scrollPathToVisible(this.selectedPath);
        }
    }

    public void focusLost(FocusEvent focusEvent) {
        this.selectedPath = this.tree.getSelectionPath();
        this.tree.setSelectionPath((TreePath) null);
    }

    public void mouseDragged(MouseEvent mouseEvent) {
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        TreePath pathForLocation = this.tree.getPathForLocation((int) mouseEvent.getPoint().getX(), (int) mouseEvent.getPoint().getY());
        if (pathForLocation != null) {
            if (pathForLocation.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) pathForLocation.getLastPathComponent()).getUserObject();
                if ((userObject instanceof ShowFileLinkAction) || (userObject instanceof AOSimpleSignInfo)) {
                    this.tree.setCursor(HAND_CURSOR);
                    return;
                } else {
                    this.tree.setCursor(DEFAULT_CURSOR);
                    return;
                }
            }
            this.tree.setCursor(DEFAULT_CURSOR);
            return;
        }
        this.tree.setCursor(DEFAULT_CURSOR);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        DefaultMutableTreeNode defaultMutableTreeNode;
        if (this.tree.getCursor().equals(HAND_CURSOR) && (defaultMutableTreeNode = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent()) != null && this.focusAction != null) {
            this.focusAction.openTreeNode(defaultMutableTreeNode.getUserObject());
        }
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mousePressed(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void keyPressed(KeyEvent keyEvent) {
        DefaultMutableTreeNode defaultMutableTreeNode;
        if ((32 != keyEvent.getKeyCode() && 10 != keyEvent.getKeyCode()) || this.focusAction == null || (defaultMutableTreeNode = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent()) == null) {
            return;
        }
        this.focusAction.openTreeNode(defaultMutableTreeNode.getUserObject());
    }
}
