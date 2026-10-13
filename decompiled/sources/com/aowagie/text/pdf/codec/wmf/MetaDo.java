package com.aowagie.text.pdf.codec.wmf;

import com.aowagie.text.DocumentException;
import com.aowagie.text.Image;
import com.aowagie.text.pdf.BaseFont;
import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.PdfContentByte;
import com.aowagie.text.pdf.codec.BmpImage;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import java.awt.Color;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/wmf/MetaDo.class */
public class MetaDo {
    private static final int META_SETBKCOLOR = 513;
    private static final int META_SETBKMODE = 258;
    private static final int META_SETPOLYFILLMODE = 262;
    private static final int META_SETTEXTCOLOR = 521;
    private static final int META_SETWINDOWORG = 523;
    private static final int META_SETWINDOWEXT = 524;
    private static final int META_LINETO = 531;
    private static final int META_MOVETO = 532;
    private static final int META_INTERSECTCLIPRECT = 1046;
    private static final int META_ARC = 2071;
    private static final int META_ELLIPSE = 1048;
    private static final int META_PIE = 2074;
    private static final int META_RECTANGLE = 1051;
    private static final int META_ROUNDRECT = 1564;
    private static final int META_SAVEDC = 30;
    private static final int META_SETPIXEL = 1055;
    private static final int META_TEXTOUT = 1313;
    private static final int META_POLYGON = 804;
    private static final int META_POLYLINE = 805;
    private static final int META_RESTOREDC = 295;
    private static final int META_SELECTOBJECT = 301;
    private static final int META_SETTEXTALIGN = 302;
    private static final int META_CHORD = 2096;
    private static final int META_EXTTEXTOUT = 2610;
    private static final int META_POLYPOLYGON = 1336;
    private static final int META_DIBSTRETCHBLT = 2881;
    private static final int META_DIBCREATEPATTERNBRUSH = 322;
    private static final int META_STRETCHDIB = 3907;
    private static final int META_DELETEOBJECT = 496;
    private static final int META_CREATEPALETTE = 247;
    private static final int META_CREATEPENINDIRECT = 762;
    private static final int META_CREATEFONTINDIRECT = 763;
    private static final int META_CREATEBRUSHINDIRECT = 764;
    private static final int META_CREATEREGION = 1791;
    private final PdfContentByte cb;
    private final InputMeta in;
    private int left;
    private int top;
    private int right;
    private int bottom;
    private int inch;
    private final MetaState state = new MetaState();

    public MetaDo(InputStream in, PdfContentByte cb) {
        this.cb = cb;
        this.in = new InputMeta(in);
    }

