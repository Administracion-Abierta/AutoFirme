package javax.help.plaf.basic;

import com.aowagie.text.ElementTags;
import com.sun.jna.platform.win32.WinNT;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.InvalidHelpSetContextException;
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
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Highlighter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.parser.ParserDelegator;

/* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicContentViewerUI.class */
public class BasicContentViewerUI extends HelpContentViewerUI implements HelpModelListener, TextHelpModelListener, HyperlinkListener, PropertyChangeListener, Serializable {
    protected JHelpContentViewer theViewer;
    private static Dimension PREF_SIZE = new Dimension(200, 300);
    private static Dimension MIN_SIZE = new Dimension(80, 80);
    private JEditorPane html;
    private JViewport vp;
    private Hashtable registry;
    private boolean loadingURL;
    private TextHelpModelEvent pendingHighlightsEvent;
    private static final boolean debug = false;
    private static final boolean debug1 = false;

    public static ComponentUI createUI(JComponent jComponent) {
        debug("createUI");
        return new BasicContentViewerUI((JHelpContentViewer) jComponent);
    }

    public BasicContentViewerUI(JHelpContentViewer jHelpContentViewer) {
        debug("createUI - sort of");
    }

    public void setEditorKit(String str, EditorKit editorKit) {
        debug(new StringBuffer().append("setEditorKit(").append(str).append(", ").append(editorKit).append(")").toString());
        if (this.registry == null) {
            this.registry = new Hashtable(3);
        }
        this.registry.put(str, editorKit);
        if (this.html != null) {
            debug(new StringBuffer().append("  type: ").append(str).toString());
            debug(new StringBuffer().append("  kit: ").append(editorKit).toString());
            this.html.setEditorKitForContentType(str, editorKit);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicContentViewerUI$JHEditorPane.class */
    class JHEditorPane extends JEditorPane {
        private Hashtable typeHandlers;
        private final BasicContentViewerUI this$0;

        JHEditorPane(BasicContentViewerUI basicContentViewerUI) {
            this.this$0 = basicContentViewerUI;
        }

        public EditorKit getEditorKitForContentType(String str) {
            if (this.typeHandlers == null) {
                this.typeHandlers = new Hashtable(3);
            }
            EditorKit editorKitForContentType = (EditorKit) this.typeHandlers.get(str);
            if (editorKitForContentType == null) {
                editorKitForContentType = this.this$0.theViewer.createEditorKitForContentType(str);
                if (editorKitForContentType != null) {
                    setEditorKitForContentType(str, editorKitForContentType);
                    this.typeHandlers.put(str, editorKitForContentType);
                }
            }
            if (editorKitForContentType == null) {
                editorKitForContentType = super.getEditorKitForContentType(str);
                if (editorKitForContentType != null) {
                    this.typeHandlers.put(str, editorKitForContentType);
                }
            }
            return editorKitForContentType;
        }
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
        this.html = new JHEditorPane(this);
        this.html.addPropertyChangeListener(this);
        this.html.getAccessibleContext().setAccessibleName(HelpUtilities.getString(HelpUtilities.getLocale(this.html), "access.contentViewer"));
        this.html.setEditable(false);
        this.html.addHyperlinkListener(this);
        if (model != null && (currentURL = model.getCurrentURL()) != null) {
            try {
                this.html.setPage(currentURL);
            } catch (IOException e) {
            }
        }
        Component jScrollPane = new JScrollPane();
        jScrollPane.setBorder(new BevelBorder(1, Color.white, Color.gray));
        this.vp = jScrollPane.getViewport();
        this.vp.add(this.html);
        this.vp.setBackingStoreEnabled(true);
        this.theViewer.add("Center", jScrollPane);
        this.loadingURL = false;
        this.pendingHighlightsEvent = null;
    }

    public void uninstallUI(JComponent jComponent) {
        debug("uninstallUI");
        JHelpContentViewer jHelpContentViewer = (JHelpContentViewer) jComponent;
        jHelpContentViewer.removePropertyChangeListener(this);
        this.html.removePropertyChangeListener(this);
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
        try {
            Highlighter highlighter = this.html.getHighlighter();
            debug("removeAllHighlights");
            highlighter.removeAllHighlights();
            try {
                this.loadingURL = true;
                this.html.setPage(url);
            } catch (Exception e) {
                this.loadingURL = false;
            }
            debug(new StringBuffer().append("html current EditorKit is: ").append(this.html.getEditorKit()).toString());
            debug(new StringBuffer().append("html current ContentType is: ").append(this.html.getContentType()).toString());
        } catch (Exception e2) {
            debug("Exception geneartated");
        }
        debug("done with idChanged");
    }

    private void rebuild() {
        debug("rebuild");
        TextHelpModel model = this.theViewer.getModel();
        if (model == null) {
            debug("rebuild-end: model is null");
            return;
        }
        Highlighter highlighter = this.html.getHighlighter();
        debug("removeAllHighlights");
        highlighter.removeAllHighlights();
        HelpSet helpSet = model.getHelpSet();
        if (this.theViewer.getSynch()) {
            try {
                model.setCurrentID(helpSet.getHomeID(), HelpUtilities.getString(helpSet.getLocale(), "history.homePage"), (JHelpNavigator) null);
                this.html.setPage(model.getCurrentURL());
            } catch (Exception e) {
            }
        }
        debug("rebuild-end");
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        URL page;
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
                Font font = (Font) propertyChangeEvent.getNewValue();
                HTMLEditorKit editorKit = this.html.getEditorKit();
                if (editorKit instanceof HTMLEditorKit) {
                    StringBuffer stringBuffer = new StringBuffer(60);
                    stringBuffer.append("body { font: ");
                    stringBuffer.append(font.getSize()).append("pt ");
                    if (font.isBold()) {
                        stringBuffer.append("bold ");
                    }
                    if (font.isItalic()) {
                        stringBuffer.append("italic ");
                    }
                    stringBuffer.append('\"').append(font.getFamily()).append('\"');
                    stringBuffer.append(" }");
                    String string = stringBuffer.toString();
                    editorKit.getStyleSheet().addRule(string);
                    this.html.getDocument().getStyleSheet().addRule(string);
                    return;
                }
                return;
            }
            if (propertyName.equals("clear")) {
                this.html.setText("");
                return;
            } else {
                if (propertyName.equals("reload") && (page = this.html.getPage()) != null) {
                    try {
                        this.html.setPage(page);
                        return;
                    } catch (IOException e) {
                        return;
                    }
                }
                return;
            }
        }
        if (propertyChangeEvent.getSource() == this.html && propertyChangeEvent.getPropertyName().equals("page")) {
            debug("page finished loading");
            this.loadingURL = false;
            if (this.pendingHighlightsEvent != null) {
                debug("Loading the highlights now");
                highlightsChanged(this.pendingHighlightsEvent);
                this.pendingHighlightsEvent = null;
            }
            this.theViewer.getModel().setDocumentTitle((String) this.html.getDocument().getProperty("title"));
            this.theViewer.firePropertyChange(propertyChangeEvent.getPropertyName(), false, true);
        }
    }

