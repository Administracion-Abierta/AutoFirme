package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.io.FileUtil;
import com.twelvemonkeys.lang.StringUtil;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/util/ReaderFileSuffixFilter.class */
public final class ReaderFileSuffixFilter extends FileFilter implements java.io.FileFilter {
    private final String description;
    private final Map<String, Boolean> knownSuffixes;

    public ReaderFileSuffixFilter() {
        this("Images (all supported input formats)");
    }

    public ReaderFileSuffixFilter(String str) {
        this.knownSuffixes = new HashMap(32);
        this.description = str;
    }

    @Override // java.io.FileFilter
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String extension = FileUtil.getExtension(file);
        return !StringUtil.isEmpty(extension) && hasReaderForSuffix(extension);
    }

    private boolean hasReaderForSuffix(String str) {
        if (this.knownSuffixes.get(str) == Boolean.TRUE) {
            return true;
        }
        try {
            if (ImageIO.getImageReadersBySuffix(str).hasNext()) {
                this.knownSuffixes.put(str, Boolean.TRUE);
                return true;
            }
            this.knownSuffixes.put(str, Boolean.FALSE);
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String getDescription() {
        return this.description;
    }
}
