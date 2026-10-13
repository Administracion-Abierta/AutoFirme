package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/handlers/PDAppearanceHandler.class */
public interface PDAppearanceHandler {
    void generateAppearanceStreams();

    void generateNormalAppearance();

    void generateRolloverAppearance();

    void generateDownAppearance();
}
