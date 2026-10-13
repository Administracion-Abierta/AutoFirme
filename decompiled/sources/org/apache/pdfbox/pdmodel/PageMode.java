package org.apache.pdfbox.pdmodel;

import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PageMode.class */
public enum PageMode {
    USE_NONE(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE_USE_NONE),
    USE_OUTLINES(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE_USE_OUTLINES),
    USE_THUMBS(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE_USE_THUMBS),
    FULL_SCREEN("FullScreen"),
    USE_OPTIONAL_CONTENT(PDViewerPreferences.NON_FULL_SCREEN_PAGE_MODE_USE_OPTIONAL_CONTENT),
    USE_ATTACHMENTS("UseAttachments");

    private final String value;

    public static PageMode fromString(String value) {
        for (PageMode instance : values()) {
            if (instance.value.equals(value)) {
                return instance;
            }
        }
        throw new IllegalArgumentException(value);
    }

    PageMode(String value) {
        this.value = value;
    }

    public String stringValue() {
        return this.value;
    }
}
