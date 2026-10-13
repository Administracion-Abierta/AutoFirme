package com.sun.java.help.impl;

import com.aowagie.text.ElementTags;
import com.aowagie.text.html.Markup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;
import javax.help.HelpSet;
import javax.help.JHelpContentViewer;
import javax.help.Map;
import javax.help.Popup;
import javax.help.SecondaryWindow;
import javax.help.TextHelpModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/JHSecondaryViewer.class */
public class JHSecondaryViewer extends JButton implements ActionListener, ViewAwareComponent {
    private View myView;
    private HelpSet hs;
    private SimpleAttributeSet textAttribs;
    private URL base;
    private HTMLDocument doc;
    private static final String buttonPropertyPrefix = "Button.";
    private static final String editorPropertyPrefix = "EditorPane.";
    private static Component container;
    private Cursor origCursor;
    private Icon viewerIcon;
    private Map.ID ident;
    private static final boolean debug = false;
    public static String POPUP = "javax.help.Popup";
    public static String SECONDARY_WINDOW = "javax.help.SecondaryWindow";
    public static String LINK_BUTTON = "javax.help.LinkButton";
    public static String LINK_LABEL = "javax.help.LinkLabel";
    private static final Cursor handCursor = Cursor.getPredefinedCursor(12);
    private int viewerHeight = 0;
    private int viewerWidth = 0;
    private int viewerX = 0;
    private int viewerY = 0;
    private String viewerName = "";
    private int viewerActivator = 0;
    private int viewerStyle = 0;
    private String content = "";

    public JHSecondaryViewer() {
        setText(">");
        setMargin(new Insets(0, 0, 0, 0));
        createLinkButton();
        addActionListener(this);
        this.origCursor = getCursor();
        addMouseListener(new MouseListener(this) { // from class: com.sun.java.help.impl.JHSecondaryViewer.1
            private final JHSecondaryViewer this$0;

            public void mouseClicked(MouseEvent mouseEvent) {
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                this.this$0.setCursor(JHSecondaryViewer.handCursor);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                this.this$0.setCursor(this.this$0.origCursor);
            }

            public void mousePressed(MouseEvent mouseEvent) {
            }

            {
                this.this$0 = this;
            }

            public void mouseReleased(MouseEvent mouseEvent) {
            }
        });
    }

    @Override // com.sun.java.help.impl.ViewAwareComponent
    public void setViewData(View view) {
        Container container2;
        TextHelpModel model;
        this.myView = view;
        this.doc = this.myView.getDocument();
        this.base = this.doc.getBase();
        Font font = getFont();
        this.textAttribs = new SimpleAttributeSet();
        this.textAttribs.removeAttribute(StyleConstants.FontSize);
        this.textAttribs.removeAttribute(StyleConstants.Bold);
        this.textAttribs.removeAttribute(StyleConstants.Italic);
        this.textAttribs.addAttribute(StyleConstants.FontFamily, font.getName());
        this.textAttribs.addAttribute(StyleConstants.FontSize, new Integer(font.getSize()));
        this.textAttribs.addAttribute(StyleConstants.Bold, new Boolean(font.isBold()));
        this.textAttribs.addAttribute(StyleConstants.Italic, new Boolean(font.isItalic()));
        Container container3 = this.myView.getContainer();
        container = container3;
        while (true) {
            container2 = container3;
            if (container2 == null || (container2 instanceof JHelpContentViewer)) {
                break;
            } else {
                container3 = container2.getParent();
            }
        }
        if (container2 != null && (model = ((JHelpContentViewer) container2).getModel()) != null) {
            this.hs = model.getHelpSet();
        }
    }

    public void setContent(String str) {
        debug("setContent");
        this.content = str;
        this.ident = null;
    }

    public String getContent() {
        if (this.ident != null) {
            try {
                return this.hs.getCombinedMap().getURLFromID(this.ident).toExternalForm();
            } catch (Exception e) {
            }
        }
        return this.content;
    }

    public void setId(String str) {
        debug("setID");
        this.ident = Map.ID.create(str, this.hs);
        this.content = "";
    }

    public String getId() {
        return this.ident.id;
    }

    public void setViewerName(String str) {
        debug("setViewerName");
        this.viewerName = str;
    }

    public String getViewerName() {
        return this.viewerName;
    }

    public void setViewerActivator(String str) {
        debug("setViewerActivator");
        if (str.compareTo(LINK_BUTTON) == 0 && this.viewerActivator != 0) {
            this.viewerActivator = 0;
            createLinkButton();
        } else if (str.compareTo(LINK_LABEL) == 0 && this.viewerActivator != 1) {
            this.viewerActivator = 1;
            createLinkLabel();
        }
    }

