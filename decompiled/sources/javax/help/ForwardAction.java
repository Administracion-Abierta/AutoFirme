package javax.help;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Locale;
import javax.help.event.HelpHistoryModelEvent;
import javax.help.event.HelpHistoryModelListener;
import javax.help.event.HelpModelEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.UIManager;

/* JADX INFO: loaded from: autofirma.jar:javax/help/ForwardAction.class */
public class ForwardAction extends AbstractHelpAction implements MouseListener, HelpHistoryModelListener {
    private static final String NAME = "ForwardAction";
    private static final int DELAY = 500;
    private Timer timer;
    private HelpHistoryModel historyModel;

    public ForwardAction(Object obj) {
        Locale locale;
        super(obj, NAME);
        if (obj instanceof JHelp) {
            JHelp jHelp = (JHelp) obj;
            this.historyModel = jHelp.getHistoryModel();
            this.historyModel.addHelpHistoryModelListener(this);
            setEnabled(this.historyModel.getIndex() > 0);
            putValue("icon", UIManager.getIcon("ForwardAction.icon"));
            try {
                locale = jHelp.getModel().getHelpSet().getLocale();
            } catch (NullPointerException e) {
                locale = Locale.getDefault();
            }
            putValue("tooltip", HelpUtilities.getString(locale, "tooltip.ForwardAction"));
            putValue("access", HelpUtilities.getString(locale, "access.ForwardAction"));
        }
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        if (this.timer != null) {
            this.timer.stop();
        }
    }

    public void mousePressed(MouseEvent mouseEvent) {
        this.timer = new Timer(500, new TimeListener(this, mouseEvent));
        this.timer.start();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if (this.historyModel != null && isEnabled()) {
            this.historyModel.goForward();
        }
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/ForwardAction$TimeListener.class */
    private class TimeListener implements ActionListener {
        private MouseEvent e;
        private final ForwardAction this$0;

        public TimeListener(ForwardAction forwardAction, MouseEvent mouseEvent) {
            this.this$0 = forwardAction;
            this.e = mouseEvent;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            this.this$0.timer.stop();
            if (this.this$0.isEnabled()) {
                this.this$0.showForwardHistory(this.e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/ForwardAction$HistoryActionListener.class */
    private class HistoryActionListener implements ActionListener {
        private int index;
        private final ForwardAction this$0;

        public HistoryActionListener(ForwardAction forwardAction, int i) {
            this.this$0 = forwardAction;
            this.index = i;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            if (this.this$0.historyModel != null) {
                this.this$0.historyModel.setHistoryEntry(this.index);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showForwardHistory(MouseEvent mouseEvent) {
        JPopupMenu jPopupMenu = new JPopupMenu("Forward History");
        if (this.historyModel == null) {
            return;
        }
        Locale locale = ((JHelp) getControl()).getModel().getHelpSet().getLocale();
        Enumeration enumerationElements = this.historyModel.getForwardHistory().elements();
        int index = this.historyModel.getIndex() + 1;
        int i = 0;
        while (enumerationElements.hasMoreElements()) {
            HelpModelEvent helpModelEvent = (HelpModelEvent) enumerationElements.nextElement();
            if (helpModelEvent != null) {
                String historyName = helpModelEvent.getHistoryName();
                if (historyName == null) {
                    historyName = HelpUtilities.getString(locale, "history.unknownTitle");
                }
                JMenuItem jMenuItem = new JMenuItem(historyName);
                jMenuItem.addActionListener(new HistoryActionListener(this, i + index));
                jPopupMenu.add(jMenuItem);
            }
            i++;
        }
        jPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
    }

    @Override // javax.help.event.HelpHistoryModelListener
    public void historyChanged(HelpHistoryModelEvent helpHistoryModelEvent) {
        setEnabled(helpHistoryModelEvent.isNext());
    }
}
