package org.apache.fontbox.util.autodetect;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/util/autodetect/FontFileFinder.class */
public class FontFileFinder {
    private static final Log LOG = LogFactory.getLog(FontFileFinder.class);
    private FontDirFinder fontDirFinder = null;

    private FontDirFinder determineDirFinder() {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            return new WindowsFontDirFinder();
        }
        if (osName.startsWith("Mac")) {
            return new MacFontDirFinder();
        }
        if (osName.startsWith("OS/400")) {
            return new OS400FontDirFinder();
        }
        return new UnixFontDirFinder();
    }

    public List<URI> find() {
        if (this.fontDirFinder == null) {
            this.fontDirFinder = determineDirFinder();
        }
        List<File> fontDirs = this.fontDirFinder.find();
        List<URI> results = new ArrayList<>();
        for (File dir : fontDirs) {
            walk(dir, results);
        }
        return results;
    }

    public List<URI> find(String dir) {
        List<URI> results = new ArrayList<>();
        File directory = new File(dir);
        if (directory.isDirectory()) {
            walk(directory, results);
        }
        return results;
    }

    private void walk(File directory, List<URI> results) {
        File[] filelist;
        if (!directory.isDirectory() || (filelist = directory.listFiles()) == null) {
            return;
        }
        for (File file : filelist) {
            if (file.isDirectory()) {
                if (!file.getName().startsWith(".")) {
                    walk(file, results);
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("checkFontfile check " + file);
                }
                if (checkFontfile(file)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("checkFontfile found " + file);
                    }
                    results.add(file.toURI());
                }
            }
        }
    }

    private boolean checkFontfile(File file) {
        String name = file.getName().toLowerCase(Locale.US);
        return (name.endsWith(".ttf") || name.endsWith(".otf") || name.endsWith(".pfb") || name.endsWith(".ttc")) && !name.startsWith("fonts.");
    }
}