    public String getViewerActivator() {
        switch (this.viewerActivator) {
            case 0:
                return LINK_BUTTON;
            case 1:
                return LINK_LABEL;
            default:
                return "unknownStyle";
        }
    }

    private void createLinkButton() {
        LookAndFeel.installBorder(this, "Button.border");
        setBorderPainted(true);
        setFocusPainted(true);
        setAlignmentY(0.5f);
        setContentAreaFilled(true);
        setBackground(UIManager.getColor("Button.background"));
        if (this.textAttribs != null && this.textAttribs.isDefined(StyleConstants.Foreground)) {
            setForeground((Color) this.textAttribs.getAttribute(StyleConstants.Foreground));
        } else {
            setForeground(UIManager.getColor("Button.foreground"));
        }
        invalidate();
    }

    private void createLinkLabel() {
        setBorder(new EmptyBorder(1, 1, 1, 1));
        setBorderPainted(false);
        setFocusPainted(false);
        setAlignmentY(getPreferredLabelAlignment());
        setContentAreaFilled(false);
        setBackground(UIManager.getColor("EditorPane.background"));
        if (this.textAttribs != null && this.textAttribs.isDefined(StyleConstants.Foreground)) {
            setForeground((Color) this.textAttribs.getAttribute(StyleConstants.Foreground));
        } else {
            setForeground(Color.blue);
        }
        invalidate();
    }

    private float getPreferredLabelAlignment() {
        Icon icon = getIcon();
        String text = getText();
        FontMetrics fontMetrics = getToolkit().getFontMetrics(getFont());
        Rectangle rectangle = new Rectangle();
        Rectangle rectangle2 = new Rectangle();
        SwingUtilities.layoutCompoundLabel(this, fontMetrics, text, icon, getVerticalAlignment(), getHorizontalAlignment(), getVerticalTextPosition(), getHorizontalTextPosition(), new Rectangle(32767, 32767), rectangle, rectangle2, text == null ? 0 : this.ui.getDefaultTextIconGap(this));
        Rectangle rectangleUnion = rectangle.union(rectangle2);
        Insets insets = getInsets();
        rectangleUnion.height += insets.top + insets.bottom;
        if (rectangleUnion.height % 2 == 0) {
            rectangleUnion.height++;
        }
        return (fontMetrics.getMaxAscent() + insets.top) / rectangleUnion.height;
    }

    public void setViewerStyle(String str) {
        debug("setViewerStyle");
        if (str.compareTo(SECONDARY_WINDOW) == 0) {
            this.viewerStyle = 0;
        } else if (str.compareTo(POPUP) == 0) {
            this.viewerStyle = 1;
        }
    }

    public String getViewerStyle() {
        switch (this.viewerStyle) {
            case 0:
                return SECONDARY_WINDOW;
            case 1:
                return POPUP;
            default:
                return "unknownStyle";
        }
    }

    public void setViewerLocation(String str) {
        debug("setViewerLocation");
        int iIndexOf = str.indexOf(",");
        if (iIndexOf != -1) {
            String strTrim = str.substring(0, iIndexOf).trim();
            String strTrim2 = str.substring(iIndexOf + 1).trim();
            if (strTrim != null && strTrim2 != null) {
                this.viewerX = Integer.parseInt(strTrim);
                this.viewerY = Integer.parseInt(strTrim2);
            }
        }
    }

    public String getViewerLocation() {
        Point location;
        String string = new StringBuffer().append(Integer.toString(this.viewerX)).append(",").append(Integer.toString(this.viewerY)).toString();
        switch (this.viewerStyle) {
            case 0:
                SecondaryWindow presentation = SecondaryWindow.getPresentation(this.viewerName);
                if (presentation != null && (location = presentation.getLocation()) != null) {
                    string = new StringBuffer().append(Integer.toString(location.x)).append(",").append(Integer.toString(location.y)).toString();
                }
                break;
        }
        return string;
    }

    public void setViewerSize(String str) {
        debug("setViewerSize");
        int iIndexOf = str.indexOf(",");
        if (iIndexOf != -1) {
            String strTrim = str.substring(0, iIndexOf).trim();
            String strTrim2 = str.substring(iIndexOf + 1).trim();
            if (strTrim != null && strTrim2 != null) {
                this.viewerWidth = Integer.parseInt(strTrim);
                this.viewerHeight = Integer.parseInt(strTrim2);
            }
        }
    }

