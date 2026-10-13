package javax.help;

import com.ctc.wstx.cfg.XmlConsts;
import com.sun.java.help.impl.Parser;
import com.sun.java.help.impl.ParserEvent;
import com.sun.java.help.impl.ParserListener;
import com.sun.java.help.impl.Tag;
import com.sun.java.help.impl.TagProperties;
import com.sun.java.help.impl.XmlReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:javax/help/FlatMap.class */
public class FlatMap implements Map, Serializable {
    private URL base;
    private ResourceBundle resource;
    private HelpSet helpset;
    public static final String publicIDString = "-//Sun Microsystems Inc.//DTD JavaHelp Map Version 1.0//EN";
    public static final String publicIDString_V2 = "-//Sun Microsystems Inc.//DTD JavaHelp Map Version 2.0//EN";
    private static final boolean debug = false;

    public FlatMap(URL url, HelpSet helpSet) throws IOException {
        debug(new StringBuffer().append("Creating FlatMap for: ").append(url).toString());
        if (helpSet.getHelpSets().hasMoreElements()) {
            throw new IllegalArgumentException("Cannot create - HelpSet is not flat");
        }
        this.resource = new FlatMapResourceBundle(this, url);
        this.base = url;
        this.helpset = helpSet;
    }

    public HelpSet getHelpSet() {
        return this.helpset;
    }

    @Override // javax.help.Map
    public boolean isValidID(String str, HelpSet helpSet) {
        debug(new StringBuffer().append("isValidID ").append(str).toString());
        try {
            this.resource.getString(str);
            return true;
        } catch (MissingResourceException e) {
            return false;
        }
    }

    @Override // javax.help.Map
    public Enumeration getAllIDs() {
        return new FlatEnumeration(this.resource.getKeys(), this.helpset);
    }

    @Override // javax.help.Map
    public URL getURLFromID(Map.ID id) throws MalformedURLException {
        debug(new StringBuffer().append("getURLFromID(").append(id).append(")").toString());
        String str = id.id;
        HelpSet helpSet = id.hs;
        if (str == null) {
            return null;
        }
        try {
            return new URL(this.base, this.resource.getString(str));
        } catch (MissingResourceException e) {
            return null;
        }
    }

