package org.apache.xml.security.c14n;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.xml.security.c14n.implementations.Canonicalizer11_OmitComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer11_WithComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclOmitComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclWithComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315OmitComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315WithComments;
import org.apache.xml.security.c14n.implementations.CanonicalizerPhysical;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.JavaUtils;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/c14n/Canonicalizer.class */
public final class Canonicalizer {
    public static final String XPATH_C14N_WITH_COMMENTS_SINGLE_NODE = "(.//. | .//@* | .//namespace::*)";
    public static final String ALGO_ID_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    public static final String ALGO_ID_C14N_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    public static final String ALGO_ID_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String ALGO_ID_C14N_EXCL_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    public static final String ALGO_ID_C14N11_OMIT_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11";
    public static final String ALGO_ID_C14N11_WITH_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
    public static final String ALGO_ID_C14N_PHYSICAL = "http://santuario.apache.org/c14n/physical";
    private final CanonicalizerSpi canonicalizerSpi;
    public static final String ENCODING = StandardCharsets.UTF_8.name();
    private static Map<String, Class<? extends CanonicalizerSpi>> canonicalizerHash = new ConcurrentHashMap();

    private Canonicalizer(String algorithmURI) throws InvalidCanonicalizerException {
        try {
            Class<? extends CanonicalizerSpi> implementingClass = canonicalizerHash.get(algorithmURI);
            this.canonicalizerSpi = (CanonicalizerSpi) JavaUtils.newInstanceWithEmptyConstructor(implementingClass);
        } catch (Exception e) {
            Object[] exArgs = {algorithmURI};
            throw new InvalidCanonicalizerException(e, "signature.Canonicalizer.UnknownCanonicalizer", exArgs);
        }
    }

    public static Canonicalizer getInstance(String algorithmURI) throws InvalidCanonicalizerException {
        return new Canonicalizer(algorithmURI);
    }

    public static void register(String str, String str2) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException {
        JavaUtils.checkRegisterPermission();
        Class<? extends CanonicalizerSpi> cls = canonicalizerHash.get(str);
        if (cls != null) {
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", new Object[]{str, cls});
        }
        canonicalizerHash.put(str, (Class<? extends CanonicalizerSpi>) ClassLoaderUtils.loadClass(str2, Canonicalizer.class));
    }

    public static void register(String algorithmURI, Class<? extends CanonicalizerSpi> implementingClass) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException {
        JavaUtils.checkRegisterPermission();
        Class<? extends CanonicalizerSpi> registeredClass = canonicalizerHash.get(algorithmURI);
        if (registeredClass != null) {
            Object[] exArgs = {algorithmURI, registeredClass};
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", exArgs);
        }
        canonicalizerHash.put(algorithmURI, implementingClass);
    }

    public static void registerDefaultAlgorithms() {
        canonicalizerHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", Canonicalizer20010315OmitComments.class);
        canonicalizerHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments", Canonicalizer20010315WithComments.class);
        canonicalizerHash.put("http://www.w3.org/2001/10/xml-exc-c14n#", Canonicalizer20010315ExclOmitComments.class);
        canonicalizerHash.put("http://www.w3.org/2001/10/xml-exc-c14n#WithComments", Canonicalizer20010315ExclWithComments.class);
        canonicalizerHash.put("http://www.w3.org/2006/12/xml-c14n11", Canonicalizer11_OmitComments.class);
        canonicalizerHash.put("http://www.w3.org/2006/12/xml-c14n11#WithComments", Canonicalizer11_WithComments.class);
        canonicalizerHash.put("http://santuario.apache.org/c14n/physical", CanonicalizerPhysical.class);
    }

    public void canonicalize(byte[] inputBytes, OutputStream writer, boolean secureValidation) throws CanonicalizationException, IOException, XMLParserException {
        this.canonicalizerSpi.engineCanonicalize(inputBytes, writer, secureValidation);
    }

    public void canonicalizeSubtree(Node node, OutputStream writer) throws CanonicalizationException {
        this.canonicalizerSpi.engineCanonicalizeSubTree(node, writer);
    }

    public void canonicalizeSubtree(Node node, String inclusiveNamespaces, OutputStream writer) throws CanonicalizationException {
        this.canonicalizerSpi.engineCanonicalizeSubTree(node, inclusiveNamespaces, writer);
    }

    public void canonicalizeSubtree(Node node, String inclusiveNamespaces, boolean propagateDefaultNamespace, OutputStream writer) throws CanonicalizationException {
        this.canonicalizerSpi.engineCanonicalizeSubTree(node, inclusiveNamespaces, propagateDefaultNamespace, writer);
    }

    public void canonicalizeXPathNodeSet(Set<Node> xpathNodeSet, OutputStream writer) throws CanonicalizationException {
        this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(xpathNodeSet, writer);
    }

    public void canonicalizeXPathNodeSet(Set<Node> xpathNodeSet, String inclusiveNamespaces, OutputStream writer) throws CanonicalizationException {
        this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(xpathNodeSet, inclusiveNamespaces, writer);
    }
}
