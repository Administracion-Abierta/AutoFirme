package org.apache.pdfbox.pdmodel.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/PDPageLabels.class */
public class PDPageLabels implements COSObjectable {
    private Map<Integer, PDPageLabelRange> labels;
    private PDDocument doc;

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/PDPageLabels$LabelHandler.class */
    private interface LabelHandler {
        void newLabel(int i, String str);
    }

    public PDPageLabels(PDDocument document) {
        this.labels = new TreeMap();
        this.doc = document;
        PDPageLabelRange defaultRange = new PDPageLabelRange();
        defaultRange.setStyle("D");
        this.labels.put(0, defaultRange);
    }

    public PDPageLabels(PDDocument document, COSDictionary dict) throws IOException {
        this(document);
        if (dict == null) {
            return;
        }
        PDNumberTreeNode root = new PDNumberTreeNode(dict, PDPageLabelRange.class);
        findLabels(root);
    }

    private void findLabels(PDNumberTreeNode node) throws IOException {
        List<PDNumberTreeNode> kids = node.getKids();
        if (node.getKids() != null) {
            for (PDNumberTreeNode kid : kids) {
                findLabels(kid);
            }
            return;
        }
        Map<Integer, COSObjectable> numbers = node.getNumbers();
        if (numbers != null) {
            for (Map.Entry<Integer, COSObjectable> i : numbers.entrySet()) {
                if (i.getKey().intValue() >= 0) {
                    this.labels.put(i.getKey(), (PDPageLabelRange) i.getValue());
                }
            }
        }
    }

    public int getPageRangeCount() {
        return this.labels.size();
    }

    public PDPageLabelRange getPageLabelRange(int startPage) {
        return this.labels.get(Integer.valueOf(startPage));
    }

    public void setLabelItem(int startPage, PDPageLabelRange item) {
        if (startPage < 0) {
            throw new IllegalArgumentException("startPage parameter of setLabelItem may not be < 0");
        }
        this.labels.put(Integer.valueOf(startPage), item);
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        COSDictionary dict = new COSDictionary();
        COSArray arr = new COSArray();
        for (Map.Entry<Integer, PDPageLabelRange> i : this.labels.entrySet()) {
            arr.add((COSBase) COSInteger.get(i.getKey().intValue()));
            arr.add(i.getValue());
        }
        dict.setItem(COSName.NUMS, (COSBase) arr);
        return dict;
    }

    public Map<String, Integer> getPageIndicesByLabels() {
        final Map<String, Integer> labelMap = new HashMap<>(this.doc.getNumberOfPages());
        computeLabels(new LabelHandler() { // from class: org.apache.pdfbox.pdmodel.common.PDPageLabels.1
            @Override // org.apache.pdfbox.pdmodel.common.PDPageLabels.LabelHandler
            public void newLabel(int pageIndex, String label) {
                labelMap.put(label, Integer.valueOf(pageIndex));
            }
        });
        return labelMap;
    }

    public String[] getLabelsByPageIndices() {
        final int numberOfPages = this.doc.getNumberOfPages();
        final String[] map = new String[numberOfPages];
        computeLabels(new LabelHandler() { // from class: org.apache.pdfbox.pdmodel.common.PDPageLabels.2
            @Override // org.apache.pdfbox.pdmodel.common.PDPageLabels.LabelHandler
            public void newLabel(int pageIndex, String label) {
                if (pageIndex < numberOfPages) {
                    map[pageIndex] = label;
                }
            }
        });
        return map;
    }

    public NavigableSet<Integer> getPageIndices() {
        return new TreeSet(this.labels.keySet());
    }

    private void computeLabels(LabelHandler handler) {
        Map.Entry<Integer, PDPageLabelRange> lastEntry;
        Iterator<Map.Entry<Integer, PDPageLabelRange>> iterator = this.labels.entrySet().iterator();
        if (!iterator.hasNext()) {
            return;
        }
        int pageIndex = 0;
        Map.Entry<Integer, PDPageLabelRange> next = iterator.next();
        while (true) {
            lastEntry = next;
            if (!iterator.hasNext()) {
                break;
            }
            Map.Entry<Integer, PDPageLabelRange> entry = iterator.next();
            int numPages = entry.getKey().intValue() - lastEntry.getKey().intValue();
            LabelGenerator gen = new LabelGenerator(lastEntry.getValue(), numPages);
            while (gen.hasNext()) {
                handler.newLabel(pageIndex, gen.next());
                pageIndex++;
            }
            next = entry;
        }
        LabelGenerator gen2 = new LabelGenerator(lastEntry.getValue(), this.doc.getNumberOfPages() - lastEntry.getKey().intValue());
        while (gen2.hasNext()) {
            handler.newLabel(pageIndex, gen2.next());
            pageIndex++;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/PDPageLabels$LabelGenerator.class */
    private static class LabelGenerator implements Iterator<String> {
        private final PDPageLabelRange labelInfo;
        private final int numPages;
        private int currentPage = 0;
        private static final String[][] ROMANS = {new String[]{"", OperatorName.SET_FLATNESS, "ii", "iii", "iv", OperatorName.CURVE_TO_REPLICATE_INITIAL_POINT, "vi", "vii", "viii", "ix"}, new String[]{"", "x", "xx", "xxx", "xl", OperatorName.LINE_TO, "lx", "lxx", "lxxx", "xc"}, new String[]{"", "c", "cc", "ccc", "cd", OperatorName.SET_LINE_DASHPATTERN, "dc", "dcc", "dccc", OperatorName.CONCAT}};

        LabelGenerator(PDPageLabelRange label, int pages) {
            this.labelInfo = label;
            this.numPages = pages;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.currentPage < this.numPages;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            StringBuilder buf = new StringBuilder();
            String label = this.labelInfo.getPrefix();
            if (label != null) {
                int index = label.indexOf(0);
                if (index > -1) {
                    label = label.substring(0, index);
                }
                buf.append(label);
            }
            String style = this.labelInfo.getStyle();
            if (style != null) {
                buf.append(getNumber(this.labelInfo.getStart() + this.currentPage, style));
            }
            this.currentPage++;
            return buf.toString();
        }

        private String getNumber(int pageIndex, String style) {
            if ("D".equals(style)) {
                return Integer.toString(pageIndex);
            }
            if (PDPageLabelRange.STYLE_LETTERS_LOWER.equals(style)) {
                return makeLetterLabel(pageIndex);
            }
            if ("A".equals(style)) {
                return makeLetterLabel(pageIndex).toUpperCase();
            }
            if (PDPageLabelRange.STYLE_ROMAN_LOWER.equals(style)) {
                return makeRomanLabel(pageIndex);
            }
            if ("R".equals(style)) {
                return makeRomanLabel(pageIndex).toUpperCase();
            }
            return Integer.toString(pageIndex);
        }

        private static String makeRomanLabel(int pageIndex) {
            StringBuilder buf = new StringBuilder();
            for (int power = 0; power < 3 && pageIndex > 0; power++) {
                buf.insert(0, ROMANS[power][pageIndex % 10]);
                pageIndex /= 10;
            }
            for (int i = 0; i < pageIndex; i++) {
                buf.insert(0, 'm');
            }
            return buf.toString();
        }

        private static String makeLetterLabel(int num) {
            StringBuilder buf = new StringBuilder();
            int numLetters = (num / 26) + Integer.signum(num % 26);
            int letter = (((num % 26) + (26 * (1 - Integer.signum(num % 26)))) + 97) - 1;
            for (int i = 0; i < numLetters; i++) {
                buf.appendCodePoint(letter);
            }
            return buf.toString();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
