package com.aowagie.text.pdf.parser;

import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.pdf.CMapAwareDocumentFont;
import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.DocumentFont;
import com.aowagie.text.pdf.PRIndirectReference;
import com.aowagie.text.pdf.PRTokeniser;
import com.aowagie.text.pdf.PdfArray;
import com.aowagie.text.pdf.PdfContentParser;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfLiteral;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfNumber;
import com.aowagie.text.pdf.PdfString;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor.class */
abstract class PdfContentStreamProcessor {
    private Map operators;
    private PdfDictionary resources;
    private final Stack gsStack = new Stack();
    private Matrix textMatrix;
    private Matrix textLineMatrix;

    public abstract void displayText(String str, Matrix matrix);

    public PdfContentStreamProcessor() {
        populateOperators();
        reset();
    }

    private void populateOperators() {
        this.operators = new LinkedHashMap();
        this.operators.put(OperatorName.SAVE, new PushGraphicsState());
        this.operators.put("Q", new PopGraphicsState());
        this.operators.put(OperatorName.CONCAT, new ModifyCurrentTransformationMatrix());
        this.operators.put(OperatorName.SET_GRAPHICS_STATE_PARAMS, new ProcessGraphicsStateResource());
        this.operators.put(OperatorName.SET_CHAR_SPACING, new SetTextCharacterSpacing());
        this.operators.put(OperatorName.SET_WORD_SPACING, new SetTextWordSpacing());
        this.operators.put(OperatorName.SET_TEXT_HORIZONTAL_SCALING, new SetTextHorizontalScaling());
        this.operators.put(OperatorName.SET_TEXT_LEADING, new SetTextLeading());
        this.operators.put(OperatorName.SET_FONT_AND_SIZE, new SetTextFont());
        this.operators.put(OperatorName.SET_TEXT_RENDERINGMODE, new SetTextRenderMode());
        this.operators.put(OperatorName.SET_TEXT_RISE, new SetTextRise());
        this.operators.put(OperatorName.BEGIN_TEXT, new BeginText());
        this.operators.put(OperatorName.END_TEXT, new EndText());
        this.operators.put(OperatorName.MOVE_TEXT, new TextMoveStartNextLine());
        this.operators.put("TD", new TextMoveStartNextLineWithLeading());
        this.operators.put(OperatorName.SET_MATRIX, new TextSetTextMatrix());
        this.operators.put(OperatorName.NEXT_LINE, new TextMoveNextLine());
        this.operators.put(OperatorName.SHOW_TEXT, new ShowText());
        this.operators.put(OperatorName.SHOW_TEXT_LINE, new MoveNextLineAndShowText());
        this.operators.put(OperatorName.SHOW_TEXT_LINE_AND_SPACE, new MoveNextLineAndShowTextWithSpacing());
        this.operators.put(OperatorName.SHOW_TEXT_ADJUSTED, new ShowTextArray());
    }

    public void reset() {
        this.gsStack.removeAllElements();
        this.gsStack.add(new GraphicsState());
        this.textMatrix = null;
        this.textLineMatrix = null;
        this.resources = null;
    }

    public GraphicsState gs() {
        return (GraphicsState) this.gsStack.peek();
    }

    public Matrix getCurrentTextMatrix() {
        return this.textMatrix;
    }

