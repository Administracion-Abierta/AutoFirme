package javax.help;

import com.ctc.wstx.cfg.XmlConsts;
import com.sun.java.help.impl.LangElement;
import com.sun.java.help.impl.Parser;
import com.sun.java.help.impl.ParserEvent;
import com.sun.java.help.impl.ParserListener;
import com.sun.java.help.impl.Tag;
import com.sun.java.help.impl.TagProperties;
import com.sun.java.help.impl.XmlReader;
import java.awt.Component;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

/* JADX INFO: loaded from: autofirma.jar:javax/help/TOCView.class */
public class TOCView extends NavigatorView {
    public static final String publicIDString = "-//Sun Microsystems Inc.//DTD JavaHelp TOC Version 1.0//EN";
    public static final String publicIDString_V2 = "-//Sun Microsystems Inc.//DTD JavaHelp TOC Version 2.0//EN";
    private Map.ID categoryOpenImageID;
    private Map.ID categoryClosedImageID;
    private Map.ID topicImageID;
    private static boolean warningOfFailures = false;
    private static final boolean debug = false;

    public TOCView(HelpSet helpSet, String str, String str2, Hashtable hashtable) {
        this(helpSet, str, str2, helpSet.getLocale(), hashtable);
    }

    public TOCView(HelpSet helpSet, String str, String str2, Locale locale, Hashtable hashtable) {
        super(helpSet, str, str2, locale, hashtable);
        this.categoryOpenImageID = null;
        this.categoryClosedImageID = null;
        this.topicImageID = null;
    }

    @Override // javax.help.NavigatorView
    public Component createNavigator(HelpModel helpModel) {
        return new JHelpTOCNavigator(this, helpModel);
    }

    @Override // javax.help.NavigatorView
    public String getMergeType() {
        String mergeType = super.getMergeType();
        if (mergeType == null) {
            return "javax.help.AppendMerge";
        }
        return mergeType;
    }

    public DefaultMutableTreeNode getDataAsTree() {
        HelpSet helpSet = getHelpSet();
        Hashtable parameters = getParameters();
        if (parameters == null || (parameters != null && !parameters.containsKey("data"))) {
            return new DefaultMutableTreeNode();
        }
        try {
            return parse(new URL(helpSet.getHelpSetURL(), (String) parameters.get("data")), helpSet, helpSet.getLocale(), new DefaultTOCFactory(), this);
        } catch (Exception e) {
            throw new Error(new StringBuffer().append("Trouble getting URL to TOC data; ").append(e).toString());
        }
    }

    public static DefaultMutableTreeNode parse(URL url, HelpSet helpSet, Locale locale, TreeItemFactory treeItemFactory) {
        return parse(url, helpSet, locale, treeItemFactory, null);
    }

    public static DefaultMutableTreeNode parse(URL url, HelpSet helpSet, Locale locale, TreeItemFactory treeItemFactory, TOCView tOCView) {
        DefaultMutableTreeNode defaultMutableTreeNode = null;
        try {
            Reader readerCreateReader = XmlReader.createReader(url.openConnection());
            treeItemFactory.parsingStarted(url);
            defaultMutableTreeNode = new TOCParser(treeItemFactory, tOCView).parse(readerCreateReader, helpSet, locale);
            readerCreateReader.close();
        } catch (Exception e) {
            treeItemFactory.reportMessage(new StringBuffer().append("Exception caught while parsing ").append(url).append(e.toString()).toString(), false);
        }
        return treeItemFactory.parsingEnded(defaultMutableTreeNode);
    }

    public void setCategoryOpenImageID(String str) {
        if (str == null) {
            return;
        }
        try {
            this.categoryOpenImageID = Map.ID.create(str, getHelpSet());
        } catch (BadIDException e) {
        }
    }

    public Map.ID getCategoryOpenImageID() {
        if (this.categoryOpenImageID == null) {
            return this.categoryClosedImageID;
        }
        return this.categoryOpenImageID;
    }

    public void setCategoryClosedImageID(String str) {
        if (str == null) {
            return;
        }
        try {
            this.categoryClosedImageID = Map.ID.create(str, getHelpSet());
        } catch (BadIDException e) {
        }
    }

    public Map.ID getCategoryClosedImageID() {
        return this.categoryClosedImageID;
    }

    public void setTopicImageID(String str) {
        if (str == null) {
            this.topicImageID = null;
        } else {
            try {
                this.topicImageID = Map.ID.create(str, getHelpSet());
            } catch (BadIDException e) {
            }
        }
    }

