package com.aowagie.text;

import java.util.ArrayList;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/MarkedObject.class */
public class MarkedObject implements Element {
    protected Element element;
    protected Properties markupAttributes;

    protected MarkedObject() {
        this.markupAttributes = new Properties();
        this.element = null;
    }

    MarkedObject(Element element) {
        this.markupAttributes = new Properties();
        this.element = element;
    }

    @Override // com.aowagie.text.Element
    public ArrayList getChunks() {
        return this.element.getChunks();
    }

    @Override // com.aowagie.text.Element
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this.element);
        } catch (DocumentException e) {
            return false;
        }
    }

    @Override // com.aowagie.text.Element
    public int type() {
        return 50;
    }

    @Override // com.aowagie.text.Element
    public boolean isContent() {
        return true;
    }

    @Override // com.aowagie.text.Element
    public boolean isNestable() {
        return true;
    }

    public Properties getMarkupAttributes() {
        return this.markupAttributes;
    }
}
