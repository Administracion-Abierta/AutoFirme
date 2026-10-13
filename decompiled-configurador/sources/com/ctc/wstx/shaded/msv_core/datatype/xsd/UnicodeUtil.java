package com.ctc.wstx.shaded.msv_core.datatype.xsd;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/UnicodeUtil.class */
public class UnicodeUtil {
    public static int countLength(String str) {
        int len = str.length();
        int count = 0;
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (55296 > ch || ch >= 56320) {
                count++;
            }
        }
        return count;
    }
}
