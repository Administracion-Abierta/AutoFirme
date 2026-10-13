package org.apache.jcp.xml.dsig.internal.dom;

import java.security.AccessController;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.ProviderException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.xml.security.transforms.Transforms;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/XMLDSigRI.class */
public final class XMLDSigRI extends Provider {
    static final long serialVersionUID = -5049765099299494554L;
    private static final String INFO = "Apache Santuario XMLDSig (DOM XMLSignatureFactory; DOM KeyInfoFactory; C14N 1.0, C14N 1.1, Exclusive C14N, Base64, Enveloped, XPath, XPath2, XSLT TransformServices)";

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/XMLDSigRI$ProviderService.class */
    private static final class ProviderService extends Provider.Service {
        ProviderService(Provider p, String type, String algo, String cn) {
            super(p, type, algo, cn, null, null);
        }

        ProviderService(Provider p, String type, String algo, String cn, String[] aliases) {
            super(p, type, algo, cn, aliases == null ? null : Arrays.asList(aliases), null);
        }

        ProviderService(Provider p, String type, String algo, String cn, String[] aliases, Map<String, String> attrs) {
            super(p, type, algo, cn, aliases == null ? null : Arrays.asList(aliases), attrs);
        }

        @Override // java.security.Provider.Service
        public Object newInstance(Object ctrParamObj) throws NoSuchAlgorithmException {
            String type = getType();
            if (ctrParamObj != null) {
                throw new InvalidParameterException("constructorParameter not used with " + type + " engines");
            }
            String algo = getAlgorithm();
            try {
                if ("XMLSignatureFactory".equals(type)) {
                    if ("DOM".equals(algo)) {
                        return new DOMXMLSignatureFactory();
                    }
                } else if ("KeyInfoFactory".equals(type)) {
                    if ("DOM".equals(algo)) {
                        return new DOMKeyInfoFactory();
                    }
                } else if ("TransformService".equals(type)) {
                    if (algo.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315") || algo.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments")) {
                        return new DOMCanonicalXMLC14NMethod();
                    }
                    if ("http://www.w3.org/2006/12/xml-c14n11".equals(algo) || "http://www.w3.org/2006/12/xml-c14n11#WithComments".equals(algo)) {
                        return new DOMCanonicalXMLC14N11Method();
                    }
                    if (algo.equals("http://www.w3.org/2001/10/xml-exc-c14n#") || algo.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")) {
                        return new DOMExcC14NMethod();
                    }
                    if (algo.equals("http://www.w3.org/2000/09/xmldsig#base64")) {
                        return new DOMBase64Transform();
                    }
                    if (algo.equals("http://www.w3.org/2000/09/xmldsig#enveloped-signature")) {
                        return new DOMEnvelopedTransform();
                    }
                    if (algo.equals("http://www.w3.org/2002/06/xmldsig-filter2")) {
                        return new DOMXPathFilter2Transform();
                    }
                    if (algo.equals(Transforms.TRANSFORM_XPATH)) {
                        return new DOMXPathTransform();
                    }
                    if (algo.equals(Transforms.TRANSFORM_XSLT)) {
                        return new DOMXSLTTransform();
                    }
                }
                throw new ProviderException("No impl for " + algo + " " + type);
            } catch (Exception ex) {
                throw new NoSuchAlgorithmException("Error constructing " + type + " for " + algo + " using XMLDSig", ex);
            }
        }
    }

    public XMLDSigRI() {
        super("ApacheXMLDSig", 3.05d, INFO);
        AccessController.doPrivileged(new PrivilegedAction<Void>() { // from class: org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public Void run() {
                Map<String, String> MECH_TYPE = new HashMap<>();
                MECH_TYPE.put("MechanismType", "DOM");
                XMLDSigRI.this.putService(new ProviderService(this, "XMLSignatureFactory", "DOM", "org.apache.jcp.xml.dsig.internal.dom.DOMXMLSignatureFactory"));
                XMLDSigRI.this.putService(new ProviderService(this, "KeyInfoFactory", "DOM", "org.apache.jcp.xml.dsig.internal.dom.DOMKeyInfoFactory"));
                XMLDSigRI.this.putService(new ProviderService(this, "TransformService", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315", "org.apache.jcp.xml.dsig.internal.dom.DOMCanonicalXMLC14NMethod", new String[]{"INCLUSIVE"}, MECH_TYPE));
                XMLDSigRI.this.putService(new ProviderService(this, "TransformService", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments", "org.apache.jcp.xml.dsig.internal.dom.DOMCanonicalXMLC14NMethod", new String[]{"INCLUSIVE_WITH_COMMENTS"}, MECH_TYPE));
                XMLDSigRI.this.putService(new ProviderService(this, "TransformService", "http://www.w3.org/2006/12/xml-c14n11", "org.apache.jcp.xml.dsig.internal.dom.DOMCanonicalXMLC14N11Method", null, MECH_TYPE));
                XMLDSigRI.this.putService(new ProviderService(this, "TransformService", "http://www.w3.org/2006/12/xml-c14n11#WithComments", "org.apache.jcp.xml.dsig.internal.dom.DOMCanonicalXMLC14N11Method", null, MECH_TYPE));
                XMLDSigRI.this.putService(new ProviderService(this, "TransformService", "http://www.w3.org/2001/10/xml-exc-c14n#", "org.apache.jcp.xml.dsig.internal.dom.DOMExcC14NMethod", new String[]{"EXCLUSIVE"}, MECH_TYPE));
                XMLDSigRI.this.putService(new ProviderService(this, "TransformService", "http://www.w3.org/2001/10/xml-exc-c14n#WithComments", "org.apache.jcp.xml.dsig.internal.dom.DOMExcC14NMethod", new String[]{"EXCLUSIVE_WITH_COMMENTS"}, MECH_TYPE));
                XMLDSigRI.this.putService(new ProviderService(this, "TransformService", "http://www.w3.org/2000/09/xmldsig#base64", "org.apache.jcp.xml.dsig.internal.dom.DOMBase64Transform", new String[]{"BASE64"}, MECH_TYPE));
                XMLDSigRI.this.putService(new ProviderService(this, "TransformService", "http://www.w3.org/2000/09/xmldsig#enveloped-signature", "org.apache.jcp.xml.dsig.internal.dom.DOMEnvelopedTransform", new String[]{"ENVELOPED"}, MECH_TYPE));
                XMLDSigRI.this.putService(new ProviderService(this, "TransformService", "http://www.w3.org/2002/06/xmldsig-filter2", "org.apache.jcp.xml.dsig.internal.dom.DOMXPathFilter2Transform", new String[]{"XPATH2"}, MECH_TYPE));
                XMLDSigRI.this.putService(new ProviderService(this, "TransformService", Transforms.TRANSFORM_XPATH, "org.apache.jcp.xml.dsig.internal.dom.DOMXPathTransform", new String[]{"XPATH"}, MECH_TYPE));
                XMLDSigRI.this.putService(new ProviderService(this, "TransformService", Transforms.TRANSFORM_XSLT, "org.apache.jcp.xml.dsig.internal.dom.DOMXSLTTransform", new String[]{"XSLT"}, MECH_TYPE));
                return null;
            }
        });
    }
}
