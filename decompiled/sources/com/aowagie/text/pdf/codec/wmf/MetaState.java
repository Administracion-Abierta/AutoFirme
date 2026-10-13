package com.aowagie.text.pdf.codec.wmf;

import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.PdfContentByte;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/wmf/MetaState.class */
class MetaState {
    static final int TA_RIGHT = 2;
    static final int TA_CENTER = 6;
    static final int TA_BOTTOM = 8;
    static final int TA_BASELINE = 24;
    public static final int OPAQUE = 2;
    static final int ALTERNATE = 1;
    private Stack savedStates;
    private ArrayList MetaObjects;
    private Point currentPoint;
    private MetaPen currentPen;
    private MetaBrush currentBrush;
    private MetaFont currentFont;
    private Color currentBackgroundColor;
    private Color currentTextColor;
    private int backgroundMode;
    private int polyFillMode;
    private int lineJoin;
    private int textAlign;
    private int offsetWx;
    private int offsetWy;
    private int extentWx;
    private int extentWy;
    private float scalingX;
    private float scalingY;

    public MetaState() {
        this.currentBackgroundColor = Color.white;
        this.currentTextColor = Color.black;
        this.backgroundMode = 2;
        this.polyFillMode = 1;
        this.lineJoin = 1;
        this.savedStates = new Stack();
        this.MetaObjects = new ArrayList();
        this.currentPoint = new Point(0, 0);
        this.currentPen = new MetaPen();
        this.currentBrush = new MetaBrush();
        this.currentFont = new MetaFont();
    }

    private MetaState(MetaState state) {
        this.currentBackgroundColor = Color.white;
        this.currentTextColor = Color.black;
        this.backgroundMode = 2;
        this.polyFillMode = 1;
        this.lineJoin = 1;
        setMetaState(state);
    }

    public void setMetaState(MetaState state) {
        this.savedStates = state.savedStates;
        this.MetaObjects = state.MetaObjects;
        this.currentPoint = state.currentPoint;
        this.currentPen = state.currentPen;
        this.currentBrush = state.currentBrush;
        this.currentFont = state.currentFont;
        this.currentBackgroundColor = state.currentBackgroundColor;
        this.currentTextColor = state.currentTextColor;
        this.backgroundMode = state.backgroundMode;
        this.polyFillMode = state.polyFillMode;
        this.textAlign = state.textAlign;
        this.lineJoin = state.lineJoin;
        this.offsetWx = state.offsetWx;
        this.offsetWy = state.offsetWy;
        this.extentWx = state.extentWx;
        this.extentWy = state.extentWy;
        this.scalingX = state.scalingX;
        this.scalingY = state.scalingY;
    }

    public void addMetaObject(MetaObject object) {
        for (int k = 0; k < this.MetaObjects.size(); k++) {
            if (this.MetaObjects.get(k) == null) {
                this.MetaObjects.set(k, object);
                return;
            }
        }
        this.MetaObjects.add(object);
    }

