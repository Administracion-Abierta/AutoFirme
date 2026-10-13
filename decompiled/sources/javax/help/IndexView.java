package javax.help;

import com.ctc.wstx.cfg.XmlConsts;
import com.sun.java.help.impl.LangElement;
import com.sun.java.help.impl.Parser;
import com.sun.java.help.impl.ParserEvent;
import com.sun.java.help.impl.ParserListener;
import com.sun.java.help.impl.Tag;
import com.sun.java.help.impl.TagProperties;
import com.sun.java.help.impl.XmlReader;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
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

/* JADX INFO: loaded from: autofirma.jar:javax/help/IndexView.class */
public class IndexView extends NavigatorView {
    public static final String publicIDString = "-//Sun Microsystems Inc.//DTD JavaHelp Index Version 1.0//EN";
    public static final String publicIDString_V2 = "-//Sun Microsystems Inc.//DTD JavaHelp Index Version 2.0//EN";
    private static boolean warningOfFailures = false;
    private static final boolean debug = false;

    public IndexView(HelpSet helpSet, String str, String str2, Hashtable hashtable) {
        super(helpSet, str, str2, helpSet.getLocale(), hashtable);
    }

    public IndexView(HelpSet helpSet, String str, String str2, Locale locale, Hashtable hashtable) {
        super(helpSet, str, str2, locale, hashtable);
    }

    @Override // javax.help.NavigatorView
    public Component createNavigator(HelpModel helpModel) {
        return new JHelpIndexNavigator(this, helpModel);
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
        debug(new StringBuffer().append("helpSet in ").append(this).append(helpSet.toString()).toString());
        Hashtable parameters = getParameters();
        if (parameters == null || (parameters != null && !parameters.containsKey("data"))) {
            return new DefaultMutableTreeNode();
        }
        try {
            URL url = new URL(helpSet.getHelpSetURL(), (String) parameters.get("data"));
            debug(new StringBuffer().append("url,hs: ").append(url.toString()).append(PolicyIdFilter.OID_SEPARATOR).append(helpSet.toString()).toString());
            return parse(url, helpSet, helpSet.getLocale(), new DefaultIndexFactory());
        } catch (Exception e) {
            throw new Error(new StringBuffer().append("Trouble getting URL to Index data; ").append(e).toString());
        }
    }

