package org.apache.pdfbox.pdmodel.graphics.shading;

import com.aowagie.text.pdf.ColumnText;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/PDShading.class */
public abstract class PDShading implements COSObjectable {
    private final COSDictionary dictionary;
    private COSArray background;
    private PDRectangle bBox;
    private PDColorSpace colorSpace;
    private PDFunction function;
    private PDFunction[] functionArray;
    public static final int SHADING_TYPE1 = 1;
    public static final int SHADING_TYPE2 = 2;
    public static final int SHADING_TYPE3 = 3;
    public static final int SHADING_TYPE4 = 4;
    public static final int SHADING_TYPE5 = 5;
    public static final int SHADING_TYPE6 = 6;
    public static final int SHADING_TYPE7 = 7;

    public abstract int getShadingType();

    public abstract Paint toPaint(Matrix matrix);

    public PDShading() {
        this.background = null;
        this.bBox = null;
        this.colorSpace = null;
        this.function = null;
        this.functionArray = null;
        this.dictionary = new COSDictionary();
    }

    public PDShading(COSDictionary shadingDictionary) {
        this.background = null;
        this.bBox = null;
        this.colorSpace = null;
        this.function = null;
        this.functionArray = null;
        this.dictionary = shadingDictionary;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public String getType() {
        return COSName.SHADING.getName();
    }

    public void setShadingType(int shadingType) {
        this.dictionary.setInt(COSName.SHADING_TYPE, shadingType);
    }

    public void setBackground(COSArray newBackground) {
        this.background = newBackground;
        this.dictionary.setItem(COSName.BACKGROUND, (COSBase) newBackground);
    }

    public COSArray getBackground() {
        if (this.background == null) {
            this.background = (COSArray) this.dictionary.getDictionaryObject(COSName.BACKGROUND);
        }
        return this.background;
    }

    public PDRectangle getBBox() {
        COSArray array;
        if (this.bBox == null && (array = (COSArray) this.dictionary.getDictionaryObject(COSName.BBOX)) != null) {
            this.bBox = new PDRectangle(array);
        }
        return this.bBox;
    }

    public void setBBox(PDRectangle newBBox) {
        this.bBox = newBBox;
        if (this.bBox == null) {
            this.dictionary.removeItem(COSName.BBOX);
        } else {
            this.dictionary.setItem(COSName.BBOX, (COSBase) this.bBox.getCOSArray());
        }
    }

    public Rectangle2D getBounds(AffineTransform xform, Matrix matrix) throws IOException {
        return null;
    }

    public void setAntiAlias(boolean antiAlias) {
        this.dictionary.setBoolean(COSName.ANTI_ALIAS, antiAlias);
    }

    public boolean getAntiAlias() {
        return this.dictionary.getBoolean(COSName.ANTI_ALIAS, false);
    }

    public PDColorSpace getColorSpace() throws IOException {
        if (this.colorSpace == null) {
            COSBase colorSpaceDictionary = this.dictionary.getDictionaryObject(COSName.CS, COSName.COLORSPACE);
            this.colorSpace = PDColorSpace.create(colorSpaceDictionary);
        }
        return this.colorSpace;
    }

    public void setColorSpace(PDColorSpace colorSpace) {
        this.colorSpace = colorSpace;
        if (colorSpace != null) {
            this.dictionary.setItem(COSName.COLORSPACE, colorSpace.getCOSObject());
        } else {
            this.dictionary.removeItem(COSName.COLORSPACE);
        }
    }

    public static PDShading create(COSDictionary shadingDictionary) throws IOException {
        PDShading shading;
        int shadingType = shadingDictionary.getInt(COSName.SHADING_TYPE, 0);
        switch (shadingType) {
            case 1:
                shading = new PDShadingType1(shadingDictionary);
                break;
            case 2:
                shading = new PDShadingType2(shadingDictionary);
                break;
            case 3:
                shading = new PDShadingType3(shadingDictionary);
                break;
            case 4:
                shading = new PDShadingType4(shadingDictionary);
                break;
            case 5:
                shading = new PDShadingType5(shadingDictionary);
                break;
            case 6:
                shading = new PDShadingType6(shadingDictionary);
                break;
            case 7:
                shading = new PDShadingType7(shadingDictionary);
                break;
            default:
                throw new IOException("Error: Unknown shading type " + shadingType);
        }
        return shading;
    }

    public void setFunction(PDFunction newFunction) {
        this.functionArray = null;
        this.function = newFunction;
        getCOSObject().setItem(COSName.FUNCTION, newFunction);
    }

    public void setFunction(COSArray newFunctions) {
        this.functionArray = null;
        this.function = null;
        getCOSObject().setItem(COSName.FUNCTION, (COSBase) newFunctions);
    }

    public PDFunction getFunction() throws IOException {
        COSBase dictionaryFunctionObject;
        if (this.function == null && (dictionaryFunctionObject = getCOSObject().getDictionaryObject(COSName.FUNCTION)) != null) {
            this.function = PDFunction.create(dictionaryFunctionObject);
        }
        return this.function;
    }

    private PDFunction[] getFunctionsArray() throws IOException {
        if (this.functionArray == null) {
            COSBase functionObject = getCOSObject().getDictionaryObject(COSName.FUNCTION);
            if (functionObject instanceof COSDictionary) {
                this.functionArray = new PDFunction[1];
                this.functionArray[0] = PDFunction.create(functionObject);
            } else if (functionObject instanceof COSArray) {
                COSArray functionCOSArray = (COSArray) functionObject;
                int numberOfFunctions = functionCOSArray.size();
                this.functionArray = new PDFunction[numberOfFunctions];
                for (int i = 0; i < numberOfFunctions; i++) {
                    this.functionArray[i] = PDFunction.create(functionCOSArray.get(i));
                }
            } else {
                throw new IOException("mandatory /Function element must be a dictionary or an array");
            }
        }
        return this.functionArray;
    }

    public float[] evalFunction(float inputValue) throws IOException {
        return evalFunction(new float[]{inputValue});
    }

    public float[] evalFunction(float[] input) throws IOException {
        float[] returnValues;
        PDFunction[] functions = getFunctionsArray();
        int numberOfFunctions = functions.length;
        if (numberOfFunctions == 1) {
            returnValues = functions[0].eval(input);
        } else {
            returnValues = new float[numberOfFunctions];
            for (int i = 0; i < numberOfFunctions; i++) {
                float[] newValue = functions[i].eval(input);
                returnValues[i] = newValue[0];
            }
        }
        for (int i2 = 0; i2 < returnValues.length; i2++) {
            if (returnValues[i2] < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                returnValues[i2] = 0.0f;
            } else if (returnValues[i2] > 1.0f) {
                returnValues[i2] = 1.0f;
            }
        }
        return returnValues;
    }
}
