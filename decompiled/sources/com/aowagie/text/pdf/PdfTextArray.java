package com.aowagie.text.pdf;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfTextArray.class */
class PdfTextArray {
    private final ArrayList arrayList = new ArrayList();
    private String lastStr;
    private Float lastNum;

    PdfTextArray(String str) {
        add(str);
    }

    public PdfTextArray() {
    }

    void add(float number) {
        if (number != ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            if (this.lastNum != null) {
                this.lastNum = Float.valueOf(number + this.lastNum.floatValue());
                if (this.lastNum.floatValue() != ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                    replaceLast(this.lastNum);
                } else {
                    this.arrayList.remove(this.arrayList.size() - 1);
                }
            } else {
                this.lastNum = Float.valueOf(number);
                this.arrayList.add(this.lastNum);
            }
            this.lastStr = null;
        }
    }

    void add(String str) {
        if (str.length() > 0) {
            if (this.lastStr != null) {
                this.lastStr += str;
                replaceLast(this.lastStr);
            } else {
                this.lastStr = str;
                this.arrayList.add(this.lastStr);
            }
            this.lastNum = null;
        }
    }

    ArrayList getArrayList() {
        return this.arrayList;
    }

    private void replaceLast(Object obj) {
        this.arrayList.set(this.arrayList.size() - 1, obj);
    }
}
