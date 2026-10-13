package com.aowagie.text;

import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.GrayColor;
import java.awt.Color;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Rectangle.class */
public class Rectangle implements Element {
    public static final int UNDEFINED = -1;
    public static final int TOP = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int NO_BORDER = 0;
    public static final int BOX = 15;
    protected float llx;
    protected float lly;
    protected float urx;
    protected float ury;
    protected int rotation;
    private Color backgroundColor;
    protected int border;
    private boolean useVariableBorders;
    protected float borderWidth;
    private float borderWidthLeft;
    private float borderWidthRight;
    private float borderWidthTop;
    private float borderWidthBottom;
    private Color borderColor;
    private Color borderColorLeft;
    private Color borderColorRight;
    private Color borderColorTop;
    private Color borderColorBottom;

    public Rectangle(float llx, float lly, float urx, float ury) {
        this.rotation = 0;
        this.backgroundColor = null;
        this.border = -1;
        this.useVariableBorders = false;
        this.borderWidth = -1.0f;
        this.borderWidthLeft = -1.0f;
        this.borderWidthRight = -1.0f;
        this.borderWidthTop = -1.0f;
        this.borderWidthBottom = -1.0f;
        this.borderColor = null;
        this.borderColorLeft = null;
        this.borderColorRight = null;
        this.borderColorTop = null;
        this.borderColorBottom = null;
        this.llx = llx;
        this.lly = lly;
        this.urx = urx;
        this.ury = ury;
    }

    public Rectangle(float urx, float ury) {
        this(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, urx, ury);
    }

    public Rectangle(Rectangle rect) {
        this(rect.llx, rect.lly, rect.urx, rect.ury);
        cloneNonPositionParameters(rect);
    }