    public void readAll() throws IOException, DocumentException {
        String s;
        byte c;
        String s2;
        byte c2;
        if (this.in.readInt() != -1698247209) {
            throw new DocumentException("Not a placeable windows metafile");
        }
        this.in.readWord();
        this.left = this.in.readShort();
        this.top = this.in.readShort();
        this.right = this.in.readShort();
        this.bottom = this.in.readShort();
        this.inch = this.in.readWord();
        this.state.setScalingX(((this.right - this.left) / this.inch) * 72.0f);
        this.state.setScalingY(((this.bottom - this.top) / this.inch) * 72.0f);
        this.state.setOffsetWx(this.left);
        this.state.setOffsetWy(this.top);
        this.state.setExtentWx(this.right - this.left);
        this.state.setExtentWy(this.bottom - this.top);
        this.in.readInt();
        this.in.readWord();
        this.in.skip(18);
        this.cb.setLineCap(1);
        this.cb.setLineJoin(1);
        while (true) {
            int lenMarker = this.in.getLength();
            int tsize = this.in.readInt();
            if (tsize >= 3) {
                int function = this.in.readWord();
                switch (function) {
                    case 30:
                        this.state.saveState(this.cb);
                        break;
                    case META_CREATEPALETTE /* 247 */:
                    case 322:
                    case 1791:
                        this.state.addMetaObject(new MetaObject());
                        break;
                    case 258:
                        this.state.setBackgroundMode(this.in.readWord());
                        break;
                    case 262:
                        this.state.setPolyFillMode(this.in.readWord());
                        break;
                    case META_RESTOREDC /* 295 */:
                        int idx = this.in.readShort();
                        this.state.restoreState(idx, this.cb);
                        break;
                    case 301:
                        int idx2 = this.in.readWord();
                        this.state.selectMetaObject(idx2, this.cb);
                        break;
                    case 302:
                        this.state.setTextAlign(this.in.readWord());
                        break;
                    case META_DELETEOBJECT /* 496 */:
                        int idx3 = this.in.readWord();
                        this.state.deleteMetaObject(idx3);
                        break;
                    case 513:
                        this.state.setCurrentBackgroundColor(this.in.readColor());
                        break;
                    case 521:
                        this.state.setCurrentTextColor(this.in.readColor());
                        break;
                    case META_SETWINDOWORG /* 523 */:
                        this.state.setOffsetWy(this.in.readShort());
                        this.state.setOffsetWx(this.in.readShort());
                        break;
                    case 524:
                        this.state.setExtentWy(this.in.readShort());
                        this.state.setExtentWx(this.in.readShort());
                        break;
                    case 531:
                        int y = this.in.readShort();
                        int x = this.in.readShort();
                        Point p = this.state.getCurrentPoint();
                        this.cb.moveTo(this.state.transformX(p.x), this.state.transformY(p.y));
                        this.cb.lineTo(this.state.transformX(x), this.state.transformY(y));
                        this.cb.stroke();
                        this.state.setCurrentPoint(new Point(x, y));
                        break;
                    case 532:
                        int y2 = this.in.readShort();
                        this.state.setCurrentPoint(new Point(this.in.readShort(), y2));
                        break;
                    case 762:
                        MetaPen pen = new MetaPen();
                        pen.init(this.in);
                        this.state.addMetaObject(pen);
                        break;
                    case 763:
                        MetaFont font = new MetaFont();
                        font.init(this.in);
                        this.state.addMetaObject(font);
                        break;
                    case 764:
                        MetaBrush brush = new MetaBrush();
                        brush.init(this.in);
                        this.state.addMetaObject(brush);
                        break;
                    case META_POLYGON /* 804 */:
                        if (!isNullStrokeFill(false)) {
                            int len = this.in.readWord();
                            int sx = this.in.readShort();
                            int sy = this.in.readShort();
                            this.cb.moveTo(this.state.transformX(sx), this.state.transformY(sy));
                            for (int k = 1; k < len; k++) {
                                int x2 = this.in.readShort();
                                int y3 = this.in.readShort();
                                this.cb.lineTo(this.state.transformX(x2), this.state.transformY(y3));
                            }
                            this.cb.lineTo(this.state.transformX(sx), this.state.transformY(sy));
                            strokeAndFill();
                        }
                        break;
                    case META_POLYLINE /* 805 */:
                        this.state.setLineJoinPolygon(this.cb);
                        int len2 = this.in.readWord();
                        int x3 = this.in.readShort();
                        int y4 = this.in.readShort();
                        this.cb.moveTo(this.state.transformX(x3), this.state.transformY(y4));
                        for (int k2 = 1; k2 < len2; k2++) {
                            int x4 = this.in.readShort();
                            int y5 = this.in.readShort();
                            this.cb.lineTo(this.state.transformX(x4), this.state.transformY(y5));
                        }
                        this.cb.stroke();
                        break;
                    case META_INTERSECTCLIPRECT /* 1046 */:
                        float b = this.state.transformY(this.in.readShort());
                        float r = this.state.transformX(this.in.readShort());
                        float t = this.state.transformY(this.in.readShort());
                        float l = this.state.transformX(this.in.readShort());
                        this.cb.rectangle(l, b, r - l, t - b);
                        this.cb.eoClip();
                        this.cb.newPath();
                        break;
                    case META_ELLIPSE /* 1048 */:
                        if (!isNullStrokeFill(this.state.getLineNeutral())) {
                            int b2 = this.in.readShort();
                            int r2 = this.in.readShort();
                            int t2 = this.in.readShort();
                            this.cb.arc(this.state.transformX(this.in.readShort()), this.state.transformY(b2), this.state.transformX(r2), this.state.transformY(t2), ColumnText.GLOBAL_SPACE_CHAR_RATIO, 360.0f);
                            strokeAndFill();
                        }
                        break;
                    case 1051:
                        if (!isNullStrokeFill(true)) {
                            float b3 = this.state.transformY(this.in.readShort());
                            float r3 = this.state.transformX(this.in.readShort());
                            float t3 = this.state.transformY(this.in.readShort());
                            float l2 = this.state.transformX(this.in.readShort());
                            this.cb.rectangle(l2, b3, r3 - l2, t3 - b3);
                            strokeAndFill();
                        }
                        break;
                    case 1055:
                        Color color = this.in.readColor();
                        int y6 = this.in.readShort();
                        int x5 = this.in.readShort();
                        this.cb.saveState();
                        this.cb.setColorFill(color);
                        this.cb.rectangle(this.state.transformX(x5), this.state.transformY(y6), 0.2f, 0.2f);
                        this.cb.fill();
                        this.cb.restoreState();
                        break;
                    case 1313:
                        int count = this.in.readWord();
                        byte[] text = new byte[count];
                        int k3 = 0;
                        while (k3 < count && (c = (byte) this.in.readByte()) != 0) {
                            text[k3] = c;
                            k3++;
                        }
                        try {
                            s = new String(text, 0, k3, "Cp1252");
                        } catch (UnsupportedEncodingException e) {
                            s = new String(text, 0, k3);
                        }
                        this.in.skip(((count + 1) & JPEG.COM) - k3);
                        int y7 = this.in.readShort();
                        int x6 = this.in.readShort();
                        outputText(x6, y7, 0, 0, 0, 0, 0, s);
                        break;
                    case 1336:
                        if (!isNullStrokeFill(false)) {
                            int numPoly = this.in.readWord();
                            int[] lens = new int[numPoly];
                            for (int k4 = 0; k4 < lens.length; k4++) {
                                lens[k4] = this.in.readWord();
                            }
                            for (int len3 : lens) {
                                int sx2 = this.in.readShort();
                                int sy2 = this.in.readShort();
                                this.cb.moveTo(this.state.transformX(sx2), this.state.transformY(sy2));
                                for (int k5 = 1; k5 < len3; k5++) {
                                    int x7 = this.in.readShort();
                                    int y8 = this.in.readShort();
                                    this.cb.lineTo(this.state.transformX(x7), this.state.transformY(y8));
                                }
                                this.cb.lineTo(this.state.transformX(sx2), this.state.transformY(sy2));
                            }
                            strokeAndFill();
                        }
                        break;
                    case META_ROUNDRECT /* 1564 */:
                        if (!isNullStrokeFill(true)) {
                            float h = this.state.transformY(0) - this.state.transformY(this.in.readShort());
                            float w = this.state.transformX(this.in.readShort()) - this.state.transformX(0);
                            float b4 = this.state.transformY(this.in.readShort());
                            float r4 = this.state.transformX(this.in.readShort());
                            float t4 = this.state.transformY(this.in.readShort());
                            float l3 = this.state.transformX(this.in.readShort());
                            this.cb.roundRectangle(l3, b4, r4 - l3, t4 - b4, (h + w) / 4.0f);
                            strokeAndFill();
                        }
                        break;
                    case META_ARC /* 2071 */:
                        if (!isNullStrokeFill(this.state.getLineNeutral())) {
                            float yend = this.state.transformY(this.in.readShort());
                            float xend = this.state.transformX(this.in.readShort());
                            float ystart = this.state.transformY(this.in.readShort());
                            float xstart = this.state.transformX(this.in.readShort());
                            float b5 = this.state.transformY(this.in.readShort());
                            float r5 = this.state.transformX(this.in.readShort());
                            float t5 = this.state.transformY(this.in.readShort());
                            float l4 = this.state.transformX(this.in.readShort());
                            float cx = (r5 + l4) / 2.0f;
                            float cy = (t5 + b5) / 2.0f;
                            float arc1 = getArc(cx, cy, xstart, ystart);
                            float arc2 = getArc(cx, cy, xend, yend) - arc1;
                            if (arc2 <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                                arc2 += 360.0f;
                            }
                            this.cb.arc(l4, b5, r5, t5, arc1, arc2);
                            this.cb.stroke();
                        }
                        break;
                    case META_PIE /* 2074 */:
                        if (!isNullStrokeFill(this.state.getLineNeutral())) {
                            float yend2 = this.state.transformY(this.in.readShort());
                            float xend2 = this.state.transformX(this.in.readShort());
                            float ystart2 = this.state.transformY(this.in.readShort());
                            float xstart2 = this.state.transformX(this.in.readShort());
                            float b6 = this.state.transformY(this.in.readShort());
                            float r6 = this.state.transformX(this.in.readShort());
                            float t6 = this.state.transformY(this.in.readShort());
                            float l5 = this.state.transformX(this.in.readShort());
                            float cx2 = (r6 + l5) / 2.0f;
                            float cy2 = (t6 + b6) / 2.0f;
                            float arc3 = getArc(cx2, cy2, xstart2, ystart2);
                            float arc4 = getArc(cx2, cy2, xend2, yend2) - arc3;
                            if (arc4 <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                                arc4 += 360.0f;
                            }
                            ArrayList ar = PdfContentByte.bezierArc(l5, b6, r6, t6, arc3, arc4);
                            if (!ar.isEmpty()) {
                                float[] pt = (float[]) ar.get(0);
                                this.cb.moveTo(cx2, cy2);
                                this.cb.lineTo(pt[0], pt[1]);
                                for (int k6 = 0; k6 < ar.size(); k6++) {
                                    float[] pt2 = (float[]) ar.get(k6);
                                    this.cb.curveTo(pt2[2], pt2[3], pt2[4], pt2[5], pt2[6], pt2[7]);
                                }
                                this.cb.lineTo(cx2, cy2);
                                strokeAndFill();
                            }
                        }
                        break;
                    case META_CHORD /* 2096 */:
                        if (!isNullStrokeFill(this.state.getLineNeutral())) {
                            float yend3 = this.state.transformY(this.in.readShort());
                            float xend3 = this.state.transformX(this.in.readShort());
                            float ystart3 = this.state.transformY(this.in.readShort());
                            float xstart3 = this.state.transformX(this.in.readShort());
                            float b7 = this.state.transformY(this.in.readShort());
                            float r7 = this.state.transformX(this.in.readShort());
                            float t7 = this.state.transformY(this.in.readShort());
                            float l6 = this.state.transformX(this.in.readShort());
                            float cx3 = (r7 + l6) / 2.0f;
                            float cy3 = (t7 + b7) / 2.0f;
                            float arc5 = getArc(cx3, cy3, xstart3, ystart3);
                            float arc6 = getArc(cx3, cy3, xend3, yend3) - arc5;
                            if (arc6 <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                                arc6 += 360.0f;
                            }
                            ArrayList ar2 = PdfContentByte.bezierArc(l6, b7, r7, t7, arc5, arc6);
                            if (!ar2.isEmpty()) {
                                float[] pt3 = (float[]) ar2.get(0);
                                float cx4 = pt3[0];
                                float cy4 = pt3[1];
                                this.cb.moveTo(cx4, cy4);
                                for (int k7 = 0; k7 < ar2.size(); k7++) {
                                    float[] pt4 = (float[]) ar2.get(k7);
                                    this.cb.curveTo(pt4[2], pt4[3], pt4[4], pt4[5], pt4[6], pt4[7]);
                                }
                                this.cb.lineTo(cx4, cy4);
                                strokeAndFill();
                            }
                        }
                        break;
                    case 2610:
                        int y9 = this.in.readShort();
                        int x8 = this.in.readShort();
                        int count2 = this.in.readWord();
                        int flag = this.in.readWord();
                        int x1 = 0;
                        int y1 = 0;
                        int x9 = 0;
                        int y10 = 0;
                        if ((flag & 6) != 0) {
                            x1 = this.in.readShort();
                            y1 = this.in.readShort();
                            x9 = this.in.readShort();
                            y10 = this.in.readShort();
                        }
                        byte[] text2 = new byte[count2];
                        int k8 = 0;
                        while (k8 < count2 && (c2 = (byte) this.in.readByte()) != 0) {
                            text2[k8] = c2;
                            k8++;
                        }
                        try {
                            s2 = new String(text2, 0, k8, "Cp1252");
                        } catch (UnsupportedEncodingException e2) {
                            s2 = new String(text2, 0, k8);
                        }
                        outputText(x8, y9, flag, x1, y1, x9, y10, s2);
                        break;
                    case META_DIBSTRETCHBLT /* 2881 */:
                    case META_STRETCHDIB /* 3907 */:
                        this.in.readInt();
                        if (function == META_STRETCHDIB) {
                            this.in.readWord();
                        }
                        int srcHeight = this.in.readShort();
                        int srcWidth = this.in.readShort();
                        int ySrc = this.in.readShort();
                        int xSrc = this.in.readShort();
                        float destHeight = this.state.transformY(this.in.readShort()) - this.state.transformY(0);
                        float destWidth = this.state.transformX(this.in.readShort()) - this.state.transformX(0);
                        float yDest = this.state.transformY(this.in.readShort());
                        float xDest = this.state.transformX(this.in.readShort());
                        byte[] b8 = new byte[(tsize * 2) - (this.in.getLength() - lenMarker)];
                        for (int k9 = 0; k9 < b8.length; k9++) {
                            b8[k9] = (byte) this.in.readByte();
                        }
                        try {
                            ByteArrayInputStream inb = new ByteArrayInputStream(b8);
                            Image bmp = BmpImage.getImage(inb, true, b8.length);
                            this.cb.saveState();
                            this.cb.rectangle(xDest, yDest, destWidth, destHeight);
                            this.cb.clip();
                            this.cb.newPath();
                            bmp.scaleAbsolute((destWidth * bmp.getWidth()) / srcWidth, ((-destHeight) * bmp.getHeight()) / srcHeight);
                            bmp.setAbsolutePosition(xDest - ((destWidth * xSrc) / srcWidth), (yDest + ((destHeight * ySrc) / srcHeight)) - bmp.getScaledHeight());
                            this.cb.addImage(bmp);
                            this.cb.restoreState();
                        } catch (Exception e3) {
                        }
                        break;
                }
                this.in.skip((tsize * 2) - (this.in.getLength() - lenMarker));
            } else {
                this.state.cleanup(this.cb);
                return;
            }
        }
    }

