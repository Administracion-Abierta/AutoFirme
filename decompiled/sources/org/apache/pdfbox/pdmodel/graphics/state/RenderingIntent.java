package org.apache.pdfbox.pdmodel.graphics.state;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/state/RenderingIntent.class */
public enum RenderingIntent {
    ABSOLUTE_COLORIMETRIC("AbsoluteColorimetric"),
    RELATIVE_COLORIMETRIC("RelativeColorimetric"),
    SATURATION("Saturation"),
    PERCEPTUAL("Perceptual");

    private final String value;

    public static RenderingIntent fromString(String value) {
        for (RenderingIntent instance : values()) {
            if (instance.value.equals(value)) {
                return instance;
            }
        }
        return RELATIVE_COLORIMETRIC;
    }

    RenderingIntent(String value) {
        this.value = value;
    }

    public String stringValue() {
        return this.value;
    }
}
