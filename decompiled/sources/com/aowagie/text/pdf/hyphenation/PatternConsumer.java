package com.aowagie.text.pdf.hyphenation;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/hyphenation/PatternConsumer.class */
interface PatternConsumer {
    void addClass(String str);

    void addException(String str, ArrayList arrayList);

    void addPattern(String str, String str2);
}
