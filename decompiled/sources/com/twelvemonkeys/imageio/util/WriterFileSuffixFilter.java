package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.io.FileUtil;
import com.twelvemonkeys.lang.StringUtil;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/util/WriterFileSuffixFilter.class */
public final class WriterFileSuffixFilter extends FileFilter implements java.io.FileFilter {
    private final String description;
    private Map<String, Boolean> knownSuffixes;

    public WriterFileSuffixFilter() {
        this("Images (all supported output formats)");
    }

    public WriterFileSuffixFilter(String str) {
        this.knownSuffixes = new HashMap(32);
        this.description = str;
    }

    @Override // java.io.FileFilter
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String extension = FileUtil.getExtension(file);
        return !StringUtil.isEmpty(extension) && hasWriterForSuffix(extension);
    }

    private boolean hasWriterForSuffix(String str) {
        if (this.knownSuffixes.get(str) == Boolean.TRUE) {
            return true;
        }
        try {
            if (ImageIO.getImageWritersBySuffix(str).hasNext()) {
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
