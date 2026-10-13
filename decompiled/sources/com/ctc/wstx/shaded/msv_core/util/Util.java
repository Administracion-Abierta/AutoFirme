package com.ctc.wstx.shaded.msv_core.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/util/Util.class */
public class Util {
    public static InputSource getInputSource(String fileOrURL) {
        try {
            new URL(fileOrURL);
            return new InputSource(fileOrURL);
        } catch (MalformedURLException e) {
            String path = new File(fileOrURL).getAbsolutePath();
            if (File.separatorChar != '/') {
                path = path.replace(File.separatorChar, '/');
            }
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            return new InputSource("file://" + path);
        }
    }

    public static boolean isAbsoluteURI(String uri) {
        int len = uri.length();
        if (len == 0) {
            return true;
        }
        if (len < 2) {
            return false;
        }
        char ch = uri.charAt(0);
        if (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z')) {
            for (int i = 1; i < len; i++) {
                char ch2 = uri.charAt(i);
                if (ch2 == ':') {
                    return true;
                }
                if (('a' > ch2 || ch2 > 'z') && (('A' > ch2 || ch2 > 'Z') && ch2 != '-' && ch2 != '+' && ch2 != '.')) {
                    return false;
                }
            }
            return false;
        }
        return false;
    }

    public static String which(Class clazz) {
        return which(clazz.getName(), clazz.getClassLoader());
    }

    public static String which(String classname, ClassLoader loader) {
        String classnameAsResource = classname.replace('.', '/') + ".class";
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        URL it = loader.getResource(classnameAsResource);
        if (it != null) {
            return it.toString();
        }
        return null;
    }
}
