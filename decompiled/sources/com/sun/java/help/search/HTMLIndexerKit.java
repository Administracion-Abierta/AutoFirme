package com.sun.java.help.search;

import com.aowagie.text.html.HtmlTags;
import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Stack;
import javax.help.search.ConfigFile;
import javax.help.search.IndexBuilder;
import javax.help.search.IndexerKit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit.class */
public class HTMLIndexerKit extends DefaultIndexerKit {
    private static HTMLEditorKit.Parser defaultParser = null;
    private static HTMLParserCallback defaultCallback = null;
    private static char[] NEWLINE = new char[1];
    private boolean debugFlag = false;

    /* JADX INFO: renamed from: com.sun.java.help.search.HTMLIndexerKit$1, reason: invalid class name */
    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$1.class */
    static class AnonymousClass1 {
    }

    @Override // com.sun.java.help.search.DefaultIndexerKit, javax.help.search.IndexerKit
    public Object clone() {
        return new HTMLIndexerKit();
    }

    @Override // com.sun.java.help.search.DefaultIndexerKit, javax.help.search.IndexerKit
    public String getContentType() {
        return "text/html";
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.swing.text.BadLocationException */
    @Override // com.sun.java.help.search.DefaultIndexerKit, javax.help.search.IndexerKit
    public void parse(Reader reader, String str, boolean z, IndexBuilder indexBuilder, ConfigFile configFile) throws IOException {
        this.builder = indexBuilder;
        this.config = configFile;
        this.file = str;
        this.documentStarted = false;
        HTMLEditorKit.Parser parser = getParser();
        if (parser == null) {
            throw new IOException("Can't load parser");
        }
        if (defaultCallback == null) {
            defaultCallback = getParserCallback(this);
        }
        defaultCallback.initialize();
        try {
            parser.parse(reader, defaultCallback, z);
            try {
                defaultCallback.flush();
                try {
                    storeTitle(defaultCallback.getTitle());
                    endStoreDocument();
                    this.builder = null;
                    this.config = null;
                } catch (Exception e) {
                    throw new IOException("Can't store title");
                }
            } catch (BadLocationException e2) {
                throw new IOException("Can't flush parser");
            }
        } catch (javax.swing.text.ChangedCharSetException e3) {
            throw new ChangedCharSetException(e3.getCharSetSpec(), e3.keyEqualsCharSet());
        }
    }

    protected HTMLEditorKit.Parser getParser() {
        if (defaultParser == null) {
            try {
                defaultParser = (HTMLEditorKit.Parser) Class.forName("javax.swing.text.html.parser.ParserDelegator").newInstance();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        return defaultParser;
    }

    public HTMLParserCallback getParserCallback(IndexerKit indexerKit) {
        return new HTMLParserCallback(this, indexerKit);
    }

    static {
        NEWLINE[0] = '\n';
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback.class */
    public class HTMLParserCallback extends HTMLEditorKit.ParserCallback {
        private IndexerKit kit;
        private String title;
        private String header;
        private boolean firstHeader;
        private int currentPos;
        private boolean receivedEndHTML;
        private boolean insertAfterImplied;
        private boolean inParagraph;
        private boolean impliedP;
        private boolean inPre;
        private boolean inTextArea;
        private boolean inTitle;
        private boolean lastWasNewline;
        private boolean emptyAnchor;
        private boolean inBody;
        private boolean foundInsertTag;
        private boolean inHead;
        private boolean inStyle;
        private boolean inOption;
        private boolean inFirstHeader;
        private boolean startTagType;
        private boolean preservesUnknownTags;
        Hashtable tagMap = new Hashtable(57);
        int inBlock;
        Stack tagStack;
        String defaultLang;
        String lastLang;
        private final HTMLIndexerKit this$0;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$LangElement.class */
        private class LangElement {
            HTML.Tag tag;
            String lang;
            private final HTMLParserCallback this$1;

            public LangElement(HTMLParserCallback hTMLParserCallback, HTML.Tag tag, String str) {
                this.this$1 = hTMLParserCallback;
                this.tag = tag;
                this.lang = str;
            }

            public HTML.Tag getTag() {
                return this.tag;
            }

            public String getLang() {
                return this.lang;
            }
        }

        public HTMLParserCallback(HTMLIndexerKit hTMLIndexerKit, IndexerKit indexerKit) {
            this.this$0 = hTMLIndexerKit;
            this.kit = indexerKit;
            new TagAction(this);
            BlockAction blockAction = new BlockAction(this);
            ParagraphAction paragraphAction = new ParagraphAction(this);
            CharacterAction characterAction = new CharacterAction(this);
            SpecialAction specialAction = new SpecialAction(this);
            FormAction formAction = new FormAction(this);
            HiddenAction hiddenAction = new HiddenAction(this);
            ConvertAction convertAction = new ConvertAction(this);
            this.tagMap.put(HTML.Tag.A, new AnchorAction(this));
            this.tagMap.put(HTML.Tag.ADDRESS, characterAction);
            this.tagMap.put(HTML.Tag.APPLET, hiddenAction);
            this.tagMap.put(HTML.Tag.AREA, new AreaAction(this));
            this.tagMap.put(HTML.Tag.B, characterAction);
            this.tagMap.put(HTML.Tag.BASE, new BaseAction(this));
            this.tagMap.put(HTML.Tag.BASEFONT, characterAction);
            this.tagMap.put(HTML.Tag.BIG, characterAction);
            this.tagMap.put(HTML.Tag.BLOCKQUOTE, blockAction);
            this.tagMap.put(HTML.Tag.BODY, blockAction);
            this.tagMap.put(HTML.Tag.BR, specialAction);
            this.tagMap.put(HTML.Tag.CAPTION, blockAction);
            this.tagMap.put(HTML.Tag.CENTER, blockAction);
            this.tagMap.put(HTML.Tag.CITE, characterAction);
            this.tagMap.put(HTML.Tag.CODE, characterAction);
            this.tagMap.put(HTML.Tag.DD, blockAction);
            this.tagMap.put(HTML.Tag.DFN, characterAction);
            this.tagMap.put(HTML.Tag.DIR, blockAction);
            this.tagMap.put(HTML.Tag.DIV, blockAction);
            this.tagMap.put(HTML.Tag.DL, blockAction);
            this.tagMap.put(HTML.Tag.DT, paragraphAction);
            this.tagMap.put(HTML.Tag.EM, characterAction);
            this.tagMap.put(HTML.Tag.FONT, convertAction);
            this.tagMap.put(HTML.Tag.FORM, new FormTagAction(this, null));
            this.tagMap.put(HTML.Tag.FRAME, specialAction);
            this.tagMap.put(HTML.Tag.FRAMESET, blockAction);
            this.tagMap.put(HTML.Tag.H1, paragraphAction);
            this.tagMap.put(HTML.Tag.H2, paragraphAction);
            this.tagMap.put(HTML.Tag.H3, paragraphAction);
            this.tagMap.put(HTML.Tag.H4, paragraphAction);
            this.tagMap.put(HTML.Tag.H5, paragraphAction);
            this.tagMap.put(HTML.Tag.H6, paragraphAction);
            this.tagMap.put(HTML.Tag.HEAD, new HeadAction(this));
            this.tagMap.put(HTML.Tag.HR, specialAction);
            this.tagMap.put(HTML.Tag.HTML, blockAction);
            this.tagMap.put(HTML.Tag.I, convertAction);
            this.tagMap.put(HTML.Tag.IMG, specialAction);
            this.tagMap.put(HTML.Tag.INPUT, formAction);
            this.tagMap.put(HTML.Tag.ISINDEX, new IsindexAction(this));
            this.tagMap.put(HTML.Tag.KBD, characterAction);
            this.tagMap.put(HTML.Tag.LI, blockAction);
            this.tagMap.put(HTML.Tag.LINK, new LinkAction(this));
            this.tagMap.put(HTML.Tag.MAP, new MapAction(this));
            this.tagMap.put(HTML.Tag.MENU, blockAction);
            this.tagMap.put(HTML.Tag.META, new MetaAction(this));
            HTML.Tag tag = HTML.getTag("NOBR");
            if (tag != null) {
                this.tagMap.put(tag, characterAction);
            }
            this.tagMap.put(HTML.Tag.NOFRAMES, blockAction);
            this.tagMap.put(HTML.Tag.OBJECT, specialAction);
            this.tagMap.put(HTML.Tag.OL, blockAction);
            this.tagMap.put(HTML.Tag.OPTION, formAction);
            this.tagMap.put(HTML.Tag.P, paragraphAction);
            this.tagMap.put(HTML.Tag.PARAM, new ObjectAction(this));
            this.tagMap.put(HTML.Tag.PRE, new PreAction(this));
            this.tagMap.put(HTML.Tag.SAMP, characterAction);
            this.tagMap.put(HTML.Tag.SCRIPT, hiddenAction);
            this.tagMap.put(HTML.Tag.SELECT, formAction);
            this.tagMap.put(HTML.Tag.SMALL, characterAction);
            this.tagMap.put(HTML.Tag.STRIKE, convertAction);
            this.tagMap.put(HTML.Tag.S, characterAction);
            this.tagMap.put(HTML.Tag.STRONG, characterAction);
            this.tagMap.put(HTML.Tag.STYLE, new StyleAction(this));
            this.tagMap.put(HTML.Tag.SUB, convertAction);
            this.tagMap.put(HTML.Tag.SUP, convertAction);
            this.tagMap.put(HTML.Tag.TABLE, blockAction);
            this.tagMap.put(HTML.Tag.TD, blockAction);
            this.tagMap.put(HTML.Tag.TEXTAREA, formAction);
            this.tagMap.put(HTML.Tag.TH, blockAction);
            this.tagMap.put(HTML.Tag.TITLE, new TitleAction(this));
            this.tagMap.put(HTML.Tag.TR, blockAction);
            this.tagMap.put(HTML.Tag.TT, characterAction);
            this.tagMap.put(HTML.Tag.U, convertAction);
            this.tagMap.put(HTML.Tag.UL, blockAction);
            this.tagMap.put(HTML.Tag.VAR, characterAction);
        }

        public void initialize() {
            this.title = null;
            this.header = null;
            this.firstHeader = true;
            this.currentPos = 0;
            this.receivedEndHTML = false;
            this.insertAfterImplied = false;
            this.inParagraph = false;
            this.impliedP = false;
            this.inPre = false;
            this.inTitle = false;
            this.inOption = false;
            this.inFirstHeader = false;
            this.startTagType = false;
            this.emptyAnchor = false;
            this.inBlock = 0;
            this.tagStack = new Stack();
            this.defaultLang = this.kit.getLocale().toString();
            this.lastLang = this.defaultLang;
            this.inTextArea = false;
            this.lastWasNewline = false;
            this.inBody = false;
            this.foundInsertTag = true;
            this.preservesUnknownTags = true;
            this.inHead = false;
            this.inStyle = false;
        }

        public String getTitle() {
            if (this.title == null || this.title.length() < 1) {
                this.title = this.header;
                if (this.title == null || this.title.length() < 1) {
                    this.title = "No Title";
                }
            }
            return this.title;
        }

        public void flush() throws BadLocationException {
        }

        public void handleText(char[] cArr, int i) {
            if (!this.receivedEndHTML && !this.inTextArea) {
                if (this.inPre) {
                    preContent(cArr);
                    return;
                }
                if (this.inTitle) {
                    titleContent(new String(cArr));
                    return;
                }
                if (!this.inOption && !this.inStyle && this.inBlock > 0) {
                    if (this.inFirstHeader) {
                        headerContent(new String(cArr));
                    }
                    if (!this.foundInsertTag && this.insertAfterImplied) {
                        foundInsertTag(false);
                        this.foundInsertTag = true;
                        this.impliedP = true;
                        this.inParagraph = true;
                    }
                    if (cArr.length >= 1) {
                        addContent(cArr, 0, cArr.length);
                    }
                }
            }
        }

        public void handleStartTag(HTML.Tag tag, MutableAttributeSet mutableAttributeSet, int i) {
            if (this.receivedEndHTML) {
                return;
            }
            if (!this.inBody && tag == HTML.Tag.BODY) {
                this.inBody = true;
            }
            TagAction tagAction = (TagAction) this.tagMap.get(tag);
            if (tagAction != null) {
                tagAction.start(tag, mutableAttributeSet);
            }
        }

        public void handleComment(char[] cArr, int i) {
            if (this.receivedEndHTML || this.inStyle || !this.preservesUnknownTags || this.inBlock == 0) {
                return;
            }
            SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
            simpleAttributeSet.addAttribute(HTML.Attribute.COMMENT, new String(cArr));
            addSpecialElement(HTML.Tag.COMMENT, simpleAttributeSet);
            this.this$0.debug(new StringBuffer().append("comment added currentPos=").append(this.currentPos).toString());
        }

        public void handleEndTag(HTML.Tag tag, int i) {
            if (this.receivedEndHTML) {
                return;
            }
            if (tag == HTML.Tag.HTML) {
                this.receivedEndHTML = true;
            }
            if (tag == HTML.Tag.BODY) {
                this.inBody = false;
            }
            TagAction tagAction = (TagAction) this.tagMap.get(tag);
            if (tagAction != null) {
                tagAction.end(tag);
            }
        }

        public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet mutableAttributeSet, int i) {
            if (this.receivedEndHTML) {
                return;
            }
            TagAction tagAction = (TagAction) this.tagMap.get(tag);
            if (tagAction != null) {
                tagAction.start(tag, mutableAttributeSet);
                tagAction.end(tag);
            } else if (this.preservesUnknownTags) {
                addSpecialElement(tag, mutableAttributeSet);
            }
        }

        protected void registerTag(HTML.Tag tag, TagAction tagAction) {
            this.tagMap.put(tag, tagAction);
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$TagAction.class */
        public class TagAction {
            private final HTMLParserCallback this$1;

            public TagAction(HTMLParserCallback hTMLParserCallback) {
                this.this$1 = hTMLParserCallback;
            }

            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                String str = (String) mutableAttributeSet.getAttribute(HTML.Attribute.LANG);
                if (str == null) {
                    str = this.this$1.lastLang;
                }
                this.this$1.addTag(tag, str);
            }

            public void end(HTML.Tag tag) {
                this.this$1.removeTag(tag);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$BlockAction.class */
        public class BlockAction extends TagAction {
            private final HTMLParserCallback this$1;

            public BlockAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                this.this$1.blockOpen(tag, mutableAttributeSet);
                String str = (String) mutableAttributeSet.getAttribute(HTML.Attribute.LANG);
                if (str == null) {
                    str = this.this$1.lastLang;
                }
                this.this$1.addTag(tag, str);
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void end(HTML.Tag tag) {
                this.this$1.blockClose(tag);
                this.this$1.removeTag(tag);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$FormTagAction.class */
        private class FormTagAction extends BlockAction {
            private final HTMLParserCallback this$1;

            private FormTagAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            FormTagAction(HTMLParserCallback hTMLParserCallback, AnonymousClass1 anonymousClass1) {
                this(hTMLParserCallback);
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.BlockAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                super.start(tag, mutableAttributeSet);
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.BlockAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void end(HTML.Tag tag) {
                super.end(tag);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$ParagraphAction.class */
        public class ParagraphAction extends BlockAction {
            private final HTMLParserCallback this$1;

            public ParagraphAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.BlockAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                if (this.this$1.firstHeader && (tag == HTML.Tag.H1 || tag == HTML.Tag.H2 || tag == HTML.Tag.H3 || tag == HTML.Tag.H4 || tag == HTML.Tag.H5 || tag == HTML.Tag.H6)) {
                    this.this$1.inFirstHeader = true;
                }
                super.start(tag, mutableAttributeSet);
                this.this$1.inParagraph = true;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.BlockAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void end(HTML.Tag tag) {
                if (this.this$1.firstHeader && (tag == HTML.Tag.H1 || tag == HTML.Tag.H2 || tag == HTML.Tag.H3 || tag == HTML.Tag.H4 || tag == HTML.Tag.H5 || tag == HTML.Tag.H6)) {
                    this.this$1.inFirstHeader = false;
                    this.this$1.firstHeader = false;
                }
                super.end(tag);
                this.this$1.inParagraph = false;
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$SpecialAction.class */
        public class SpecialAction extends TagAction {
            private final HTMLParserCallback this$1;

            public SpecialAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                String str = (String) mutableAttributeSet.getAttribute(HTML.Attribute.LANG);
                if (str == null) {
                    str = this.this$1.lastLang;
                }
                this.this$1.addTag(tag, str);
                this.this$1.addSpecialElement(tag, mutableAttributeSet);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$IsindexAction.class */
        public class IsindexAction extends TagAction {
            private final HTMLParserCallback this$1;

            public IsindexAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                this.this$1.blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
                this.this$1.addSpecialElement(tag, mutableAttributeSet);
                this.this$1.blockClose(HTML.Tag.IMPLIED);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$HiddenAction.class */
        public class HiddenAction extends TagAction {
            private final HTMLParserCallback this$1;

            public HiddenAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                String str = (String) mutableAttributeSet.getAttribute(HTML.Attribute.LANG);
                if (str == null) {
                    str = this.this$1.lastLang;
                }
                this.this$1.addTag(tag, str);
                this.this$1.addSpecialElement(tag, mutableAttributeSet);
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void end(HTML.Tag tag) {
                if (!isEmpty(tag)) {
                    MutableAttributeSet simpleAttributeSet = new SimpleAttributeSet();
                    simpleAttributeSet.addAttribute(HTML.Attribute.ENDTAG, "true");
                    this.this$1.addSpecialElement(tag, simpleAttributeSet);
                }
                this.this$1.removeTag(tag);
            }

            private boolean isEmpty(HTML.Tag tag) {
                if (tag == HTML.Tag.APPLET || tag == HTML.Tag.TITLE || tag == HTML.Tag.SCRIPT) {
                    return false;
                }
                return true;
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$MetaAction.class */
        class MetaAction extends HiddenAction {
            private final HTMLParserCallback this$1;

            MetaAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.HiddenAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                super.start(tag, mutableAttributeSet);
            }

            private boolean isEmpty(HTML.Tag tag) {
                return true;
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$HeadAction.class */
        class HeadAction extends BlockAction {
            private final HTMLParserCallback this$1;

            HeadAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.BlockAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                this.this$1.inHead = true;
                super.start(tag, mutableAttributeSet);
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.BlockAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void end(HTML.Tag tag) {
                this.this$1.inHead = this.this$1.inStyle = false;
                super.end(tag);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$LinkAction.class */
        class LinkAction extends HiddenAction {
            private final HTMLParserCallback this$1;

            LinkAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.HiddenAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                super.start(tag, mutableAttributeSet);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$MapAction.class */
        class MapAction extends TagAction {
            private final HTMLParserCallback this$1;

            MapAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$AreaAction.class */
        class AreaAction extends TagAction {
            private final HTMLParserCallback this$1;

            AreaAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$StyleAction.class */
        class StyleAction extends TagAction {
            private final HTMLParserCallback this$1;

            StyleAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                if (this.this$1.inHead) {
                    this.this$1.inStyle = true;
                }
                super.start(tag, mutableAttributeSet);
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void end(HTML.Tag tag) {
                this.this$1.inStyle = false;
                super.end(tag);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$PreAction.class */
        public class PreAction extends BlockAction {
            private final HTMLParserCallback this$1;

            public PreAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.BlockAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                this.this$1.inPre = true;
                this.this$1.blockOpen(tag, mutableAttributeSet);
                mutableAttributeSet.addAttribute(CSS.Attribute.WHITE_SPACE, HtmlTags.PRE);
                this.this$1.blockOpen(HTML.Tag.IMPLIED, mutableAttributeSet);
                String str = (String) mutableAttributeSet.getAttribute(HTML.Attribute.LANG);
                if (str == null) {
                    str = this.this$1.lastLang;
                }
                this.this$1.addTag(tag, str);
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.BlockAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void end(HTML.Tag tag) {
                this.this$1.blockClose(HTML.Tag.IMPLIED);
                this.this$1.inPre = false;
                this.this$1.blockClose(tag);
                this.this$1.removeTag(tag);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$CharacterAction.class */
        public class CharacterAction extends TagAction {
            private final HTMLParserCallback this$1;

            public CharacterAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                String str = (String) mutableAttributeSet.getAttribute(HTML.Attribute.LANG);
                if (str == null) {
                    str = this.this$1.lastLang;
                }
                this.this$1.addTag(tag, str);
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void end(HTML.Tag tag) {
                this.this$1.removeTag(tag);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$ConvertAction.class */
        class ConvertAction extends TagAction {
            private final HTMLParserCallback this$1;

            ConvertAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$AnchorAction.class */
        class AnchorAction extends CharacterAction {
            private boolean post4207472;
            private final HTMLParserCallback this$1;

            AnchorAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
                this.post4207472 = isPost4207472();
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.CharacterAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                this.this$1.emptyAnchor = true;
                super.start(tag, mutableAttributeSet);
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.CharacterAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void end(HTML.Tag tag) {
                if (this.this$1.emptyAnchor) {
                    char[] cArr = new char[1];
                    if (this.post4207472) {
                        cArr[0] = '\n';
                    } else {
                        cArr[0] = ' ';
                    }
                    this.this$1.this$0.debug(new StringBuffer().append("emptyAnchor currentPos=").append(this.this$1.currentPos).toString());
                    this.this$1.addContent(cArr, 0, 1);
                }
                super.end(tag);
            }

            private boolean isPost4207472() {
                try {
                    String property = System.getProperty("java.version");
                    int i = Integer.parseInt(property.substring(2, 3));
                    int i2 = 0;
                    if (property.length() > 6) {
                        i2 = Integer.parseInt(property.substring(6, 8));
                    }
                    if (i > 5) {
                        return true;
                    }
                    if (i == 5 && i2 >= 4) {
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    this.this$1.this$0.debug(new StringBuffer().append("Exception in isPost4207472 : ").append(e).toString());
                    return true;
                }
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$TitleAction.class */
        class TitleAction extends HiddenAction {
            private final HTMLParserCallback this$1;

            TitleAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.HiddenAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                this.this$1.inTitle = true;
                super.start(tag, mutableAttributeSet);
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.HiddenAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void end(HTML.Tag tag) {
                this.this$1.inTitle = false;
                super.end(tag);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$BaseAction.class */
        class BaseAction extends TagAction {
            private final HTMLParserCallback this$1;

            BaseAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$ObjectAction.class */
        class ObjectAction extends SpecialAction {
            private final HTMLParserCallback this$1;

            ObjectAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.SpecialAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                if (tag != HTML.Tag.PARAM) {
                    super.start(tag, mutableAttributeSet);
                }
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void end(HTML.Tag tag) {
                if (tag != HTML.Tag.PARAM) {
                    super.end(tag);
                }
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/HTMLIndexerKit$HTMLParserCallback$FormAction.class */
        public class FormAction extends SpecialAction {
            private final HTMLParserCallback this$1;

            public FormAction(HTMLParserCallback hTMLParserCallback) {
                super(hTMLParserCallback);
                this.this$1 = hTMLParserCallback;
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.SpecialAction, com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void start(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
                if (tag == HTML.Tag.TEXTAREA) {
                    this.this$1.inTextArea = true;
                }
                if (tag != HTML.Tag.OPTION) {
                    super.start(tag, mutableAttributeSet);
                }
            }

            @Override // com.sun.java.help.search.HTMLIndexerKit.HTMLParserCallback.TagAction
            public void end(HTML.Tag tag) {
                if (tag == HTML.Tag.TEXTAREA) {
                    this.this$1.inTextArea = false;
                }
                if (tag != HTML.Tag.OPTION) {
                    super.end(tag);
                }
            }
        }

        protected void titleContent(String str) {
            if (this.title == null) {
                this.title = new String(str);
            } else {
                this.title.concat(str);
            }
        }

        protected void headerContent(String str) {
            if (this.header == null) {
                this.header = new String(str);
            } else {
                this.header.concat(str);
            }
        }

        protected void preContent(char[] cArr) {
            int i = 0;
            for (int i2 = 0; i2 < cArr.length; i2++) {
                if (cArr[i2] == '\n') {
                    this.this$0.debug(new StringBuffer().append("preContent currentPos=").append(this.currentPos).toString());
                    addContent(cArr, i, (i2 - i) + 1);
                    blockClose(HTML.Tag.IMPLIED);
                    SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
                    simpleAttributeSet.addAttribute(CSS.Attribute.WHITE_SPACE, HtmlTags.PRE);
                    blockOpen(HTML.Tag.IMPLIED, simpleAttributeSet);
                    i = i2 + 1;
                }
            }
            if (i < cArr.length) {
                this.this$0.debug(new StringBuffer().append("preContent currentPos=").append(this.currentPos).toString());
                addContent(cArr, i, cArr.length - i);
            }
        }

        protected void blockOpen(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
            this.this$0.debug("blockOpen");
            if (this.impliedP) {
                blockClose(HTML.Tag.IMPLIED);
            }
            this.inBlock++;
            if (!canInsertTag(tag, mutableAttributeSet, true)) {
                return;
            }
            this.startTagType = true;
            this.lastWasNewline = false;
        }

        protected void blockClose(HTML.Tag tag) {
            this.this$0.debug("blockClose");
            this.inBlock--;
            if (!this.foundInsertTag) {
                return;
            }
            if (!this.lastWasNewline) {
                this.this$0.debug(new StringBuffer().append("blockClose adding NEWLINE currentPos=").append(this.currentPos).toString());
                addContent(HTMLIndexerKit.NEWLINE, 0, 1, false);
                this.lastWasNewline = true;
            }
            if (this.impliedP) {
                this.impliedP = false;
                this.inParagraph = false;
                if (tag != HTML.Tag.IMPLIED) {
                    blockClose(HTML.Tag.IMPLIED);
                }
            }
            if (this.startTagType) {
                this.this$0.debug(new StringBuffer().append("blockclose open/close nocontent currentPos=").append(this.currentPos).toString());
                addContent(new char[]{' '}, 0, 1);
            }
            this.startTagType = false;
        }

        protected void addContent(char[] cArr, int i, int i2) {
            addContent(cArr, i, i2, true);
        }

        protected void addContent(char[] cArr, int i, int i2, boolean z) {
            this.this$0.debug("addContent");
            if (!this.foundInsertTag) {
                return;
            }
            if (z && !this.inParagraph && !this.inPre) {
                blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
                this.inParagraph = true;
                this.impliedP = true;
            }
            this.emptyAnchor = false;
            this.this$0.debug(new StringBuffer().append("Pre parseIntoTokens String=").append(new String(cArr, i, i2)).append(" currentPos=").append(this.currentPos).toString());
            this.startTagType = false;
            this.currentPos = this.kit.parseIntoTokens(new String(cArr, i, i2), this.currentPos);
            this.this$0.debug(new StringBuffer().append("Post parseIntoTokens currentPos=").append(this.currentPos).toString());
            if (i2 > 0) {
                this.lastWasNewline = cArr[(i + i2) - 1] == '\n';
            }
        }

        protected void addSpecialElement(HTML.Tag tag, MutableAttributeSet mutableAttributeSet) {
            if (tag != HTML.Tag.FRAME && !this.inParagraph && !this.inPre) {
                blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
                this.inParagraph = true;
                this.impliedP = true;
            }
            if (!canInsertTag(tag, mutableAttributeSet, true)) {
                return;
            }
            this.emptyAnchor = false;
            this.currentPos++;
            this.this$0.debug(new StringBuffer().append("addingSpecialElement tag=").append(tag).append(" currentPos=").append(this.currentPos).toString());
            this.startTagType = false;
            if (tag == HTML.Tag.FRAME) {
                this.lastWasNewline = true;
            }
        }

        private boolean canInsertTag(HTML.Tag tag, AttributeSet attributeSet, boolean z) {
            if (!this.foundInsertTag) {
                foundInsertTag(z);
                return false;
            }
            return true;
        }

        private boolean isInsertTag(HTML.Tag tag) {
            return false;
        }

        private void foundInsertTag(boolean z) {
            this.foundInsertTag = true;
        }

        protected void addTag(HTML.Tag tag, String str) {
            this.tagStack.push(new LangElement(this, tag, str));
            if (this.lastLang.compareTo(str) != 0) {
                this.kit.setLocale(str);
                this.lastLang = str;
            }
        }

        protected void removeTag(HTML.Tag tag) {
            String string = tag.toString();
            String lang = this.defaultLang;
            while (!this.tagStack.empty()) {
                if (((LangElement) this.tagStack.pop()).getTag().toString().compareTo(string) == 0) {
                    if (this.tagStack.empty()) {
                        lang = this.defaultLang;
                        break;
                    } else {
                        lang = ((LangElement) this.tagStack.peek()).getLang();
                        break;
                    }
                }
            }
            if (this.lastLang.compareTo(lang) != 0) {
                this.kit.setLocale(lang);
                this.lastLang = lang;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void debug(String str) {
        if (this.debugFlag) {
            System.err.println(new StringBuffer().append("HTMLIndexerKit: ").append(str).toString());
        }
    }
}