    public Matrix getCurrentTextLineMatrix() {
        return this.textLineMatrix;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invokeOperator(PdfLiteral operator, ArrayList operands) {
        ContentOperator op = (ContentOperator) this.operators.get(operator.toString());
        if (op == null) {
            return;
        }
        op.invoke(this, operator, operands);
    }

    private String decode(PdfString in) {
        byte[] bytes = in.getBytes();
        return gs().font.decode(bytes, 0, bytes.length);
    }

    private float getStringWidth(String string, float tj) {
        DocumentFont font = gs().font;
        char[] chars = string.toCharArray();
        float totalWidth = 0.0f;
        int length = chars.length;
        for (int i = 0; i < length; i++) {
            char c = chars[i];
            float w = font.getWidth(c) / 1000.0f;
            float wordSpacing = c == ' ' ? gs().wordSpacing : ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            totalWidth += (((w - (tj / 1000.0f)) * gs().fontSize) + gs().characterSpacing + wordSpacing) * gs().horizontalScaling;
        }
        return totalWidth;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayPdfString(PdfString string, float tj) {
        String unicode = decode(string);
        float width = getStringWidth(unicode, tj);
        Matrix nextTextMatrix = new Matrix(width, ColumnText.GLOBAL_SPACE_CHAR_RATIO).multiply(this.textMatrix);
        displayText(unicode, nextTextMatrix);
        this.textMatrix = nextTextMatrix;
    }

    public void processContent(byte[] contentBytes, PdfDictionary resources) {
        reset();
        this.resources = resources;
        try {
            PdfContentParser ps = new PdfContentParser(new PRTokeniser(contentBytes));
            ArrayList operands = new ArrayList();
            while (ps.parse(operands).size() > 0) {
                PdfLiteral operator = (PdfLiteral) operands.get(operands.size() - 1);
                invokeOperator(operator, operands);
            }
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$ShowTextArray.class */
    private static class ShowTextArray implements ContentOperator {
        private ShowTextArray() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            PdfArray array = (PdfArray) operands.get(0);
            float tj = 0.0f;
            Iterator i = array.listIterator();
            while (i.hasNext()) {
                Object entryObj = i.next();
                if (entryObj instanceof PdfString) {
                    processor.displayPdfString((PdfString) entryObj, tj);
                    tj = 0.0f;
                } else {
                    tj = ((PdfNumber) entryObj).floatValue();
                }
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$MoveNextLineAndShowTextWithSpacing.class */
    private static class MoveNextLineAndShowTextWithSpacing implements ContentOperator {
        private MoveNextLineAndShowTextWithSpacing() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            PdfNumber aw = (PdfNumber) operands.get(0);
            PdfNumber ac = (PdfNumber) operands.get(1);
            PdfString string = (PdfString) operands.get(2);
            ArrayList twOperands = new ArrayList(1);
            twOperands.add(0, aw);
            processor.invokeOperator(new PdfLiteral(OperatorName.SET_WORD_SPACING), twOperands);
            ArrayList tcOperands = new ArrayList(1);
            tcOperands.add(0, ac);
            processor.invokeOperator(new PdfLiteral(OperatorName.SET_CHAR_SPACING), tcOperands);
            ArrayList tickOperands = new ArrayList(1);
            tickOperands.add(0, string);
            processor.invokeOperator(new PdfLiteral(OperatorName.SHOW_TEXT_LINE), tickOperands);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$MoveNextLineAndShowText.class */
    private static class MoveNextLineAndShowText implements ContentOperator {
        private MoveNextLineAndShowText() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            processor.invokeOperator(new PdfLiteral(OperatorName.NEXT_LINE), new ArrayList(0));
            processor.invokeOperator(new PdfLiteral(OperatorName.SHOW_TEXT), operands);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$ShowText.class */
    private static class ShowText implements ContentOperator {
        private ShowText() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            PdfString string = (PdfString) operands.get(0);
            processor.displayPdfString(string, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$TextMoveNextLine.class */
    private static class TextMoveNextLine implements ContentOperator {
        private TextMoveNextLine() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            ArrayList tdoperands = new ArrayList(2);
            tdoperands.add(0, new PdfNumber(0));
            tdoperands.add(1, new PdfNumber(processor.gs().leading));
            processor.invokeOperator(new PdfLiteral(OperatorName.MOVE_TEXT), tdoperands);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$TextSetTextMatrix.class */
    private static class TextSetTextMatrix implements ContentOperator {
        private TextSetTextMatrix() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            float a = ((PdfNumber) operands.get(0)).floatValue();
            float b = ((PdfNumber) operands.get(1)).floatValue();
            float c = ((PdfNumber) operands.get(2)).floatValue();
            float d = ((PdfNumber) operands.get(3)).floatValue();
            float e = ((PdfNumber) operands.get(4)).floatValue();
            float f = ((PdfNumber) operands.get(5)).floatValue();
            processor.textLineMatrix = new Matrix(a, b, c, d, e, f);
            processor.textMatrix = processor.textLineMatrix;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$TextMoveStartNextLineWithLeading.class */
    private static class TextMoveStartNextLineWithLeading implements ContentOperator {
        private TextMoveStartNextLineWithLeading() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            float ty = ((PdfNumber) operands.get(1)).floatValue();
            ArrayList tlOperands = new ArrayList(1);
            tlOperands.add(0, new PdfNumber(-ty));
            processor.invokeOperator(new PdfLiteral(OperatorName.SET_TEXT_LEADING), tlOperands);
            processor.invokeOperator(new PdfLiteral(OperatorName.MOVE_TEXT), operands);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$TextMoveStartNextLine.class */
    private static class TextMoveStartNextLine implements ContentOperator {
        private TextMoveStartNextLine() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            float tx = ((PdfNumber) operands.get(0)).floatValue();
            float ty = ((PdfNumber) operands.get(1)).floatValue();
            Matrix translationMatrix = new Matrix(tx, ty);
            processor.textMatrix = translationMatrix.multiply(processor.textLineMatrix);
            processor.textLineMatrix = processor.textMatrix;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$SetTextFont.class */
    private static class SetTextFont implements ContentOperator {
        private SetTextFont() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            PdfName fontResourceName = (PdfName) operands.get(0);
            float size = ((PdfNumber) operands.get(1)).floatValue();
            PdfDictionary fontsDictionary = processor.resources.getAsDict(PdfName.FONT);
            CMapAwareDocumentFont font = new CMapAwareDocumentFont((PRIndirectReference) fontsDictionary.get(fontResourceName));
            processor.gs().font = font;
            processor.gs().fontSize = size;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$SetTextRenderMode.class */
    private static class SetTextRenderMode implements ContentOperator {
        private SetTextRenderMode() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            PdfNumber render = (PdfNumber) operands.get(0);
            processor.gs().renderMode = render.intValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$SetTextRise.class */
    private static class SetTextRise implements ContentOperator {
        private SetTextRise() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            PdfNumber rise = (PdfNumber) operands.get(0);
            processor.gs().rise = rise.floatValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$SetTextLeading.class */
    private static class SetTextLeading implements ContentOperator {
        private SetTextLeading() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            PdfNumber leading = (PdfNumber) operands.get(0);
            processor.gs().leading = leading.floatValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$SetTextHorizontalScaling.class */
    private static class SetTextHorizontalScaling implements ContentOperator {
        private SetTextHorizontalScaling() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            PdfNumber scale = (PdfNumber) operands.get(0);
            processor.gs().horizontalScaling = scale.floatValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$SetTextCharacterSpacing.class */
    private static class SetTextCharacterSpacing implements ContentOperator {
        private SetTextCharacterSpacing() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            PdfNumber charSpace = (PdfNumber) operands.get(0);
            processor.gs().characterSpacing = charSpace.floatValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$SetTextWordSpacing.class */
    private static class SetTextWordSpacing implements ContentOperator {
        private SetTextWordSpacing() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            PdfNumber wordSpace = (PdfNumber) operands.get(0);
            processor.gs().wordSpacing = wordSpace.floatValue();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$ProcessGraphicsStateResource.class */
    private static class ProcessGraphicsStateResource implements ContentOperator {
        private ProcessGraphicsStateResource() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            PdfName dictionaryName = (PdfName) operands.get(0);
            PdfDictionary extGState = processor.resources.getAsDict(PdfName.EXTGSTATE);
            if (extGState == null) {
                throw new IllegalArgumentException("Resources do not contain ExtGState entry. Unable to process operator " + operator);
            }
            PdfDictionary gsDic = extGState.getAsDict(dictionaryName);
            if (gsDic == null) {
                throw new IllegalArgumentException(dictionaryName + " is an unknown graphics state dictionary");
            }
            PdfArray fontParameter = gsDic.getAsArray(PdfName.FONT);
            if (fontParameter != null) {
                CMapAwareDocumentFont font = new CMapAwareDocumentFont((PRIndirectReference) fontParameter.getPdfObject(0));
                float size = fontParameter.getAsNumber(1).floatValue();
                processor.gs().font = font;
                processor.gs().fontSize = size;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$PushGraphicsState.class */
    private static class PushGraphicsState implements ContentOperator {
        private PushGraphicsState() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            GraphicsState gs = (GraphicsState) processor.gsStack.peek();
            GraphicsState copy = new GraphicsState(gs);
            processor.gsStack.push(copy);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$ModifyCurrentTransformationMatrix.class */
    private static class ModifyCurrentTransformationMatrix implements ContentOperator {
        private ModifyCurrentTransformationMatrix() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            float a = ((PdfNumber) operands.get(0)).floatValue();
            float b = ((PdfNumber) operands.get(1)).floatValue();
            float c = ((PdfNumber) operands.get(2)).floatValue();
            float d = ((PdfNumber) operands.get(3)).floatValue();
            float e = ((PdfNumber) operands.get(4)).floatValue();
            float f = ((PdfNumber) operands.get(5)).floatValue();
            Matrix matrix = new Matrix(a, b, c, d, e, f);
            GraphicsState gs = (GraphicsState) processor.gsStack.peek();
            gs.ctm = gs.ctm.multiply(matrix);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$PopGraphicsState.class */
    private static class PopGraphicsState implements ContentOperator {
        private PopGraphicsState() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            processor.gsStack.pop();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$BeginText.class */
    private static class BeginText implements ContentOperator {
        private BeginText() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            processor.textMatrix = new Matrix();
            processor.textLineMatrix = processor.textMatrix;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/PdfContentStreamProcessor$EndText.class */
    private static class EndText implements ContentOperator {
        private EndText() {
        }

        @Override // com.aowagie.text.pdf.parser.ContentOperator
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList operands) {
            processor.textMatrix = null;
            processor.textLineMatrix = null;
        }
    }
}
