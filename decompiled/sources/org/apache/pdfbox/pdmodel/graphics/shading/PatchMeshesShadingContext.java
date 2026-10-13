package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/PatchMeshesShadingContext.class */
abstract class PatchMeshesShadingContext extends TriangleBasedShadingContext {
    private List<Patch> patchList;

    protected PatchMeshesShadingContext(PDMeshBasedShadingType shading, ColorModel colorModel, AffineTransform xform, Matrix matrix, Rectangle deviceBounds, int controlPoints) throws IOException {
        super(shading, colorModel, xform, matrix);
        this.patchList = shading.collectPatches(xform, matrix, controlPoints);
        createPixelTable(deviceBounds);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.TriangleBasedShadingContext
    protected Map<Point, Integer> calcPixelTable(Rectangle deviceBounds) throws IOException {
        Map<Point, Integer> map = new HashMap<>();
        for (Patch it : this.patchList) {
            super.calcPixelTable(it.listOfTriangles, map, deviceBounds);
        }
        return map;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.TriangleBasedShadingContext, org.apache.pdfbox.pdmodel.graphics.shading.ShadingContext
    public void dispose() {
        this.patchList = null;
        super.dispose();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.TriangleBasedShadingContext
    protected boolean isDataEmpty() {
        return this.patchList.isEmpty();
    }
}
