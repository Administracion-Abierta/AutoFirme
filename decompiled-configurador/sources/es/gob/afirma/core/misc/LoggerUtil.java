package es.gob.afirma.core.misc;

import java.util.Arrays;
import org.spongycastle.asn1.x509.DisplayText;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/LoggerUtil.class */
public final class LoggerUtil {
    private static Boolean allowExtendedLogs = null;

    private LoggerUtil() {
    }

    public static String getCleanUserHomePath(String str) {
        return str.replace(Platform.getUserHome(), "USERHOME");
    }

    public static String getTrimStr(String str) {
        if (allowExtendedLogs == null) {
            allowExtendedLogs = Boolean.valueOf(Boolean.getBoolean("allow.extended.logs"));
        }
        if (!allowExtendedLogs.booleanValue() && str != null && str.length() >= 200) {
            return str.substring(0, DisplayText.DISPLAY_TEXT_MAXIMUM_SIZE) + "...";
        }
        return str;
    }

    public static String getTrimBytes(byte[] bArr) {
        if (allowExtendedLogs == null) {
            allowExtendedLogs = Boolean.valueOf(Boolean.getBoolean("allow.extended.logs"));
        }
        if (!allowExtendedLogs.booleanValue() && bArr != null && bArr.length > 200) {
            return new String(Arrays.copyOfRange(bArr, 0, DisplayText.DISPLAY_TEXT_MAXIMUM_SIZE)) + "...";
        }
        return new String(bArr);
    }
}
