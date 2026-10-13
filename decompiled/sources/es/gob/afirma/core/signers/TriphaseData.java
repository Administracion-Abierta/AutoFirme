package es.gob.afirma.core.signers;

import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.SecureXmlBuilder;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/TriphaseData.class */
public final class TriphaseData {
    private final List<TriSign> signs;
    private String format;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/TriphaseData$TriSign.class */
    public static final class TriSign {
        private final Map<String, String> dict;
        private final String id;
        private final String signatureId;

        public TriSign(TriSign triSign) {
            this.id = triSign.getId();
            this.signatureId = triSign.getSignatureId();
            this.dict = new ConcurrentHashMap(triSign.getDict().size());
            for (String str : triSign.getDict().keySet()) {
                this.dict.put(str, triSign.getProperty(str));
            }
        }

        public TriSign(Map<String, String> map, String str) {
            this(map, str, null);
        }

        public TriSign(Map<String, String> map, String str, String str2) {
            if (map == null) {
                throw new IllegalArgumentException("El diccionario de propiedades de la firma no puede ser nulo");
            }
            this.dict = map;
            this.id = str != null ? str : UUID.randomUUID().toString();
            this.signatureId = str2;
        }

        public String toString() {
            return "Firma trifasica individual con identificador " + getId();
        }

        public String getId() {
            return this.id;
        }

        public String getSignatureId() {
            return this.signatureId;
        }

        public String getProperty(String str) {
            return this.dict.get(str);
        }

        public void addProperty(String str, String str2) {
            this.dict.put(str, str2);
        }

        public void deleteProperty(String str) {
            this.dict.remove(str);
        }

        public Map<String, String> getDict() {
            return new ConcurrentHashMap(this.dict);
        }
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String str) {
        this.format = str;
    }

    public TriSign getTriSign(String str) {
        if (str == null) {
            throw new IllegalArgumentException("El ID de la firma no puede ser nulo");
        }
        for (TriSign triSign : this.signs) {
            if (belongToSignature(str, triSign)) {
                return triSign;
            }
        }
        return null;
    }

    public List<TriSign> getTriSigns(String str) {
        if (str == null) {
            throw new IllegalArgumentException("El ID de la firma no puede ser nulo");
        }
        ArrayList arrayList = new ArrayList();
        for (TriSign triSign : this.signs) {
            if (belongToSignature(str, triSign)) {
                arrayList.add(new TriSign(triSign));
            }
        }
        if (arrayList.size() == 0) {
            return null;
        }
        return arrayList;
    }

    private static boolean belongToSignature(String str, TriSign triSign) {
        return str.equals(triSign.getSignatureId()) || str.equals(triSign.getId());
    }

    public List<TriSign> getTriSigns() {
        return this.signs;
    }

    public TriphaseData() {
        this.signs = new ArrayList();
        this.format = null;
    }

    public TriphaseData(List<TriSign> list) {
        this(list, null);
    }

    public TriphaseData(List<TriSign> list, String str) {
        this.signs = list;
        this.format = str;
    }

    public void addSignOperation(TriSign triSign) {
        this.signs.add(triSign);
    }

    public TriSign getSign(int i) {
        return this.signs.get(i);
    }

    public int getSignsCount() {
        return this.signs.size();
    }

