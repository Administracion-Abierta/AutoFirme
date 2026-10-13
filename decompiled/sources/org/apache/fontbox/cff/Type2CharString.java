package org.apache.fontbox.cff;

import com.aowagie.text.pdf.ColumnText;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.fontbox.type1.Type1CharStringReader;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/Type2CharString.class */
public class Type2CharString extends Type1CharString {
    private float defWidthX;
    private float nominalWidthX;
    private int pathCount;
    private final List<Object> type2sequence;
    private final int gid;

    public Type2CharString(Type1CharStringReader font, String fontName, String glyphName, int gid, List<Object> sequence, int defaultWidthX, int nomWidthX) {
        super(font, fontName, glyphName);
        this.defWidthX = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.nominalWidthX = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.pathCount = 0;
        this.gid = gid;
        this.type2sequence = sequence;
        this.defWidthX = defaultWidthX;
        this.nominalWidthX = nomWidthX;
        convertType1ToType2(sequence);
    }

    public int getGID() {
        return this.gid;
    }

    public List<Object> getType2Sequence() {
        return this.type2sequence;
    }

    private void convertType1ToType2(List<Object> sequence) {
        this.type1Sequence = new ArrayList();
        this.pathCount = 0;
        CharStringHandler handler = new CharStringHandler() { // from class: org.apache.fontbox.cff.Type2CharString.1
            @Override // org.apache.fontbox.cff.CharStringHandler
            public List<Number> handleCommand(List<Number> numbers, CharStringCommand command) {
                return Type2CharString.this.handleCommand(numbers, command);
            }
        };
        handler.handleSequence(sequence);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<Number> handleCommand(List<Number> numbers, CharStringCommand command) {
        this.commandCount++;
        String name = CharStringCommand.TYPE2_VOCABULARY.get(command.getKey());
        if ("hstem".equals(name)) {
            expandStemHints(clearStack(numbers, numbers.size() % 2 != 0), true);
            return null;
        }
        if ("vstem".equals(name)) {
            expandStemHints(clearStack(numbers, numbers.size() % 2 != 0), false);
            return null;
        }
        if ("vmoveto".equals(name)) {
            List<Number> numbers2 = clearStack(numbers, numbers.size() > 1);
            markPath();
            addCommand(numbers2, command);
            return null;
        }
        if ("rlineto".equals(name)) {
            addCommandList(split(numbers, 2), command);
            return null;
        }
        if ("hlineto".equals(name)) {
            drawAlternatingLine(numbers, true);
            return null;
        }
        if ("vlineto".equals(name)) {
            drawAlternatingLine(numbers, false);
            return null;
        }
        if ("rrcurveto".equals(name)) {
            addCommandList(split(numbers, 6), command);
            return null;
        }
        if ("endchar".equals(name)) {
            List<Number> numbers3 = clearStack(numbers, numbers.size() == 5 || numbers.size() == 1);
            closeCharString2Path();
            if (numbers3.size() == 4) {
                numbers3.add(0, 0);
                addCommand(numbers3, new CharStringCommand(12, 6));
                return null;
            }
            addCommand(numbers3, command);
            return null;
        }
        if ("rmoveto".equals(name)) {
            List<Number> numbers4 = clearStack(numbers, numbers.size() > 2);
            markPath();
            addCommand(numbers4, command);
            return null;
        }
        if ("hmoveto".equals(name)) {
            List<Number> numbers5 = clearStack(numbers, numbers.size() > 1);
            markPath();
            addCommand(numbers5, command);
            return null;
        }
        if ("vhcurveto".equals(name)) {
            drawAlternatingCurve(numbers, false);
            return null;
        }
        if ("hvcurveto".equals(name)) {
            drawAlternatingCurve(numbers, true);
            return null;
        }
        if ("hflex".equals(name)) {
            List<Number> first = Arrays.asList(numbers.get(0), 0, numbers.get(1), numbers.get(2), numbers.get(3), 0);
            List<Number> second = Arrays.asList(numbers.get(4), 0, numbers.get(5), Float.valueOf(-numbers.get(2).floatValue()), numbers.get(6), 0);
            addCommandList(Arrays.asList(first, second), new CharStringCommand(8));
            return null;
        }
        if ("flex".equals(name)) {
            List<Number> first2 = numbers.subList(0, 6);
            List<Number> second2 = numbers.subList(6, 12);
            addCommandList(Arrays.asList(first2, second2), new CharStringCommand(8));
            return null;
        }
        if ("hflex1".equals(name)) {
            List<Number> first3 = Arrays.asList(numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3), numbers.get(4), 0);
            List<Number> second3 = Arrays.asList(numbers.get(5), 0, numbers.get(6), numbers.get(7), numbers.get(8), 0);
            addCommandList(Arrays.asList(first3, second3), new CharStringCommand(8));
            return null;
        }
        if ("flex1".equals(name)) {
            int dx = 0;
            int dy = 0;
            for (int i = 0; i < 5; i++) {
                dx += numbers.get(i * 2).intValue();
                dy += numbers.get((i * 2) + 1).intValue();
            }
            List<Number> first4 = numbers.subList(0, 6);
            Number[] numberArr = new Number[6];
            numberArr[0] = numbers.get(6);
            numberArr[1] = numbers.get(7);
            numberArr[2] = numbers.get(8);
            numberArr[3] = numbers.get(9);
            numberArr[4] = Math.abs(dx) > Math.abs(dy) ? numbers.get(10) : Integer.valueOf(-dx);
            numberArr[5] = Math.abs(dx) > Math.abs(dy) ? Integer.valueOf(-dy) : numbers.get(10);
            List<Number> second4 = Arrays.asList(numberArr);
            addCommandList(Arrays.asList(first4, second4), new CharStringCommand(8));
            return null;
        }
        if ("hstemhm".equals(name)) {
            expandStemHints(clearStack(numbers, numbers.size() % 2 != 0), true);
            return null;
        }
        if ("hintmask".equals(name) || "cntrmask".equals(name)) {
            List<Number> numbers6 = clearStack(numbers, numbers.size() % 2 != 0);
            if (!numbers6.isEmpty()) {
                expandStemHints(numbers6, false);
                return null;
            }
            return null;
        }
        if ("vstemhm".equals(name)) {
            expandStemHints(clearStack(numbers, numbers.size() % 2 != 0), false);
            return null;
        }
        if ("rcurveline".equals(name)) {
            if (numbers.size() >= 2) {
                addCommandList(split(numbers.subList(0, numbers.size() - 2), 6), new CharStringCommand(8));
                addCommand(numbers.subList(numbers.size() - 2, numbers.size()), new CharStringCommand(5));
                return null;
            }
            return null;
        }
        if ("rlinecurve".equals(name)) {
            if (numbers.size() >= 6) {
                addCommandList(split(numbers.subList(0, numbers.size() - 6), 2), new CharStringCommand(5));
                addCommand(numbers.subList(numbers.size() - 6, numbers.size()), new CharStringCommand(8));
                return null;
            }
            return null;
        }
        if ("vvcurveto".equals(name)) {
            drawCurve(numbers, false);
            return null;
        }
        if ("hhcurveto".equals(name)) {
            drawCurve(numbers, true);
            return null;
        }
        addCommand(numbers, command);
        return null;
    }

