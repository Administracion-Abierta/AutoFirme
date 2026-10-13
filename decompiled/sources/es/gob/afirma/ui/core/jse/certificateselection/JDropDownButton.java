package es.gob.afirma.ui.core.jse.certificateselection;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JButton;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/JDropDownButton.class */
public class JDropDownButton extends JButton {
    private static final long serialVersionUID = -3983995903253627847L;

    public JDropDownButton(Icon icon) {
        super(icon);
        addActionListener(new ActionListener() { // from class: es.gob.afirma.ui.core.jse.certificateselection.JDropDownButton.1
            public void actionPerformed(ActionEvent actionEvent) {
                if (JDropDownButton.this.getComponentPopupMenu() != null) {
                    JDropDownButton.this.getComponentPopupMenu().show(JDropDownButton.this, 0, JDropDownButton.this.getHeight());
                }
            }
        });
    }

    public Point getPopupLocation(MouseEvent mouseEvent) {
        return new Point(0, getHeight());
    }
}
