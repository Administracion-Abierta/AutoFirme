package com.ctc.wstx.shaded.msv_core.scanner.dtd;

import com.ctc.wstx.io.CharsetNames;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/Resolver.class */
public class Resolver implements EntityResolver {
    private boolean ignoringMIME;
    private Hashtable id2uri;
    private Hashtable id2resource;
    private Hashtable id2loader;
    private static final String[] types = {"application/xml", "text/xml", "text/plain", "text/html", "application/x-netcdf", "content/unknown"};

    public static InputSource createInputSource(String contentType, InputStream stream, boolean checkType, String scheme) throws IOException {
        String charset = null;
        if (contentType != null) {
            String contentType2 = contentType.toLowerCase();
            int index = contentType2.indexOf(59);
            if (index != -1) {
                String attributes = contentType2.substring(index + 1);
                contentType2 = contentType2.substring(0, index);
                int index2 = attributes.indexOf("charset");
                if (index2 != -1) {
                    String attributes2 = attributes.substring(index2 + 7);
                    int index3 = attributes2.indexOf(59);
                    if (index3 != -1) {
                        attributes2 = attributes2.substring(0, index3);
                    }
                    int index4 = attributes2.indexOf(61);
                    if (index4 != -1) {
                        String attributes3 = attributes2.substring(index4 + 1);
                        int index5 = attributes3.indexOf(40);
                        if (index5 != -1) {
                            attributes3 = attributes3.substring(0, index5);
                        }
                        int index6 = attributes3.indexOf(34);
                        if (index6 != -1) {
                            String attributes4 = attributes3.substring(index6 + 1);
                            attributes3 = attributes4.substring(0, attributes4.indexOf(34));
                        }
                        charset = attributes3.trim();
                    }
                }
            }
            if (checkType) {
                boolean isOK = false;
                for (int i = 0; i < types.length; i++) {
                    if (types[i].equals(contentType2)) {
                        isOK = true;
                        break;
                    }
                }
                if (!isOK) {
                    throw new IOException("Not XML: " + contentType2);
                }
            }
            if (charset == null && contentType2.trim().startsWith("text/") && !"file".equalsIgnoreCase(scheme)) {
                charset = CharsetNames.CS_US_ASCII;
            }
        }
        InputSource retval = new InputSource(XmlReader.createReader(stream, charset));
        retval.setByteStream(stream);
        retval.setEncoding(charset);
        return retval;
    }

    public static InputSource createInputSource(URL uri, boolean checkType) throws IOException {
        InputSource retval;
        URLConnection conn = uri.openConnection();
        if (checkType) {
            String contentType = conn.getContentType();
            retval = createInputSource(contentType, conn.getInputStream(), false, uri.getProtocol());
        } else {
            retval = new InputSource(XmlReader.createReader(conn.getInputStream()));
        }
        retval.setSystemId(conn.getURL().toString());
        return retval;
    }

    public static InputSource createInputSource(File file) throws IOException {
        InputSource retval = new InputSource(XmlReader.createReader(new FileInputStream(file)));
        String path = file.getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith("/") && file.isDirectory()) {
            path = path + "/";
        }
        retval.setSystemId("file:" + path);
        return retval;
    }

    @Override // org.xml.sax.EntityResolver
    public InputSource resolveEntity(String name, String uri) throws IOException {
        String uri2;
        InputSource retval;
        InputStream stream;
        String mappedURI = name2uri(name);
        if (mappedURI == null && (stream = mapResource(name)) != null) {
            uri2 = "java:resource:" + ((String) this.id2resource.get(name));
            retval = new InputSource(XmlReader.createReader(stream));
        } else {
            if (mappedURI != null) {
                uri = mappedURI;
            } else if (uri == null) {
                return null;
            }
            URL url = new URL(uri);
            URLConnection conn = url.openConnection();
            uri2 = conn.getURL().toString();
            if (this.ignoringMIME) {
                retval = new InputSource(XmlReader.createReader(conn.getInputStream()));
            } else {
                String contentType = conn.getContentType();
                retval = createInputSource(contentType, conn.getInputStream(), false, url.getProtocol());
            }
        }
        retval.setSystemId(uri2);
        retval.setPublicId(name);
        return retval;
    }

    public boolean isIgnoringMIME() {
        return this.ignoringMIME;
    }

    public void setIgnoringMIME(boolean value) {
        this.ignoringMIME = value;
    }

    private String name2uri(String publicId) {
        if (publicId == null || this.id2uri == null) {
            return null;
        }
        return (String) this.id2uri.get(publicId);
    }

    public void registerCatalogEntry(String publicId, String uri) {
        if (this.id2uri == null) {
            this.id2uri = new Hashtable(17);
        }
        this.id2uri.put(publicId, uri);
    }

    private InputStream mapResource(String publicId) {
        if (publicId == null || this.id2resource == null) {
            return null;
        }
        String resourceName = (String) this.id2resource.get(publicId);
        ClassLoader loader = null;
        if (resourceName == null) {
            return null;
        }
        if (this.id2loader != null) {
            loader = (ClassLoader) this.id2loader.get(publicId);
        }
        if (loader == null) {
            return ClassLoader.getSystemResourceAsStream(resourceName);
        }
        return loader.getResourceAsStream(resourceName);
    }

    public void registerCatalogEntry(String publicId, String resourceName, ClassLoader loader) {
        if (this.id2resource == null) {
            this.id2resource = new Hashtable(17);
        }
        this.id2resource.put(publicId, resourceName);
        if (loader != null) {
            if (this.id2loader == null) {
                this.id2loader = new Hashtable(17);
            }
            this.id2loader.put(publicId, loader);
        }
    }
}
