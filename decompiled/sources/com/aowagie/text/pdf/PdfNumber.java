package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfNumber.class */
public class PdfNumber extends PdfObject {
    private double value;

    public PdfNumber(String content) {
        super(2);
        try {
            this.value = Double.parseDouble(content.trim());
            setContent(content);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException(content + " is not a valid number - " + nfe.toString());
        }
    }

    public PdfNumber(int value) {
        super(2);
        this.value = value;
        setContent(String.valueOf(value));
    }

    PdfNumber(double value) {
        super(2);
        this.value = value;
        setContent(ByteBuffer.formatDouble(value));
    }

    public PdfNumber(float value) {
        this(value);
    }

    public int intValue() {
        return (int) this.value;
    }

    public double doubleValue() {
        return this.value;
    }

    public float floatValue() {
        return (float) this.value;
    }
}