    private List<Number> clearStack(List<Number> numbers, boolean flag) {
        if (this.type1Sequence.isEmpty()) {
            if (flag) {
                addCommand(Arrays.asList(Float.valueOf(ColumnText.GLOBAL_SPACE_CHAR_RATIO), Float.valueOf(numbers.get(0).floatValue() + this.nominalWidthX)), new CharStringCommand(13));
                numbers = numbers.subList(1, numbers.size());
            } else {
                addCommand(Arrays.asList(Float.valueOf(ColumnText.GLOBAL_SPACE_CHAR_RATIO), Float.valueOf(this.defWidthX)), new CharStringCommand(13));
            }
        }
        return numbers;
    }

    private void expandStemHints(List<Number> numbers, boolean horizontal) {
    }

    private void markPath() {
        if (this.pathCount > 0) {
            closeCharString2Path();
        }
        this.pathCount++;
    }

    private void closeCharString2Path() {
        CharStringCommand command = this.pathCount > 0 ? (CharStringCommand) this.type1Sequence.get(this.type1Sequence.size() - 1) : null;
        CharStringCommand closepathCommand = new CharStringCommand(9);
        if (command != null && !closepathCommand.equals(command)) {
            addCommand(Collections.emptyList(), closepathCommand);
        }
    }

