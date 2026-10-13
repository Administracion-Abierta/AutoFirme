package org.apache.pdfbox.contentstream.operator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.pdfbox.cos.COSDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/Operator.class */
public final class Operator {
    private final String theOperator;
    private byte[] imageData;
    private COSDictionary imageParameters;
    private static final ConcurrentMap<String, Operator> operators = new ConcurrentHashMap();

    private Operator(String aOperator) {
        this.theOperator = aOperator;
        if (aOperator.startsWith("/")) {
            throw new IllegalArgumentException("Operators are not allowed to start with / '" + aOperator + OperatorName.SHOW_TEXT_LINE);
        }
    }

    public static Operator getOperator(String operator) {
        Operator operation;
        if (operator.equals("ID") || OperatorName.BEGIN_INLINE_IMAGE.equals(operator)) {
            operation = new Operator(operator);
        } else {
            operation = operators.get(operator);
            if (operation == null) {
                operation = operators.putIfAbsent(operator, new Operator(operator));
                if (operation == null) {
                    operation = operators.get(operator);
                }
            }
        }
        return operation;
    }

    public String getName() {
        return this.theOperator;
    }

    public String toString() {
        return "PDFOperator{" + this.theOperator + "}";
    }

    public byte[] getImageData() {
        return this.imageData;
    }

    public void setImageData(byte[] imageDataArray) {
        this.imageData = imageDataArray;
    }

    public COSDictionary getImageParameters() {
        return this.imageParameters;
    }

    public void setImageParameters(COSDictionary params) {
        this.imageParameters = params;
    }
}
