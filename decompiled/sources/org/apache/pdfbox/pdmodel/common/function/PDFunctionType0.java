package org.apache.pdfbox.pdmodel.common.function;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRange;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/PDFunctionType0.class */
public class PDFunctionType0 extends PDFunction {
    private static final Log LOG = LogFactory.getLog(PDFunctionType0.class);
    private COSArray encode;
    private COSArray decode;
    private COSArray size;
    private int[][] samples;

    public PDFunctionType0(COSBase function) {
        super(function);
        this.encode = null;
        this.decode = null;
        this.size = null;
        this.samples = (int[][]) null;
    }

    @Override // org.apache.pdfbox.pdmodel.common.function.PDFunction
    public int getFunctionType() {
        return 0;
    }

    public COSArray getSize() {
        if (this.size == null) {
            this.size = (COSArray) getCOSObject().getDictionaryObject(COSName.SIZE);
        }
        return this.size;
    }

    public int getBitsPerSample() {
        return getCOSObject().getInt(COSName.BITS_PER_SAMPLE);
    }

    public int getOrder() {
        return getCOSObject().getInt(COSName.ORDER, 1);
    }

    public void setBitsPerSample(int bps) {
        getCOSObject().setInt(COSName.BITS_PER_SAMPLE, bps);
    }

    private COSArray getEncodeValues() {
        if (this.encode == null) {
            this.encode = (COSArray) getCOSObject().getDictionaryObject(COSName.ENCODE);
            if (this.encode == null) {
                this.encode = new COSArray();
                COSArray sizeValues = getSize();
                int sizeValuesSize = sizeValues.size();
                for (int i = 0; i < sizeValuesSize; i++) {
                    this.encode.add((COSBase) COSInteger.ZERO);
                    this.encode.add((COSBase) COSInteger.get(sizeValues.getInt(i) - 1));
                }
            }
        }
        return this.encode;
    }

    private COSArray getDecodeValues() {
        if (this.decode == null) {
            this.decode = (COSArray) getCOSObject().getDictionaryObject(COSName.DECODE);
            if (this.decode == null) {
                this.decode = getRangeValues();
            }
        }
        return this.decode;
    }

    public PDRange getEncodeForParameter(int paramNum) {
        PDRange retval = null;
        COSArray encodeValues = getEncodeValues();
        if (encodeValues != null && encodeValues.size() >= (paramNum * 2) + 1) {
            retval = new PDRange(encodeValues, paramNum);
        }
        return retval;
    }

    public void setEncodeValues(COSArray encodeValues) {
        this.encode = encodeValues;
        getCOSObject().setItem(COSName.ENCODE, (COSBase) encodeValues);
    }

    public PDRange getDecodeForParameter(int paramNum) {
        PDRange retval = null;
        COSArray decodeValues = getDecodeValues();
        if (decodeValues != null && decodeValues.size() >= (paramNum * 2) + 1) {
            retval = new PDRange(decodeValues, paramNum);
        }
        return retval;
    }