    private void outputText(int x, int y, int flag, int x1, int y1, int x2, int y2, String text) {
        float ty;
        MetaFont font = this.state.getCurrentFont();
        float refX = this.state.transformX(x);
        float refY = this.state.transformY(y);
        float angle = this.state.transformAngle(font.getAngle());
        float sin = (float) Math.sin(angle);
        float cos = (float) Math.cos(angle);
        float fontSize = font.getFontSize(this.state);
        BaseFont bf = font.getFont();
        int align = this.state.getTextAlign();
        float textWidth = bf.getWidthPoint(text, fontSize);
        float tx = 0.0f;
        float descender = bf.getFontDescriptor(3, fontSize);
        float ury = bf.getFontDescriptor(8, fontSize);
        this.cb.saveState();
        this.cb.concatCTM(cos, sin, -sin, cos, refX, refY);
        if ((align & 6) == 6) {
            tx = (-textWidth) / 2.0f;
        } else if ((align & 2) == 2) {
            tx = -textWidth;
        }
        if ((align & 24) == 24) {
            ty = 0.0f;
        } else if ((align & 8) == 8) {
            ty = -descender;
        } else {
            ty = -ury;
        }
        if (this.state.getBackgroundMode() == 2) {
            Color textColor = this.state.getCurrentBackgroundColor();
            this.cb.setColorFill(textColor);
            this.cb.rectangle(tx, ty + descender, textWidth, ury - descender);
            this.cb.fill();
        }
        Color textColor2 = this.state.getCurrentTextColor();
        this.cb.setColorFill(textColor2);
        this.cb.beginText();
        this.cb.setFontAndSize(bf, fontSize);
        this.cb.setTextMatrix(tx, ty);
        this.cb.showText(text);
        this.cb.endText();
        if (font.isUnderline()) {
            this.cb.rectangle(tx, ty - (fontSize / 4.0f), textWidth, fontSize / 15.0f);
            this.cb.fill();
        }
        if (font.isStrikeout()) {
            this.cb.rectangle(tx, ty + (fontSize / 3.0f), textWidth, fontSize / 15.0f);
            this.cb.fill();
        }
        this.cb.restoreState();
    }

