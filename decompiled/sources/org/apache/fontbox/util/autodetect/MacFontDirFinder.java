package org.apache.fontbox.util.autodetect;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/util/autodetect/MacFontDirFinder.class */
public class MacFontDirFinder extends NativeFontDirFinder {
    @Override // org.apache.fontbox.util.autodetect.NativeFontDirFinder
    protected String[] getSearchableDirectories() {
        return new String[]{System.getProperty("user.home") + "/Library/Fonts/", "/Library/Fonts/", "/System/Library/Fonts/", "/Network/Library/Fonts/"};
    }
}
