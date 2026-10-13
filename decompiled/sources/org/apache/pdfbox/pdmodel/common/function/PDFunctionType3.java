package org.apache.pdfbox.pdmodel.common.function;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRange;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/PDFunctionType3.class */
public class PDFunctionType3 extends PDFunction {
    private COSArray functions;
    private COSArray encode;
    private COSArray bounds;
    private PDFunction[] functionsArray;
    private float[] boundsValues;

    public PDFunctionType3(COSBase functionStream) {
        super(functionStream);
        this.functions = null;
        this.encode = null;
        this.bounds = null;
        this.functionsArray = null;
        this.boundsValues = null;
    }

    @Override // org.apache.pdfbox.pdmodel.common.function.PDFunction
    public int getFunctionType() {
        return 3;
    }

    @Override // org.apache.pdfbox.pdmodel.common.function.PDFunction
    public float[] eval(float[] input) throws IOException {
        PDFunction function = null;
        float x = input[0];
        PDRange domain = getDomainForInput(0);
        float x2 = clipToRange(x, domain.getMin(), domain.getMax());
        if (this.functionsArray == null) {
            COSArray ar = getFunctions();
            this.functionsArray = new PDFunction[ar.size()];
            for (int i = 0; i < ar.size(); i++) {
                this.functionsArray[i] = PDFunction.create(ar.getObject(i));
            }
        }
        if (this.functionsArray.length == 1) {
            function = this.functionsArray[0];
            PDRange encRange = getEncodeForParameter(0);
            x2 = interpolate(x2, domain.getMin(), domain.getMax(), encRange.getMin(), encRange.getMax());
        } else {
            if (this.boundsValues == null) {
                this.boundsValues = getBounds().toFloatArray();
            }
            int boundsSize = this.boundsValues.length;
            float[] partitionValues = new float[boundsSize + 2];
            int partitionValuesSize = partitionValues.length;
            partitionValues[0] = domain.getMin();
            partitionValues[partitionValuesSize - 1] = domain.getMax();
            System.arraycopy(this.boundsValues, 0, partitionValues, 1, boundsSize);
            for (int i2 = 0; i2 < partitionValuesSize - 1; i2++) {
                if (x2 >= partitionValues[i2] && (x2 < partitionValues[i2 + 1] || (i2 == partitionValuesSize - 2 && x2 == partitionValues[i2 + 1]))) {
                    function = this.functionsArray[i2];
                    PDRange encRange2 = getEncodeForParameter(i2);
                    x2 = interpolate(x2, partitionValues[i2], partitionValues[i2 + 1], encRange2.getMin(), encRange2.getMax());
                    break;
                }
            }
            if (function == null) {
                throw new IOException("partition not found in type 3 function");
            }
        }
        float[] functionValues = {x2};
        float[] functionResult = function.eval(functionValues);
        return clipToRange(functionResult);
    }

    public COSArray getFunctions() {
        if (this.functions == null) {
            this.functions = (COSArray) getCOSObject().getDictionaryObject(COSName.FUNCTIONS);
        }
        return this.functions;
    }

    public COSArray getBounds() {
        if (this.bounds == null) {
            this.bounds = (COSArray) getCOSObject().getDictionaryObject(COSName.BOUNDS);
        }
        return this.bounds;
    }

    public COSArray getEncode() {
        if (this.encode == null) {
            this.encode = (COSArray) getCOSObject().getDictionaryObject(COSName.ENCODE);
        }
        return this.encode;
    }

    private PDRange getEncodeForParameter(int n) {
        COSArray encodeValues = getEncode();
        return new PDRange(encodeValues, n);
    }
}
