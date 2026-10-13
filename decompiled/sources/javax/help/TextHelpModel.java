package javax.help;

import javax.help.event.TextHelpModelListener;

/* JADX INFO: loaded from: autofirma.jar:javax/help/TextHelpModel.class */
public interface TextHelpModel extends HelpModel {

    /* JADX INFO: loaded from: autofirma.jar:javax/help/TextHelpModel$Highlight.class */
    public interface Highlight {
        int getStartOffset();

        int getEndOffset();
    }

    String getDocumentTitle();

    void setDocumentTitle(String str);

    void removeAllHighlights();

    void addHighlight(int i, int i2);

    void setHighlights(Highlight[] highlightArr);

    Highlight[] getHighlights();

    void addTextHelpModelListener(TextHelpModelListener textHelpModelListener);

    void removeTextHelpModelListener(TextHelpModelListener textHelpModelListener);
}