    public Map.ID getTopicImageID() {
        return this.topicImageID;
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/TOCView$DefaultTOCFactory.class */
    public static class DefaultTOCFactory implements TreeItemFactory {
        private URL source;
        private Vector messages = new Vector();
        private boolean validParse = true;

        @Override // javax.help.TreeItemFactory
        public void parsingStarted(URL url) {
            if (url == null) {
                throw new NullPointerException("source");
            }
            this.source = url;
        }

        @Override // javax.help.TreeItemFactory
        public void processDOCTYPE(String str, String str2, String str3) {
            if (str2 == null || (str2.compareTo(TOCView.publicIDString) != 0 && str2.compareTo(TOCView.publicIDString_V2) != 0)) {
                reportMessage(HelpUtilities.getText("toc.wrongPublicID", str2), false);
            }
        }

        @Override // javax.help.TreeItemFactory
        public void processPI(HelpSet helpSet, String str, String str2) {
        }

        @Override // javax.help.TreeItemFactory
        public TreeItem createItem(String str, Hashtable hashtable, HelpSet helpSet, Locale locale) {
            if (str == null || !str.equals("tocitem")) {
                throw new IllegalArgumentException("tagName");
            }
            String str2 = null;
            String str3 = null;
            String str4 = null;
            String str5 = null;
            String str6 = null;
            String str7 = null;
            String str8 = null;
            if (hashtable != null) {
                str2 = (String) hashtable.get("target");
                str3 = (String) hashtable.get("image");
                str4 = (String) hashtable.get("text");
                str5 = (String) hashtable.get("mergetype");
                str6 = (String) hashtable.get("expand");
                str7 = (String) hashtable.get("presentationtype");
                str8 = (String) hashtable.get("presentationname");
            }
            Map.ID idCreate = null;
            Map.ID idCreate2 = null;
            try {
                idCreate = Map.ID.create(str2, helpSet);
            } catch (BadIDException e) {
            }
            try {
                idCreate2 = Map.ID.create(str3, helpSet);
            } catch (BadIDException e2) {
            }
            TOCItem tOCItem = new TOCItem(idCreate, idCreate2, helpSet, locale);
            if (str4 != null) {
                tOCItem.setName(str4);
            }
            if (str5 != null) {
                tOCItem.setMergeType(str5);
            }
            if (str6 != null) {
                if (str6.equals("true")) {
                    tOCItem.setExpansionType(1);
                } else if (str6.equals("false")) {
                    tOCItem.setExpansionType(0);
                }
            }
            if (str7 != null) {
                tOCItem.setPresentation(str7);
            }
            if (str8 != null) {
                tOCItem.setPresentationName(str8);
            }
            return tOCItem;
        }

        @Override // javax.help.TreeItemFactory
        public TreeItem createItem() {
            return new TOCItem();
        }

        @Override // javax.help.TreeItemFactory
        public void reportMessage(String str, boolean z) {
            this.messages.addElement(str);
            this.validParse = this.validParse && z;
        }

        @Override // javax.help.TreeItemFactory
        public Enumeration listMessages() {
            return this.messages.elements();
        }

        @Override // javax.help.TreeItemFactory
        public DefaultMutableTreeNode parsingEnded(DefaultMutableTreeNode defaultMutableTreeNode) {
            DefaultMutableTreeNode defaultMutableTreeNode2 = defaultMutableTreeNode;
            if (!this.validParse) {
                defaultMutableTreeNode2 = null;
                System.err.println(new StringBuffer().append("Parsing failed for ").append(this.source).toString());
                Enumeration enumerationElements = this.messages.elements();
                while (enumerationElements.hasMoreElements()) {
                    System.err.println((String) enumerationElements.nextElement());
                }
            }
            return defaultMutableTreeNode2;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/TOCView$TOCParser.class */
    private static class TOCParser implements ParserListener {
        private HelpSet currentParseHS;
        private Stack nodeStack;
        private Stack itemStack;
        private boolean startedtoc;
        private Stack tagStack;
        private Locale defaultLocale;
        private Locale lastLocale;
        private TreeItemFactory factory;
        private TOCView tocView;

        TOCParser(TreeItemFactory treeItemFactory, TOCView tOCView) {
            this.factory = treeItemFactory;
            this.tocView = tOCView;
        }

        synchronized DefaultMutableTreeNode parse(Reader reader, HelpSet helpSet, Locale locale) throws IOException {
            this.nodeStack = new Stack();
            this.tagStack = new Stack();
            this.itemStack = new Stack();
            if (locale == null) {
                this.defaultLocale = Locale.getDefault();
            } else {
                this.defaultLocale = locale;
            }
            this.lastLocale = this.defaultLocale;
            DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode();
            this.nodeStack.push(defaultMutableTreeNode);
            this.currentParseHS = helpSet;
            Parser parser = new Parser(reader);
            parser.addParserListener(this);
            parser.parse();
            return defaultMutableTreeNode;
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void tagFound(ParserEvent parserEvent) {
            TOCItem tOCItem;
            Locale localeLocaleFromLang = null;
            Tag tag = parserEvent.getTag();
            TOCView.debug(new StringBuffer().append("TagFound: ").append(tag.name).toString());
            TagProperties tagProperties = tag.atts;
            if (tagProperties != null) {
                localeLocaleFromLang = HelpUtilities.localeFromLang(tagProperties.getProperty("xml:lang"));
            }
            if (localeLocaleFromLang == null) {
                localeLocaleFromLang = this.lastLocale;
            }
            if (tag.name.equals("tocitem")) {
                if (!this.startedtoc) {
                    this.factory.reportMessage(HelpUtilities.getText("toc.invalidTOCFormat"), false);
                }
                if (tag.isEnd && !tag.isEmpty) {
                    this.nodeStack.pop();
                    this.itemStack.pop();
                    removeTag(tag);
                    return;
                }
                Hashtable hashtable = null;
                if (tagProperties != null) {
                    try {
                        hashtable = tagProperties.getHashtable();
                    } catch (Exception e) {
                        if (TOCView.warningOfFailures) {
                            String property = null;
                            if (tagProperties != null) {
                                property = tagProperties.getProperty("target");
                                tagProperties.getProperty("image");
                            }
                            System.err.println("Failure in IndexItem Creation; ");
                            System.err.println(new StringBuffer().append("  id: ").append(property).toString());
                            System.err.println(new StringBuffer().append("  hs: ").append(this.currentParseHS).toString());
                        }
                        tOCItem = (TOCItem) this.factory.createItem();
                    }
                }
                tOCItem = (TOCItem) this.factory.createItem("tocitem", hashtable, this.currentParseHS, localeLocaleFromLang);
                if (!this.itemStack.empty()) {
                    TOCItem tOCItem2 = (TOCItem) this.itemStack.peek();
                    if (tOCItem.getExpansionType() == -1 && tOCItem2 != null && tOCItem2.getExpansionType() != -1) {
                        tOCItem.setExpansionType(tOCItem2.getExpansionType());
                    }
                }
                DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(tOCItem);
                ((DefaultMutableTreeNode) this.nodeStack.peek()).add(defaultMutableTreeNode);
                if (!tag.isEmpty) {
                    this.itemStack.push(tOCItem);
                    this.nodeStack.push(defaultMutableTreeNode);
                    addTag(tag, localeLocaleFromLang);
                    return;
                }
                return;
            }
            if (tag.name.equals("toc")) {
                TOCView.debug(new StringBuffer().append("attr: ").append(tagProperties).toString());
                if (!tag.isEnd) {
                    if (tagProperties != null) {
                        String property2 = tagProperties.getProperty(XmlConsts.XML_DECL_KW_VERSION);
                        if (property2 != null && property2.compareTo("1.0") != 0 && property2.compareTo("2.0") != 0) {
                            this.factory.reportMessage(HelpUtilities.getText("toc.unknownVersion", property2), false);
                        }
                        if (this.tocView != null) {
                            this.tocView.setCategoryOpenImageID(tagProperties.getProperty("categoryopenimage"));
                            this.tocView.setCategoryClosedImageID(tagProperties.getProperty("categoryclosedimage"));
                            this.tocView.setTopicImageID(tagProperties.getProperty("topicimage"));
                        }
                    }
                    if (this.startedtoc) {
                        this.factory.reportMessage(HelpUtilities.getText("toc.invalidTOCFormat"), false);
                    }
                    this.startedtoc = true;
                    addTag(tag, localeLocaleFromLang);
                    return;
                }
                if (this.startedtoc) {
                    this.startedtoc = false;
                }
                removeTag(tag);
            }
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void piFound(ParserEvent parserEvent) {
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void doctypeFound(ParserEvent parserEvent) {
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void textFound(ParserEvent parserEvent) {
            TOCView.debug(new StringBuffer().append("TextFound: ").append(parserEvent.getText().trim()).toString());
            if (!this.tagStack.empty() && ((LangElement) this.tagStack.peek()).getTag().name.equals("tocitem")) {
                TOCItem tOCItem = (TOCItem) this.itemStack.peek();
                String name = tOCItem.getName();
                if (name == null) {
                    tOCItem.setName(parserEvent.getText().trim());
                } else {
                    tOCItem.setName(name.concat(parserEvent.getText()).trim());
                }
            }
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void commentFound(ParserEvent parserEvent) {
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void errorFound(ParserEvent parserEvent) {
            this.factory.reportMessage(parserEvent.getText(), false);
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
            String str = tag.name;
            Locale locale = null;
            while (!this.tagStack.empty()) {
                if (((LangElement) this.tagStack.pop()).getTag().name.equals(str)) {
                    if (this.tagStack.empty()) {
                        locale = this.defaultLocale;
                        break;
                    } else {
                        locale = ((LangElement) this.tagStack.peek()).getLocale();
                        break;
                    }
                }
            }
            if (this.lastLocale == null) {
                this.lastLocale = locale;
            } else if (locale == null) {
                this.lastLocale = locale;
            } else if (!this.lastLocale.equals(locale)) {
                this.lastLocale = locale;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void debug(String str) {
    }
}
