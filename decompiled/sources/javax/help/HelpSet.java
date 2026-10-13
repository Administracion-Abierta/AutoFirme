package javax.help;

import com.aowagie.text.ElementTags;
import com.ctc.wstx.cfg.XmlConsts;
import com.sun.java.help.impl.LangElement;
import com.sun.java.help.impl.Parser;
import com.sun.java.help.impl.ParserEvent;
import com.sun.java.help.impl.ParserListener;
import com.sun.java.help.impl.Tag;
import com.sun.java.help.impl.TagProperties;
import com.sun.java.help.impl.XmlReader;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;
import javax.help.event.EventListenerList;
import javax.help.event.HelpSetEvent;
import javax.help.event.HelpSetListener;
import javax.swing.ImageIcon;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:javax/help/HelpSet.class */
public class HelpSet implements Serializable {
    protected EventListenerList listenerList;
    public static final String publicIDString = "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN";
    public static final String publicIDString_V2 = "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN";
    public static final String helpBrokerClass = "helpBroker/class";
    public static final String helpBrokerLoader = "helpBroker/loader";
    private String title;
    private Map map;
    private TryMap combinedMap;
    private URL helpset;
    private String homeID;
    private Locale locale;
    private transient ClassLoader loader;
    private Vector views;
    private Vector presentations;
    private Presentation defaultPresentation;
    private Vector helpsets;
    private static HelpBroker defaultHelpBroker;
    private Vector subHelpSets;
    private static Hashtable defaultKeys;
    private Hashtable localKeys;
    private PropertyChangeSupport changes;
    private static final boolean debug = false;
    static Class class$javax$help$event$HelpSetListener;
    static Class class$java$lang$Object;
    static Class class$javax$help$HelpSet;
    private static String errorMsg = null;
    public static final Object implRegistry = new StringBuffer("HelpSet.implRegistry");
    public static final Object kitTypeRegistry = new StringBuffer("JHelpViewer.kitTypeRegistry");
    public static final Object kitLoaderRegistry = new StringBuffer("JHelpViewer.kitLoaderRegistry");

    static {
        Class clsClass$;
        setDefaultKeyData(implRegistry, helpBrokerClass, "javax.help.DefaultHelpBroker");
        setDefaultKeyData(kitTypeRegistry, "text/html", "com.sun.java.help.impl.CustomKit");
        if (class$javax$help$HelpSet == null) {
            clsClass$ = class$("javax.help.HelpSet");
            class$javax$help$HelpSet = clsClass$;
        } else {
            clsClass$ = class$javax$help$HelpSet;
        }
        ClassLoader classLoader = clsClass$.getClassLoader();
        if (classLoader != null) {
            setDefaultKeyData(implRegistry, helpBrokerLoader, classLoader);
            setDefaultKeyData(kitLoaderRegistry, "text/html", classLoader);
        }
        defaultHelpBroker = null;
    }

    public HelpSet(ClassLoader classLoader) {
        this.listenerList = new EventListenerList();
        this.locale = Locale.getDefault();
        this.views = new Vector();
        this.presentations = new Vector();
        this.defaultPresentation = null;
        this.subHelpSets = new Vector();
        this.localKeys = new Hashtable();
        this.changes = new PropertyChangeSupport(this);
        this.helpsets = new Vector();
        this.loader = classLoader;
    }

    public HelpSet() {
        this.listenerList = new EventListenerList();
        this.locale = Locale.getDefault();
        this.views = new Vector();
        this.presentations = new Vector();
        this.defaultPresentation = null;
        this.subHelpSets = new Vector();
        this.localKeys = new Hashtable();
        this.changes = new PropertyChangeSupport(this);
        this.helpsets = new Vector();
        this.loader = null;
    }

    public HelpSet(ClassLoader classLoader, URL url) throws HelpSetException {
        this(classLoader);
        this.helpset = url;
        DefaultHelpSetFactory defaultHelpSetFactory = new DefaultHelpSetFactory();
        parseInto(url, defaultHelpSetFactory);
        if (defaultHelpSetFactory.parsingEnded(this) == null) {
            throw new HelpSetException(new StringBuffer().append("Could not parse\n").append(errorMsg).toString());
        }
    }

    public static URL findHelpSet(ClassLoader classLoader, String str, String str2, Locale locale) {
        return HelpUtilities.getLocalizedResource(classLoader, str, str2, locale, true);
    }

    public static URL findHelpSet(ClassLoader classLoader, String str, Locale locale) {
        String strSubstring;
        String str2;
        if (str.endsWith(".hs")) {
            strSubstring = str.substring(0, str.length() - 3);
            str2 = ".hs";
        } else {
            strSubstring = str;
            str2 = ".hs";
        }
        return findHelpSet(classLoader, strSubstring, str2, locale);
    }

    public static URL findHelpSet(ClassLoader classLoader, String str) {
        return findHelpSet(classLoader, str, Locale.getDefault());
    }

    public HelpBroker createHelpBroker() {
        return createHelpBroker(null);
    }

