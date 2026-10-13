package com.ctc.wstx.shaded.msv.org_isorelax.verifier;

import com.ctc.wstx.shaded.msv_core.relaxns.reader.RELAXNSReader;
import com.twelvemonkeys.util.service.ServiceRegistry;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_isorelax/verifier/VerifierFactory.class */
public abstract class VerifierFactory {
    private EntityResolver resolver = null;
    private static HashMap providerMap = new HashMap();

    public abstract Schema compileSchema(InputSource inputSource) throws VerifierConfigurationException, SAXException, IOException;

    public Verifier newVerifier(String s) throws VerifierConfigurationException, SAXException, IOException {
        return compileSchema(s).newVerifier();
    }

    public Verifier newVerifier(File file) throws VerifierConfigurationException, SAXException, IOException {
        return compileSchema(file).newVerifier();
    }

    public Verifier newVerifier(InputStream inputstream) throws VerifierConfigurationException, SAXException, IOException {
        return compileSchema(inputstream, null).newVerifier();
    }

    public Verifier newVerifier(InputStream inputstream, String s) throws VerifierConfigurationException, SAXException, IOException {
        return compileSchema(inputstream, s).newVerifier();
    }

    public Verifier newVerifier(InputSource inputsource) throws VerifierConfigurationException, SAXException, IOException {
        return compileSchema(inputsource).newVerifier();
    }

    public Schema compileSchema(String s) throws VerifierConfigurationException, SAXException, IOException {
        return compileSchema(new InputSource(s));
    }

    public Schema compileSchema(InputStream inputstream) throws VerifierConfigurationException, SAXException, IOException {
        return compileSchema(inputstream, null);
    }

    public Schema compileSchema(InputStream inputstream, String s) throws VerifierConfigurationException, SAXException, IOException {
        InputSource inputsource = new InputSource(inputstream);
        inputsource.setSystemId(s);
        return compileSchema(inputsource);
    }

    public Schema compileSchema(File file) throws VerifierConfigurationException, SAXException, IOException {
        String s = "file:" + file.getAbsolutePath();
        if (File.separatorChar == '\\') {
            s = s.replace('\\', '/');
        }
        return compileSchema(new InputSource(s));
    }

    public boolean isFeature(String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (Verifier.FEATURE_HANDLER.equals(s) || Verifier.FEATURE_FILTER.equals(s)) {
            return true;
        }
        throw new SAXNotRecognizedException(s);
    }

    public void setFeature(String s, boolean flag) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(s);
    }

    public Object getProperty(String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(s);
    }

    public void setProperty(String s, Object obj) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(s);
    }

    public void setEntityResolver(EntityResolver entityresolver) {
        this.resolver = entityresolver;
    }

    public EntityResolver getEntityResolver() {
        return this.resolver;
    }

    public static VerifierFactory newInstance() throws VerifierConfigurationException {
        return newInstance(RELAXNSReader.RELAXNamespaceNamespace);
    }

    public static VerifierFactory newInstance(String s) throws VerifierConfigurationException {
        Iterator iterator = providers(VerifierFactoryLoader.class);
        while (iterator.hasNext()) {
            VerifierFactoryLoader verifierfactoryloader = (VerifierFactoryLoader) iterator.next();
            VerifierFactory verifierfactory = verifierfactoryloader.createFactory(s);
            if (verifierfactory != null) {
                return verifierfactory;
            }
        }
        throw new VerifierConfigurationException("no validation engine available for: " + s);
    }

    private static synchronized Iterator providers(Class class1) {
        ClassLoader classloader = class1.getClassLoader();
        String s = ServiceRegistry.SERVICES + class1.getName();
        Vector vector = (Vector) providerMap.get(s);
        if (vector != null) {
            return vector.iterator();
        }
        Vector vector2 = new Vector();
        providerMap.put(s, vector2);
        try {
            Enumeration<URL> resources = classloader.getResources(s);
            while (resources.hasMoreElements()) {
                try {
                    URL url = resources.nextElement();
                    InputStream inputstream = url.openStream();
                    InputStreamReader inputstreamreader = new InputStreamReader(inputstream, "UTF-8");
                    BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
                    String s1 = bufferedreader.readLine();
                    while (s1 != null) {
                        try {
                            int i = s1.indexOf(35);
                            if (i != -1) {
                                s1 = s1.substring(0, i);
                            }
                            String s2 = s1.trim();
                            if (s2.length() == 0) {
                                s1 = bufferedreader.readLine();
                            } else {
                                Object obj = classloader.loadClass(s2).newInstance();
                                vector2.add(obj);
                                s1 = bufferedreader.readLine();
                            }
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e2) {
                }
            }
            return vector2.iterator();
        } catch (IOException e3) {
            return vector2.iterator();
        }
    }
}
