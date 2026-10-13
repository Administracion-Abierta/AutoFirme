package org.codehaus.stax2.validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/validation/XMLValidationSchemaFactory.class */
public abstract class XMLValidationSchemaFactory {
    public static final String INTERNAL_ID_SCHEMA_DTD = "dtd";
    public static final String INTERNAL_ID_SCHEMA_RELAXNG = "relaxng";
    public static final String INTERNAL_ID_SCHEMA_W3C = "w3c";
    public static final String INTERNAL_ID_SCHEMA_TREX = "trex";
    static final HashMap<String, String> sSchemaIds = new HashMap<>();
    static final String JAXP_PROP_FILENAME = "jaxp.properties";
    public static final String SYSTEM_PROPERTY_FOR_IMPL = "org.codehaus.stax2.validation.XMLValidationSchemaFactory.";
    public static final String SERVICE_DEFINITION_PATH = "META-INF/services/org.codehaus.stax2.validation.XMLValidationSchemaFactory.";
    public static final String P_IS_NAMESPACE_AWARE = "org.codehaus2.stax2.validation.isNamespaceAware";
    public static final String P_ENABLE_CACHING = "org.codehaus2.stax2.validation.enableCaching";
    protected final String mSchemaType;

    public abstract XMLValidationSchema createSchema(InputStream inputStream, String str, String str2, String str3) throws XMLStreamException;

    public abstract XMLValidationSchema createSchema(Reader reader, String str, String str2) throws XMLStreamException;

    public abstract XMLValidationSchema createSchema(URL url) throws XMLStreamException;

    public abstract XMLValidationSchema createSchema(File file) throws XMLStreamException;

    public abstract boolean isPropertySupported(String str);

    public abstract boolean setProperty(String str, Object obj);

    public abstract Object getProperty(String str);

    static {
        sSchemaIds.put("http://www.w3.org/XML/1998/namespace", INTERNAL_ID_SCHEMA_DTD);
        sSchemaIds.put(XMLValidationSchema.SCHEMA_ID_RELAXNG, INTERNAL_ID_SCHEMA_RELAXNG);
        sSchemaIds.put("http://www.w3.org/2001/XMLSchema", INTERNAL_ID_SCHEMA_W3C);
        sSchemaIds.put("http://www.thaiopensource.com/trex", INTERNAL_ID_SCHEMA_TREX);
    }

    protected XMLValidationSchemaFactory(String st) {
        this.mSchemaType = st;
    }

    public static XMLValidationSchemaFactory newInstance(String schemaType) throws FactoryConfigurationError {
        return newInstance(schemaType, Thread.currentThread().getContextClassLoader());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.FactoryConfigurationError */
    public static XMLValidationSchemaFactory newInstance(String schemaType, ClassLoader classLoader) throws FactoryConfigurationError {
        Enumeration<?> en;
        String internalId = sSchemaIds.get(schemaType);
        if (internalId == null) {
            throw new FactoryConfigurationError("Unrecognized schema type (id '" + schemaType + "')");
        }
        String propertyId = SYSTEM_PROPERTY_FOR_IMPL + internalId;
        SecurityException secEx = null;
        try {
            String clsName = System.getProperty(propertyId);
            if (clsName != null && clsName.length() > 0) {
                return createNewInstance(classLoader, clsName);
            }
        } catch (SecurityException se) {
            secEx = se;
        }
        try {
            String home = System.getProperty("java.home");
            File f = new File(new File(new File(home), "lib"), JAXP_PROP_FILENAME);
            if (f.exists()) {
                try {
                    Properties props = new Properties();
                    props.load(new FileInputStream(f));
                    String clsName2 = props.getProperty(propertyId);
                    if (clsName2 != null && clsName2.length() > 0) {
                        return createNewInstance(classLoader, clsName2);
                    }
                } catch (IOException e) {
                }
            }
        } catch (SecurityException se2) {
            secEx = se2;
        }
        String path = SERVICE_DEFINITION_PATH + internalId;
        try {
            if (classLoader == null) {
                en = ClassLoader.getSystemResources(path);
            } else {
                en = classLoader.getResources(path);
            }
            if (en != null) {
                while (en.hasMoreElements()) {
                    URL url = en.nextElement();
                    InputStream is = url.openStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
                    String clsName3 = null;
                    while (true) {
                        try {
                            String line = rd.readLine();
                            if (line == null) {
                                break;
                            }
                            String line2 = line.trim();
                            if (line2.length() > 0 && line2.charAt(0) != '#') {
                                clsName3 = line2;
                                break;
                            }
                        } catch (Throwable th) {
                            rd.close();
                            throw th;
                        }
                    }
                    rd.close();
                    if (clsName3 != null && clsName3.length() > 0) {
                        return createNewInstance(classLoader, clsName3);
                    }
                }
            }
        } catch (IOException e2) {
        } catch (SecurityException se3) {
            secEx = se3;
        }
        String msg = "No XMLValidationSchemaFactory implementation class specified or accessible (via system property '" + propertyId + "', or service definition under '" + path + "')";
        if (secEx != null) {
            throw new FactoryConfigurationError(msg + " (possibly caused by: " + secEx + ")", secEx);
        }
        throw new FactoryConfigurationError(msg);
    }

    public XMLValidationSchema createSchema(InputStream in) throws XMLStreamException {
        return createSchema(in, null);
    }

    public XMLValidationSchema createSchema(InputStream in, String encoding) throws XMLStreamException {
        return createSchema(in, encoding, null, null);
    }

    public XMLValidationSchema createSchema(Reader r) throws XMLStreamException {
        return createSchema(r, null, null);
    }

    public final String getSchemaType() {
        return this.mSchemaType;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.FactoryConfigurationError */
    private static XMLValidationSchemaFactory createNewInstance(ClassLoader cloader, String clsName) throws FactoryConfigurationError {
        Class<?> factoryClass;
        try {
            if (cloader == null) {
                factoryClass = Class.forName(clsName);
            } else {
                factoryClass = cloader.loadClass(clsName);
            }
            return (XMLValidationSchemaFactory) factoryClass.newInstance();
        } catch (ClassNotFoundException x) {
            throw new FactoryConfigurationError("XMLValidationSchemaFactory implementation '" + clsName + "' not found (missing jar in classpath?)", x);
        } catch (Exception x2) {
            throw new FactoryConfigurationError("XMLValidationSchemaFactory implementation '" + clsName + "' could not be instantiated: " + x2, x2);
        }
    }
}