    public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            if (hyperlinkEvent instanceof HTMLFrameHyperlinkEvent) {
                this.html.getDocument().processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent) hyperlinkEvent);
            } else {
                linkActivated(hyperlinkEvent.getURL());
            }
        }
    }

    protected void linkActivated(URL url) {
        debug(new StringBuffer().append("linkActivated - URL=").append(url).toString());
        Cursor cursor = this.html.getCursor();
        this.html.setCursor(Cursor.getPredefinedCursor(3));
        String ref = url.getRef();
        if (ref != null) {
            String file = url.getFile();
            if (file.endsWith("/") || file.endsWith("\\")) {
                URL page = this.html.getPage();
                debug(new StringBuffer().append("current u=").append(page).toString());
                String file2 = page.getFile();
                debug(new StringBuffer().append("file=").append(file2).toString());
                try {
                    url = new URL(page.getProtocol(), page.getHost(), page.getPort(), new StringBuffer().append(file2).append("#").append(ref).toString());
                    debug(new StringBuffer().append("new u=").append(url).toString());
                } catch (MalformedURLException e) {
                    return;
                }
            }
        }
        SwingUtilities.invokeLater(new PageLoader(this, url, cursor));
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicContentViewerUI$PageLoader.class */
    class PageLoader implements Runnable {
        String title = null;
        URL url;
        Cursor cursor;
        private final BasicContentViewerUI this$0;

        PageLoader(BasicContentViewerUI basicContentViewerUI, URL url, Cursor cursor) {
            this.this$0 = basicContentViewerUI;
            this.url = url;
            this.cursor = cursor;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.url == null) {
                this.this$0.html.setCursor(this.cursor);
                RepaintManager.currentManager(this.this$0.html).markCompletelyDirty(this.this$0.html);
                return;
            }
            Document document = this.this$0.html.getDocument();
            try {
                this.this$0.html.setPage(this.url);
                this.this$0.loadingURL = true;
                this.title = (String) this.this$0.html.getDocument().getProperty("title");
                String ref = this.url.getRef();
                if (this.title == null) {
                    this.title = findTitle(this.url);
                }
                if (ref != null) {
                    this.title = new StringBuffer().append(this.title).append("-").append(ref).toString();
                }
                TextHelpModel model = this.this$0.theViewer.getModel();
                model.setDocumentTitle(this.title);
                Map.ID iDFromURL = model.getHelpSet().getCombinedMap().getIDFromURL(this.url);
                if (iDFromURL != null) {
                    try {
                        model.setCurrentID(iDFromURL, this.title, (JHelpNavigator) null);
                    } catch (InvalidHelpSetContextException e) {
                        model.setCurrentURL(this.url, this.title, (JHelpNavigator) null);
                    }
                } else {
                    model.setCurrentURL(this.url, this.title, (JHelpNavigator) null);
                }
            } catch (IOException e2) {
                this.this$0.loadingURL = false;
                this.this$0.html.setDocument(document);
                this.this$0.html.getToolkit().beep();
            } finally {
                this.url = null;
                SwingUtilities.invokeLater(this);
            }
        }

        private String findTitle(URL url) {
            try {
                new ParserDelegator().parse(new InputStreamReader(url.openConnection().getInputStream()), new Callback(this), true);
            } catch (Exception e) {
                System.err.println(e);
            }
            return this.title;
        }

        /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicContentViewerUI$PageLoader$Callback.class */
        class Callback extends HTMLEditorKit.ParserCallback {
            boolean wasTitle = false;
            private final PageLoader this$1;

            Callback(PageLoader pageLoader) {
                this.this$1 = pageLoader;
            }

            public void handleStartTag(HTML.Tag tag, MutableAttributeSet mutableAttributeSet, int i) {
                if (tag.equals(HTML.Tag.TITLE)) {
                    this.wasTitle = true;
                }
            }

            public void handleText(char[] cArr, int i) {
                if (this.wasTitle) {
                    this.this$1.title = new String(cArr);
                    this.wasTitle = false;
                }
            }
        }
    }

    @Override // javax.help.event.TextHelpModelListener
    public void highlightsChanged(TextHelpModelEvent textHelpModelEvent) {
        debug(new StringBuffer().append("highlightsChanged ").append(textHelpModelEvent).toString());
        if (this.loadingURL) {
            debug("Humm. loadingURL wait a little");
            this.pendingHighlightsEvent = textHelpModelEvent;
            return;
        }
        Highlighter highlighter = this.html.getHighlighter();
        debug1("removeAllHighlights");
        highlighter.removeAllHighlights();
        TextHelpModel.Highlight[] highlights = ((TextHelpModel) textHelpModelEvent.getSource()).getHighlights();
        DefaultHighlighter.DefaultHighlightPainter defaultHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(this.html.getSelectionColor());
        for (int i = 0; i < highlights.length; i++) {
            int startOffset = highlights[i].getStartOffset();
            int endOffset = highlights[i].getEndOffset();
            debug(new StringBuffer().append("  highlight: ").append(startOffset).append(", ").append(endOffset).toString());
            try {
                highlighter.addHighlight(startOffset, endOffset, defaultHighlightPainter);
                if (i == 0) {
                    SwingUtilities.invokeLater(new ScrollToPosition(this, this.html, endOffset));
                }
            } catch (BadLocationException e) {
                debug(new StringBuffer().append("badLocationExcetpion thrown - ").append(e).toString());
            }
        }
        RepaintManager.currentManager(this.html).markCompletelyDirty(this.html);
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicContentViewerUI$ScrollToPosition.class */
    private class ScrollToPosition implements Runnable {
        private int pos;
        private JEditorPane html;
        private final BasicContentViewerUI this$0;

        public ScrollToPosition(BasicContentViewerUI basicContentViewerUI, JEditorPane jEditorPane, int i) {
            this.this$0 = basicContentViewerUI;
            this.html = jEditorPane;
            this.pos = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                Rectangle rectangleModelToView = this.html.modelToView(this.pos);
                if (rectangleModelToView != null) {
                    this.html.scrollRectToVisible(rectangleModelToView);
                }
            } catch (BadLocationException e) {
            }
        }
    }

    private static void debug(String str) {
    }

    private static void debug1(String str) {
    }
}