    public String getViewerSize() {
        Dimension size;
        String string = "";
        if (this.viewerWidth != 0) {
            string = new StringBuffer().append(Integer.toString(this.viewerWidth)).append(",").append(Integer.toString(this.viewerHeight)).toString();
        }
        switch (this.viewerStyle) {
            case 0:
                SecondaryWindow presentation = SecondaryWindow.getPresentation(this.viewerName);
                if (presentation != null && (size = presentation.getSize()) != null) {
                    string = new StringBuffer().append(Integer.toString(size.width)).append(",").append(Integer.toString(size.height)).toString();
                }
                break;
        }
        return string;
    }

    public void setIconByName(String str) {
        debug("setIconByName");
        try {
            ImageIcon imageIcon = new ImageIcon(new URL(this.base, str));
            if (imageIcon != null) {
                setIcon(imageIcon);
                if (getText().compareTo(">") == 0) {
                    setText("");
                }
            }
        } catch (MalformedURLException e) {
        }
    }

    public void setIconByID(String str) {
        debug("setIconByID");
        try {
            ImageIcon imageIcon = new ImageIcon(this.hs.getCombinedMap().getURLFromID(Map.ID.create(str, this.hs)));
            if (imageIcon != null) {
                setIcon(imageIcon);
                if (getText().compareTo(">") == 0) {
                    setText("");
                }
            }
        } catch (MalformedURLException e) {
        }
    }

    public void setTextFontFamily(String str) {
        this.textAttribs.removeAttribute(StyleConstants.FontFamily);
        this.textAttribs.addAttribute(StyleConstants.FontFamily, str);
        setFont(getAttributeSetFont(this.textAttribs));
        getFont();
    }

    public String getTextFontFamily() {
        return StyleConstants.getFontFamily(this.textAttribs);
    }

    public void setTextFontSize(String str) {
        int pointSize;
        StyleSheet styleSheet = this.doc.getStyleSheet();
        try {
            if (str.equals("xx-small")) {
                pointSize = (int) styleSheet.getPointSize(0);
            } else if (str.equals("x-small")) {
                pointSize = (int) styleSheet.getPointSize(1);
            } else if (str.equals("small")) {
                pointSize = (int) styleSheet.getPointSize(2);
            } else if (str.equals("medium")) {
                pointSize = (int) styleSheet.getPointSize(3);
            } else if (str.equals("large")) {
                pointSize = (int) styleSheet.getPointSize(4);
            } else if (str.equals("x-large")) {
                pointSize = (int) styleSheet.getPointSize(5);
            } else if (str.equals("xx-large")) {
                pointSize = (int) styleSheet.getPointSize(6);
            } else if (str.equals("bigger")) {
                pointSize = (int) styleSheet.getPointSize("+1");
            } else if (str.equals("smaller")) {
                pointSize = (int) styleSheet.getPointSize("-1");
            } else if (str.endsWith("pt")) {
                pointSize = Integer.parseInt(str.substring(0, str.length() - 2));
            } else {
                pointSize = (int) styleSheet.getPointSize(str);
            }
            if (pointSize == 0) {
                return;
            }
            this.textAttribs.removeAttribute(StyleConstants.FontSize);
            this.textAttribs.addAttribute(StyleConstants.FontSize, new Integer(pointSize));
            setFont(getAttributeSetFont(this.textAttribs));
            getFont();
        } catch (NumberFormatException e) {
        }
    }

    public String getTextFontSize() {
        return Integer.toString(StyleConstants.getFontSize(this.textAttribs));
    }

    public void setTextFontWeight(String str) {
        boolean z;
        if (str.compareTo(Markup.CSS_VALUE_BOLD) == 0) {
            z = true;
        } else {
            z = false;
        }
        this.textAttribs.removeAttribute(StyleConstants.Bold);
        this.textAttribs.addAttribute(StyleConstants.Bold, new Boolean(z));
        setFont(getAttributeSetFont(this.textAttribs));
        getFont();
    }

    public String getTextFontWeight() {
        if (StyleConstants.isBold(this.textAttribs)) {
            return Markup.CSS_VALUE_BOLD;
        }
        return "plain";
    }

    public void setTextFontStyle(String str) {
        boolean z;
        if (str.compareTo(Markup.CSS_VALUE_ITALIC) == 0) {
            z = true;
        } else {
            z = false;
        }
        this.textAttribs.removeAttribute(StyleConstants.Italic);
        this.textAttribs.addAttribute(StyleConstants.Italic, new Boolean(z));
        setFont(getAttributeSetFont(this.textAttribs));
        getFont();
    }

