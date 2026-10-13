package org.apache.fontbox.util.autodetect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/util/autodetect/NativeFontDirFinder.class */
public abstract class NativeFontDirFinder implements FontDirFinder {
    protected abstract String[] getSearchableDirectories();

    @Override // org.apache.fontbox.util.autodetect.FontDirFinder
    public List<File> find() {
        List<File> fontDirList = new ArrayList<>();
        String[] searchableDirectories = getSearchableDirectories();
        if (searchableDirectories != null) {
            for (String searchableDirectorie : searchableDirectories) {
                File fontDir = new File(searchableDirectorie);
                try {
                    if (fontDir.exists() && fontDir.canRead()) {
                        fontDirList.add(fontDir);
                    }
                } catch (SecurityException e) {
                }
            }
        }
        return fontDirList;
    }
}
