package nu.xom.xslt;

import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.XMLException;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/xslt/XSLTransform.class */
public final class XSLTransform {
    private Templates templates;
    private NodeFactory factory;
    private Map parameters;
    private static ErrorListener errorsAreFatal = new FatalListener(null);

    /* JADX INFO: renamed from: nu.xom.xslt.XSLTransform$1, reason: invalid class name */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/xslt/XSLTransform$1.class */
    static class AnonymousClass1 {
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/xslt/XSLTransform$FatalListener.class */
    private static class FatalListener implements ErrorListener {
        private FatalListener() {
        }

        @Override // javax.xml.transform.ErrorListener
        public void warning(TransformerException transformerException) {
        }

        @Override // javax.xml.transform.ErrorListener
        public void error(TransformerException transformerException) throws TransformerException {
            throw transformerException;
        }

        @Override // javax.xml.transform.ErrorListener
        public void fatalError(TransformerException transformerException) throws TransformerException {
            throw transformerException;
        }

        /* synthetic */ FatalListener(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private XSLTransform(Source source) throws XSLException {
        this.parameters = new HashMap();
        try {
            TransformerFactory transformerFactoryNewInstance = TransformerFactory.newInstance();
            transformerFactoryNewInstance.setErrorListener(errorsAreFatal);
            this.templates = transformerFactoryNewInstance.newTemplates(source);
        } catch (TransformerConfigurationException e) {
            throw new XSLException("Syntax error in stylesheet", e);
        } catch (TransformerFactoryConfigurationError e2) {
            throw new XSLException("Could not locate a TrAX TransformerFactory", e2);
        }
    }

    public XSLTransform(Document document) throws XSLException {
        this(document, new NodeFactory());
    }

    public XSLTransform(Document document, NodeFactory nodeFactory) throws XSLException {
        this(new XOMSource(document));
        if (nodeFactory == null) {
            this.factory = new NodeFactory();
        } else {
            this.factory = nodeFactory;
        }
    }

    public Nodes transform(Document document) throws XSLException {
        return transform(new XOMSource(document));
    }

    public void setParameter(String str, Object obj) {
        setParameter(str, null, obj);
    }

    public void setParameter(String str, String str2, Object obj) {
        if (str2 == null || "".equals(str2)) {
            _setParameter(str, obj);
        } else {
            _setParameter(new StringBuffer().append("{").append(str2).append("}").append(str).toString(), obj);
        }
    }

    private void _setParameter(String str, Object obj) {
        if (obj == null) {
            this.parameters.remove(str);
        } else {
            this.parameters.put(str, obj);
        }
    }

    public Nodes transform(Nodes nodes) throws XSLException {
        return nodes.size() == 0 ? new Nodes() : transform(new XOMSource(nodes));
    }

    private Nodes transform(Source source) throws XSLException {
        Throwable exception;
        Exception exception2;
        try {
            XOMResult xOMResult = new XOMResult(this.factory);
            Transformer transformerNewTransformer = this.templates.newTransformer();
            transformerNewTransformer.setOutputProperty("method", CanonicalizerBase.XML);
            transformerNewTransformer.setErrorListener(errorsAreFatal);
            for (String str : this.parameters.keySet()) {
                transformerNewTransformer.setParameter(str, this.parameters.get(str));
            }
            transformerNewTransformer.transform(source, xOMResult);
            return xOMResult.getResult();
        } catch (Exception e) {
            Throwable th = e;
            if ((th instanceof TransformerException) && (exception = ((TransformerException) th).getException()) != null) {
                th = exception;
                if ((th instanceof SAXParseException) && (exception2 = ((SAXParseException) th).getException()) != null) {
                    th = exception2;
                }
            }
            throw new XSLException(e.getMessage(), th);
        }
    }

    public static Document toDocument(Nodes nodes) {
        Element element = null;
        int i = 0;
        for (int i2 = 0; i2 < nodes.size(); i2++) {
            if (nodes.get(i2) instanceof Element) {
                i = i2;
                element = (Element) nodes.get(i2);
                break;
            }
        }
        if (element == null) {
            throw new XMLException("No root element");
        }
        Document document = new Document(element);
        for (int i3 = 0; i3 < i; i3++) {
            document.insertChild(nodes.get(i3), i3);
        }
        for (int i4 = i + 1; i4 < nodes.size(); i4++) {
            document.appendChild(nodes.get(i4));
        }
        return document;
    }

    public String toString() {
        return new StringBuffer().append("[").append(getClass().getName()).append(": ").append(this.templates).append("]").toString();
    }
}