    public String getTextFontStyle() {
        if (StyleConstants.isItalic(this.textAttribs)) {
            return Markup.CSS_VALUE_ITALIC;
        }
        return "plain";
    }

    public void setTextColor(String str) {
        Color color = null;
        if (str.compareTo("black") == 0) {
            color = Color.black;
        } else if (str.compareTo(ElementTags.BLUE) == 0) {
            color = Color.blue;
        } else if (str.compareTo("cyan") == 0) {
            color = Color.cyan;
        } else if (str.compareTo("darkGray") == 0) {
            color = Color.darkGray;
        } else if (str.compareTo("gray") == 0) {
            color = Color.gray;
        } else if (str.compareTo(ElementTags.GREEN) == 0) {
            color = Color.green;
        } else if (str.compareTo("lightGray") == 0) {
            color = Color.lightGray;
        } else if (str.compareTo("magenta") == 0) {
            color = Color.magenta;
        } else if (str.compareTo("orange") == 0) {
            color = Color.orange;
        } else if (str.compareTo("pink") == 0) {
            color = Color.pink;
        } else if (str.compareTo(ElementTags.RED) == 0) {
            color = Color.red;
        } else if (str.compareTo("white") == 0) {
            color = Color.white;
        } else if (str.compareTo("yellow") == 0) {
            color = Color.yellow;
        }
        if (color == null) {
            return;
        }
        this.textAttribs.removeAttribute(StyleConstants.Foreground);
        this.textAttribs.addAttribute(StyleConstants.Foreground, color);
        setForeground(color);
    }

    public String getTextColor() {
        return getForeground().toString();
    }

    private Font getAttributeSetFont(AttributeSet attributeSet) {
        int i = 0;
        if (StyleConstants.isBold(attributeSet)) {
            i = 0 | 1;
        }
        if (StyleConstants.isItalic(attributeSet)) {
            i |= 2;
        }
        String fontFamily = StyleConstants.getFontFamily(attributeSet);
        int fontSize = StyleConstants.getFontSize(attributeSet);
        if (StyleConstants.isSuperscript(attributeSet) || StyleConstants.isSubscript(attributeSet)) {
            fontSize -= 2;
        }
        return this.doc.getStyleSheet().getFont(fontFamily, i, fontSize);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        debug("actionPerformed");
        switch (this.viewerStyle) {
            case 0:
                if (this.hs != null) {
                    SecondaryWindow secondaryWindow = (SecondaryWindow) SecondaryWindow.getPresentation(this.hs, this.viewerName);
                    if (this.ident != null) {
                        debug(new StringBuffer().append("ident=").append(this.ident).toString());
                        try {
                            secondaryWindow.setCurrentID(this.ident);
                        } catch (Exception e) {
                            debug("error setting CurrentID");
                        }
                    } else if (this.content != null) {
                        debug(new StringBuffer().append("content=").append(this.content).toString());
                        try {
                            secondaryWindow.setCurrentURL(new URL(this.base, this.content));
                        } catch (Exception e2) {
                            debug("error setting URL");
                        }
                    }
                    if (this.viewerX != -1) {
                        secondaryWindow.setLocation(new Point(this.viewerX, this.viewerY));
                    }
                    if (this.viewerWidth != 0) {
                        secondaryWindow.setSize(new Dimension(this.viewerWidth, this.viewerHeight));
                    }
                    secondaryWindow.setDisplayed(true);
                }
                break;
            case 1:
                if (this.hs != null) {
                    Popup popup = (Popup) Popup.getPresentation(this.hs, this.viewerName);
                    if (this.ident != null) {
                        debug(new StringBuffer().append("ident=").append(this.ident).toString());
                        try {
                            popup.setCurrentID(this.ident);
                        } catch (Exception e3) {
                            debug("error setting CurrentID");
                        }
                    } else if (this.content != null) {
                        debug(new StringBuffer().append("content=").append(this.content).toString());
                        try {
                            popup.setCurrentURL(new URL(this.base, this.content));
                        } catch (Exception e4) {
                            debug("error setting URL");
                        }
                    }
                    if (this.viewerWidth != 0) {
                        popup.setSize(new Dimension(this.viewerWidth, this.viewerHeight));
                    }
                    popup.setInvoker(this);
                    popup.setDisplayed(true);
                }
                break;
            default:
                System.out.println("Unknown viewerStyle");
                break;
        }
    }

    private static void debug(String str) {
    }
}