    private boolean isNullStrokeFill(boolean isRectangle) {
        MetaPen pen = this.state.getCurrentPen();
        MetaBrush brush = this.state.getCurrentBrush();
        boolean noPen = pen.getStyle() == 5;
        int style = brush.getStyle();
        boolean isBrush = style == 0 || (style == 2 && this.state.getBackgroundMode() == 2);
        boolean result = noPen && !isBrush;
        if (!noPen) {
            if (isRectangle) {
                this.state.setLineJoinRectangle(this.cb);
            } else {
                this.state.setLineJoinPolygon(this.cb);
            }
        }
        return result;
    }

    private void strokeAndFill() {
        MetaPen pen = this.state.getCurrentPen();
        MetaBrush brush = this.state.getCurrentBrush();
        int penStyle = pen.getStyle();
        int brushStyle = brush.getStyle();
        if (penStyle == 5) {
            this.cb.closePath();
            if (this.state.getPolyFillMode() == 1) {
                this.cb.eoFill();
                return;
            } else {
                this.cb.fill();
                return;
            }
        }
        boolean isBrush = brushStyle == 0 || (brushStyle == 2 && this.state.getBackgroundMode() == 2);
        if (isBrush) {
            if (this.state.getPolyFillMode() == 1) {
                this.cb.closePathEoFillStroke();
                return;
            } else {
                this.cb.closePathFillStroke();
                return;
            }
        }
        this.cb.closePathStroke();
    }

    private static float getArc(float xCenter, float yCenter, float xDot, float yDot) {
        double s = Math.atan2(yDot - yCenter, xDot - xCenter);
        if (s < 0.0d) {
            s += 6.283185307179586d;
        }
        return (float) ((s / 3.141592653589793d) * 180.0d);
    }

    private static void writeWord(OutputStream os, int v) throws IOException {
        os.write(v & 255);
        os.write((v >>> 8) & 255);
    }

    private static void writeDWord(OutputStream os, int v) throws IOException {
        writeWord(os, v & 65535);
        writeWord(os, (v >>> 16) & 65535);
    }
}