    public HelpBroker createHelpBroker(String str) {
        HelpBroker helpBroker;
        Presentation defaultPresentation;
        Class<?> cls;
        String str2 = (String) getKeyData(implRegistry, helpBrokerClass);
        ClassLoader loader = (ClassLoader) getKeyData(implRegistry, helpBrokerLoader);
        if (loader == null) {
            loader = getLoader();
        }
        try {
            if (loader != null) {
                cls = loader.loadClass(str2);
            } else {
                cls = Class.forName(str2);
            }
            helpBroker = (HelpBroker) cls.newInstance();
        } catch (Throwable th) {
            helpBroker = null;
        }
        if (helpBroker != null) {
            helpBroker.setHelpSet(this);
            if (str != null) {
                defaultPresentation = getPresentation(str);
            } else {
                defaultPresentation = getDefaultPresentation();
            }
            if (defaultPresentation != null) {
                helpBroker.setHelpSetPresentation(defaultPresentation);
            }
        }
        return helpBroker;
    }

    public void add(HelpSet helpSet) {
        debug(new StringBuffer().append("add(").append(helpSet).append(")").toString());
        this.helpsets.addElement(helpSet);
        fireHelpSetAdded(this, helpSet);
        this.combinedMap = null;
    }

    public boolean remove(HelpSet helpSet) {
        if (this.helpsets.removeElement(helpSet)) {
            fireHelpSetRemoved(this, helpSet);
            this.combinedMap = null;
            return true;
        }
        return false;
    }

    public Enumeration getHelpSets() {
        return this.helpsets.elements();
    }

    public boolean contains(HelpSet helpSet) {
        if (helpSet == this) {
            return true;
        }
        Enumeration enumerationElements = this.helpsets.elements();
        while (enumerationElements.hasMoreElements()) {
            if (((HelpSet) enumerationElements.nextElement()).contains(helpSet)) {
                return true;
            }
        }
        return false;
    }

