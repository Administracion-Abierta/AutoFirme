package com.twelvemonkeys.net;

import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.lang.SystemUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/net/MIMEUtil.class */
public final class MIMEUtil {
    private static Map<String, List<String>> sExtToMIME = new HashMap();
    private static Map<String, List<String>> sUnmodifiableExtToMIME = Collections.unmodifiableMap(sExtToMIME);
    private static Map<String, List<String>> sMIMEToExt = new HashMap();
    private static Map<String, List<String>> sUnmodifiableMIMEToExt = Collections.unmodifiableMap(sMIMEToExt);

    static {
        try {
            for (Map.Entry entry : SystemUtil.loadProperties(MIMEUtil.class).entrySet()) {
                List<String> listUnmodifiableList = Collections.unmodifiableList(Arrays.asList(StringUtil.toStringArray(StringUtil.toLowerCase((String) entry.getKey()), ";, ")));
                List<String> listUnmodifiableList2 = Collections.unmodifiableList(Arrays.asList(StringUtil.toStringArray(StringUtil.toLowerCase((String) entry.getValue()), ";, ")));
                Iterator<String> it = listUnmodifiableList.iterator();
                while (it.hasNext()) {
                    sExtToMIME.put(it.next(), listUnmodifiableList2);
                }
                Iterator<String> it2 = listUnmodifiableList2.iterator();
                while (it2.hasNext()) {
                    sMIMEToExt.put(it2.next(), listUnmodifiableList);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read properties for MIMEUtil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private MIMEUtil() {
    }

    public static String getMIMEType(String str) {
        List<String> list = sExtToMIME.get(StringUtil.toLowerCase(str));
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static List<String> getMIMETypes(String str) {
        return maskNull(sExtToMIME.get(StringUtil.toLowerCase(str)));
    }

    public static Map<String, List<String>> getMIMETypeMappings() {
        return sUnmodifiableExtToMIME;
    }

    public static String getExtension(String str) {
        List<String> list = sMIMEToExt.get(bareMIME(StringUtil.toLowerCase(str)));
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static List<String> getExtensions(String str) {
        String strBareMIME = bareMIME(StringUtil.toLowerCase(str));
        if (strBareMIME.endsWith("/*")) {
            return getExtensionForWildcard(strBareMIME);
        }
        return maskNull(sMIMEToExt.get(strBareMIME));
    }

    private static List<String> getExtensionForWildcard(String str) {
        String strSubstring = str.substring(0, str.length() - 1);
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (Map.Entry<String, List<String>> entry : sMIMEToExt.entrySet()) {
            if ("*/".equals(strSubstring) || entry.getKey().startsWith(strSubstring)) {
                linkedHashSet.addAll(entry.getValue());
            }
        }
        return Collections.unmodifiableList(new ArrayList(linkedHashSet));
    }

    public static Map<String, List<String>> getExtensionMappings() {
        return sUnmodifiableMIMEToExt;
    }

    static boolean includes(String str, String str2) {
        String strBareMIME = bareMIME(str2);
        return strBareMIME.equals(str) || "*/*".equals(str) || (str.endsWith("/*") && str.startsWith(strBareMIME.substring(0, strBareMIME.indexOf(47))));
    }

    public static String bareMIME(String str) {
        int iIndexOf;
        if (str != null && (iIndexOf = str.indexOf(59)) >= 0) {
            return str.substring(0, iIndexOf);
        }
        return str;
    }

    private static List<String> maskNull(List<String> list) {
        return list == null ? Collections.emptyList() : list;
    }

    public static void main(String[] strArr) {
        if (strArr.length > 1) {
            String str = strArr[0];
            String str2 = strArr[1];
            System.out.println("Mime type family " + str2 + (includes(str2, str) ? " includes " : " does not include ") + "type " + str);
        }
        if (strArr.length > 0) {
            String str3 = strArr[0];
            if (str3.indexOf(47) >= 0) {
                String extension = getExtension(str3);
                System.out.println("Default extension for MIME type '" + str3 + "' is " + (extension != null ? ": '" + extension + OperatorName.SHOW_TEXT_LINE : "unknown") + ".");
                System.out.println("All possible: " + getExtensions(str3));
                return;
            } else {
                String mIMEType = getMIMEType(str3);
                System.out.println("Default MIME type for extension '" + str3 + "' is " + (mIMEType != null ? ": '" + mIMEType + OperatorName.SHOW_TEXT_LINE : "unknown") + ".");
                System.out.println("All possible: " + getMIMETypes(str3));
                return;
            }
        }
        Set<String> setKeySet = sMIMEToExt.keySet();
        String[] strArr2 = new String[setKeySet.size()];
        int i = 0;
        Iterator<String> it = setKeySet.iterator();
        while (it.hasNext()) {
            strArr2[i] = it.next();
            i++;
        }
        Arrays.sort(strArr2);
        System.out.println("Known MIME types (" + strArr2.length + "):");
        for (int i2 = 0; i2 < strArr2.length; i2++) {
            String str4 = strArr2[i2];
            if (i2 != 0) {
                System.out.print(", ");
            }
            System.out.print(str4);
        }
        System.out.println("\n");
        Set<String> setKeySet2 = sExtToMIME.keySet();
        String[] strArr3 = new String[setKeySet2.size()];
        int i3 = 0;
        Iterator<String> it2 = setKeySet2.iterator();
        while (it2.hasNext()) {
            strArr3[i3] = it2.next();
            i3++;
        }
        Arrays.sort(strArr3);
        System.out.println("Known file types (" + strArr3.length + "):");
        for (int i4 = 0; i4 < strArr3.length; i4++) {
            String str5 = strArr3[i4];
            if (i4 != 0) {
                System.out.print(", ");
            }
            System.out.print(str5);
        }
        System.out.println();
    }
}
