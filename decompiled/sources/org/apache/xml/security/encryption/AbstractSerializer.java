package org.apache.xml.security.encryption;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/AbstractSerializer.class */
public abstract class AbstractSerializer implements Serializer {
    private final Canonicalizer canon;
    protected final boolean secureValidation;

    protected AbstractSerializer(String canonAlg, boolean secureValidation) throws InvalidCanonicalizerException {
        this.canon = Canonicalizer.getInstance(canonAlg);
        this.secureValidation = secureValidation;
    }

    @Override // org.apache.xml.security.encryption.Serializer
    public byte[] serializeToByteArray(Element element) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Throwable th = null;
        try {
            try {
                this.canon.canonicalizeSubtree(element, baos);
                byte[] byteArray = baos.toByteArray();
                $closeResource(null, baos);
                return byteArray;
            } catch (Throwable th2) {
                th = th2;
                throw th2;
            }
        } catch (Throwable th3) {
            $closeResource(th, baos);
            throw th3;
        }
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) throws Exception {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
        } catch (Throwable th) {
            x0.addSuppressed(th);
        }
    }

    @Override // org.apache.xml.security.encryption.Serializer
    public byte[] serializeToByteArray(NodeList content) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Throwable th = null;
        for (int i = 0; i < content.getLength(); i++) {
            try {
                try {
                    this.canon.canonicalizeSubtree(content.item(i), baos);
                } catch (Throwable th2) {
                    th = th2;
                    throw th2;
                }
            } catch (Throwable th3) {
                $closeResource(th, baos);
                throw th3;
            }
        }
        byte[] byteArray = baos.toByteArray();
        $closeResource(null, baos);
        return byteArray;
    }

    /* JADX WARN: Not initialized variable reg: 7, insn: 0x0125: MOVE (r1 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = 
      (r7 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('byteArrayOutputStream' java.io.ByteArrayOutputStream)])
    , block:B:37:0x0125 */
    /* JADX WARN: Not initialized variable reg: 8, insn: 0x0124: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r8 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:36:0x0124 */
    protected static byte[] createContext(byte[] source, Node ctx) throws Exception {
        Throwable th;
        ByteArrayOutputStream byteArrayOutputStream;
        try {
            try {
                ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream2, StandardCharsets.UTF_8);
                Throwable th2 = null;
                try {
                    try {
                        outputStreamWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dummy");
                        Map<String, String> storedNamespaces = new HashMap<>();
                        for (Node wk = ctx; wk != null; wk = wk.getParentNode()) {
                            NamedNodeMap atts = wk.getAttributes();
                            if (atts != null) {
                                for (int i = 0; i < atts.getLength(); i++) {
                                    Node att = atts.item(i);
                                    String nodeName = att.getNodeName();
                                    if ((CanonicalizerBase.XMLNS.equals(nodeName) || nodeName.startsWith("xmlns:")) && !storedNamespaces.containsKey(att.getNodeName())) {
                                        outputStreamWriter.write(" ");
                                        outputStreamWriter.write(nodeName);
                                        outputStreamWriter.write("=\"");
                                        outputStreamWriter.write(att.getNodeValue());
                                        outputStreamWriter.write(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
                                        storedNamespaces.put(nodeName, att.getNodeValue());
                                    }
                                }
                            }
                        }
                        outputStreamWriter.write(">");
                        outputStreamWriter.flush();
                        byteArrayOutputStream2.write(source);
                        outputStreamWriter.write("</dummy>");
                        outputStreamWriter.close();
                        byte[] byteArray = byteArrayOutputStream2.toByteArray();
                        $closeResource(null, outputStreamWriter);
                        $closeResource(null, byteArrayOutputStream2);
                        return byteArray;
                    } catch (Throwable th3) {
                        th2 = th3;
                        throw th3;
                    }
                } catch (Throwable th4) {
                    $closeResource(th2, outputStreamWriter);
                    throw th4;
                }
            } catch (Throwable th5) {
                $closeResource(th, byteArrayOutputStream);
                throw th5;
            }
        } catch (IOException e) {
            throw new XMLEncryptionException(e);
        }
    }
}