    public void addHelpSetListener(HelpSetListener helpSetListener) {
        Class clsClass$;
        debug(new StringBuffer().append("addHelpSetListener(").append(helpSetListener).append(")").toString());
        EventListenerList eventListenerList = this.listenerList;
        if (class$javax$help$event$HelpSetListener == null) {
            clsClass$ = class$("javax.help.event.HelpSetListener");
            class$javax$help$event$HelpSetListener = clsClass$;
        } else {
            clsClass$ = class$javax$help$event$HelpSetListener;
        }
        eventListenerList.add(clsClass$, helpSetListener);
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public void removeHelpSetListener(HelpSetListener helpSetListener) {
        Class clsClass$;
        EventListenerList eventListenerList = this.listenerList;
        if (class$javax$help$event$HelpSetListener == null) {
            clsClass$ = class$("javax.help.event.HelpSetListener");
            class$javax$help$event$HelpSetListener = clsClass$;
        } else {
            clsClass$ = class$javax$help$event$HelpSetListener;
        }
        eventListenerList.remove(clsClass$, helpSetListener);
    }

    protected void fireHelpSetAdded(Object obj, HelpSet helpSet) {
        Class clsClass$;
        Object[] listenerList = this.listenerList.getListenerList();
        HelpSetEvent helpSetEvent = null;
        for (int length = listenerList.length - 2; length >= 0; length -= 2) {
            Object obj2 = listenerList[length];
            if (class$javax$help$event$HelpSetListener == null) {
                clsClass$ = class$("javax.help.event.HelpSetListener");
                class$javax$help$event$HelpSetListener = clsClass$;
            } else {
                clsClass$ = class$javax$help$event$HelpSetListener;
            }
            if (obj2 == clsClass$) {
                if (helpSetEvent == null) {
                    helpSetEvent = new HelpSetEvent(this, helpSet, 0);
                }
                ((HelpSetListener) listenerList[length + 1]).helpSetAdded(helpSetEvent);
            }
        }
    }

    protected void fireHelpSetRemoved(Object obj, HelpSet helpSet) {
        Class clsClass$;
        Object[] listenerList = this.listenerList.getListenerList();
        HelpSetEvent helpSetEvent = null;
        for (int length = listenerList.length - 2; length >= 0; length -= 2) {
            Object obj2 = listenerList[length];
            if (class$javax$help$event$HelpSetListener == null) {
                clsClass$ = class$("javax.help.event.HelpSetListener");
                class$javax$help$event$HelpSetListener = clsClass$;
            } else {
                clsClass$ = class$javax$help$event$HelpSetListener;
            }
            if (obj2 == clsClass$) {
                if (helpSetEvent == null) {
                    helpSetEvent = new HelpSetEvent(this, helpSet, 1);
                }
                ((HelpSetListener) listenerList[length + 1]).helpSetRemoved(helpSetEvent);
            }
        }
    }

    public String getTitle() {
        if (this.title == null) {
            return "";
        }
        return this.title;
    }

    public void setTitle(String str) {
        String str2 = this.title;
        this.title = str;
        this.changes.firePropertyChange("title", str2, str);
    }

    public Locale getLocale() {
        return this.locale;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLocale(Locale locale) {
        Locale locale2 = this.locale;
        this.locale = locale;
        this.changes.firePropertyChange("locale", locale2, this.locale);
    }

    public Map.ID getHomeID() {
        if (this.homeID == null) {
            return null;
        }
        try {
            return Map.ID.create(this.homeID, this);
        } catch (Exception e) {
            return null;
        }
    }

    public void setHomeID(String str) {
        this.homeID = str;
        this.changes.firePropertyChange("homeID", str, str);
    }

    public Map getCombinedMap() {
        if (this.combinedMap == null) {
            this.combinedMap = new TryMap();
            if (this.map != null) {
                this.combinedMap.add(this.map);
            }
            Enumeration enumerationElements = this.helpsets.elements();
            while (enumerationElements.hasMoreElements()) {
                this.combinedMap.add(((HelpSet) enumerationElements.nextElement()).getCombinedMap());
            }
        }
        return this.combinedMap;
    }

    public Map getLocalMap() {
        return this.map;
    }

    public void setLocalMap(Map map) {
        this.map = map;
    }

    public URL getHelpSetURL() {
        return this.helpset;
    }

    public ClassLoader getLoader() {
        return this.loader;
    }

    public NavigatorView[] getNavigatorViews() {
        NavigatorView[] navigatorViewArr = new NavigatorView[this.views.size()];
        this.views.copyInto(navigatorViewArr);
        return navigatorViewArr;
    }

    public NavigatorView getNavigatorView(String str) {
        debug(new StringBuffer().append("getNavigatorView(").append(str).append(")").toString());
        for (int i = 0; i < this.views.size(); i++) {
            NavigatorView navigatorView = (NavigatorView) this.views.elementAt(i);
            if (navigatorView.getName().equals(str)) {
                debug(new StringBuffer().append("  = ").append(navigatorView).toString());
                return navigatorView;
            }
        }
        debug("  = null");
        return null;
    }

    public Presentation[] getPresentations() {
        Presentation[] presentationArr = new Presentation[this.presentations.size()];
        this.presentations.copyInto(presentationArr);
        return presentationArr;
    }

    public Presentation getPresentation(String str) {
        debug(new StringBuffer().append("getPresentation(").append(str).append(")").toString());
        for (int i = 0; i < this.presentations.size(); i++) {
            Presentation presentation = (Presentation) this.presentations.elementAt(i);
            if (presentation.getName().equals(str)) {
                debug(new StringBuffer().append("  = ").append(presentation).toString());
                return presentation;
            }
        }
        debug("  = null");
        return null;
    }

    public Presentation getDefaultPresentation() {
        return this.defaultPresentation;
    }

    public String toString() {
        return getTitle();
    }

    public static HelpSet parse(URL url, ClassLoader classLoader, HelpSetFactory helpSetFactory) {
        HelpSet helpSet = new HelpSet(classLoader);
        helpSet.helpset = url;
        helpSet.parseInto(url, helpSetFactory);
        return helpSetFactory.parsingEnded(helpSet);
    }

    public void parseInto(URL url, HelpSetFactory helpSetFactory) {
        try {
            Reader readerCreateReader = XmlReader.createReader(url.openConnection());
            helpSetFactory.parsingStarted(url);
            new HelpSetParser(helpSetFactory).parseInto(readerCreateReader, this);
            readerCreateReader.close();
        } catch (Exception e) {
            helpSetFactory.reportMessage(new StringBuffer().append("Got an IOException (").append(e.getMessage()).append(")").toString(), false);
        }
        for (int i = 0; i < this.subHelpSets.size(); i++) {
            add((HelpSet) this.subHelpSets.elementAt(i));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/HelpSet$DefaultHelpSetFactory.class */
    public static class DefaultHelpSetFactory implements HelpSetFactory {
        private URL source;
        private Vector messages = new Vector();
        private boolean validParse = true;

        @Override // javax.help.HelpSetFactory
        public void parsingStarted(URL url) {
            if (url == null) {
                throw new NullPointerException("source");
            }
            this.source = url;
        }

        @Override // javax.help.HelpSetFactory
        public void processDOCTYPE(String str, String str2, String str3) {
            if (str2 == null || (str2.compareTo(HelpSet.publicIDString) != 0 && str2.compareTo(HelpSet.publicIDString_V2) != 0)) {
                parsingError("helpset.wrongPublicID", str2);
            }
        }

        @Override // javax.help.HelpSetFactory
        public void processPI(HelpSet helpSet, String str, String str2) {
        }

        @Override // javax.help.HelpSetFactory
        public void processTitle(HelpSet helpSet, String str) {
            String title = helpSet.getTitle();
            if (title != null && !title.equals("")) {
                parsingWarning("helpset.wrongTitle", str, title);
            }
            helpSet.setTitle(str);
        }

        @Override // javax.help.HelpSetFactory
        public void processHomeID(HelpSet helpSet, String str) {
            Map.ID homeID = helpSet.getHomeID();
            if (homeID == null || homeID.equals("")) {
                helpSet.setHomeID(str);
            } else {
                parsingError("helpset.wrongHomeID", str, homeID.id);
            }
        }

        @Override // javax.help.HelpSetFactory
        public void processMapRef(HelpSet helpSet, Hashtable hashtable) {
            String str = (String) hashtable.get("location");
            try {
                Map flatMap = new FlatMap(new URL(helpSet.getHelpSetURL(), str), helpSet);
                Map localMap = helpSet.getLocalMap();
                if (localMap == null) {
                    HelpSet.debug("map is null");
                    helpSet.setLocalMap(flatMap);
                } else if (localMap instanceof TryMap) {
                    HelpSet.debug("map is TryMap");
                    ((TryMap) localMap).add(flatMap);
                    helpSet.setLocalMap(localMap);
                } else {
                    HelpSet.debug("map is not TryMap");
                    TryMap tryMap = new TryMap();
                    tryMap.add(localMap);
                    tryMap.add(flatMap);
                    helpSet.setLocalMap(tryMap);
                }
            } catch (MalformedURLException e) {
                parsingError("helpset.malformedURL", str);
            } catch (IOException e2) {
                parsingError("helpset.incorrectURL", str);
            } catch (Exception e3) {
            }
        }

        @Override // javax.help.HelpSetFactory
        public void processView(HelpSet helpSet, String str, String str2, String str3, Hashtable hashtable, String str4, Hashtable hashtable2, Locale locale) {
            if (str4 != null) {
                if (hashtable2 == null) {
                    try {
                        hashtable2 = new Hashtable();
                    } catch (Exception e) {
                        return;
                    }
                }
                hashtable2.put("data", str4);
            }
            NavigatorView navigatorViewCreate = NavigatorView.create(helpSet, str, str2, locale, str3, hashtable2);
            if (navigatorViewCreate != null) {
                helpSet.addView(navigatorViewCreate);
            }
        }

        @Override // javax.help.HelpSetFactory
        public void processPresentation(HelpSet helpSet, String str, boolean z, boolean z2, boolean z3, Dimension dimension, Point point, String str2, String str3, boolean z4, Vector vector) {
            Map.ID idCreate = null;
            try {
                idCreate = Map.ID.create(str3, helpSet);
            } catch (BadIDException e) {
            }
            try {
                Presentation presentation = new Presentation(str, z2, z3, dimension, point, str2, idCreate, z4, vector);
                if (presentation != null) {
                    helpSet.addPresentation(presentation, z);
                }
            } catch (Exception e2) {
            }
        }

        @Override // javax.help.HelpSetFactory
        public void processSubHelpSet(HelpSet helpSet, Hashtable hashtable) {
            HelpSet helpSet2;
            HelpSet.debug("createSubHelpSet");
            String str = (String) hashtable.get("location");
            URL helpSetURL = helpSet.getHelpSetURL();
            HelpSet.debug(new StringBuffer().append("  location: ").append(str).toString());
            HelpSet.debug(new StringBuffer().append("  base helpset: ").append(helpSetURL).toString());
            try {
                URL url = new URL(helpSetURL, str);
                if (url.openStream() != null && (helpSet2 = new HelpSet(helpSet.getLoader(), url)) != null) {
                    helpSet.addSubHelpSet(helpSet2);
                }
            } catch (MalformedURLException e) {
            } catch (IOException e2) {
            } catch (HelpSetException e3) {
                parsingError("helpset.subHelpSetTrouble", str);
            }
        }

        @Override // javax.help.HelpSetFactory
        public void reportMessage(String str, boolean z) {
            this.messages.addElement(str);
            this.validParse = this.validParse && z;
        }

        @Override // javax.help.HelpSetFactory
        public Enumeration listMessages() {
            return this.messages.elements();
        }

        @Override // javax.help.HelpSetFactory
        public HelpSet parsingEnded(HelpSet helpSet) {
            HelpSet helpSet2 = helpSet;
            if (!this.validParse) {
                helpSet2 = null;
                this.messages.addElement(new StringBuffer().append("Parsing failed for ").append(this.source).toString());
                Enumeration enumerationElements = this.messages.elements();
                while (enumerationElements.hasMoreElements()) {
                    String str = (String) enumerationElements.nextElement();
                    if (HelpSet.errorMsg == null) {
                        String unused = HelpSet.errorMsg = str;
                    } else {
                        String unused2 = HelpSet.errorMsg = new StringBuffer().append(HelpSet.errorMsg).append("\n").toString();
                        String unused3 = HelpSet.errorMsg = new StringBuffer().append(HelpSet.errorMsg).append(str).toString();
                    }
                }
            }
            return helpSet2;
        }

        private void parsingError(String str) {
            reportMessage(HelpUtilities.getText(str), false);
        }

        private void parsingError(String str, String str2) {
            reportMessage(HelpUtilities.getText(str, str2), false);
        }

        private void parsingError(String str, String str2, String str3) {
            reportMessage(HelpUtilities.getText(str, str2, str3), false);
        }

        private void parsingWarning(String str, String str2, String str3) {
            reportMessage(HelpUtilities.getText(str, str2, str3), true);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/HelpSet$Presentation.class */
    public static class Presentation {
        private String name;
        private boolean displayViews;
        private boolean displayViewImages;
        private Dimension size;
        private Point location;
        private String title;
        private boolean toolbar;
        private Vector helpActions;
        private Map.ID imageID;

        public Presentation(String str, boolean z, boolean z2, Dimension dimension, Point point, String str2, Map.ID id, boolean z3, Vector vector) {
            this.name = str;
            this.displayViews = z;
            this.displayViewImages = z2;
            this.size = dimension;
            this.location = point;
            this.title = str2;
            this.imageID = id;
            this.toolbar = z3;
            this.helpActions = vector;
        }

        public String getName() {
            return this.name;
        }

        public String getTitle() {
            return this.title;
        }

        public Map.ID getImageID() {
            return this.imageID;
        }

        public boolean isViewDisplayed() {
            return this.displayViews;
        }

        public boolean isViewImagesDisplayed() {
            return this.displayViewImages;
        }

        public Dimension getSize() {
            return this.size;
        }

        public Point getLocation() {
            return this.location;
        }

        public boolean isToolbar() {
            return this.toolbar;
        }

        public Enumeration getHelpActions(HelpSet helpSet, Object obj) {
            Class<?> clsClass$;
            Class<?> clsLoadClass;
            Vector vector = new Vector();
            ClassLoader loader = helpSet.getLoader();
            if (this.helpActions == null) {
                return vector.elements();
            }
            Enumeration enumerationElements = this.helpActions.elements();
            while (enumerationElements.hasMoreElements()) {
                HelpSetFactory.HelpAction helpAction = (HelpSetFactory.HelpAction) enumerationElements.nextElement();
                try {
                    Class<?>[] clsArr = new Class[1];
                    if (HelpSet.class$java$lang$Object == null) {
                        clsClass$ = HelpSet.class$("java.lang.Object");
                        HelpSet.class$java$lang$Object = clsClass$;
                    } else {
                        clsClass$ = HelpSet.class$java$lang$Object;
                    }
                    clsArr[0] = clsClass$;
                    Object[] objArr = {obj};
                    if (loader == null) {
                        clsLoadClass = Class.forName(helpAction.className);
                    } else {
                        clsLoadClass = loader.loadClass(helpAction.className);
                    }
                    HelpAction helpAction2 = (HelpAction) clsLoadClass.getConstructor(clsArr).newInstance(objArr);
                    if (helpAction.attr.containsKey("image")) {
                        try {
                            helpAction2.putValue("icon", new ImageIcon(helpSet.getCombinedMap().getURLFromID(Map.ID.create((String) helpAction.attr.get("image"), helpSet))));
                        } catch (Exception e) {
                        }
                    }
                    vector.add(helpAction2);
                } catch (Exception e2) {
                    throw new RuntimeException(new StringBuffer().append("Could not create HelpAction ").append(helpAction.className).toString());
                }
            }
            return vector.elements();
        }
    }

    protected void addView(NavigatorView navigatorView) {
        this.views.addElement(navigatorView);
    }

    protected void addSubHelpSet(HelpSet helpSet) {
        this.subHelpSets.addElement(helpSet);
    }

    protected void addPresentation(Presentation presentation, boolean z) {
        this.presentations.addElement(presentation);
        if (z) {
            this.defaultPresentation = presentation;
        }
    }

    public Object getKeyData(Object obj, String str) {
        Hashtable hashtable;
        Object obj2 = null;
        Hashtable hashtable2 = (Hashtable) this.localKeys.get(obj);
        if (hashtable2 != null) {
            obj2 = hashtable2.get(str);
        }
        if (obj2 == null && (hashtable = (Hashtable) defaultKeys.get(obj)) != null) {
            obj2 = hashtable.get(str);
        }
        return obj2;
    }

    public void setKeyData(Object obj, String str, Object obj2) {
        Hashtable hashtable = (Hashtable) this.localKeys.get(obj);
        if (hashtable == null) {
            hashtable = new Hashtable();
            this.localKeys.put(obj, hashtable);
        }
        hashtable.put(str, obj2);
    }

    private static void setDefaultKeyData(Object obj, String str, Object obj2) {
        if (defaultKeys == null) {
            defaultKeys = new Hashtable();
        }
        Hashtable hashtable = (Hashtable) defaultKeys.get(obj);
        if (hashtable == null) {
            hashtable = new Hashtable();
            defaultKeys.put(obj, hashtable);
        }
        hashtable.put(str, obj2);
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/HelpSet$HelpSetParser.class */
    private static class HelpSetParser implements ParserListener {
        private Stack tagStack;
        private Locale defaultLocale;
        private Locale lastLocale;
        private HelpSet myHS;
        private Locale myHSLocale;
        private HelpSetFactory factory;
        private String tagName;
        private String viewLabel;
        private String viewType;
        private String viewEngine;
        private String tagImage;
        private String helpActionImage;
        private String viewData;
        private String viewMergeType;
        private Hashtable htData;
        private boolean defaultPresentation = false;
        private boolean displayViews = true;
        private boolean displayViewImages = true;
        private Dimension size;
        private Point location;
        private String presentationTitle;
        private boolean toolbar;
        private Vector helpActions;
        private String helpAction;

        HelpSetParser(HelpSetFactory helpSetFactory) {
            this.factory = helpSetFactory;
        }

        synchronized void parseInto(Reader reader, HelpSet helpSet) throws IOException {
            this.tagStack = new Stack();
            this.defaultLocale = helpSet.getLocale();
            this.lastLocale = this.defaultLocale;
            this.myHS = helpSet;
            this.myHSLocale = helpSet.getLocale();
            Parser parser = new Parser(reader);
            parser.addParserListener(this);
            parser.parse();
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void tagFound(ParserEvent parserEvent) {
            Class clsClass$;
            String property;
            String property2;
            HelpSet.debug(new StringBuffer().append("tagFound ").append(parserEvent.getTag().name).toString());
            Locale localeLocaleFromLang = null;
            Tag tag = parserEvent.getTag();
            String str = tag.name;
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            TagProperties tagProperties = tag.atts;
            Hashtable hashtable = tagProperties == null ? null : tagProperties.getHashtable();
            if (tagProperties != null) {
                localeLocaleFromLang = HelpUtilities.localeFromLang(tagProperties.getProperty("xml:lang"));
                this.viewMergeType = tagProperties.getProperty("mergetype");
                this.helpActionImage = tagProperties.getProperty("image");
                String property3 = tagProperties.getProperty(ElementTags.WIDTH);
                if (property3 != null) {
                    i3 = Integer.parseInt(property3);
                }
                String property4 = tagProperties.getProperty("height");
                if (property4 != null) {
                    i4 = Integer.parseInt(property4);
                }
                String property5 = tagProperties.getProperty("x");
                if (property5 != null) {
                    i = Integer.parseInt(property5);
                }
                String property6 = tagProperties.getProperty(OperatorName.CURVE_TO_REPLICATE_FINAL_POINT);
                if (property6 != null) {
                    i2 = Integer.parseInt(property6);
                }
                String property7 = tagProperties.getProperty(PreferencesManager.VALUE_KEYSTORE_DEFAULT);
                if (property7 != null && property7.equals("true")) {
                    this.defaultPresentation = true;
                }
                String property8 = tagProperties.getProperty("displayviews");
                if (property8 != null && property8.equals("false")) {
                    this.displayViews = false;
                }
                String property9 = tagProperties.getProperty("displayviewimages");
                if (property9 != null && property9.equals("false")) {
                    this.displayViewImages = false;
                }
            }
            if (localeLocaleFromLang == null) {
                localeLocaleFromLang = this.lastLocale;
            }
            if (str.equals("helpset")) {
                if (tag.isEnd) {
                    removeTag(tag);
                    return;
                }
                if (!localeLocaleFromLang.equals(this.defaultLocale) && !localeLocaleFromLang.equals(this.myHSLocale) && localeLocaleFromLang != null) {
                    this.myHS.setLocale(localeLocaleFromLang);
                    this.defaultLocale = localeLocaleFromLang;
                }
                if (tagProperties != null && (property2 = tagProperties.getProperty(XmlConsts.XML_DECL_KW_VERSION)) != null && property2.compareTo("1.0") != 0 && property2.compareTo("2.0") != 0) {
                    parsingError("helpset.unknownVersion", property2);
                }
                addTag(tag, localeLocaleFromLang);
                return;
            }
            if (this.tagStack.empty()) {
                parsingError("helpset.wrongTopLevel", str);
            }
            String str2 = ((LangElement) this.tagStack.peek()).getTag().name;
            if (str.equals("title")) {
                if (tag.isEnd) {
                    removeTag(tag);
                    return;
                }
                if (!str2.equals("helpset") && !str2.equals("presentation")) {
                    wrongParent(str, str2);
                }
                if (!localeLocaleFromLang.equals(this.defaultLocale) && !localeLocaleFromLang.equals(this.myHSLocale)) {
                    wrongLocale(localeLocaleFromLang, this.defaultLocale, this.myHSLocale);
                }
                addTag(tag, localeLocaleFromLang);
                return;
            }
            if (str.equals("homeID")) {
                if (tag.isEnd) {
                    removeTag(tag);
                    return;
                }
                if (!str2.equals("maps")) {
                    wrongParent(str, str2);
                }
                addTag(tag, localeLocaleFromLang);
                return;
            }
            if (str.equals("mapref")) {
                if (tag.isEnd && !tag.isEmpty) {
                    removeTag(tag);
                    return;
                }
                if (!str2.equals("maps")) {
                    wrongParent(str, str2);
                }
                if (!tag.isEmpty) {
                    addTag(tag, localeLocaleFromLang);
                }
                this.factory.processMapRef(this.myHS, hashtable);
                return;
            }
            if (str.equals("data")) {
                if (tag.isEnd) {
                    removeTag(tag);
                    return;
                }
                if (!str2.equals("view")) {
                    wrongParent(str, str2);
                } else {
                    addTag(tag, localeLocaleFromLang);
                }
                this.htData = hashtable;
                return;
            }
            if (str.equals("name") || str.equals("type") || str.equals("image")) {
                if (tag.isEnd) {
                    removeTag(tag);
                    return;
                } else if (!str2.equals("view") && !str2.equals("presentation")) {
                    wrongParent(str, str2);
                    return;
                } else {
                    addTag(tag, localeLocaleFromLang);
                    return;
                }
            }
            if (str.equals(AnnotatedPrivateKey.LABEL)) {
                if (tag.isEnd) {
                    removeTag(tag);
                    return;
                }
                if (!str2.equals("view")) {
                    wrongParent(str, str2);
                    return;
                }
                if (!localeLocaleFromLang.equals(this.defaultLocale) && !localeLocaleFromLang.equals(this.myHSLocale)) {
                    wrongLocale(localeLocaleFromLang, this.defaultLocale, this.myHSLocale);
                }
                addTag(tag, localeLocaleFromLang);
                return;
            }
            if (str.equals("view")) {
                if (tag.isEnd) {
                    removeTag(tag);
                    if (this.tagImage != null) {
                        if (this.htData == null) {
                            this.htData = new Hashtable();
                        }
                        this.htData.put("imageID", this.tagImage);
                    }
                    if (this.viewMergeType != null) {
                        if (this.htData == null) {
                            this.htData = new Hashtable();
                        }
                        this.htData.put("mergetype", this.viewMergeType);
                    }
                    this.factory.processView(this.myHS, this.tagName, this.viewLabel, this.viewType, hashtable, this.viewData, this.htData, localeLocaleFromLang);
                    this.tagName = null;
                    this.viewLabel = null;
                    this.viewType = null;
                    this.tagImage = null;
                    this.viewData = null;
                    this.htData = null;
                    this.viewMergeType = null;
                    return;
                }
                if (!str2.equals("helpset")) {
                    wrongParent(str, str2);
                    return;
                } else {
                    addTag(tag, localeLocaleFromLang);
                    return;
                }
            }
            if (str.equals("presentation")) {
                if (tag.isEnd) {
                    removeTag(tag);
                    this.factory.processPresentation(this.myHS, this.tagName, this.defaultPresentation, this.displayViews, this.displayViewImages, this.size, this.location, this.presentationTitle, this.tagImage, this.toolbar, this.helpActions);
                    this.tagName = null;
                    this.defaultPresentation = false;
                    this.displayViews = true;
                    this.displayViewImages = true;
                    this.size = null;
                    this.location = null;
                    this.presentationTitle = null;
                    this.tagImage = null;
                    this.toolbar = false;
                    this.helpActions = null;
                    return;
                }
                if (!str2.equals("helpset")) {
                    wrongParent(str, str2);
                    return;
                } else {
                    addTag(tag, localeLocaleFromLang);
                    return;
                }
            }
            if (str.equals(ElementTags.SIZE)) {
                if (tag.isEnd) {
                    if (this.size == null) {
                        this.size = new Dimension(i3, i4);
                    } else {
                        this.size.setSize(i3, i4);
                    }
                    if (!tag.isEmpty) {
                        removeTag(tag);
                        return;
                    }
                    return;
                }
                if (!str2.equals("presentation")) {
                    wrongParent(str, str2);
                    return;
                } else {
                    addTag(tag, localeLocaleFromLang);
                    this.size = new Dimension();
                    return;
                }
            }
            if (str.equals("location")) {
                if (tag.isEnd) {
                    if (this.location == null) {
                        this.location = new Point(i, i2);
                    } else {
                        this.location.setLocation(i, i2);
                    }
                    if (!tag.isEmpty) {
                        removeTag(tag);
                        return;
                    }
                    return;
                }
                if (!str2.equals("presentation")) {
                    wrongParent(str, str2);
                    return;
                } else {
                    addTag(tag, localeLocaleFromLang);
                    this.location = new Point();
                    return;
                }
            }
            if (str.equals("toolbar")) {
                if (tag.isEnd) {
                    removeTag(tag);
                    return;
                } else {
                    if (!str2.equals("presentation")) {
                        wrongParent(str, str2);
                        return;
                    }
                    addTag(tag, localeLocaleFromLang);
                    this.helpActions = new Vector();
                    this.toolbar = true;
                    return;
                }
            }
            if (str.equals("helpaction")) {
                if (tag.isEnd) {
                    removeTag(tag);
                    if (this.helpAction != null) {
                        Hashtable hashtable2 = new Hashtable();
                        this.helpActions.add(new HelpSetFactory.HelpAction(this.helpAction, hashtable2));
                        if (this.helpActionImage != null) {
                            hashtable2.put("image", this.helpActionImage);
                            this.helpActionImage = null;
                        }
                        this.helpAction = null;
                        return;
                    }
                    return;
                }
                if (!str2.equals("toolbar")) {
                    wrongParent(str, str2);
                    return;
                } else {
                    addTag(tag, localeLocaleFromLang);
                    return;
                }
            }
            if (str.equals("maps")) {
                if (tag.isEnd) {
                    removeTag(tag);
                    return;
                } else if (!str2.equals("helpset")) {
                    wrongParent(str, str2);
                    return;
                } else {
                    addTag(tag, localeLocaleFromLang);
                    return;
                }
            }
            if (str.equals("subhelpset")) {
                if (tag.isEnd && !tag.isEmpty) {
                    removeTag(tag);
                    return;
                }
                if (!tag.isEmpty) {
                    addTag(tag, localeLocaleFromLang);
                }
                this.factory.processSubHelpSet(this.myHS, hashtable);
                return;
            }
            if (str.equals("impl")) {
                if (tag.isEnd) {
                    removeTag(tag);
                    return;
                } else if (!str2.equals("helpset")) {
                    wrongParent(str, str2);
                    return;
                } else {
                    addTag(tag, localeLocaleFromLang);
                    return;
                }
            }
            if (str.equals("helpsetregistry")) {
                if (tag.isEnd && !tag.isEmpty) {
                    removeTag(tag);
                    return;
                }
                if (!str2.equals("impl")) {
                    wrongParent(str, str2);
                    return;
                }
                if (!tag.isEnd) {
                    addTag(tag, localeLocaleFromLang);
                }
                if (tagProperties != null && (property = tagProperties.getProperty("helpbrokerclass")) != null) {
                    this.myHS.setKeyData(HelpSet.implRegistry, HelpSet.helpBrokerClass, property);
                    return;
                }
                return;
            }
            if (str.equals("viewerregistry")) {
                if (tag.isEnd && !tag.isEmpty) {
                    removeTag(tag);
                    return;
                }
                if (!str2.equals("impl")) {
                    wrongParent(str, str2);
                    return;
                }
                if (!tag.isEnd) {
                    addTag(tag, localeLocaleFromLang);
                }
                if (tagProperties != null) {
                    String property10 = tagProperties.getProperty("viewertype");
                    String property11 = tagProperties.getProperty("viewerclass");
                    if (property10 != null && property11 != null) {
                        if (HelpSet.class$javax$help$HelpSet == null) {
                            clsClass$ = HelpSet.class$("javax.help.HelpSet");
                            HelpSet.class$javax$help$HelpSet = clsClass$;
                        } else {
                            clsClass$ = HelpSet.class$javax$help$HelpSet;
                        }
                        ClassLoader classLoader = clsClass$.getClassLoader();
                        this.myHS.setKeyData(HelpSet.kitTypeRegistry, property10, property11);
                        this.myHS.setKeyData(HelpSet.kitLoaderRegistry, property10, classLoader);
                    }
                }
            }
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void piFound(ParserEvent parserEvent) {
            this.factory.processPI(this.myHS, parserEvent.getTarget(), parserEvent.getData());
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void doctypeFound(ParserEvent parserEvent) {
            this.factory.processDOCTYPE(parserEvent.getRoot(), parserEvent.getPublicId(), parserEvent.getSystemId());
        }

        private void checkNull(String str, String str2) {
            if (!str2.equals("")) {
                parsingError("helpset.wrongText", str, str2);
            }
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void textFound(ParserEvent parserEvent) {
            HelpSet.debug("textFound: ");
            HelpSet.debug(new StringBuffer().append("  text: ").append(parserEvent.getText()).toString());
            if (this.tagStack.empty()) {
                return;
            }
            Tag tag = ((LangElement) this.tagStack.peek()).getTag();
            TagProperties tagProperties = tag.atts;
            Hashtable hashtable = tagProperties == null ? null : tagProperties.getHashtable();
            String strTrim = parserEvent.getText().trim();
            String str = tag.name;
            if (str.equals("helpset")) {
                checkNull("helpset", strTrim);
                return;
            }
            int size = this.tagStack.size();
            String str2 = "";
            if (size >= 2) {
                str2 = ((LangElement) this.tagStack.elementAt(size - 2)).getTag().name;
            }
            if (str.equals("title")) {
                if (str2.equals("helpset")) {
                    this.factory.processTitle(this.myHS, strTrim);
                    return;
                } else {
                    this.presentationTitle = strTrim.trim();
                    return;
                }
            }
            if (str.equals("homeID")) {
                this.factory.processHomeID(this.myHS, strTrim);
                return;
            }
            if (str.equals("mapref")) {
                checkNull("mapref", strTrim);
                return;
            }
            if (str.equals("subhelpset")) {
                checkNull("subhelpset", strTrim);
                return;
            }
            if (str.equals("data")) {
                this.viewData = strTrim.trim();
                return;
            }
            if (str.equals(AnnotatedPrivateKey.LABEL)) {
                this.viewLabel = strTrim.trim();
                return;
            }
            if (str.equals("name")) {
                this.tagName = strTrim.trim();
                return;
            }
            if (str.equals("helpaction")) {
                this.helpAction = strTrim.trim();
                return;
            }
            if (str.equals("type")) {
                this.viewType = strTrim.trim();
                return;
            }
            if (str.equals("image")) {
                this.tagImage = strTrim.trim();
                return;
            }
            if (str.equals("view")) {
                checkNull("view", strTrim);
            } else if (str.equals("maps")) {
                checkNull("maps", strTrim);
            } else if (str.equals("mergetype")) {
                checkNull("mergetype", strTrim);
            }
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void errorFound(ParserEvent parserEvent) {
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void commentFound(ParserEvent parserEvent) {
        }

        protected void addTag(Tag tag, Locale locale) {
            this.tagStack.push(new LangElement(tag, locale));
            if (this.lastLocale == null) {
                this.lastLocale = locale;
            } else if (locale == null) {
                this.lastLocale = locale;
            } else if (!this.lastLocale.equals(locale)) {
                this.lastLocale = locale;
            }
        }

        protected void removeTag(Tag tag) {
            Locale locale;
            String str = tag.name;
            do {
                if (this.tagStack.empty()) {
                    unbalanced(str);
                }
            } while (!((LangElement) this.tagStack.pop()).getTag().name.equals(str));
            if (this.tagStack.empty()) {
                locale = this.defaultLocale;
            } else {
                locale = ((LangElement) this.tagStack.peek()).getLocale();
            }
            if (this.lastLocale == null) {
                this.lastLocale = locale;
            } else if (locale == null) {
                this.lastLocale = locale;
            } else if (!this.lastLocale.equals(locale)) {
                this.lastLocale = locale;
            }
        }

        private void parsingError(String str) {
            this.factory.reportMessage(HelpUtilities.getText(str), false);
        }

        private void parsingError(String str, String str2) {
            this.factory.reportMessage(HelpUtilities.getText(str, str2), false);
        }

        private void parsingError(String str, String str2, String str3) {
            this.factory.reportMessage(HelpUtilities.getText(str, str2, str3), false);
        }

        private void wrongParent(String str, String str2) {
            parsingError("helpset.wrongParent", str, str2);
        }

        private void unbalanced(String str) {
            parsingError("helpset.unbalanced", str);
        }

        private void wrongLocale(Locale locale, Locale locale2, Locale locale3) {
            this.factory.reportMessage(HelpUtilities.getText("helpset.wrongLocale", locale.toString(), locale2.toString(), locale3.toString()), true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void debug(String str) {
    }
}
