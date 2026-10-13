package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.signers.AOSimpleSignInfo;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/LinksTreeCellRenderer.class */
final class LinksTreeCellRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 335850028229506214L;

    LinksTreeCellRenderer() {
    }

    public Component getTreeCellRendererComponent(JTree jTree, Object obj, boolean z, boolean z2, boolean z3, int i, boolean z4) {
        Component treeCellRendererComponent = super.getTreeCellRendererComponent(jTree, obj, z, z2, z3, i, z4);
        if ((((DefaultMutableTreeNode) obj).getUserObject() instanceof ShowFileLinkAction) || (((DefaultMutableTreeNode) obj).getUserObject() instanceof AOSimpleSignInfo)) {
            JLabel jLabel = new JLabel();
            jLabel.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("SignDataPanel.46") + " " + getText());
            if (z4) {
                jLabel.setText(getText());
                jLabel.setOpaque(true);
                if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                    jLabel.setForeground(Color.WHITE);
                    jLabel.setBackground(Color.decode("#39698a"));
                } else {
                    jLabel.setForeground(Color.YELLOW);
                    jLabel.setBackground(Color.BLUE);
                }
            } else {
                jLabel.setText(getText());
                if (!LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
                    jLabel.setForeground(Color.BLUE);
                } else {
                    jLabel.setForeground(Color.YELLOW);
                }
                jLabel.setOpaque(false);
            }
            Font font = jLabel.getFont();
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            jLabel.setFont(font.deriveFont(attributes));
            return jLabel;
        }
        return treeCellRendererComponent;
    }
}
