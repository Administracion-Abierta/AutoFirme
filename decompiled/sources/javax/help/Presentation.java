package javax.help;

import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:javax/help/Presentation.class */
public abstract class Presentation {
    private HelpSet helpset = null;
    private TextHelpModel model = null;
    private Locale locale = null;
    private Font font = null;
    private int width = 645;
    private int height = 495;
    private static final boolean debug = false;

    public abstract void setDisplayed(boolean z);

    public abstract boolean isDisplayed();

    public static Presentation getPresentation(HelpSet helpSet, String str) {
        return null;
    }

    public void setHelpSetPresentation(HelpSet.Presentation presentation) {
        Dimension size;
        debug("setHelpSetPresentation");
        if (presentation != null && (size = presentation.getSize()) != null) {
            setSize(size);
        }
    }

    public Map.ID getCurrentID() {
        debug("getCurrentID");
        if (this.model != null) {
            return this.model.getCurrentID();
        }
        return null;
    }

    public void setCurrentID(String str) throws BadIDException {
        debug("setCurrentID - String");
        try {
            setCurrentID(Map.ID.create(str, this.helpset));
        } catch (InvalidHelpSetContextException e) {
            new Error("internal error?");
        }
    }

    public void setCurrentID(Map.ID id) throws InvalidHelpSetContextException {
        debug("setCurrentID - ID");
        createHelpModel();
        this.model.setCurrentID(id);
    }

    public URL getCurrentURL() {
        debug("getCurrentURL");
        if (this.model != null) {
            return this.model.getCurrentURL();
        }
        return null;
    }

    public void setCurrentURL(URL url) {
        debug("setCurrentURL");
        createHelpModel();
        this.model.setCurrentURL(url);
    }

    public HelpSet getHelpSet() {
        debug("getHelpSet");
        return this.helpset;
    }

    public void setHelpSet(HelpSet helpSet) {
        debug("setHelpSet");
        if (helpSet != null && this.helpset != helpSet) {
            this.model = new DefaultHelpModel(helpSet);
            this.helpset = helpSet;
        }
    }

    public Font getFont() {
        debug("getFont");
        return this.font;
    }

    public void setFont(Font font) {
        debug("setFont");
        this.font = font;
    }

    public Locale getLocale() {
        debug("getLocale");
        if (this.locale == null) {
            return Locale.getDefault();
        }
        return this.locale;
    }

    public void setLocale(Locale locale) {
        debug("setLocale");
        this.locale = locale;
    }

    public Dimension getSize() {
        debug("getSize");
        return new Dimension(this.width, this.height);
    }

    public void setSize(Dimension dimension) {
        debug("setSize");
        this.width = dimension.width;
        this.height = dimension.height;
    }

    private void createHelpModel() {
        if (this.model == null) {
            this.model = new DefaultHelpModel(this.helpset);
        }
    }

    protected TextHelpModel getHelpModel() {
        if (this.model == null) {
            createHelpModel();
        }
        return this.model;
    }

    private static void debug(Object obj) {
    }
}
