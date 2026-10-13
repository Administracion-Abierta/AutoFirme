package org.apache.fontbox.util.autodetect;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/util/autodetect/UnixFontDirFinder.class */
public class UnixFontDirFinder extends NativeFontDirFinder {
    @Override // org.apache.fontbox.util.autodetect.NativeFontDirFinder
    protected String[] getSearchableDirectories() {
        return new String[]{System.getProperty("user.home") + "/.fonts", "/usr/local/fonts", "/usr/local/share/fonts", "/usr/share/fonts", "/usr/X11R6/lib/X11/fonts", "/usr/share/X11/fonts"};
    }
}
