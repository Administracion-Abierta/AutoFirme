package javax.help.plaf.basic;

import com.aowagie.text.ElementTags;
import com.sun.jna.platform.win32.WinNT;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.net.URL;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.JHelpContentViewer;
import javax.help.JHelpNavigator;
import javax.help.Map;
import javax.help.TextHelpModel;
import javax.help.event.HelpModelEvent;
import javax.help.event.HelpModelListener;
import javax.help.event.TextHelpModelEvent;
import javax.help.event.TextHelpModelListener;
import javax.help.plaf.HelpContentViewerUI;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.jdic.browser.WebBrowser;

/* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicNativeContentViewerUI.class */
public class BasicNativeContentViewerUI extends HelpContentViewerUI implements HelpModelListener, TextHelpModelListener, PropertyChangeListener, Serializable {
    protected JHelpContentViewer theViewer;
    private static Dimension PREF_SIZE = new Dimension(200, 300);
    private static Dimension MIN_SIZE = new Dimension(80, 80);
    private WebBrowser html;
    private JViewport vp;
    private static final boolean debug = false;

    public static ComponentUI createUI(JComponent jComponent) {
        debug("createUI");
        return new BasicNativeContentViewerUI((JHelpContentViewer) jComponent);
    }

    public BasicNativeContentViewerUI(JHelpContentViewer jHelpContentViewer) {
        debug("createUI - sort of");
    }

    public void installUI(JComponent jComponent) {
        URL currentURL;
        debug("installUI");
        this.theViewer = (JHelpContentViewer) jComponent;
        this.theViewer.setLayout(new BorderLayout());
        this.theViewer.addPropertyChangeListener(this);
        TextHelpModel model = this.theViewer.getModel();
        if (model != null) {
            model.addHelpModelListener(this);
            model.addTextHelpModelListener(this);
        }
        this.html = new WebBrowser();
        this.html.getAccessibleContext().setAccessibleName(HelpUtilities.getString(HelpUtilities.getLocale(this.html), "access.contentViewer"));
        if (model != null && (currentURL = model.getCurrentURL()) != null) {
            this.html.setURL(currentURL);
        }
        Component jScrollPane = new JScrollPane();
        jScrollPane.setBorder(new BevelBorder(1, Color.white, Color.gray));
        this.vp = jScrollPane.getViewport();
        this.vp.add(this.html);
        this.vp.setBackingStoreEnabled(true);
        this.theViewer.add("Center", jScrollPane);
    }

    public void uninstallUI(JComponent jComponent) {
        debug("uninstallUI");
        JHelpContentViewer jHelpContentViewer = (JHelpContentViewer) jComponent;
        jHelpContentViewer.removePropertyChangeListener(this);
        TextHelpModel model = jHelpContentViewer.getModel();
        if (model != null) {
            model.removeHelpModelListener(this);
            model.removeTextHelpModelListener(this);
        }
        jHelpContentViewer.setLayout(null);
        jHelpContentViewer.removeAll();
    }

    public Dimension getPreferredSize(JComponent jComponent) {
        return PREF_SIZE;
    }

    public Dimension getMinimumSize(JComponent jComponent) {
        return MIN_SIZE;
    }

    public Dimension getMaximumSize(JComponent jComponent) {
        return new Dimension(WinNT.MAXLONG, WinNT.MAXLONG);
    }

    @Override // javax.help.event.HelpModelListener
    public void idChanged(HelpModelEvent helpModelEvent) {
        Map.ID id = helpModelEvent.getID();
        URL url = helpModelEvent.getURL();
        TextHelpModel model = this.theViewer.getModel();
        debug(new StringBuffer().append("idChanged(").append(helpModelEvent).append(")").toString());
        debug(new StringBuffer().append("  = ").append(id).append(" ").append(url).toString());
        debug(new StringBuffer().append("  my helpModel: ").append(model).toString());
        model.setDocumentTitle(null);
        this.html.setURL(url);
        debug("done with idChanged");
    }

    private void rebuild() {
        debug("rebuild");
        TextHelpModel model = this.theViewer.getModel();
        if (model == null) {
            debug("rebuild-end: model is null");
            return;
        }
        HelpSet helpSet = model.getHelpSet();
        if (this.theViewer.getSynch()) {
            try {
                model.setCurrentID(helpSet.getHomeID(), HelpUtilities.getString(helpSet.getLocale(), "history.homePage"), (JHelpNavigator) null);
                this.html.setURL(model.getCurrentURL());
            } catch (Exception e) {
            }
        }
        debug("rebuild-end");
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        debug(new StringBuffer().append("propertyChange: ").append(propertyChangeEvent.getPropertyName()).append("\n\toldValue:").append(propertyChangeEvent.getOldValue()).append("\n\tnewValue:").append(propertyChangeEvent.getNewValue()).toString());
        if (propertyChangeEvent.getSource() == this.theViewer) {
            String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName.equals("helpModel")) {
                TextHelpModel textHelpModel = (TextHelpModel) propertyChangeEvent.getOldValue();
                TextHelpModel textHelpModel2 = (TextHelpModel) propertyChangeEvent.getNewValue();
                if (textHelpModel != null) {
                    textHelpModel.removeHelpModelListener(this);
                    textHelpModel.removeTextHelpModelListener(this);
                }
                if (textHelpModel2 != null) {
                    textHelpModel2.addHelpModelListener(this);
                    textHelpModel2.addTextHelpModelListener(this);
                }
                rebuild();
                return;
            }
            if (propertyName.equals(ElementTags.FONT)) {
                debug("font changed");
            } else if (!propertyName.equals("clear") && propertyName.equals("reload")) {
                this.html.refresh();
            }
        }
    }

    @Override // javax.help.event.TextHelpModelListener
    public void highlightsChanged(TextHelpModelEvent textHelpModelEvent) {
        debug(new StringBuffer().append("highlightsChanged ").append(textHelpModelEvent).toString());
    }

    private static void debug(String str) {
    }
}