    public static TriphaseData parser(byte[] bArr) throws IOException {
        if (bArr == null) {
            throw new IllegalArgumentException("El XML de entrada no puede ser nulo");
        }
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
            Throwable th = null;
            try {
                try {
                    Document document = SecureXmlBuilder.getSecureDocumentBuilder().parse(byteArrayInputStream);
                    if (byteArrayInputStream != null) {
                        if (0 != 0) {
                            try {
                                byteArrayInputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            byteArrayInputStream.close();
                        }
                    }
                    return parser(document.getDocumentElement());
                } catch (Throwable th3) {
                    if (byteArrayInputStream != null) {
                        if (th != null) {
                            try {
                                byteArrayInputStream.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            byteArrayInputStream.close();
                        }
                    }
                    throw th3;
                }
            } catch (Throwable th5) {
                th = th5;
                throw th5;
            }
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").severe("Error al cargar el fichero XML: " + e + "\n" + LoggerUtil.getTrimBytes(bArr));
            throw new IOException("Error al cargar el fichero XML: " + e, e);
        }
    }

    public static TriphaseData parser(Element element) throws IOException {
        Node namedItem;
        if (element == null) {
            throw new IllegalArgumentException("El XML de entrada no puede ser nulo");
        }
        NodeList childNodes = element.getChildNodes();
        int iNextNodeElementIndex = nextNodeElementIndex(childNodes, 0);
        Node nodeItem = childNodes.item(iNextNodeElementIndex);
        if (iNextNodeElementIndex == -1 || !"firmas".equalsIgnoreCase(nodeItem.getNodeName())) {
            throw new IllegalArgumentException("No se encontro el nodo 'firmas' en el XML proporcionado");
        }
        String nodeValue = null;
        NamedNodeMap attributes = nodeItem.getAttributes();
        if (attributes != null && (namedItem = attributes.getNamedItem(XAdESExtraParams.FORMAT)) != null) {
            nodeValue = namedItem.getNodeValue();
        }
        return new TriphaseData(parseSignsNode(nodeItem), nodeValue);
    }

    private static List<TriSign> parseSignsNode(Node node) {
        NodeList childNodes = node.getChildNodes();
        ArrayList arrayList = new ArrayList();
        int iNextNodeElementIndex = nextNodeElementIndex(childNodes, 0);
        while (true) {
            int i = iNextNodeElementIndex;
            if (i != -1) {
                Node nodeItem = childNodes.item(i);
                String nodeValue = null;
                String nodeValue2 = null;
                NamedNodeMap attributes = nodeItem.getAttributes();
                if (attributes != null) {
                    Node namedItem = attributes.getNamedItem("Id");
                    if (namedItem != null) {
                        nodeValue = namedItem.getNodeValue();
                    }
                    Node namedItem2 = attributes.getNamedItem("signid");
                    if (namedItem2 != null) {
                        nodeValue2 = namedItem2.getNodeValue();
                    }
                }
                arrayList.add(new TriSign(parseParamsListNode(nodeItem), nodeValue, nodeValue2));
                iNextNodeElementIndex = nextNodeElementIndex(childNodes, i + 1);
            } else {
                return arrayList;
            }
        }
    }

    private static Map<String, String> parseParamsListNode(Node node) {
        NodeList childNodes = node.getChildNodes();
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        int iNextNodeElementIndex = nextNodeElementIndex(childNodes, 0);
        while (true) {
            int i = iNextNodeElementIndex;
            if (i != -1) {
                Node nodeItem = childNodes.item(i);
                concurrentHashMap.put(nodeItem.getAttributes().getNamedItem(OperatorName.ENDPATH).getNodeValue(), nodeItem.getTextContent().trim());
                iNextNodeElementIndex = nextNodeElementIndex(childNodes, i + 1);
            } else {
                return concurrentHashMap;
            }
        }
    }

    private static int nextNodeElementIndex(NodeList nodeList, int i) {
        for (int i2 = i; i2 < nodeList.getLength(); i2++) {
            if (nodeList.item(i2).getNodeType() == 1) {
                return i2;
            }
        }
        return -1;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>\n");
        sb.append(" <firmas");
        if (this.format != null) {
            sb.append(" format=\"");
            sb.append(this.format);
            sb.append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        }
        sb.append(">\n");
        for (TriSign triSign : this.signs) {
            sb.append("  <firma");
            if (triSign.getId() != null) {
                sb.append(" Id=\"");
                sb.append(triSign.getId());
                sb.append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            }
            if (triSign.getSignatureId() != null) {
                sb.append(" signid=\"");
                sb.append(triSign.getSignatureId());
                sb.append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            }
            sb.append(">\n");
            for (String str : triSign.getDict().keySet()) {
                sb.append("   <param n=\"").append(str).append("\">").append(triSign.getProperty(str)).append("</param>\n");
            }
            sb.append("  </firma>\n");
        }
        sb.append(" </firmas>\n");
        sb.append("</xml>");
        return sb.toString();
    }
}
