package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Paint;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/ShadingPaint.class */
public abstract class ShadingPaint<T extends PDShading> implements Paint {
    protected final T shading;
    protected final Matrix matrix;

    ShadingPaint(T shading, Matrix matrix) {
        this.shading = shading;
        this.matrix = matrix;
    }

    public T getShading() {
        return this.shading;
    }

    public Matrix getMatrix() {
        return this.matrix;
    }
}