    @Override // com.aowagie.text.Element
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        } catch (DocumentException e) {
            return false;
        }
    }

    @Override // com.aowagie.text.Element
    public int type() {
        return 30;
    }

    @Override // com.aowagie.text.Element
    public ArrayList getChunks() {
        return new ArrayList();
    }

    @Override // com.aowagie.text.Element
    public boolean isContent() {
        return true;
    }

    @Override // com.aowagie.text.Element
    public boolean isNestable() {
        return false;
    }

    public void setLeft(float llx) {
        this.llx = llx;
    }

    public float getLeft() {
        return this.llx;
    }

    public float getLeft(float margin) {
        return this.llx + margin;
    }

    public void setRight(float urx) {
        this.urx = urx;
    }

    public float getRight() {
        return this.urx;
    }

    public float getRight(float margin) {
        return this.urx - margin;
    }

    public float getWidth() {
        return this.urx - this.llx;
    }

    public void setTop(float ury) {
        this.ury = ury;
    }

    public float getTop() {
        return this.ury;
    }

    public float getTop(float margin) {
        return this.ury - margin;
    }

    public void setBottom(float lly) {
        this.lly = lly;
    }

    public float getBottom() {
        return this.lly;
    }

    public float getBottom(float margin) {
        return this.lly + margin;
    }

    public float getHeight() {
        return this.ury - this.lly;
    }

    public void normalize() {
        if (this.llx > this.urx) {
            float a = this.llx;
            this.llx = this.urx;
            this.urx = a;
        }
        if (this.lly > this.ury) {
            float a2 = this.lly;
            this.lly = this.ury;
            this.ury = a2;
        }
    }

    public int getRotation() {
        return this.rotation;
    }

    public Rectangle rotate() {
        Rectangle rect = new Rectangle(this.lly, this.llx, this.ury, this.urx);
        rect.rotation = this.rotation + 90;
        rect.rotation %= 360;
        return rect;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public float getGrayFill() {
        if (this.backgroundColor instanceof GrayColor) {
            return ((GrayColor) this.backgroundColor).getGray();
        }
        return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    public void setGrayFill(float value) {
        this.backgroundColor = new GrayColor(value);
    }

    public int getBorder() {
        return this.border;
    }

    public boolean hasBorders() {
        switch (this.border) {
            case -1:
            case 0:
                return false;
            default:
                return this.borderWidth > ColumnText.GLOBAL_SPACE_CHAR_RATIO || this.borderWidthLeft > ColumnText.GLOBAL_SPACE_CHAR_RATIO || this.borderWidthRight > ColumnText.GLOBAL_SPACE_CHAR_RATIO || this.borderWidthTop > ColumnText.GLOBAL_SPACE_CHAR_RATIO || this.borderWidthBottom > ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
    }

    public boolean hasBorder(int type) {
        return this.border != -1 && (this.border & type) == type;
    }

    public void setBorder(int border) {
        this.border = border;
    }

    public boolean isUseVariableBorders() {
        return this.useVariableBorders;
    }

    public void setUseVariableBorders(boolean useVariableBorders) {
        this.useVariableBorders = useVariableBorders;
    }

    public void enableBorderSide(int side) {
        if (this.border == -1) {
            this.border = 0;
        }
        this.border |= side;
    }

    public void disableBorderSide(int side) {
        if (this.border == -1) {
            this.border = 0;
        }
        this.border &= side ^ (-1);
    }

    public float getBorderWidth() {
        return this.borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    private float getVariableBorderWidth(float variableWidthValue, int side) {
        if ((this.border & side) != 0) {
            return variableWidthValue != -1.0f ? variableWidthValue : this.borderWidth;
        }
        return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    private void updateBorderBasedOnWidth(float width, int side) {
        this.useVariableBorders = true;
        if (width > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            enableBorderSide(side);
        } else {
            disableBorderSide(side);
        }
    }

    public float getBorderWidthLeft() {
        return getVariableBorderWidth(this.borderWidthLeft, 4);
    }

    public void setBorderWidthLeft(float borderWidthLeft) {
        this.borderWidthLeft = borderWidthLeft;
        updateBorderBasedOnWidth(borderWidthLeft, 4);
    }

    public float getBorderWidthRight() {
        return getVariableBorderWidth(this.borderWidthRight, 8);
    }

    public void setBorderWidthRight(float borderWidthRight) {
        this.borderWidthRight = borderWidthRight;
        updateBorderBasedOnWidth(borderWidthRight, 8);
    }

    public float getBorderWidthTop() {
        return getVariableBorderWidth(this.borderWidthTop, 1);
    }

    public void setBorderWidthTop(float borderWidthTop) {
        this.borderWidthTop = borderWidthTop;
        updateBorderBasedOnWidth(borderWidthTop, 1);
    }

    public float getBorderWidthBottom() {
        return getVariableBorderWidth(this.borderWidthBottom, 2);
    }

    public void setBorderWidthBottom(float borderWidthBottom) {
        this.borderWidthBottom = borderWidthBottom;
        updateBorderBasedOnWidth(borderWidthBottom, 2);
    }

    public Color getBorderColor() {
        return this.borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getBorderColorLeft() {
        if (this.borderColorLeft == null) {
            return this.borderColor;
        }
        return this.borderColorLeft;
    }

    public void setBorderColorLeft(Color borderColorLeft) {
        this.borderColorLeft = borderColorLeft;
    }

    public Color getBorderColorRight() {
        if (this.borderColorRight == null) {
            return this.borderColor;
        }
        return this.borderColorRight;
    }

    public void setBorderColorRight(Color borderColorRight) {
        this.borderColorRight = borderColorRight;
    }

    public Color getBorderColorTop() {
        if (this.borderColorTop == null) {
            return this.borderColor;
        }
        return this.borderColorTop;
    }

    public void setBorderColorTop(Color borderColorTop) {
        this.borderColorTop = borderColorTop;
    }

    public Color getBorderColorBottom() {
        if (this.borderColorBottom == null) {
            return this.borderColor;
        }
        return this.borderColorBottom;
    }

    public void setBorderColorBottom(Color borderColorBottom) {
        this.borderColorBottom = borderColorBottom;
    }

    public Rectangle rectangle(float top, float bottom) {
        Rectangle tmp = new Rectangle(this);
        if (getTop() > top) {
            tmp.setTop(top);
            tmp.disableBorderSide(1);
        }
        if (getBottom() < bottom) {
            tmp.setBottom(bottom);
            tmp.disableBorderSide(2);
        }
        return tmp;
    }

    public void cloneNonPositionParameters(Rectangle rect) {
        this.rotation = rect.rotation;
        this.backgroundColor = rect.backgroundColor;
        this.border = rect.border;
        this.useVariableBorders = rect.useVariableBorders;
        this.borderWidth = rect.borderWidth;
        this.borderWidthLeft = rect.borderWidthLeft;
        this.borderWidthRight = rect.borderWidthRight;
        this.borderWidthTop = rect.borderWidthTop;
        this.borderWidthBottom = rect.borderWidthBottom;
        this.borderColor = rect.borderColor;
        this.borderColorLeft = rect.borderColorLeft;
        this.borderColorRight = rect.borderColorRight;
        this.borderColorTop = rect.borderColorTop;
        this.borderColorBottom = rect.borderColorBottom;
    }

    public void softCloneNonPositionParameters(Rectangle rect) {
        if (rect.rotation != 0) {
            this.rotation = rect.rotation;
        }
        if (rect.backgroundColor != null) {
            this.backgroundColor = rect.backgroundColor;
        }
        if (rect.border != -1) {
            this.border = rect.border;
        }
        if (this.useVariableBorders) {
            this.useVariableBorders = rect.useVariableBorders;
        }
        if (rect.borderWidth != -1.0f) {
            this.borderWidth = rect.borderWidth;
        }
        if (rect.borderWidthLeft != -1.0f) {
            this.borderWidthLeft = rect.borderWidthLeft;
        }
        if (rect.borderWidthRight != -1.0f) {
            this.borderWidthRight = rect.borderWidthRight;
        }
        if (rect.borderWidthTop != -1.0f) {
            this.borderWidthTop = rect.borderWidthTop;
        }
        if (rect.borderWidthBottom != -1.0f) {
            this.borderWidthBottom = rect.borderWidthBottom;
        }
        if (rect.borderColor != null) {
            this.borderColor = rect.borderColor;
        }
        if (rect.borderColorLeft != null) {
            this.borderColorLeft = rect.borderColorLeft;
        }
        if (rect.borderColorRight != null) {
            this.borderColorRight = rect.borderColorRight;
        }
        if (rect.borderColorTop != null) {
            this.borderColorTop = rect.borderColorTop;
        }
        if (rect.borderColorBottom != null) {
            this.borderColorBottom = rect.borderColorBottom;
        }
    }

    @Override // com.aowagie.text.Element
    public String toString() {
        StringBuffer buf = new StringBuffer("Rectangle: ");
        buf.append(getWidth());
        buf.append('x');
        buf.append(getHeight());
        buf.append(" (rot: ");
        buf.append(this.rotation);
        buf.append(" degrees)");
        return buf.toString();
    }
}
