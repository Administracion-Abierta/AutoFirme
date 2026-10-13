package javax.help.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Locale;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleValue;
import javax.help.HelpUtilities;
import javax.help.Map;
import javax.help.SearchTOCItem;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicSearchCellRenderer.class */
public class BasicSearchCellRenderer extends JPanel implements TreeCellRenderer {
    protected boolean selected;
    private boolean hasFocus;
    private boolean drawsFocusBorderAroundIcon;
    protected Color textSelectionColor;
    protected Color textNonSelectionColor;
    protected Color backgroundSelectionColor;
    protected Color backgroundNonSelectionColor;
    protected Color borderSelectionColor;
    protected Map map;
    protected ValueJLabel hits;
    protected ValueJLabel quality;
    protected JLabel title;
    private static Icon high = UIManager.getIcon("SearchHigh.icon");
    private static Icon medhigh = UIManager.getIcon("SearchMedHigh.icon");
    private static Icon med = UIManager.getIcon("SearchMed.icon");
    private static Icon medlow = UIManager.getIcon("SearchMedLow.icon");
    private static Icon low = UIManager.getIcon("SearchLow.icon");
    private static boolean debug = false;

    /* JADX INFO: renamed from: javax.help.plaf.basic.BasicSearchCellRenderer$1, reason: invalid class name */
    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicSearchCellRenderer$1.class */
    static class AnonymousClass1 {
    }

    public BasicSearchCellRenderer(Map map) {
        this.map = map;
        setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
        setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
        setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
        setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
        setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
        Object obj = UIManager.get("Tree.drawsFocusBorderAroundIcon");
        this.drawsFocusBorderAroundIcon = obj != null && ((Boolean) obj).booleanValue();
        setLayout(new BoxLayout(this, 0));
        this.hits = new ValueJLabel(this, null);
        this.hits.setHorizontalAlignment(2);
        this.quality = new ValueJLabel(this, null);
        this.title = new JLabel();
        this.title.setFont(getFont());
        this.title.setHorizontalAlignment(2);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(this.quality);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(this.hits);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(this.title);
    }

    public void setTextSelectionColor(Color color) {
        this.textSelectionColor = color;
    }

    public Color getTextSelectionColor() {
        return this.textSelectionColor;
    }

    public void setTextNonSelectionColor(Color color) {
        this.textNonSelectionColor = color;
    }

    public Color getTextNonSelectionColor() {
        return this.textNonSelectionColor;
    }

    public void setBackgroundSelectionColor(Color color) {
        this.backgroundSelectionColor = color;
    }

    public Color getBackgroundSelectionColor() {
        return this.backgroundSelectionColor;
    }

    public void setBackgroundNonSelectionColor(Color color) {
        this.backgroundNonSelectionColor = color;
    }

    public Color getBackgroundNonSelectionColor() {
        return this.backgroundNonSelectionColor;
    }

    public void setBorderSelectionColor(Color color) {
        this.borderSelectionColor = color;
    }

    public Color getBorderSelectionColor() {
        return this.borderSelectionColor;
    }

    public void setFont(Font font) {
        if (font instanceof FontUIResource) {
            font = null;
        }
        if (font != null && this.title != null) {
            this.title.setFont(font);
        }
        super.setFont(font);
    }

    public void setBackground(Color color) {
        if (color instanceof ColorUIResource) {
            color = null;
        }
        super.setBackground(color);
    }

    public Component getTreeCellRendererComponent(JTree jTree, Object obj, boolean z, boolean z2, boolean z3, int i, boolean z4) {
        String string;
        SearchTOCItem searchTOCItem = (SearchTOCItem) ((DefaultMutableTreeNode) obj).getUserObject();
        String name = "";
        if (searchTOCItem != null) {
            name = searchTOCItem.getName();
        }
        if (z) {
            this.hits.setForeground(getTextNonSelectionColor());
            this.title.setForeground(getTextSelectionColor());
            this.title.setBackground(getBackgroundSelectionColor());
        } else {
            Color backgroundNonSelectionColor = getBackgroundNonSelectionColor();
            if (backgroundNonSelectionColor == null) {
                backgroundNonSelectionColor = getBackground();
            }
            this.hits.setForeground(getTextNonSelectionColor());
            this.title.setForeground(getTextNonSelectionColor());
            this.title.setBackground(backgroundNonSelectionColor);
        }
        Locale locale = Locale.getDefault();
        if (searchTOCItem != null) {
            locale = searchTOCItem.getLocale();
            if (locale != null) {
                setLocale(locale);
            }
        }
        if (searchTOCItem != null) {
            int iHitCount = searchTOCItem.hitCount();
            this.hits.setText(String.valueOf(iHitCount));
            this.hits.setAccessibility(null, HelpUtilities.getString(locale, "search.hitDesc"), new Integer(iHitCount), new Integer(iHitCount), new Integer(iHitCount));
            double confidence = searchTOCItem.getConfidence();
            if (confidence < 1.0d) {
                this.quality.setIcon(high);
                string = HelpUtilities.getString(locale, "search.high");
            } else if (confidence < 5.0d) {
                this.quality.setIcon(medhigh);
                string = HelpUtilities.getString(locale, "search.midhigh");
            } else if (confidence < 12.5d) {
                this.quality.setIcon(med);
                string = HelpUtilities.getString(locale, "search.mid");
            } else if (confidence < 25.0d) {
                this.quality.setIcon(medlow);
                string = HelpUtilities.getString(locale, "search.midlow");
            } else {
                this.quality.setIcon(low);
                string = HelpUtilities.getString(locale, "search.low");
            }
            this.quality.setAccessibility(string, HelpUtilities.getString(locale, "search.qualityDesc"), new Double(confidence), new Double(confidence), new Double(confidence));
            this.title.setText(name);
        }
        this.selected = z;
        this.hasFocus = z4;
        return this;
    }

