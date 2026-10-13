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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

/* JADX INFO: loaded from: autofirma.jar:javax/help/FavoritesView.class */
public class FavoritesView extends NavigatorView {
    public static final String publicIDString = "-//Sun Microsystems Inc.//DTD JavaHelp Favorites Version 2.0//EN";
    private static boolean warningOfFailures = false;
    private HelpSet hs;
    private boolean enabledSave;
    private static final boolean debug = false;

    public FavoritesView(HelpSet helpSet, String str, String str2, Hashtable hashtable) {
        super(helpSet, str, str2, helpSet.getLocale(), hashtable);
        this.enabledSave = true;
    }

    public FavoritesView(HelpSet helpSet, String str, String str2, Locale locale, Hashtable hashtable) {
        super(helpSet, str, str2, locale, hashtable);
        this.enabledSave = true;
    }

    @Override // javax.help.NavigatorView
    public Component createNavigator(HelpModel helpModel) {
        return new JHelpFavoritesNavigator(this, helpModel);
    }

    @Override // javax.help.NavigatorView
    public String getMergeType() {
        String mergeType = super.getMergeType();
        if (mergeType == null) {
            return "javax.help.NoMerge";
        }
        return mergeType;
    }

    public FavoritesNode getDataAsTree() {
        HelpSet helpSet = getHelpSet();
        debug(new StringBuffer().append("helpSet in ").append(this).append(helpSet.toString()).toString());
        return parse(helpSet, helpSet.getLocale(), new DefaultFavoritesFactory());
    }

    public FavoritesNode parse(HelpSet helpSet, Locale locale, TreeItemFactory treeItemFactory) {
        FavoritesNode favoritesNode = null;
        URL url = null;
        try {
            File file = new File(new StringBuffer().append(System.getProperty("user.home")).append(File.separator).append(".JavaHelp").append(File.separator).append("Favorites.xml").toString());
            if (!file.exists()) {
                return new FavoritesNode(new FavoritesItem("Favorites"));
            }
            try {
                url = file.toURL();
            } catch (MalformedURLException e) {
                System.err.println(e);
            }
            Reader readerCreateReader = XmlReader.createReader(url.openConnection());
            treeItemFactory.parsingStarted(url);
            favoritesNode = new FavoritesParser(treeItemFactory).parse(readerCreateReader, helpSet, locale);
            readerCreateReader.close();
        } catch (Exception e2) {
            treeItemFactory.reportMessage(new StringBuffer().append("Exception caught while parsing ").append(url).append(e2.toString()).toString(), false);
        }
        return (FavoritesNode) treeItemFactory.parsingEnded(favoritesNode);
    }

