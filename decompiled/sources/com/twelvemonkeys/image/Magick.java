package com.twelvemonkeys.image;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/Magick.class */
final class Magick {
    static final boolean DEBUG = useDebug();

    private static boolean useDebug() {
        try {
            return "TRUE".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.image.magick.debug"));
        } catch (Throwable th) {
            return false;
        }
    }

    private Magick() {
    }
}
