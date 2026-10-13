package com.aowagie.text;

import java.util.EventListener;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/ElementListener.class */
public interface ElementListener extends EventListener {
    boolean add(Element element) throws DocumentException;
}
