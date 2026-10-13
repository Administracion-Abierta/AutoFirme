package org.apache.pdfbox.pdmodel.font;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/FontMappers.class */
public final class FontMappers {
    private static FontMapper instance;

    private FontMappers() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/FontMappers$DefaultFontMapper.class */
    private static class DefaultFontMapper {
        private static final FontMapper INSTANCE = new FontMapperImpl();

        private DefaultFontMapper() {
        }
    }

    public static FontMapper instance() {
        if (instance == null) {
            instance = DefaultFontMapper.INSTANCE;
        }
        return instance;
    }

    public static synchronized void set(FontMapper fontMapper) {
        instance = fontMapper;
    }
}
