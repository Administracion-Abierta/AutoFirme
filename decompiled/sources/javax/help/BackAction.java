package javax.help;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Stack;
import javax.help.event.HelpHistoryModelEvent;
import javax.help.event.HelpHistoryModelListener;
import javax.help.event.HelpModelEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.UIManager;

/* JADX INFO: loaded from: autofirma.jar:javax/help/BackAction.class */
public class BackAction extends AbstractHelpAction implements MouseListener, HelpHistoryModelListener {
    private static final String NAME = "BackAction";
    private static final int DELAY = 500;
    private Timer timer;
    private HelpHistoryModel historyModel;

    public BackAction(Object obj) {
        Locale locale;
        super(obj, NAME);
        if (obj instanceof JHelp) {
            JHelp jHelp = (JHelp) obj;
            this.historyModel = jHelp.getHistoryModel();
            this.historyModel.addHelpHistoryModelListener(this);
            setEnabled(this.historyModel.getIndex() > 0);
            putValue("icon", UIManager.getIcon("BackAction.icon"));
            try {
                locale = jHelp.getModel().getHelpSet().getLocale();
            } catch (NullPointerException e) {
                locale = Locale.getDefault();
            }
            putValue("tooltip", HelpUtilities.getString(locale, "tooltip.BackAction"));
            putValue("access", HelpUtilities.getString(locale, "access.BackAction"));
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
            this.historyModel.goBack();
        }
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/BackAction$TimeListener.class */
    private class TimeListener implements ActionListener {
        private MouseEvent e;
        private final BackAction this$0;

        public TimeListener(BackAction backAction, MouseEvent mouseEvent) {
            this.this$0 = backAction;
            this.e = mouseEvent;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            this.this$0.timer.stop();
            if (this.this$0.isEnabled()) {
                this.this$0.showBackwardHistory(this.e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/BackAction$HistoryActionListener.class */
    private class HistoryActionListener implements ActionListener {
        private int index;
        private final BackAction this$0;

        public HistoryActionListener(BackAction backAction, int i) {
            this.this$0 = backAction;
            this.index = i;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            if (this.this$0.historyModel != null) {
                this.this$0.historyModel.setHistoryEntry(this.index);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showBackwardHistory(MouseEvent mouseEvent) {
        JPopupMenu jPopupMenu = new JPopupMenu("Backward History");
        if (this.historyModel == null) {
            return;
        }
        Stack stack = new Stack();
        Locale locale = ((JHelp) getControl()).getModel().getHelpSet().getLocale();
        Enumeration enumerationElements = this.historyModel.getBackwardHistory().elements();
        int i = 0;
        while (enumerationElements.hasMoreElements()) {
            HelpModelEvent helpModelEvent = (HelpModelEvent) enumerationElements.nextElement();
            if (helpModelEvent != null) {
                String historyName = helpModelEvent.getHistoryName();
                if (historyName == null) {
                    historyName = HelpUtilities.getString(locale, "history.unknownTitle");
                }
                JMenuItem jMenuItem = new JMenuItem(historyName);
                jMenuItem.addActionListener(new HistoryActionListener(this, i));
                stack.push(jMenuItem);
            }
            i++;
        }
        int size = stack.size();
        for (int i2 = 0; i2 < size; i2++) {
            jPopupMenu.add((JMenuItem) stack.pop());
        }
        jPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
    }

    @Override // javax.help.event.HelpHistoryModelListener
    public void historyChanged(HelpHistoryModelEvent helpHistoryModelEvent) {
        setEnabled(helpHistoryModelEvent.isPrevious());
    }
}
