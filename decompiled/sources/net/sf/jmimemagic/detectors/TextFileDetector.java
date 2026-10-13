package net.sf.jmimemagic.detectors;

import es.gob.afirma.standalone.CommandLineParameters;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.sf.jmimemagic.MagicDetector;
import org.apache.oro.text.perl.Perl5Util;

/* JADX INFO: loaded from: autofirma.jar:net/sf/jmimemagic/detectors/TextFileDetector.class */
public final class TextFileDetector implements MagicDetector {
    @Override // net.sf.jmimemagic.MagicDetector
    public String getDisplayName() {
        return "Text File Detector";
    }

    @Override // net.sf.jmimemagic.MagicDetector
    public String getVersion() {
        return "0.1";
    }

    @Override // net.sf.jmimemagic.MagicDetector
    public String[] getHandledExtensions() {
        return new String[]{CommandLineParameters.FORMAT_HASH_DIR_PLAIN, "text"};
    }

    @Override // net.sf.jmimemagic.MagicDetector
    public String[] getHandledTypes() {
        return new String[]{"text/plain"};
    }

    @Override // net.sf.jmimemagic.MagicDetector
    public String getName() {
        return "textfiledetector";
    }

    @Override // net.sf.jmimemagic.MagicDetector
    public String[] process(byte[] data, int offset, int length, long bitmask, char comparator, String mimeType, Map<String, String> params) {
        Perl5Util util = new Perl5Util();
        String s = new String(data, StandardCharsets.UTF_8);
        if (!util.match("/[^[:ascii:][:space:]]/", s)) {
            return new String[]{"text/plain"};
        }
        return null;
    }

    @Override // net.sf.jmimemagic.MagicDetector
    public String[] process(File file, int offset, int length, long bitmask, char comparator, String mimeType, Map<String, String> params) {
        return new String[0];
    }
}
