package javax.help;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.Point;
import java.net.URL;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:javax/help/ServletHelpBroker.class */
public class ServletHelpBroker implements HelpBroker {
    protected HelpSet helpset = null;
    protected DefaultHelpModel model = null;
    protected NavigatorView curNav = null;
    protected boolean viewDisplayed = true;
    protected Locale locale = null;
    protected Font font;
    private static final boolean debug = false;

    @Override // javax.help.HelpBroker
    public HelpSet getHelpSet() {
        return this.helpset;
    }

    @Override // javax.help.HelpBroker
    public void setHelpSet(HelpSet helpSet) {
        if (helpSet != null && this.helpset != helpSet) {
            this.model = new DefaultHelpModel(helpSet);
            this.helpset = helpSet;
        }
    }

    @Override // javax.help.HelpBroker
    public Locale getLocale() {
        if (this.locale == null) {
            return Locale.getDefault();
        }
        return this.locale;
    }

    @Override // javax.help.HelpBroker
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override // javax.help.HelpBroker
    public Font getFont() {
        return this.font;
    }

    @Override // javax.help.HelpBroker
    public void setFont(Font font) {
        this.font = font;
    }

    @Override // javax.help.HelpBroker
    public void setCurrentView(String str) {
        NavigatorView[] navigatorViews = this.helpset.getNavigatorViews();
        for (int i = 0; i < navigatorViews.length; i++) {
            if (navigatorViews[i].getName().equals(str)) {
                this.curNav = navigatorViews[i];
                return;
            }
        }
        throw new IllegalArgumentException("Invalid view name");
    }

    @Override // javax.help.HelpBroker
    public String getCurrentView() {
        if (this.curNav == null) {
            if (this.helpset != null) {
                this.curNav = this.helpset.getNavigatorViews()[0];
            } else {
                return null;
            }
        }
        return this.curNav.getName();
    }

    public NavigatorView getCurrentNavigatorView() {
        if (this.curNav == null) {
            if (this.helpset != null) {
                this.curNav = this.helpset.getNavigatorViews()[0];
            } else {
                return null;
            }
        }
        return this.curNav;
    }

    @Override // javax.help.HelpBroker
    public void initPresentation() {
    }

    @Override // javax.help.HelpBroker
    public void setDisplayed(boolean z) {
    }

    @Override // javax.help.HelpBroker
    public boolean isDisplayed() {
        return true;
    }

    @Override // javax.help.HelpBroker
    public void setLocation(Point point) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    @Override // javax.help.HelpBroker
    public Point getLocation() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    @Override // javax.help.HelpBroker
    public void setSize(Dimension dimension) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    @Override // javax.help.HelpBroker
    public Dimension getSize() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    @Override // javax.help.HelpBroker
    public void setScreen(int i) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    @Override // javax.help.HelpBroker
    public int getScreen() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    @Override // javax.help.HelpBroker
    public void setViewDisplayed(boolean z) {
        this.viewDisplayed = z;
    }

    @Override // javax.help.HelpBroker
    public boolean isViewDisplayed() {
        return this.viewDisplayed;
    }

    @Override // javax.help.HelpBroker
    public void setCurrentID(String str) throws BadIDException {
        try {
            setCurrentID(Map.ID.create(str, this.helpset));
        } catch (InvalidHelpSetContextException e) {
            new Error("internal error?");
        }
    }

    @Override // javax.help.HelpBroker
    public void setCurrentID(Map.ID id) throws InvalidHelpSetContextException {
        debug("setCurrentID");
        this.model.setCurrentID(id);
    }

    @Override // javax.help.HelpBroker
    public Map.ID getCurrentID() {
        return this.model.getCurrentID();
    }

    @Override // javax.help.HelpBroker
    public void setCurrentURL(URL url) {
        this.model.setCurrentURL(url);
    }

    @Override // javax.help.HelpBroker
    public URL getCurrentURL() {
        return this.model.getCurrentURL();
    }

    @Override // javax.help.HelpBroker
    public void enableHelpKey(Component component, String str, HelpSet helpSet) {
    }

    @Override // javax.help.HelpBroker
    public void enableHelp(Component component, String str, HelpSet helpSet) {
    }

    @Override // javax.help.HelpBroker
    public void enableHelp(MenuItem menuItem, String str, HelpSet helpSet) {
    }

    @Override // javax.help.HelpBroker
    public void enableHelpOnButton(Component component, String str, HelpSet helpSet) {
    }

    @Override // javax.help.HelpBroker
    public void enableHelpOnButton(MenuItem menuItem, String str, HelpSet helpSet) {
    }

    @Override // javax.help.HelpBroker
    public void setHelpSetPresentation(HelpSet.Presentation presentation) {
    }

    @Override // javax.help.HelpBroker
    public void showID(String str, String str2, String str3) throws BadIDException {
    }

    @Override // javax.help.HelpBroker
    public void showID(Map.ID id, String str, String str2) throws InvalidHelpSetContextException {
    }

    @Override // javax.help.HelpBroker
    public void enableHelpKey(Component component, String str, HelpSet helpSet, String str2, String str3) {
    }

    @Override // javax.help.HelpBroker
    public void enableHelpOnButton(Object obj, String str, HelpSet helpSet, String str2, String str3) {
    }

    private static void debug(Object obj) {
    }
}
