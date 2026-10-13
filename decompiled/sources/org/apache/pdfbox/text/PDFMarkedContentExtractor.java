package org.apache.pdfbox.text;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.contentstream.operator.markedcontent.BeginMarkedContentSequence;
import org.apache.pdfbox.contentstream.operator.markedcontent.BeginMarkedContentSequenceWithProperties;
import org.apache.pdfbox.contentstream.operator.markedcontent.DrawObject;
import org.apache.pdfbox.contentstream.operator.markedcontent.EndMarkedContentSequence;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/text/PDFMarkedContentExtractor.class */
public class PDFMarkedContentExtractor extends LegacyPDFStreamEngine {
    private boolean suppressDuplicateOverlappingText;
    private final List<PDMarkedContent> markedContents;
    private final Deque<PDMarkedContent> currentMarkedContents;
    private final Map<String, List<TextPosition>> characterListMapping;

    @Override // org.apache.pdfbox.text.LegacyPDFStreamEngine, org.apache.pdfbox.contentstream.PDFStreamEngine
    public /* bridge */ /* synthetic */ void processPage(PDPage pDPage) throws IOException {
        super.processPage(pDPage);
    }

    public PDFMarkedContentExtractor() throws IOException {
        this(null);
    }

    public PDFMarkedContentExtractor(String encoding) throws IOException {
        this.suppressDuplicateOverlappingText = true;
        this.markedContents = new ArrayList();
        this.currentMarkedContents = new ArrayDeque();
        this.characterListMapping = new HashMap();
        addOperator(new BeginMarkedContentSequenceWithProperties());
        addOperator(new BeginMarkedContentSequence());
        addOperator(new EndMarkedContentSequence());
        addOperator(new DrawObject());
    }

    public boolean isSuppressDuplicateOverlappingText() {
        return this.suppressDuplicateOverlappingText;
    }

    public void setSuppressDuplicateOverlappingText(boolean suppressDuplicateOverlappingText) {
        this.suppressDuplicateOverlappingText = suppressDuplicateOverlappingText;
    }

    private boolean within(float first, float second, float variance) {
        return second > first - variance && second < first + variance;
    }

    @Override // org.apache.pdfbox.contentstream.PDFStreamEngine
    public void beginMarkedContentSequence(COSName tag, COSDictionary properties) {
        PDMarkedContent markedContent = PDMarkedContent.create(tag, properties);
        if (this.currentMarkedContents.isEmpty()) {
            this.markedContents.add(markedContent);
        } else {
            PDMarkedContent currentMarkedContent = this.currentMarkedContents.peek();
            if (currentMarkedContent != null) {
                currentMarkedContent.addMarkedContent(markedContent);
            }
        }
        this.currentMarkedContents.push(markedContent);
    }

    @Override // org.apache.pdfbox.contentstream.PDFStreamEngine
    public void endMarkedContentSequence() {
        if (!this.currentMarkedContents.isEmpty()) {
            this.currentMarkedContents.pop();
        }
    }

    public void xobject(PDXObject xobject) {
        if (!this.currentMarkedContents.isEmpty()) {
            this.currentMarkedContents.peek().addXObject(xobject);
        }
    }

    @Override // org.apache.pdfbox.text.LegacyPDFStreamEngine
    protected void processTextPosition(TextPosition text) {
        boolean showCharacter = true;
        if (this.suppressDuplicateOverlappingText) {
            showCharacter = false;
            String textCharacter = text.getUnicode();
            float textX = text.getX();
            float textY = text.getY();
            List<TextPosition> sameTextCharacters = this.characterListMapping.get(textCharacter);
            if (sameTextCharacters == null) {
                sameTextCharacters = new ArrayList();
                this.characterListMapping.put(textCharacter, sameTextCharacters);
            }
            boolean suppressCharacter = false;
            float tolerance = (text.getWidth() / textCharacter.length()) / 3.0f;
            for (TextPosition sameTextCharacter : sameTextCharacters) {
                String charCharacter = sameTextCharacter.getUnicode();
                float charX = sameTextCharacter.getX();
                float charY = sameTextCharacter.getY();
                if (charCharacter != null && within(charX, textX, tolerance) && within(charY, textY, tolerance)) {
                    suppressCharacter = true;
                    break;
                }
            }
            if (!suppressCharacter) {
                sameTextCharacters.add(text);
                showCharacter = true;
            }
        }
        if (showCharacter) {
            List<TextPosition> textList = new ArrayList<>();
            if (textList.isEmpty()) {
                textList.add(text);
            } else {
                TextPosition previousTextPosition = textList.get(textList.size() - 1);
                if (text.isDiacritic() && previousTextPosition.contains(text)) {
                    previousTextPosition.mergeDiacritic(text);
                } else if (previousTextPosition.isDiacritic() && text.contains(previousTextPosition)) {
                    text.mergeDiacritic(previousTextPosition);
                    textList.remove(textList.size() - 1);
                    textList.add(text);
                } else {
                    textList.add(text);
                }
            }
            if (!this.currentMarkedContents.isEmpty()) {
                this.currentMarkedContents.peek().addText(text);
            }
        }
    }

    public List<PDMarkedContent> getMarkedContents() {
        return this.markedContents;
    }
}
