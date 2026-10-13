package com.ctc.wstx.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/util/URLUtil.class */
public final class URLUtil {
    private static final Pattern URI_WINDOWS_FILE_PATTERN = Pattern.compile("^file:///\\p{Alpha}|.*$");

    private URLUtil() {
    }

    public static URL urlFromSystemId(String sysId) throws IOException {
        try {
            String sysId2 = cleanSystemId(sysId);
            int ix = sysId2.indexOf(58, 0);
            if (ix >= 3 && ix <= 8) {
                return new URL(sysId2);
            }
            String absPath = new File(sysId2).getAbsolutePath();
            char sep = File.separatorChar;
            if (sep != '/') {
                absPath = absPath.replace(sep, '/');
            }
            if (absPath.length() > 0 && absPath.charAt(0) != '/') {
                absPath = "/" + absPath;
            }
            return new URL("file", "", absPath);
        } catch (MalformedURLException e) {
            throwIOException(e, sysId);
            return null;
        }
    }

    public static URI uriFromSystemId(String sysId) throws IOException {
        try {
            if (sysId.indexOf(124, 0) > 0 && URI_WINDOWS_FILE_PATTERN.matcher(sysId).matches()) {
                return new URI(sysId.replace('|', ':'));
            }
            int ix = sysId.indexOf(58, 0);
            if (ix >= 3 && ix <= 8) {
                return new URI(sysId);
            }
            String absPath = new File(sysId).getAbsolutePath();
            char sep = File.separatorChar;
            if (sep != '/') {
                absPath = absPath.replace(sep, '/');
            }
            if (absPath.length() > 0 && absPath.charAt(0) != '/') {
                absPath = "/" + absPath;
            }
            return new URI("file", absPath, null);
        } catch (URISyntaxException e) {
            throwIOException(e, sysId);
            return null;
        }
    }

    public static URL urlFromSystemId(String sysId, URL ctxt) throws IOException {
        if (ctxt == null) {
            return urlFromSystemId(sysId);
        }
        try {
            sysId = cleanSystemId(sysId);
            return new URL(ctxt, sysId);
        } catch (MalformedURLException e) {
            throwIOException(e, sysId);
            return null;
        }
    }

    public static URL urlFromCurrentDir() throws IOException {
        File parent = new File("a").getAbsoluteFile().getParentFile();
        return toURL(parent);
    }

    public static InputStream inputStreamFromURL(URL url) throws IOException {
        String host;
        if ("file".equals(url.getProtocol()) && ((host = url.getHost()) == null || host.length() == 0)) {
            String path = url.getPath();
            if (path.indexOf(37) >= 0) {
                path = URLDecoder.decode(path, "UTF-8");
            }
            return new FileInputStream(path);
        }
        return url.openStream();
    }

    public static OutputStream outputStreamFromURL(URL url) throws IOException {
        String host;
        if ("file".equals(url.getProtocol()) && ((host = url.getHost()) == null || host.length() == 0)) {
            return new FileOutputStream(url.getPath());
        }
        return url.openConnection().getOutputStream();
    }

    public static URL toURL(File f) throws IOException {
        return f.toURI().toURL();
    }

    private static String cleanSystemId(String sysId) {
        int ix = sysId.indexOf(124);
        if (ix > 0 && URI_WINDOWS_FILE_PATTERN.matcher(sysId).matches()) {
            StringBuilder sb = new StringBuilder(sysId);
            sb.setCharAt(ix, ':');
            return sb.toString();
        }
        return sysId;
    }

    private static void throwIOException(Exception mex, String sysId) throws IOException {
        String msg = "[resolving systemId '" + sysId + "']: " + mex.toString();
        throw ExceptionUtil.constructIOException(msg, mex);
    }
}
