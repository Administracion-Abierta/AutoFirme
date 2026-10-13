package es.gob.afirma.signers.pades;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.SecureXmlBuilder;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.standalone.CommandLineParameters;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Properties;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/PdfSignResult.class */
public final class PdfSignResult implements Serializable {
    private static final long serialVersionUID = 2;
    private String fileID;
    private byte[] sign;
    private byte[] timestamp;
    private GregorianCalendar signTime;
    private Properties extraParams;

    protected PdfSignResult() {
        this.fileID = null;
        this.sign = new byte[0];
        this.signTime = new GregorianCalendar();
        this.extraParams = new Properties();
    }

    public PdfSignResult(String str, byte[] bArr, byte[] bArr2, GregorianCalendar gregorianCalendar, Properties properties) {
        if (gregorianCalendar == null) {
            throw new IllegalArgumentException("Es obligatorio proporcionar un momento de firmado");
        }
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("Es obligatorio proporcionar un MAC de PDF");
        }
        if (bArr == null || bArr.length < 1) {
            throw new IllegalArgumentException("Es obligatorio una pre-firma");
        }
        this.fileID = str;
        this.sign = (byte[]) bArr.clone();
        this.timestamp = bArr2 != null ? (byte[]) bArr2.clone() : null;
        this.signTime = gregorianCalendar;
        this.extraParams = properties != null ? properties : new Properties();
    }

    public void setExtraParams(Properties properties) {
        this.extraParams = properties != null ? (Properties) properties.clone() : null;
    }

    Properties getExtraParams() {
        if (this.extraParams != null) {
            return (Properties) this.extraParams.clone();
        }
        return null;
    }

    public String getFileID() {
        return this.fileID;
    }

    public byte[] getSign() {
        return (byte[]) this.sign.clone();
    }

    public byte[] getTimestamp() {
        if (this.timestamp != null) {
            return (byte[]) this.timestamp.clone();
        }
        return null;
    }

    GregorianCalendar getSignTime() {
        return this.signTime;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        try {
            objectOutputStream.write(("<signResult>\n <extraParams>\n" + AOUtil.properties2Base64(getExtraParams()) + "\n </extraParams>\n <pdfId>\n" + getFileID() + "\n </pdfIf>\n <sign>\n" + Base64.encode(getSign()) + "\n </sign>\n <timestamp>\n" + (this.timestamp != null ? Base64.encode(getTimestamp()) : "") + "\n </timestamp>\n <signTime>\n" + DatatypeFactory.newInstance().newXMLGregorianCalendar(getSignTime()).toXMLFormat() + "\n </signTime>\n</signResult>").getBytes());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException {
        try {
            NodeList childNodes = SecureXmlBuilder.getSecureDocumentBuilder().parse(objectInputStream).getChildNodes();
            int nextElementNode = getNextElementNode(childNodes, 0);
            Node nodeItem = childNodes.item(nextElementNode);
            if (!"extraParams".equalsIgnoreCase(nodeItem.getNodeName())) {
                throw new IOException("No se encontro el nodo 'extraParams' del PdfSignResultSerializado");
            }
            this.extraParams = AOUtil.base642Properties(nodeItem.getTextContent().trim());
            int nextElementNode2 = getNextElementNode(childNodes, nextElementNode + 1);
            Node nodeItem2 = childNodes.item(nextElementNode2);
            if (!"pdfId".equalsIgnoreCase(nodeItem2.getNodeName())) {
                throw new IOException("No se encontro el nodo 'pdfId' del PdfSignResultSerializado");
            }
            this.fileID = nodeItem2.getTextContent().trim();
            int nextElementNode3 = getNextElementNode(childNodes, nextElementNode2 + 1);
            Node nodeItem3 = childNodes.item(nextElementNode3);
            if (!CommandLineParameters.MASSIVE_OP_SIGN.equalsIgnoreCase(nodeItem3.getNodeName())) {
                throw new IOException("No se encontro el nodo 'sign' del PdfSignResultSerializado");
            }
            this.sign = Base64.decode(nodeItem3.getTextContent().trim());
            int nextElementNode4 = getNextElementNode(childNodes, nextElementNode3 + 1);
            Node nodeItem4 = childNodes.item(nextElementNode4);
            if (!"timestamp".equalsIgnoreCase(nodeItem4.getNodeName())) {
                throw new IOException("No se encontro el nodo 'timestamp' del PdfSignResultSerializado, aunque no haya sello de tiempo el nodo debe existir (aunque vacio)");
            }
            this.timestamp = nodeItem4.getTextContent().trim().isEmpty() ? null : Base64.decode(nodeItem4.getTextContent().trim());
            Node nodeItem5 = childNodes.item(getNextElementNode(childNodes, nextElementNode4 + 1));
            if (!PdfExtraParams.SIGN_TIME.equalsIgnoreCase(nodeItem5.getNodeName())) {
                throw new IOException("No se encontro el nodo 'signTime' del PdfSignResultSerializado");
            }
            try {
                this.signTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(nodeItem5.getTextContent().trim()).toGregorianCalendar();
            } catch (Exception e) {
                throw new IOException(e);
            }
        } catch (ParserConfigurationException e2) {
            throw new IOException(e2);
        } catch (SAXException e3) {
            throw new IOException(e3);
        }
    }

    private static int getNextElementNode(NodeList nodeList, int i) throws IOException {
        for (int i2 = i; i2 < nodeList.getLength(); i2++) {
            if (nodeList.item(i2).getNodeType() == 1) {
                return i2;
            }
        }
        throw new IOException("No se encontraron todos los campos del PdfSignResult serializado");
    }
}
