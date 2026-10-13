package javax.help;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.Point;
import java.net.URL;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:javax/help/HelpBroker.class */
public interface HelpBroker {
    void setHelpSet(HelpSet helpSet);

    HelpSet getHelpSet();

    void setHelpSetPresentation(HelpSet.Presentation presentation);

    Locale getLocale();

    void setLocale(Locale locale);

    Font getFont();

    void setFont(Font font);

    void setCurrentView(String str);

    String getCurrentView();

    void initPresentation();

    void setDisplayed(boolean z) throws UnsupportedOperationException;

    boolean isDisplayed();

    void setLocation(Point point) throws UnsupportedOperationException;

    Point getLocation() throws UnsupportedOperationException;

    void setSize(Dimension dimension) throws UnsupportedOperationException;

    Dimension getSize() throws UnsupportedOperationException;

    void setScreen(int i) throws UnsupportedOperationException;

    int getScreen() throws UnsupportedOperationException;

    void setViewDisplayed(boolean z);

    boolean isViewDisplayed();

    void showID(Map.ID id, String str, String str2) throws InvalidHelpSetContextException;

    void showID(String str, String str2, String str3) throws BadIDException;

    void setCurrentID(Map.ID id) throws InvalidHelpSetContextException;

    void setCurrentID(String str) throws BadIDException;

    Map.ID getCurrentID();

    void setCurrentURL(URL url);

    URL getCurrentURL();

    void enableHelpKey(Component component, String str, HelpSet helpSet);

    void enableHelpKey(Component component, String str, HelpSet helpSet, String str2, String str3);

    void enableHelp(Component component, String str, HelpSet helpSet);

    void enableHelp(MenuItem menuItem, String str, HelpSet helpSet);

    void enableHelpOnButton(Component component, String str, HelpSet helpSet) throws IllegalArgumentException;

    void enableHelpOnButton(MenuItem menuItem, String str, HelpSet helpSet);

    void enableHelpOnButton(Object obj, String str, HelpSet helpSet, String str2, String str3) throws IllegalArgumentException;
}