    @Override // javax.help.Map
    public boolean isID(URL url) {
        Enumeration<String> keys = this.resource.getKeys();
        while (keys.hasMoreElements()) {
            try {
                if (url.sameFile(new URL(this.base, (String) this.resource.getObject(keys.nextElement())))) {
                    return true;
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    @Override // javax.help.Map
    public Map.ID getIDFromURL(URL url) {
        if (url == null) {
            return null;
        }
        String externalForm = url.toExternalForm();
        Enumeration<String> keys = this.resource.getKeys();
        while (keys.hasMoreElements()) {
            String strNextElement = keys.nextElement();
            try {
                URL url2 = new URL(this.base, this.resource.getString(strNextElement));
                if (url2 != null && externalForm.compareTo(url2.toExternalForm()) == 0) {
                    return Map.ID.create(strNextElement, this.helpset);
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    @Override // javax.help.Map
    public Map.ID getClosestID(URL url) {
        return getIDFromURL(url);
    }

    @Override // javax.help.Map
    public Enumeration getIDs(URL url) {
        Vector vector = new Vector();
        Enumeration<String> keys = this.resource.getKeys();
        while (keys.hasMoreElements()) {
            String strNextElement = keys.nextElement();
            try {
                if (url.sameFile(new URL(this.base, this.resource.getString(strNextElement)))) {
                    vector.addElement(strNextElement);
                }
            } catch (Exception e) {
            }
        }
        return new FlatEnumeration(vector.elements(), this.helpset);
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/FlatMap$FlatEnumeration.class */
    private static class FlatEnumeration implements Enumeration {
        private Enumeration e;
        private HelpSet hs;

        public FlatEnumeration(Enumeration enumeration, HelpSet helpSet) {
            this.e = enumeration;
            this.hs = helpSet;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.e.hasMoreElements();
        }

        @Override // java.util.Enumeration
        public Object nextElement() {
            Map.ID idCreate = null;
            try {
                idCreate = Map.ID.create((String) this.e.nextElement(), this.hs);
            } catch (Exception e) {
            }
            return idCreate;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/FlatMap$FlatMapResourceBundle.class */
    protected class FlatMapResourceBundle extends ResourceBundle implements ParserListener, Serializable {
        private boolean startedmap;
        private URL source;
        private final FlatMap this$0;
        private Hashtable lookup = null;
        private Vector messages = new Vector();
        private boolean validParse = true;

        public FlatMapResourceBundle(FlatMap flatMap, URL url) {
            this.this$0 = flatMap;
            this.source = url;
            try {
                Reader readerCreateReader = XmlReader.createReader(url.openConnection());
                parse(readerCreateReader);
                readerCreateReader.close();
            } catch (Exception e) {
                reportMessage(new StringBuffer().append("Exception caught while parsing ").append(url).append(" ").append(e.toString()).toString(), false);
            }
            parsingEnded();
            Enumeration enumerationKeys = this.lookup.keys();
            while (enumerationKeys.hasMoreElements()) {
            }
        }

        @Override // java.util.ResourceBundle
        public final Object handleGetObject(String str) {
            return this.lookup.get(str);
        }

        @Override // java.util.ResourceBundle
        public Enumeration getKeys() {
            return this.lookup.keys();
        }

        synchronized void parse(Reader reader) throws IOException {
            this.lookup = new Hashtable(10);
            Parser parser = new Parser(reader);
            parser.addParserListener(this);
            parser.parse();
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void tagFound(ParserEvent parserEvent) {
            String property;
            Tag tag = parserEvent.getTag();
            FlatMap.debug(new StringBuffer().append("TagFound: ").append(tag.name).toString());
            TagProperties tagProperties = tag.atts;
            if (tag.name.equals("mapID")) {
                if (!this.startedmap) {
                    parsingError("map.invalidMapFormat");
                }
                String property2 = null;
                String property3 = null;
                if (tagProperties != null) {
                    property2 = tagProperties.getProperty("target");
                    property3 = tagProperties.getProperty("url");
                }
                if (property2 == null || property3 == null) {
                    reportMessage("Failure in mapID Creation;", true);
                    reportMessage(new StringBuffer().append("  target: ").append(property2).toString(), true);
                    reportMessage(new StringBuffer().append("  url: ").append(property3).toString(), true);
                    return;
                }
                this.lookup.put(property2, property3);
                return;
            }
            if (tag.name.equals("map")) {
                if (!tag.isEnd) {
                    if (tagProperties != null && (property = tagProperties.getProperty(XmlConsts.XML_DECL_KW_VERSION)) != null && property.compareTo("1.0") != 0 && property.compareTo("2.0") != 0) {
                        parsingError("map.unknownVersion", property);
                    }
                    if (this.startedmap) {
                        parsingError("map.invalidMapFormat");
                    }
                    this.startedmap = true;
                    return;
                }
                if (this.startedmap) {
                    this.startedmap = false;
                }
            }
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void piFound(ParserEvent parserEvent) {
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void doctypeFound(ParserEvent parserEvent) {
            String publicId = parserEvent.getPublicId();
            if (publicId == null || (publicId.compareTo(FlatMap.publicIDString) != 0 && publicId.compareTo(FlatMap.publicIDString_V2) != 0)) {
                parsingError("map.wrongPublicID", publicId);
            }
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void textFound(ParserEvent parserEvent) {
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void commentFound(ParserEvent parserEvent) {
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void errorFound(ParserEvent parserEvent) {
            reportMessage(parserEvent.getText(), false);
        }

        public void reportMessage(String str, boolean z) {
            this.messages.addElement(str);
            this.validParse = this.validParse && z;
        }

        public Enumeration listMessages() {
            return this.messages.elements();
        }

        private void parsingEnded() {
            if (!this.validParse) {
                if (this.lookup != null) {
                    this.lookup.clear();
                }
                FlatMap.debug(new StringBuffer().append("Parsing failed for ").append(this.source).toString());
                Enumeration enumerationElements = this.messages.elements();
                while (enumerationElements.hasMoreElements()) {
                    FlatMap.debug((String) enumerationElements.nextElement());
                }
                return;
            }
            this.source = null;
        }

        private void parsingError(String str) {
            reportMessage(HelpUtilities.getText(str), false);
        }

        private void parsingError(String str, String str2) {
            reportMessage(HelpUtilities.getText(str, str2), false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void debug(String str) {
    }
}
