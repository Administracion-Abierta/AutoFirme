package org.apache.pdfbox.rendering;

import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/rendering/TilingPaintFactory.class */
class TilingPaintFactory {
    private final PageDrawer drawer;
    private final Map<TilingPaintParameter, WeakReference<Paint>> weakCache = new WeakHashMap();

    TilingPaintFactory(PageDrawer drawer) {
        this.drawer = drawer;
    }

    Paint create(PDTilingPattern pattern, PDColorSpace colorSpace, PDColor color, AffineTransform xform) throws IOException {
        Paint paint = null;
        TilingPaintParameter tilingPaintParameter = new TilingPaintParameter(this.drawer.getInitialMatrix(), pattern.getCOSObject(), colorSpace, color, xform);
        WeakReference<Paint> weakRef = this.weakCache.get(tilingPaintParameter);
        if (weakRef != null) {
            paint = weakRef.get();
        }
        if (paint == null) {
            paint = new TilingPaint(this.drawer, pattern, colorSpace, color, xform);
            this.weakCache.put(tilingPaintParameter, new WeakReference<>(paint));
        }
        return paint;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/rendering/TilingPaintFactory$TilingPaintParameter.class */
    private static class TilingPaintParameter {
        private final Matrix matrix;
        private final COSDictionary patternDict;
        private final PDColorSpace colorSpace;
        private final PDColor color;
        private final AffineTransform xform;

        private TilingPaintParameter(Matrix matrix, COSDictionary patternDict, PDColorSpace colorSpace, PDColor color, AffineTransform xform) {
            this.matrix = matrix.m1288clone();
            this.patternDict = patternDict;
            this.colorSpace = colorSpace;
            this.color = color;
            this.xform = xform;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            TilingPaintParameter other = (TilingPaintParameter) obj;
            if (this.matrix != other.matrix && (this.matrix == null || !this.matrix.equals(other.matrix))) {
                return false;
            }
            if (this.patternDict != other.patternDict && (this.patternDict == null || !this.patternDict.equals(other.patternDict))) {
                return false;
            }
            if (this.colorSpace != other.colorSpace && (this.colorSpace == null || !this.colorSpace.equals(other.colorSpace))) {
                return false;
            }
            if (this.color == null && other.color != null) {
                return false;
            }
            if (this.color != null && other.color == null) {
                return false;
            }
            if (this.color != null && this.color.getColorSpace() != other.color.getColorSpace()) {
                return false;
            }
            try {
                if (this.color == null || other.color == null || this.color == other.color || this.color.toRGB() == other.color.toRGB()) {
                    return this.xform == other.xform || (this.xform != null && this.xform.equals(other.xform));
                }
                return false;
            } catch (IOException e) {
                return false;
            }
        }

        public int hashCode() {
            int hash = (23 * 7) + (this.matrix != null ? this.matrix.hashCode() : 0);
            return (23 * ((23 * ((23 * ((23 * hash) + (this.patternDict != null ? this.patternDict.hashCode() : 0))) + (this.colorSpace != null ? this.colorSpace.hashCode() : 0))) + (this.color != null ? this.color.hashCode() : 0))) + (this.xform != null ? this.xform.hashCode() : 0);
        }

        public String toString() {
            return "TilingPaintParameter{matrix=" + this.matrix + ", pattern=" + this.patternDict + ", colorSpace=" + this.colorSpace + ", color=" + this.color + ", xform=" + this.xform + '}';
        }
    }
}
