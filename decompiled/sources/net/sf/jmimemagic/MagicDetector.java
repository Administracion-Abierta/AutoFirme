package net.sf.jmimemagic;

import java.io.File;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:net/sf/jmimemagic/MagicDetector.class */
public interface MagicDetector {
    String getName();

    String getDisplayName();

    String getVersion();

    String[] getHandledTypes();

    String[] getHandledExtensions();

    String[] process(byte[] bArr, int i, int i2, long j, char c, String str, Map<String, String> map);

    String[] process(File file, int i, int i2, long j, char c, String str, Map<String, String> map);
}
