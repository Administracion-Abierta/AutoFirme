package jakarta.xml.bind;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/JAXB.class */
public final class JAXB {
    private static volatile WeakReference<Cache> cache;

    private JAXB() {
    }

    /* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/JAXB$Cache.class */
    private static final class Cache {
        final Class type;
        final JAXBContext context;

        public Cache(Class type) throws JAXBException {
            this.type = type;
            this.context = JAXBContext.newInstance((Class<?>[]) new Class[]{type});
        }
    }

    private static <T> JAXBContext getContext(Class<T> type) throws JAXBException {
        Cache d;
        WeakReference<Cache> c = cache;
        if (c != null && (d = c.get()) != null && d.type == type) {
            return d.context;
        }
        Cache d2 = new Cache(type);
        cache = new WeakReference<>(d2);
        return d2.context;
    }

    public static <T> T unmarshal(File xml, Class<T> type) {
        try {
            JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(new StreamSource(xml), type);
            return item.getValue();
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        }
    }

    public static <T> T unmarshal(URL xml, Class<T> type) {
        try {
            JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
            return item.getValue();
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        } catch (IOException e2) {
            throw new DataBindingException(e2);
        }
    }

    public static <T> T unmarshal(URI xml, Class<T> type) {
        try {
            JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
            return item.getValue();
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        } catch (IOException e2) {
            throw new DataBindingException(e2);
        }
    }

    public static <T> T unmarshal(String xml, Class<T> type) {
        try {
            JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
            return item.getValue();
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        } catch (IOException e2) {
            throw new DataBindingException(e2);
        }
    }

    public static <T> T unmarshal(InputStream xml, Class<T> type) {
        try {
            JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
            return item.getValue();
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        } catch (IOException e2) {
            throw new DataBindingException(e2);
        }
    }

    public static <T> T unmarshal(Reader xml, Class<T> type) {
        try {
            JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
            return item.getValue();
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        } catch (IOException e2) {
            throw new DataBindingException(e2);
        }
    }

    public static <T> T unmarshal(Source xml, Class<T> type) {
        try {
            JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
            return item.getValue();
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        } catch (IOException e2) {
            throw new DataBindingException(e2);
        }
    }

    private static Source toSource(Object xml) throws IOException {
        if (xml == null) {
            throw new IllegalArgumentException("no XML is given");
        }
        if (xml instanceof String) {
            try {
                xml = new URI((String) xml);
            } catch (URISyntaxException e) {
                xml = new File((String) xml);
            }
        }
        if (xml instanceof File) {
            File file = (File) xml;
            return new StreamSource(file);
        }
        if (xml instanceof URI) {
            URI uri = (URI) xml;
            xml = uri.toURL();
        }
        if (xml instanceof URL) {
            URL url = (URL) xml;
            return new StreamSource(url.toExternalForm());
        }
        if (xml instanceof InputStream) {
            InputStream in = (InputStream) xml;
            return new StreamSource(in);
        }
        if (xml instanceof Reader) {
            Reader r = (Reader) xml;
            return new StreamSource(r);
        }
        if (xml instanceof Source) {
            return (Source) xml;
        }
        throw new IllegalArgumentException("I don't understand how to handle " + xml.getClass());
    }

    public static void marshal(Object jaxbObject, File xml) {
        _marshal(jaxbObject, xml);
    }

    public static void marshal(Object jaxbObject, URL xml) {
        _marshal(jaxbObject, xml);
    }

    public static void marshal(Object jaxbObject, URI xml) {
        _marshal(jaxbObject, xml);
    }

    public static void marshal(Object jaxbObject, String xml) {
        _marshal(jaxbObject, xml);
    }

    public static void marshal(Object jaxbObject, OutputStream xml) {
        _marshal(jaxbObject, xml);
    }

    public static void marshal(Object jaxbObject, Writer xml) {
        _marshal(jaxbObject, xml);
    }

    public static void marshal(Object jaxbObject, Result xml) {
        _marshal(jaxbObject, xml);
    }

    private static void _marshal(Object jaxbObject, Object xml) {
        JAXBContext context;
        try {
            if (jaxbObject instanceof JAXBElement) {
                context = getContext(((JAXBElement) jaxbObject).getDeclaredType());
            } else {
                Class<?> clazz = jaxbObject.getClass();
                XmlRootElement r = (XmlRootElement) clazz.getAnnotation(XmlRootElement.class);
                context = getContext(clazz);
                if (r == null) {
                    jaxbObject = new JAXBElement(new QName(inferName(clazz)), clazz, jaxbObject);
                }
            }
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(jaxbObject, toResult(xml));
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        } catch (IOException e2) {
            throw new DataBindingException(e2);
        }
    }

    private static String inferName(Class clazz) {
        String simpleName = clazz.getSimpleName();
        if (simpleName == null || simpleName.isEmpty()) {
            return simpleName;
        }
        if (simpleName.length() > 1 && Character.isUpperCase(simpleName.charAt(1)) && Character.isUpperCase(simpleName.charAt(0))) {
            return simpleName;
        }
        char[] chars = simpleName.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    private static Result toResult(Object xml) throws IOException {
        if (xml == null) {
            throw new IllegalArgumentException("no XML is given");
        }
        if (xml instanceof String) {
            try {
                xml = new URI((String) xml);
            } catch (URISyntaxException e) {
                xml = new File((String) xml);
            }
        }
        if (xml instanceof File) {
            File file = (File) xml;
            return new StreamResult(file);
        }
        if (xml instanceof URI) {
            URI uri = (URI) xml;
            xml = uri.toURL();
        }
        if (xml instanceof URL) {
            URL url = (URL) xml;
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(false);
            con.connect();
            return new StreamResult(con.getOutputStream());
        }
        if (xml instanceof OutputStream) {
            OutputStream os = (OutputStream) xml;
            return new StreamResult(os);
        }
        if (xml instanceof Writer) {
            Writer w = (Writer) xml;
            return new StreamResult(w);
        }
        if (xml instanceof Result) {
            return (Result) xml;
        }
        throw new IllegalArgumentException("I don't understand how to handle " + xml.getClass());
    }
}