    public void setDecodeValues(COSArray decodeValues) {
        this.decode = decodeValues;
        getCOSObject().setItem(COSName.DECODE, (COSBase) decodeValues);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/PDFunctionType0$Rinterpol.class */
    private class Rinterpol {
        private final float[] in;
        private final int[] inPrev;
        private final int[] inNext;
        private final int numberOfInputValues;
        private final int numberOfOutputValues;

        Rinterpol(float[] input, int[] inputPrev, int[] inputNext) {
            this.numberOfOutputValues = PDFunctionType0.this.getNumberOfOutputParameters();
            this.in = input;
            this.inPrev = inputPrev;
            this.inNext = inputNext;
            this.numberOfInputValues = input.length;
        }

        float[] rinterpolate() {
            return rinterpol(new int[this.numberOfInputValues], 0);
        }

        private float[] rinterpol(int[] coord, int step) {
            float[] resultSample = new float[this.numberOfOutputValues];
            if (step == this.in.length - 1) {
                if (this.inPrev[step] == this.inNext[step]) {
                    coord[step] = this.inPrev[step];
                    int[] tmpSample = getSamples()[calcSampleIndex(coord)];
                    for (int i = 0; i < this.numberOfOutputValues; i++) {
                        resultSample[i] = tmpSample[i];
                    }
                    return resultSample;
                }
                coord[step] = this.inPrev[step];
                int[] sample1 = getSamples()[calcSampleIndex(coord)];
                coord[step] = this.inNext[step];
                int[] sample2 = getSamples()[calcSampleIndex(coord)];
                for (int i2 = 0; i2 < this.numberOfOutputValues; i2++) {
                    resultSample[i2] = PDFunctionType0.this.interpolate(this.in[step], this.inPrev[step], this.inNext[step], sample1[i2], sample2[i2]);
                }
                return resultSample;
            }
            if (this.inPrev[step] == this.inNext[step]) {
                coord[step] = this.inPrev[step];
                return rinterpol(coord, step + 1);
            }
            coord[step] = this.inPrev[step];
            float[] sample3 = rinterpol(coord, step + 1);
            coord[step] = this.inNext[step];
            float[] sample4 = rinterpol(coord, step + 1);
            for (int i3 = 0; i3 < this.numberOfOutputValues; i3++) {
                resultSample[i3] = PDFunctionType0.this.interpolate(this.in[step], this.inPrev[step], this.inNext[step], sample3[i3], sample4[i3]);
            }
            return resultSample;
        }

        private int calcSampleIndex(int[] vector) {
            float[] sizeValues = PDFunctionType0.this.getSize().toFloatArray();
            int index = 0;
            int sizeProduct = 1;
            int dimension = vector.length;
            for (int i = dimension - 2; i >= 0; i--) {
                sizeProduct = (int) (sizeProduct * sizeValues[i]);
            }
            for (int i2 = dimension - 1; i2 >= 0; i2--) {
                index += sizeProduct * vector[i2];
                if (i2 - 1 >= 0) {
                    sizeProduct = (int) (sizeProduct / sizeValues[i2 - 1]);
                }
            }
            return index;
        }

        private int[][] getSamples() {
            if (PDFunctionType0.this.samples == null) {
                int arraySize = 1;
                int nIn = PDFunctionType0.this.getNumberOfInputParameters();
                int nOut = PDFunctionType0.this.getNumberOfOutputParameters();
                COSArray sizes = PDFunctionType0.this.getSize();
                for (int i = 0; i < nIn; i++) {
                    arraySize *= sizes.getInt(i);
                }
                PDFunctionType0.this.samples = new int[arraySize][nOut];
                int bitsPerSample = PDFunctionType0.this.getBitsPerSample();
                int index = 0;
                try {
                    InputStream inputStream = PDFunctionType0.this.getPDStream().createInputStream();
                    MemoryCacheImageInputStream memoryCacheImageInputStream = new MemoryCacheImageInputStream(inputStream);
                    for (int i2 = 0; i2 < arraySize; i2++) {
                        for (int k = 0; k < nOut; k++) {
                            PDFunctionType0.this.samples[index][k] = (int) memoryCacheImageInputStream.readBits(bitsPerSample);
                        }
                        index++;
                    }
                    memoryCacheImageInputStream.close();
                    inputStream.close();
                } catch (IOException exception) {
                    PDFunctionType0.LOG.error("IOException while reading the sample values of this function.", exception);
                }
            }
            return PDFunctionType0.this.samples;
        }
    }

    @Override // org.apache.pdfbox.pdmodel.common.function.PDFunction
    public float[] eval(float[] input) throws IOException {
        float[] sizeValues = getSize().toFloatArray();
        int bitsPerSample = getBitsPerSample();
        float maxSample = (float) (Math.pow(2.0d, bitsPerSample) - 1.0d);
        int numberOfInputValues = input.length;
        int numberOfOutputValues = getNumberOfOutputParameters();
        int[] inputPrev = new int[numberOfInputValues];
        int[] inputNext = new int[numberOfInputValues];
        float[] input2 = (float[]) input.clone();
        for (int i = 0; i < numberOfInputValues; i++) {
            PDRange domain = getDomainForInput(i);
            PDRange encodeValues = getEncodeForParameter(i);
            input2[i] = clipToRange(input2[i], domain.getMin(), domain.getMax());
            input2[i] = interpolate(input2[i], domain.getMin(), domain.getMax(), encodeValues.getMin(), encodeValues.getMax());
            input2[i] = clipToRange(input2[i], ColumnText.GLOBAL_SPACE_CHAR_RATIO, sizeValues[i] - 1.0f);
            inputPrev[i] = (int) Math.floor(input2[i]);
            inputNext[i] = (int) Math.ceil(input2[i]);
        }
        float[] outputValues = new Rinterpol(input2, inputPrev, inputNext).rinterpolate();
        for (int i2 = 0; i2 < numberOfOutputValues; i2++) {
            PDRange range = getRangeForOutput(i2);
            PDRange decodeValues = getDecodeForParameter(i2);
            outputValues[i2] = interpolate(outputValues[i2], ColumnText.GLOBAL_SPACE_CHAR_RATIO, maxSample, decodeValues.getMin(), decodeValues.getMax());
            outputValues[i2] = clipToRange(outputValues[i2], range.getMin(), range.getMax());
        }
        return outputValues;
    }
}
