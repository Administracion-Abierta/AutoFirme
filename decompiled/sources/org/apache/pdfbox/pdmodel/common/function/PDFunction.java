package org.apache.pdfbox.pdmodel.common.function;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRange;
import org.apache.pdfbox.pdmodel.common.PDStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/PDFunction.class */
public abstract class PDFunction implements COSObjectable {
    private PDStream functionStream;
    private COSDictionary functionDictionary;
    private COSArray domain = null;
    private COSArray range = null;
    private int numberOfInputValues = -1;
    private int numberOfOutputValues = -1;

    public abstract int getFunctionType();

    public abstract float[] eval(float[] fArr) throws IOException;

    public PDFunction(COSBase function) {
        this.functionStream = null;
        this.functionDictionary = null;
        if (function instanceof COSStream) {
            this.functionStream = new PDStream((COSStream) function);
            this.functionStream.getCOSObject().setItem(COSName.TYPE, (COSBase) COSName.FUNCTION);
        } else if (function instanceof COSDictionary) {
            this.functionDictionary = (COSDictionary) function;
        }
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        if (this.functionStream != null) {
            return this.functionStream.getCOSObject();
        }
        return this.functionDictionary;
    }

    protected PDStream getPDStream() {
        return this.functionStream;
    }

    public static PDFunction create(COSBase function) throws IOException {
        if (function == COSName.IDENTITY) {
            return new PDFunctionTypeIdentity(null);
        }
        COSBase base = function;
        if (function instanceof COSObject) {
            base = ((COSObject) function).getObject();
        }
        if (!(base instanceof COSDictionary)) {
            throw new IOException("Error: Function must be a Dictionary, but is " + (base == null ? "(null)" : base.getClass().getSimpleName()));
        }
        COSDictionary functionDictionary = (COSDictionary) base;
        int functionType = functionDictionary.getInt(COSName.FUNCTION_TYPE);
        switch (functionType) {
            case 0:
                return new PDFunctionType0(functionDictionary);
            case 1:
            default:
                throw new IOException("Error: Unknown function type " + functionType);
            case 2:
                return new PDFunctionType2(functionDictionary);
            case 3:
                return new PDFunctionType3(functionDictionary);
            case 4:
                return new PDFunctionType4(functionDictionary);
        }
    }

    public int getNumberOfOutputParameters() {
        if (this.numberOfOutputValues == -1) {
            COSArray rangeValues = getRangeValues();
            this.numberOfOutputValues = rangeValues.size() / 2;
        }
        return this.numberOfOutputValues;
    }

    public PDRange getRangeForOutput(int n) {
        COSArray rangeValues = getRangeValues();
        return new PDRange(rangeValues, n);
    }

    public void setRangeValues(COSArray rangeValues) {
        this.range = rangeValues;
        getCOSObject().setItem(COSName.RANGE, (COSBase) rangeValues);
    }

    public int getNumberOfInputParameters() {
        if (this.numberOfInputValues == -1) {
            COSArray array = getDomainValues();
            this.numberOfInputValues = array.size() / 2;
        }
        return this.numberOfInputValues;
    }

    public PDRange getDomainForInput(int n) {
        COSArray domainValues = getDomainValues();
        return new PDRange(domainValues, n);
    }

    public void setDomainValues(COSArray domainValues) {
        this.domain = domainValues;
        getCOSObject().setItem(COSName.DOMAIN, (COSBase) domainValues);
    }

    @Deprecated
    public COSArray eval(COSArray input) throws IOException {
        float[] outputValues = eval(input.toFloatArray());
        COSArray array = new COSArray();
        array.setFloatArray(outputValues);
        return array;
    }

    protected COSArray getRangeValues() {
        if (this.range == null) {
            this.range = (COSArray) getCOSObject().getDictionaryObject(COSName.RANGE);
        }
        return this.range;
    }

    private COSArray getDomainValues() {
        if (this.domain == null) {
            this.domain = (COSArray) getCOSObject().getDictionaryObject(COSName.DOMAIN);
        }
        return this.domain;
    }

    protected float[] clipToRange(float[] inputValues) {
        float[] result;
        COSArray rangesArray = getRangeValues();
        if (rangesArray != null && rangesArray.size() > 0) {
            float[] rangeValues = rangesArray.toFloatArray();
            int numberOfRanges = rangeValues.length / 2;
            result = new float[numberOfRanges];
            for (int i = 0; i < numberOfRanges; i++) {
                int index = i << 1;
                result[i] = clipToRange(inputValues[i], rangeValues[index], rangeValues[index + 1]);
            }
        } else {
            result = inputValues;
        }
        return result;
    }

    protected float clipToRange(float x, float rangeMin, float rangeMax) {
        if (x < rangeMin) {
            return rangeMin;
        }
        if (x > rangeMax) {
            return rangeMax;
        }
        return x;
    }

    protected float interpolate(float x, float xRangeMin, float xRangeMax, float yRangeMin, float yRangeMax) {
        return yRangeMin + (((x - xRangeMin) * (yRangeMax - yRangeMin)) / (xRangeMax - xRangeMin));
    }

    public String toString() {
        return "FunctionType" + getFunctionType();
    }
}