    public static DefaultMutableTreeNode parse(URL url, HelpSet helpSet, Locale locale, TreeItemFactory treeItemFactory) {
        DefaultMutableTreeNode defaultMutableTreeNode = null;
        try {
            Reader readerCreateReader = XmlReader.createReader(url.openConnection());
            treeItemFactory.parsingStarted(url);
            defaultMutableTreeNode = new IndexParser(treeItemFactory).parse(readerCreateReader, helpSet, locale);
            readerCreateReader.close();
        } catch (Exception e) {
            treeItemFactory.reportMessage(new StringBuffer().append("Exception caught while parsing ").append(url).append(e.toString()).toString(), false);
        }
        return treeItemFactory.parsingEnded(defaultMutableTreeNode);
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/IndexView$DefaultIndexFactory.class */
    public static class DefaultIndexFactory implements TreeItemFactory {
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
            if (str2 == null || (str2.compareTo(IndexView.publicIDString) != 0 && str2.compareTo(IndexView.publicIDString_V2) != 0)) {
                reportMessage(HelpUtilities.getText("index.invalidIndexFormat", str2), false);
            }
        }

        @Override // javax.help.TreeItemFactory
        public void processPI(HelpSet helpSet, String str, String str2) {
        }

        @Override // javax.help.TreeItemFactory
        public TreeItem createItem(String str, Hashtable hashtable, HelpSet helpSet, Locale locale) {
            IndexItem indexItem;
            if (str == null || !str.equals("indexitem")) {
                throw new IllegalArgumentException("tagName");
            }
            String str2 = null;
            String str3 = null;
            String str4 = null;
            String str5 = null;
            String str6 = null;
            String str7 = null;
            if (hashtable != null) {
                str2 = (String) hashtable.get("target");
                str3 = (String) hashtable.get("text");
                str4 = (String) hashtable.get("mergetype");
                str5 = (String) hashtable.get("expand");
                str6 = (String) hashtable.get("presentationtype");
                str7 = (String) hashtable.get("presentationname");
            }
            try {
                indexItem = new IndexItem(Map.ID.create(str2, helpSet), helpSet, locale);
            } catch (BadIDException e) {
                indexItem = new IndexItem();
            }
            if (str3 != null) {
                indexItem.setName(str3);
            }
            if (str4 != null) {
                indexItem.setMergeType(str4);
            }
            if (str5 != null) {
                if (str5.equals("true")) {
                    indexItem.setExpansionType(1);
                } else if (str5.equals("false")) {
                    indexItem.setExpansionType(0);
                }
            }
            if (str6 != null) {
                indexItem.setPresentation(str6);
            }
            if (str7 != null) {
                indexItem.setPresentationName(str7);
            }
            return indexItem;
        }

        @Override // javax.help.TreeItemFactory
        public TreeItem createItem() {
            return new IndexItem();
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

    /* JADX INFO: loaded from: autofirma.jar:javax/help/IndexView$IndexParser.class */
    private static class IndexParser implements ParserListener {
        private HelpSet currentParseHS;
        private Stack nodeStack;
        private Stack itemStack;
        private Stack tagStack;
        private Locale defaultLocale;
        private Locale lastLocale;
        private boolean startedindex;
        private TreeItemFactory factory;

        IndexParser(TreeItemFactory treeItemFactory) {
            this.factory = treeItemFactory;
        }

        synchronized DefaultMutableTreeNode parse(Reader reader, HelpSet helpSet, Locale locale) throws IOException {
            this.nodeStack = new Stack();
            this.itemStack = new Stack();
            this.tagStack = new Stack();
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
            String property;
            TreeItem treeItemCreateItem;
            Locale localeLocaleFromLang = null;
            Tag tag = parserEvent.getTag();
            TagProperties tagProperties = tag.atts;
            if (tagProperties != null) {
                localeLocaleFromLang = HelpUtilities.localeFromLang(tagProperties.getProperty("xml:lang"));
            }
            if (localeLocaleFromLang == null) {
                localeLocaleFromLang = this.lastLocale;
            }
            if (tag.name.equals("indexitem")) {
                if (!this.startedindex) {
                    this.factory.reportMessage(HelpUtilities.getText("index.invalidIndexFormat"), false);
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
                        if (IndexView.warningOfFailures) {
                            String property2 = null;
                            if (tagProperties != null) {
                                property2 = tagProperties.getProperty("target");
                            }
                            System.err.println("Failure in IndexItem Creation; ");
                            System.err.println(new StringBuffer().append("  id: ").append(property2).toString());
                            System.err.println(new StringBuffer().append("  hs: ").append(this.currentParseHS).toString());
                        }
                        treeItemCreateItem = this.factory.createItem();
                    }
                }
                treeItemCreateItem = this.factory.createItem("indexitem", hashtable, this.currentParseHS, localeLocaleFromLang);
                if (!this.itemStack.empty()) {
                    IndexItem indexItem = (IndexItem) this.itemStack.peek();
                    if (treeItemCreateItem.getExpansionType() == -1 && indexItem != null && indexItem.getExpansionType() != -1) {
                        treeItemCreateItem.setExpansionType(indexItem.getExpansionType());
                    }
                }
                DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(treeItemCreateItem);
                ((DefaultMutableTreeNode) this.nodeStack.peek()).add(defaultMutableTreeNode);
                if (!tag.isEmpty) {
                    this.itemStack.push(treeItemCreateItem);
                    this.nodeStack.push(defaultMutableTreeNode);
                    addTag(tag, localeLocaleFromLang);
                    return;
                }
                return;
            }
            if (tag.name.equals("index")) {
                if (!tag.isEnd) {
                    if (tagProperties != null && (property = tagProperties.getProperty(XmlConsts.XML_DECL_KW_VERSION)) != null && property.compareTo("1.0") != 0 && property.compareTo("2.0") != 0) {
                        this.factory.reportMessage(HelpUtilities.getText("index.unknownVersion", property), false);
                    }
                    if (this.startedindex) {
                        this.factory.reportMessage(HelpUtilities.getText("index.invalidIndexFormat"), false);
                    }
                    this.startedindex = true;
                    addTag(tag, localeLocaleFromLang);
                    return;
                }
                if (this.startedindex) {
                    this.startedindex = false;
                }
                removeTag(tag);
            }
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void piFound(ParserEvent parserEvent) {
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void doctypeFound(ParserEvent parserEvent) {
            this.factory.processDOCTYPE(parserEvent.getRoot(), parserEvent.getPublicId(), parserEvent.getSystemId());
        }

        @Override // com.sun.java.help.impl.ParserListener
        public void textFound(ParserEvent parserEvent) {
            if (!this.tagStack.empty() && ((LangElement) this.tagStack.peek()).getTag().name.equals("indexitem")) {
                IndexItem indexItem = (IndexItem) this.itemStack.peek();
                String name = indexItem.getName();
                if (name == null) {
                    indexItem.setName(parserEvent.getText().trim());
                } else {
                    indexItem.setName(name.concat(parserEvent.getText()).trim());
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
                if (!((LangElement) this.tagStack.pop()).getTag().name.equals(str)) {
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

    private static void debug(String str) {
    }
}