    public void selectMetaObject(int index, PdfContentByte cb) {
        MetaObject obj = (MetaObject) this.MetaObjects.get(index);
        if (obj == null) {
        }
        switch (obj.getType()) {
            case 1:
                this.currentPen = (MetaPen) obj;
                int style = this.currentPen.getStyle();
                if (style != 5) {
                    Color color = this.currentPen.getColor();
                    cb.setColorStroke(color);
                    cb.setLineWidth(Math.abs((this.currentPen.getPenWidth() * this.scalingX) / this.extentWx));
                    switch (style) {
                        case 1:
                            cb.setLineDash(18.0f, 6.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                            break;
                        case 2:
                            cb.setLineDash(3.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                            break;
                        case 3:
                            cb.setLiteral("[9 6 3 6]0 d\n");
                            break;
                        case 4:
                            cb.setLiteral("[9 3 3 3 3 3]0 d\n");
                            break;
                        default:
                            cb.setLineDash(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                            break;
                    }
                }
                break;
            case 2:
                this.currentBrush = (MetaBrush) obj;
                int style2 = this.currentBrush.getStyle();
                if (style2 == 0) {
                    Color color2 = this.currentBrush.getColor();
                    cb.setColorFill(color2);
                } else if (style2 == 2) {
                    Color color3 = this.currentBackgroundColor;
                    cb.setColorFill(color3);
                }
                break;
            case 3:
                this.currentFont = (MetaFont) obj;
                break;
        }
    }

    public void deleteMetaObject(int index) {
        this.MetaObjects.set(index, null);
    }

    public void saveState(PdfContentByte cb) {
        cb.saveState();
        MetaState state = new MetaState(this);
        this.savedStates.push(state);
    }

    public void restoreState(int index, PdfContentByte cb) {
        int pops;
        if (index < 0) {
            pops = Math.min(-index, this.savedStates.size());
        } else {
            pops = Math.max(this.savedStates.size() - index, 0);
        }
        if (pops == 0) {
            return;
        }
        MetaState metaState = null;
        while (true) {
            MetaState state = metaState;
            int i = pops;
            pops--;
            if (i != 0) {
                cb.restoreState();
                metaState = (MetaState) this.savedStates.pop();
            } else {
                setMetaState(state);
                return;
            }
        }
    }

    public void cleanup(PdfContentByte cb) {
        int k = this.savedStates.size();
        while (true) {
            int i = k;
            k--;
            if (i > 0) {
                cb.restoreState();
            } else {
                return;
            }
        }
    }

    public float transformX(int x) {
        return ((x - this.offsetWx) * this.scalingX) / this.extentWx;
    }

    public float transformY(int y) {
        return (1.0f - ((y - this.offsetWy) / this.extentWy)) * this.scalingY;
    }

    public void setScalingX(float scalingX) {
        this.scalingX = scalingX;
    }

    public void setScalingY(float scalingY) {
        this.scalingY = scalingY;
    }

    public void setOffsetWx(int offsetWx) {
        this.offsetWx = offsetWx;
    }

    public void setOffsetWy(int offsetWy) {
        this.offsetWy = offsetWy;
    }

    public void setExtentWx(int extentWx) {
        this.extentWx = extentWx;
    }

    public void setExtentWy(int extentWy) {
        this.extentWy = extentWy;
    }

    public float transformAngle(float angle) {
        float ta = this.scalingY < ColumnText.GLOBAL_SPACE_CHAR_RATIO ? -angle : angle;
        return (float) (this.scalingX < ColumnText.GLOBAL_SPACE_CHAR_RATIO ? 3.141592653589793d - ((double) ta) : ta);
    }

    public void setCurrentPoint(Point p) {
        this.currentPoint = p;
    }

    public Point getCurrentPoint() {
        return this.currentPoint;
    }

    public MetaBrush getCurrentBrush() {
        return this.currentBrush;
    }

    public MetaPen getCurrentPen() {
        return this.currentPen;
    }

    public MetaFont getCurrentFont() {
        return this.currentFont;
    }

    public Color getCurrentBackgroundColor() {
        return this.currentBackgroundColor;
    }

    public void setCurrentBackgroundColor(Color currentBackgroundColor) {
        this.currentBackgroundColor = currentBackgroundColor;
    }

    public Color getCurrentTextColor() {
        return this.currentTextColor;
    }

    public void setCurrentTextColor(Color currentTextColor) {
        this.currentTextColor = currentTextColor;
    }

    public int getBackgroundMode() {
        return this.backgroundMode;
    }

    public void setBackgroundMode(int backgroundMode) {
        this.backgroundMode = backgroundMode;
    }

    public int getTextAlign() {
        return this.textAlign;
    }

    public void setTextAlign(int textAlign) {
        this.textAlign = textAlign;
    }

    public int getPolyFillMode() {
        return this.polyFillMode;
    }

    public void setPolyFillMode(int polyFillMode) {
        this.polyFillMode = polyFillMode;
    }

    public void setLineJoinRectangle(PdfContentByte cb) {
        if (this.lineJoin != 0) {
            this.lineJoin = 0;
            cb.setLineJoin(0);
        }
    }

    public void setLineJoinPolygon(PdfContentByte cb) {
        if (this.lineJoin == 0) {
            this.lineJoin = 1;
            cb.setLineJoin(1);
        }
    }

    public boolean getLineNeutral() {
        return this.lineJoin == 0;
    }
}