    public void saveFavorites(FavoritesNode favoritesNode) {
        if (!this.enabledSave) {
            return;
        }
        try {
            File file = new File(new StringBuffer().append(System.getProperty("user.home")).append(File.separator).append(".JavaHelp").toString());
            file.mkdirs();
            String string = new StringBuffer().append(file.getPath()).append(File.separator).append("Favorites.xml").toString();
            debug(new StringBuffer().append("new file:").append(string).toString());
            FileOutputStream fileOutputStream = new FileOutputStream(string);
            favoritesNode.export(fileOutputStream);
            fileOutputStream.close();
        } catch (SecurityException e) {
            this.enabledSave = false;
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/FavoritesView$DefaultFavoritesFactory.class */
    public static class DefaultFavoritesFactory implements TreeItemFactory {
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
            if (str2 == null || !str2.equals(FavoritesView.publicIDString)) {
                reportMessage(HelpUtilities.getText("favorites.invalidFavoritesFormat", str2), false);
            }
        }

        @Override // javax.help.TreeItemFactory
        public void processPI(HelpSet helpSet, String str, String str2) {
        }

        @Override // javax.help.TreeItemFactory
        public TreeItem createItem(String str, Hashtable hashtable, HelpSet helpSet, Locale locale) {
            FavoritesItem favoritesItem;
            if (str == null || !str.equals(FavoritesNode.ELEMENT)) {
                throw new IllegalArgumentException("tagName");
            }
            if (hashtable != null) {
                String str2 = (String) hashtable.get("target");
                FavoritesView.debug(new StringBuffer().append("target:").append(str2).toString());
                favoritesItem = new FavoritesItem((String) hashtable.get("text"), str2, (String) hashtable.get("url"), (String) hashtable.get("hstitle"), locale);
                if (favoritesItem.getTarget() == null && favoritesItem.getURLSpec() == null) {
                    favoritesItem.setAsFolder();
                }
            } else {
                favoritesItem = new FavoritesItem();
            }
            return favoritesItem;
        }

        @Override // javax.help.TreeItemFactory
        public TreeItem createItem() {
            FavoritesView.debug("empty item created");
            return new FavoritesItem();
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

    /* JADX INFO: loaded from: autofirma.jar:javax/help/FavoritesView$FavoritesParser.class */
    private static class FavoritesParser implements ParserListener {
        private HelpSet currentParseHS;
        private Stack nodeStack;
        private Stack itemStack;
        private Stack tagStack;
        private Locale defaultLocale;
        private Locale lastLocale;
        private boolean startedfavorites;
        private TreeItemFactory factory;

        FavoritesParser(TreeItemFactory treeItemFactory) {
            this.factory = treeItemFactory;
        }

        synchronized FavoritesNode parse(Reader reader, HelpSet helpSet, Locale locale) throws IOException {
            this.nodeStack = new Stack();
            this.itemStack = new Stack();
            this.tagStack = new Stack();
            if (locale == null) {
                this.defaultLocale = Locale.getDefault();
            } else {
                this.defaultLocale = locale;
            }
            this.lastLocale = this.defaultLocale;
            FavoritesNode favoritesNode = new FavoritesNode(new FavoritesItem("Favorites"));
            this.nodeStack.push(favoritesNode);
            this.currentParseHS = helpSet;
            Parser parser = new Parser(reader);
            parser.addParserListener(this);
            parser.parse();
            return favoritesNode;
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
            if (tag.name.equals(FavoritesNode.ELEMENT)) {
                if (!this.startedfavorites) {
                    this.factory.reportMessage(HelpUtilities.getText("favorites.invalidFavoritesFormat"), false);
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
                        if (FavoritesView.warningOfFailures) {
                            String property2 = null;
                            if (tagProperties != null) {
                                property2 = tagProperties.getProperty("target");
                            }
                            System.err.println("Failure in FavoritesItem Creation; ");
                            System.err.println(new StringBuffer().append("  id: ").append(property2).toString());
                            System.err.println(new StringBuffer().append("  hs: ").append(this.currentParseHS).toString());
                            e.printStackTrace();
                        }
                        FavoritesView.debug("empty item !");
                        treeItemCreateItem = this.factory.createItem();
                    }
                }
                treeItemCreateItem = this.factory.createItem(FavoritesNode.ELEMENT, hashtable, this.currentParseHS, localeLocaleFromLang);
                FavoritesNode favoritesNode = new FavoritesNode((FavoritesItem) treeItemCreateItem);
                ((FavoritesNode) this.nodeStack.peek()).add(favoritesNode);
                if (!tag.isEmpty) {
                    this.itemStack.push(treeItemCreateItem);
                    this.nodeStack.push(favoritesNode);
                    addTag(tag, localeLocaleFromLang);
                    return;
                }
                return;
            }
            if (tag.name.equals("favorites")) {
                if (!tag.isEnd) {
                    if (tagProperties != null && (property = tagProperties.getProperty(XmlConsts.XML_DECL_KW_VERSION)) != null && property.compareTo("2.0") != 0) {
                        this.factory.reportMessage(HelpUtilities.getText("favorites.unknownVersion", property), false);
                    }
                    if (this.startedfavorites) {
                        this.factory.reportMessage(HelpUtilities.getText("favorites.invalidFavoritesFormat"), false);
                    }
                    this.startedfavorites = true;
                    addTag(tag, localeLocaleFromLang);
                    return;
                }
                if (this.startedfavorites) {
                    this.startedfavorites = false;
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
            if (!this.tagStack.empty() && ((LangElement) this.tagStack.peek()).getTag().name.equals(FavoritesNode.ELEMENT)) {
                FavoritesItem favoritesItem = (FavoritesItem) this.itemStack.peek();
                String name = favoritesItem.getName();
                if (name == null) {
                    favoritesItem.setName(parserEvent.getText().trim());
                } else {
                    favoritesItem.setName(name.concat(parserEvent.getText()).trim());
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

    /* JADX INFO: Access modifiers changed from: private */
    public static void debug(String str) {
    }
}
