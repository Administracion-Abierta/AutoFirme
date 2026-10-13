package org.apache.xml.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/Init.class */
public class Init {
    public static final String CONF_NS = "http://www.xmlsecurity.org/NS/#configuration";
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) Init.class);
    private static boolean alreadyInitialized = false;

    public static final synchronized boolean isInitialized() {
        return alreadyInitialized;
    }

    /* JADX WARN: Not initialized variable reg: 5, insn: 0x004b: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r5 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('is' java.io.InputStream)]) A[TRY_LEAVE], block:B:23:0x004b */
    /* JADX WARN: Not initialized variable reg: 6, insn: 0x004f: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r6 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:25:0x004f */
    public static synchronized void init() {
        InputStream is;
        Throwable th;
        if (alreadyInitialized) {
            return;
        }
        PrivilegedAction<InputStream> action = () -> {
            String cfile = System.getProperty("org.apache.xml.security.resource.config");
            if (cfile == null) {
                return null;
            }
            return ClassLoaderUtils.getResourceAsStream(cfile, Init.class);
        };
        try {
            try {
                InputStream is2 = (InputStream) AccessController.doPrivileged(action);
                Throwable th2 = null;
                try {
                    if (is2 == null) {
                        dynamicInit();
                    } else {
                        fileInit(is2);
                    }
                    if (is2 != null) {
                        if (0 != 0) {
                            try {
                                is2.close();
                            } catch (Throwable th3) {
                                th2.addSuppressed(th3);
                            }
                        } else {
                            is2.close();
                        }
                    }
                    alreadyInitialized = true;
                } catch (Throwable th4) {
                    throw th4;
                }
            } catch (Throwable th5) {
                if (is != null) {
                    if (th != null) {
                        try {
                            is.close();
                        } catch (Throwable th6) {
                            th.addSuppressed(th6);
                        }
                    } else {
                        is.close();
                    }
                }
                throw th5;
            }
        } catch (IOException ex) {
            LOG.warn(ex.getMessage(), (Throwable) ex);
        }
    }

    private static void dynamicInit() {
        I18n.init("en", "US");
        LOG.debug("Registering default algorithms");
        try {
            ElementProxy.registerDefaultPrefixes();
        } catch (XMLSecurityException ex) {
            LOG.error(ex.getMessage(), (Throwable) ex);
        }
        Transform.registerDefaultAlgorithms();
        SignatureAlgorithm.registerDefaultAlgorithms();
        JCEMapper.registerDefaultAlgorithms();
        Canonicalizer.registerDefaultAlgorithms();
        ResourceResolver.registerDefaultResolvers();
        KeyResolver.registerDefaultResolvers();
    }

    private static void fileInit(InputStream is) {
        Node algorithmsNode;
        try {
            Document doc = XMLUtils.read(is, true);
            Node config = doc.getFirstChild();
            while (config != null && !"Configuration".equals(config.getLocalName())) {
                config = config.getNextSibling();
            }
            if (config == null) {
                LOG.error("Error in reading configuration file - Configuration element not found");
                return;
            }
            for (Node el = config.getFirstChild(); el != null; el = el.getNextSibling()) {
                if (1 == el.getNodeType()) {
                    String tag = el.getLocalName();
                    if ("ResourceBundles".equals(tag)) {
                        Element resource = (Element) el;
                        Attr langAttr = resource.getAttributeNodeNS(null, "defaultLanguageCode");
                        Attr countryAttr = resource.getAttributeNodeNS(null, "defaultCountryCode");
                        String languageCode = langAttr == null ? null : langAttr.getNodeValue();
                        String countryCode = countryAttr == null ? null : countryAttr.getNodeValue();
                        I18n.init(languageCode, countryCode);
                    }
                    if ("CanonicalizationMethods".equals(tag)) {
                        Element[] list = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, Constants._TAG_CANONICALIZATIONMETHOD);
                        for (Element element : list) {
                            String uri = element.getAttributeNS(null, "URI");
                            String javaClass = element.getAttributeNS(null, "JAVACLASS");
                            try {
                                Canonicalizer.register(uri, javaClass);
                                LOG.debug("Canonicalizer.register({}, {})", uri, javaClass);
                            } catch (ClassNotFoundException e) {
                                Object[] exArgs = {uri, javaClass};
                                LOG.error(I18n.translate("algorithm.classDoesNotExist", exArgs));
                            }
                        }
                    }
                    if ("TransformAlgorithms".equals(tag)) {
                        Element[] tranElem = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "TransformAlgorithm");
                        for (Element element2 : tranElem) {
                            String uri2 = element2.getAttributeNS(null, "URI");
                            String javaClass2 = element2.getAttributeNS(null, "JAVACLASS");
                            try {
                                try {
                                    Transform.register(uri2, javaClass2);
                                    LOG.debug("Transform.register({}, {})", uri2, javaClass2);
                                } catch (NoClassDefFoundError e2) {
                                    LOG.warn("Not able to found dependencies for algorithm, I'll keep working.");
                                }
                            } catch (ClassNotFoundException e3) {
                                Object[] exArgs2 = {uri2, javaClass2};
                                LOG.error(I18n.translate("algorithm.classDoesNotExist", exArgs2));
                            }
                        }
                    }
                    if ("JCEAlgorithmMappings".equals(tag) && (algorithmsNode = ((Element) el).getElementsByTagName("Algorithms").item(0)) != null) {
                        Element[] algorithms = XMLUtils.selectNodes(algorithmsNode.getFirstChild(), CONF_NS, "Algorithm");
                        for (Element element3 : algorithms) {
                            String id = element3.getAttributeNS(null, "URI");
                            JCEMapper.register(id, new JCEMapper.Algorithm(element3));
                        }
                    }
                    if ("SignatureAlgorithms".equals(tag)) {
                        Element[] sigElems = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "SignatureAlgorithm");
                        for (Element sigElem : sigElems) {
                            String uri3 = sigElem.getAttributeNS(null, "URI");
                            String javaClass3 = sigElem.getAttributeNS(null, "JAVACLASS");
                            try {
                                SignatureAlgorithm.register(uri3, javaClass3);
                                LOG.debug("SignatureAlgorithm.register({}, {})", uri3, javaClass3);
                            } catch (ClassNotFoundException e4) {
                                Object[] exArgs3 = {uri3, javaClass3};
                                LOG.error(I18n.translate("algorithm.classDoesNotExist", exArgs3));
                            }
                        }
                    }
                    if ("ResourceResolvers".equals(tag)) {
                        Element[] resolverElem = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "Resolver");
                        List<String> classNames = new ArrayList<>(resolverElem.length);
                        for (Element element4 : resolverElem) {
                            String javaClass4 = element4.getAttributeNS(null, "JAVACLASS");
                            String description = element4.getAttributeNS(null, "DESCRIPTION");
                            if (description != null && description.length() > 0) {
                                LOG.debug("Register Resolver: {}: {}", javaClass4, description);
                            } else {
                                LOG.debug("Register Resolver: {}: For unknown purposes", javaClass4);
                            }
                            classNames.add(javaClass4);
                        }
                        ResourceResolver.registerClassNames(classNames);
                    }
                    if ("KeyResolver".equals(tag)) {
                        Element[] resolverElem2 = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "Resolver");
                        List<String> classNames2 = new ArrayList<>(resolverElem2.length);
                        for (Element element5 : resolverElem2) {
                            String javaClass5 = element5.getAttributeNS(null, "JAVACLASS");
                            String description2 = element5.getAttributeNS(null, "DESCRIPTION");
                            if (description2 != null && description2.length() > 0) {
                                LOG.debug("Register Resolver: {}: {}", javaClass5, description2);
                            } else {
                                LOG.debug("Register Resolver: {}: For unknown purposes", javaClass5);
                            }
                            classNames2.add(javaClass5);
                        }
                        KeyResolver.registerClassNames(classNames2);
                    }
                    if ("PrefixMappings".equals(tag)) {
                        LOG.debug("Now I try to bind prefixes:");
                        Element[] nl = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "PrefixMapping");
                        for (Element element6 : nl) {
                            String namespace = element6.getAttributeNS(null, "namespace");
                            String prefix = element6.getAttributeNS(null, "prefix");
                            LOG.debug("Now I try to bind {} to {}", prefix, namespace);
                            ElementProxy.setDefaultPrefix(namespace, prefix);
                        }
                    }
                }
            }
        } catch (Exception e5) {
            LOG.error("Bad: ", (Throwable) e5);
        }
    }
}