    public void paint(Graphics graphics) {
        Color backgroundNonSelectionColor;
        if (this.selected) {
            backgroundNonSelectionColor = getBackgroundSelectionColor();
        } else {
            backgroundNonSelectionColor = getBackgroundNonSelectionColor();
            if (backgroundNonSelectionColor == null) {
                backgroundNonSelectionColor = getBackground();
            }
        }
        int labelStart = -1;
        if (backgroundNonSelectionColor != null) {
            labelStart = getLabelStart();
            graphics.setColor(backgroundNonSelectionColor);
            Dimension size = getSize();
            graphics.fillRect(labelStart, 0, (size.width - 1) - labelStart, size.height);
        }
        if (this.hasFocus) {
            if (this.drawsFocusBorderAroundIcon) {
                labelStart = 0;
            } else if (labelStart == -1) {
                labelStart = getLabelStart();
            }
            graphics.setColor(getBorderSelectionColor());
            graphics.drawRect(labelStart, 0, (getWidth() - 1) - labelStart, getHeight() - 1);
        }
        super.paintChildren(graphics);
    }

    private int getLabelStart() {
        return this.title.getX() - 1;
    }

    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        int i = 0;
        if (preferredSize != null) {
            i = preferredSize.height;
        }
        Insets insets = getInsets();
        int i2 = 0 + insets.left + insets.right + 4;
        Dimension preferredSize2 = this.quality.getPreferredSize();
        int i3 = i2 + preferredSize2.width;
        int iMax = Math.max(i, preferredSize2.height + insets.top + insets.bottom);
        int i4 = i3 + 4;
        Dimension preferredSize3 = this.hits.getPreferredSize();
        int i5 = i4 + preferredSize3.width;
        int iMax2 = Math.max(iMax, preferredSize3.height + insets.top + insets.bottom);
        int i6 = i5 + 4;
        Dimension preferredSize4 = this.title.getPreferredSize();
        int i7 = i6 + preferredSize4.width;
        int iMax3 = Math.max(iMax2, preferredSize4.height + insets.top + insets.bottom);
        int i8 = i7 + 3;
        if (preferredSize != null) {
            preferredSize = new Dimension(preferredSize.width + 3, preferredSize.height);
        }
        preferredSize.setSize(i8, iMax3);
        return preferredSize;
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicSearchCellRenderer$ValueJLabel.class */
    private class ValueJLabel extends JLabel {
        String accessName;
        String accessDesc;
        Number accessValue;
        Number minValue;
        Number maxValue;
        private final BasicSearchCellRenderer this$0;

        private ValueJLabel(BasicSearchCellRenderer basicSearchCellRenderer) {
            this.this$0 = basicSearchCellRenderer;
        }

        ValueJLabel(BasicSearchCellRenderer basicSearchCellRenderer, AnonymousClass1 anonymousClass1) {
            this(basicSearchCellRenderer);
        }

        public void setAccessibility(String str, String str2, Number number, Number number2, Number number3) {
            this.accessName = str;
            this.accessDesc = str2;
            this.accessValue = number;
            this.minValue = number2;
            this.maxValue = number3;
        }

        public AccessibleContext getAccessibleContext() {
            if (this.accessibleContext == null) {
                this.accessibleContext = new AccessibleValueJLabel(this);
            }
            this.accessibleContext.setAccessibleDescription(this.accessDesc);
            this.accessibleContext.setAccessibleName(this.accessName);
            return this.accessibleContext;
        }

        /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicSearchCellRenderer$ValueJLabel$AccessibleValueJLabel.class */
        protected class AccessibleValueJLabel extends JLabel.AccessibleJLabel implements AccessibleValue {
            private final ValueJLabel this$1;

            protected AccessibleValueJLabel(ValueJLabel valueJLabel) {
                super(valueJLabel);
                this.this$1 = valueJLabel;
            }

            public AccessibleValue getAccessibleValue() {
                return this;
            }

            public Number getCurrentAccessibleValue() {
                return this.this$1.accessValue;
            }

            public boolean setCurrentAccessibleValue(Number number) {
                return false;
            }

            public Number getMinimumAccessibleValue() {
                return this.this$1.minValue;
            }

            public Number getMaximumAccessibleValue() {
                return this.this$1.maxValue;
            }
        }
    }

    private static void debug(String str) {
        if (debug) {
            System.out.println(new StringBuffer().append("BasicSearchCellRenderer: ").append(str).toString());
        }
    }
}
