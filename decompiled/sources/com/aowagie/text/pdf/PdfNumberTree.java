package com.aowagie.text.pdf;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfNumberTree.class */
class PdfNumberTree {
    private static final int leafSize = 64;

    PdfNumberTree() {
    }

    static PdfDictionary writeTree(HashMap items, PdfWriter pdfWriter) throws IOException {
        int top;
        if (items.isEmpty()) {
            return null;
        }
        Integer[] numbers = (Integer[]) items.keySet().toArray(new Integer[items.size()]);
        Arrays.sort(numbers);
        if (numbers.length <= 64) {
            PdfDictionary dic = new PdfDictionary();
            PdfArray ar = new PdfArray();
            for (Integer number : numbers) {
                ar.add(new PdfNumber(number.intValue()));
                ar.add((PdfObject) items.get(number));
            }
            dic.put(PdfName.NUMS, ar);
            return dic;
        }
        int skip = 64;
        PdfIndirectReference[] kids = new PdfIndirectReference[((numbers.length + 64) - 1) / 64];
        for (int k = 0; k < kids.length; k++) {
            int offset = k * 64;
            int end = Math.min(offset + 64, numbers.length);
            PdfDictionary dic2 = new PdfDictionary();
            PdfArray arr = new PdfArray();
            arr.add(new PdfNumber(numbers[offset].intValue()));
            arr.add(new PdfNumber(numbers[end - 1].intValue()));
            dic2.put(PdfName.LIMITS, arr);
            PdfArray arr2 = new PdfArray();
            while (offset < end) {
                arr2.add(new PdfNumber(numbers[offset].intValue()));
                arr2.add((PdfObject) items.get(numbers[offset]));
                offset++;
            }
            dic2.put(PdfName.NUMS, arr2);
            kids[k] = pdfWriter.addToBody(dic2).getIndirectReference();
        }
        int length = kids.length;
        while (true) {
            top = length;
            if (top <= 64) {
                break;
            }
            skip *= 64;
            int tt = ((numbers.length + skip) - 1) / skip;
            for (int k2 = 0; k2 < tt; k2++) {
                int offset2 = k2 * 64;
                int end2 = Math.min(offset2 + 64, top);
                PdfDictionary dic3 = new PdfDictionary();
                PdfArray arr3 = new PdfArray();
                arr3.add(new PdfNumber(numbers[k2 * skip].intValue()));
                arr3.add(new PdfNumber(numbers[Math.min((k2 + 1) * skip, numbers.length) - 1].intValue()));
                dic3.put(PdfName.LIMITS, arr3);
                PdfArray arr4 = new PdfArray();
                while (offset2 < end2) {
                    arr4.add(kids[offset2]);
                    offset2++;
                }
                dic3.put(PdfName.KIDS, arr4);
                kids[k2] = pdfWriter.addToBody(dic3).getIndirectReference();
            }
            length = tt;
        }
        PdfArray arr5 = new PdfArray();
        for (int k3 = 0; k3 < top; k3++) {
            arr5.add(kids[k3]);
        }
        PdfDictionary dic4 = new PdfDictionary();
        dic4.put(PdfName.KIDS, arr5);
        return dic4;
    }

    private static void iterateItems(PdfDictionary dic, HashMap items) {
        PdfArray nn = (PdfArray) PdfReader.getPdfObjectRelease(dic.get(PdfName.NUMS));
        if (nn != null) {
            int k = 0;
            while (k < nn.size()) {
                int i = k;
                int k2 = k + 1;
                PdfNumber s = (PdfNumber) PdfReader.getPdfObjectRelease(nn.getPdfObject(i));
                items.put(Integer.valueOf(s.intValue()), nn.getPdfObject(k2));
                k = k2 + 1;
            }
            return;
        }
        PdfArray nn2 = (PdfArray) PdfReader.getPdfObjectRelease(dic.get(PdfName.KIDS));
        if (nn2 != null) {
            for (int k3 = 0; k3 < nn2.size(); k3++) {
                PdfDictionary kid = (PdfDictionary) PdfReader.getPdfObjectRelease(nn2.getPdfObject(k3));
                iterateItems(kid, items);
            }
        }
    }
}
