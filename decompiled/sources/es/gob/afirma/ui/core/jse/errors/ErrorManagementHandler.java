package es.gob.afirma.ui.core.jse.errors;

import es.gob.afirma.ui.core.jse.JSEUIMessages;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/errors/ErrorManagementHandler.class */
public class ErrorManagementHandler {
    private final ErrorManagementPanel view;
    private final ErrorManagementDialog dialog;

    public ErrorManagementHandler(ErrorManagementPanel errorManagementPanel, ErrorManagementDialog errorManagementDialog) {
        this.view = errorManagementPanel;
        this.dialog = errorManagementDialog;
    }

    void registerComponents() {
        if (this.view.getDetailsButton() != null) {
            this.view.getDetailsButton().addActionListener(new ActionListener() { // from class: es.gob.afirma.ui.core.jse.errors.ErrorManagementHandler.1
                public void actionPerformed(ActionEvent actionEvent) {
                    ErrorManagementHandler.this.resizeScrollpane();
                }
            });
        }
        if (this.view.getCopyErrorButton() != null) {
            this.view.getCopyErrorButton().addActionListener(new ActionListener() { // from class: es.gob.afirma.ui.core.jse.errors.ErrorManagementHandler.2
                public void actionPerformed(ActionEvent actionEvent) {
                    ErrorManagementHandler.this.copyToClipboard();
                }
            });
        }
        this.view.getCloseButton().addActionListener(new ActionListener() { // from class: es.gob.afirma.ui.core.jse.errors.ErrorManagementHandler.3
            public void actionPerformed(ActionEvent actionEvent) {
                ErrorManagementHandler.this.getView().getWindow().dispose();
            }
        });
    }

    void resizeScrollpane() {
        if (this.view.isExpandedDetails()) {
            this.view.setExpandedDetails(false);
            this.view.getScrollPane().setVisible(false);
            this.view.getDetailsButton().setText(JSEUIMessages.getString("JSEUIManager.90"));
            this.view.getCopyErrorButton().setVisible(false);
        } else {
            this.view.setExpandedDetails(true);
            this.view.getScrollPane().setVisible(true);
            this.view.getDetailsButton().setText(JSEUIMessages.getString("JSEUIManager.91"));
            this.view.getCopyErrorButton().setVisible(true);
        }
        this.dialog.resize();
    }

    void copyToClipboard() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(this.view.getErrorTextArea().getText()), (ClipboardOwner) null);
    }

    public ErrorManagementPanel getView() {
        return this.view;
    }
}
