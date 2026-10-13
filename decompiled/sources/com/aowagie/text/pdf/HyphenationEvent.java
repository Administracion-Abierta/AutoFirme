package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/HyphenationEvent.class */
public interface HyphenationEvent {
    String getHyphenSymbol();

    String getHyphenatedWordPre(String str, BaseFont baseFont, float f, float f2);

    String getHyphenatedWordPost();
}
