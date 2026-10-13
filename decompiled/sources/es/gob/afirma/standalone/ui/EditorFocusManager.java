package es.gob.afirma.standalone.ui;

import com.sun.jna.platform.win32.WinNT;
import es.gob.afirma.standalone.LookAndFeelManager;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.AbstractList;
import java.util.Vector;
import javax.accessibility.AccessibleHyperlink;
import javax.accessibility.AccessibleHypertext;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/EditorFocusManager.class */
public final class EditorFocusManager extends KeyAdapter implements FocusListener, HyperlinkListener, ComponentListener {
    private final JEditorPane displayPane;
    private final Style linkUnfocusedStyle;
    private final Style linkFocusedStyle;
    private AbstractList<AccessibleHyperlink> hyperLinks;
    private final EditorFocusManagerAction hlAction;
    private int selectedLink = 0;
    private boolean editorFirstShow = true;

    public EditorFocusManager(JEditorPane jEditorPane, EditorFocusManagerAction editorFocusManagerAction) {
        Color color;
        Color color2;
        Color color3;
        this.displayPane = jEditorPane;
        this.hlAction = editorFocusManagerAction;
        Font font = new Font(new JLabel().getFont().getAttributes());
        this.displayPane.getDocument().getStyleSheet().addRule("body { font-family: " + font.getFamily() + "; font-size: " + font.getSize() + "pt; }");
        if (LookAndFeelManager.WINDOWS_HIGH_CONTRAST) {
            Color foreground = new JLabel().getForeground();
            this.displayPane.getDocument().getStyleSheet().addRule("body {color: " + ("rgb(" + foreground.getRed() + ", " + foreground.getGreen() + ", " + foreground.getBlue() + ")") + "; }");
        }
        StyleContext styleContext = new StyleContext();
        this.linkUnfocusedStyle = styleContext.addStyle("linkUnfocused", styleContext.getStyle(PreferencesManager.VALUE_KEYSTORE_DEFAULT));
        StyleConstants.setUnderline(this.linkUnfocusedStyle, true);
        StyleConstants.setForeground(this.linkUnfocusedStyle, Color.BLUE);
        try {
            color = new Color(0, 0, 0, 0);
            color2 = UIManager.getColor("TREE.selectionBackground");
            color3 = UIManager.getColor("TREE.selectionForeground");
        } catch (Throwable th) {
            color = Color.WHITE;
            color2 = Color.WHITE;
            color3 = Color.WHITE;
        }
        StyleConstants.setBackground(this.linkUnfocusedStyle, color != null ? color : Color.WHITE);
        this.linkFocusedStyle = styleContext.addStyle("linkFocused", styleContext.getStyle(PreferencesManager.VALUE_KEYSTORE_DEFAULT));
        StyleConstants.setBackground(this.linkFocusedStyle, color2 != null ? color2 : Color.BLUE);
        StyleConstants.setForeground(this.linkFocusedStyle, color3 != null ? color3 : Color.WHITE);
        refreshHyperlinks();
    }

    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case 10:
            case 32:
                refreshHyperlinks();
                if (this.hlAction != null) {
                    this.hlAction.openHyperLink(new HyperlinkEvent(this, HyperlinkEvent.EventType.ACTIVATED, (URL) this.hyperLinks.get(this.selectedLink).getAccessibleActionObject(0)), this.selectedLink);
                }
                break;
            case 37:
            case 38:
                if (this.hyperLinks.size() > 1) {
                    int startIndex = this.hyperLinks.get(this.selectedLink).getStartIndex();
                    this.displayPane.getDocument().setCharacterAttributes(startIndex, this.hyperLinks.get(this.selectedLink).getEndIndex() - startIndex, this.linkUnfocusedStyle, false);
                    this.selectedLink--;
                    if (this.selectedLink == -1) {
                        this.selectedLink = this.hyperLinks.size() - 1;
                    }
                    int startIndex2 = this.hyperLinks.get(this.selectedLink).getStartIndex();
                    this.displayPane.getDocument().setCharacterAttributes(startIndex2, this.hyperLinks.get(this.selectedLink).getEndIndex() - startIndex2, this.linkFocusedStyle, false);
                }
                break;
            case 39:
            case 40:
                if (this.hyperLinks.size() > 1) {
                    int startIndex3 = this.hyperLinks.get(this.selectedLink).getStartIndex();
                    this.displayPane.getDocument().setCharacterAttributes(startIndex3, this.hyperLinks.get(this.selectedLink).getEndIndex() - startIndex3, this.linkUnfocusedStyle, false);
                    this.selectedLink++;
                    if (this.selectedLink == this.hyperLinks.size()) {
                        this.selectedLink = 0;
                    }
                    int startIndex4 = this.hyperLinks.get(this.selectedLink).getStartIndex();
                    this.displayPane.getDocument().setCharacterAttributes(startIndex4, this.hyperLinks.get(this.selectedLink).getEndIndex() - startIndex4, this.linkFocusedStyle, false);
                }
                break;
        }
    }

    public void focusGained(FocusEvent focusEvent) {
        if (this.hyperLinks.size() > 0) {
            int startIndex = this.hyperLinks.get(this.selectedLink).getStartIndex();
            this.displayPane.getDocument().setCharacterAttributes(startIndex, this.hyperLinks.get(this.selectedLink).getEndIndex() - startIndex, this.linkFocusedStyle, false);
        }
    }

    public void focusLost(FocusEvent focusEvent) {
        if (this.hyperLinks.size() > 0) {
            int startIndex = this.hyperLinks.get(this.selectedLink).getStartIndex();
            this.displayPane.getDocument().setCharacterAttributes(startIndex, this.hyperLinks.get(this.selectedLink).getEndIndex() - startIndex, this.linkUnfocusedStyle, false);
        }
    }

    public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        if (HyperlinkEvent.EventType.ACTIVATED.equals(hyperlinkEvent.getEventType()) && this.hlAction != null) {
            this.hlAction.openHyperLink(hyperlinkEvent, this.selectedLink);
        }
    }

    private static int getBestFontSizeForJOptionPane(int i, int i2, String str, String str2, int i3) {
        String str3 = "body { font-family: " + str2 + "; font-size: %f%pt; }";
        for (int i4 = i3; i4 < 100; i4++) {
            JEditorPane jEditorPane = new JEditorPane("text/html", str);
            jEditorPane.getDocument().getStyleSheet().addRule(str3.replace("%f%", Integer.toString(i4)));
            jEditorPane.setSize(i, WinNT.MAXLONG);
            jEditorPane.setEditable(false);
            JPopupMenu jPopupMenu = new JPopupMenu();
            jPopupMenu.add(new JScrollPane(jEditorPane));
            Dimension preferredSize = jPopupMenu.getPreferredSize();
            jPopupMenu.setPopupSize(Math.min(i, preferredSize.width), preferredSize.height);
            if (jPopupMenu.getPreferredSize().height > i2) {
                return i4 - 1;
            }
        }
        return i3;
    }

    public void componentResized(ComponentEvent componentEvent) {
        if (this.editorFirstShow) {
            Font font = UIManager.getFont("Label.font");
            this.displayPane.getDocument().getStyleSheet().addRule("body { font-family: " + font.getFamily() + "; font-size: " + getBestFontSizeForJOptionPane(this.displayPane.getWidth(), this.displayPane.getHeight(), this.displayPane.getText(), font.getFamily(), font.getSize()) + "pt; }");
            this.editorFirstShow = false;
        }
    }

    public void componentMoved(ComponentEvent componentEvent) {
    }

    public void componentHidden(ComponentEvent componentEvent) {
    }

    public void componentShown(ComponentEvent componentEvent) {
    }

    private void refreshHyperlinks() {
        AccessibleHypertext accessibleText = this.displayPane.getAccessibleContext().getAccessibleText();
        this.hyperLinks = new Vector(accessibleText.getLinkCount());
        for (int i = 0; i < accessibleText.getLinkCount(); i++) {
            this.hyperLinks.add(accessibleText.getLink(i));
        }
    }
}
