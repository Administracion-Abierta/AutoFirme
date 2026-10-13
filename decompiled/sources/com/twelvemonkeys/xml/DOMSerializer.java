package com.twelvemonkeys.xml;

import java.io.OutputStream;
import java.io.Writer;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/xml/DOMSerializer.class */
public final class DOMSerializer {
    private static final String PARAM_PRETTY_PRINT = "format-pretty-print";
    private static final String PARAM_XML_DECLARATION = "xml-declaration";
    private final LSSerializer serializer;
    private final LSOutput output;

    private DOMSerializer() {
        DOMImplementationLS implementation = Support.getImplementation();
        this.serializer = implementation.createLSSerializer();
        this.output = implementation.createLSOutput();
    }

    public DOMSerializer(OutputStream outputStream, String str) {
        this();
        this.output.setByteStream(outputStream);
        this.output.setEncoding(str);
    }

    public DOMSerializer(Writer writer) {
        this();
        this.output.setCharacterStream(writer);
    }

    public void setPrettyPrint(boolean z) {
        DOMConfiguration domConfig = this.serializer.getDomConfig();
        if (domConfig.canSetParameter(PARAM_PRETTY_PRINT, Boolean.valueOf(z))) {
            domConfig.setParameter(PARAM_PRETTY_PRINT, Boolean.valueOf(z));
        }
    }

    public boolean getPrettyPrint() {
        return Boolean.TRUE.equals(this.serializer.getDomConfig().getParameter(PARAM_PRETTY_PRINT));
    }

    private void setXMLDeclaration(boolean z) {
        this.serializer.getDomConfig().setParameter(PARAM_XML_DECLARATION, Boolean.valueOf(z));
    }

    public void serialize(Document document) {
        serializeImpl(document, true);
    }

    public void serialize(Node node) {
        serializeImpl(node, false);
    }

    private void serializeImpl(Node node, boolean z) {
        setXMLDeclaration(z);
        this.serializer.write(node, this.output);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/xml/DOMSerializer$Support.class */
    private static class Support {
        private static final DOMImplementationRegistry DOM_REGISTRY = createDOMRegistry();

        private Support() {
        }

        static DOMImplementationLS getImplementation() {
            DOMImplementationLS dOMImplementationLS = (DOMImplementationLS) DOM_REGISTRY.getDOMImplementation("LS 3.0");
            if (dOMImplementationLS == null) {
                DOMImplementationList dOMImplementationList = DOM_REGISTRY.getDOMImplementationList("");
                System.err.println("DOM implementations (" + dOMImplementationList.getLength() + "):");
                for (int i = 0; i < dOMImplementationList.getLength(); i++) {
                    System.err.println("    " + dOMImplementationList.item(i));
                }
                throw new IllegalStateException("Could not create DOM Implementation (no LS support found)");
            }
            return dOMImplementationLS;
        }

        private static DOMImplementationRegistry createDOMRegistry() {
            try {
                return DOMImplementationRegistry.newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