    private void drawAlternatingLine(List<Number> numbers, boolean horizontal) {
        while (!numbers.isEmpty()) {
            addCommand(numbers.subList(0, 1), new CharStringCommand(horizontal ? 6 : 7));
            numbers = numbers.subList(1, numbers.size());
            horizontal = !horizontal;
        }
    }

    private void drawAlternatingCurve(List<Number> numbers, boolean horizontal) {
        while (numbers.size() >= 4) {
            boolean last = numbers.size() == 5;
            if (horizontal) {
                Number[] numberArr = new Number[6];
                numberArr[0] = numbers.get(0);
                numberArr[1] = 0;
                numberArr[2] = numbers.get(1);
                numberArr[3] = numbers.get(2);
                numberArr[4] = last ? numbers.get(4) : 0;
                numberArr[5] = numbers.get(3);
                addCommand(Arrays.asList(numberArr), new CharStringCommand(8));
            } else {
                Number[] numberArr2 = new Number[6];
                numberArr2[0] = 0;
                numberArr2[1] = numbers.get(0);
                numberArr2[2] = numbers.get(1);
                numberArr2[3] = numbers.get(2);
                numberArr2[4] = numbers.get(3);
                numberArr2[5] = last ? numbers.get(4) : 0;
                addCommand(Arrays.asList(numberArr2), new CharStringCommand(8));
            }
            numbers = numbers.subList(last ? 5 : 4, numbers.size());
            horizontal = !horizontal;
        }
    }

    private void drawCurve(List<Number> numbers, boolean horizontal) {
        while (numbers.size() >= 4) {
            boolean first = numbers.size() % 4 == 1;
            if (horizontal) {
                Number[] numberArr = new Number[6];
                numberArr[0] = numbers.get(first ? 1 : 0);
                numberArr[1] = first ? numbers.get(0) : 0;
                numberArr[2] = numbers.get(first ? 2 : 1);
                numberArr[3] = numbers.get(first ? 3 : 2);
                numberArr[4] = numbers.get(first ? 4 : 3);
                numberArr[5] = 0;
                addCommand(Arrays.asList(numberArr), new CharStringCommand(8));
            } else {
                Number[] numberArr2 = new Number[6];
                numberArr2[0] = first ? numbers.get(0) : 0;
                numberArr2[1] = numbers.get(first ? 1 : 0);
                numberArr2[2] = numbers.get(first ? 2 : 1);
                numberArr2[3] = numbers.get(first ? 3 : 2);
                numberArr2[4] = 0;
                numberArr2[5] = numbers.get(first ? 4 : 3);
                addCommand(Arrays.asList(numberArr2), new CharStringCommand(8));
            }
            numbers = numbers.subList(first ? 5 : 4, numbers.size());
        }
    }

    private void addCommandList(List<List<Number>> numbers, CharStringCommand command) {
        for (List<Number> ns : numbers) {
            addCommand(ns, command);
        }
    }

    private void addCommand(List<Number> numbers, CharStringCommand command) {
        this.type1Sequence.addAll(numbers);
        this.type1Sequence.add(command);
    }

    private static <E> List<List<E>> split(List<E> list, int size) {
        int listSize = list.size() / size;
        List<List<E>> result = new ArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) {
            result.add(list.subList(i * size, (i + 1) * size));
        }
        return result;
    }
}
