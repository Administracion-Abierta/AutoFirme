package org.apache.pdfbox.pdmodel.fdf;

import com.aowagie.text.Chunk;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.util.Hex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFAnnotationStamp.class */
public class FDFAnnotationStamp extends FDFAnnotation {
    private static final Log LOG = LogFactory.getLog(FDFAnnotationStamp.class);
    public static final String SUBTYPE = "Stamp";

    public FDFAnnotationStamp() {
        this.annot.setName(COSName.SUBTYPE, "Stamp");
    }

    public FDFAnnotationStamp(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationStamp(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, "Stamp");
        XPath xpath = XPathFactory.newInstance().newXPath();
        LOG.debug("Get the DOM Document for the stamp appearance");
        try {
            String base64EncodedAppearance = xpath.evaluate("appearance", element);
            try {
                byte[] decodedAppearanceXML = Hex.decodeBase64(base64EncodedAppearance);
                if (base64EncodedAppearance != null && !base64EncodedAppearance.isEmpty()) {
                    LOG.debug("Decoded XML: " + new String(decodedAppearanceXML));
                    Document stampAppearance = org.apache.pdfbox.util.XMLUtil.parse(new ByteArrayInputStream(decodedAppearanceXML));
                    Element appearanceEl = stampAppearance.getDocumentElement();
                    if (!"dict".equalsIgnoreCase(appearanceEl.getNodeName())) {
                        throw new IOException("Error while reading stamp document, root should be 'dict' and not '" + appearanceEl.getNodeName() + OperatorName.SHOW_TEXT_LINE);
                    }
                    LOG.debug("Generate and set the appearance dictionary to the stamp annotation");
                    this.annot.setItem(COSName.AP, (COSBase) parseStampAnnotationAppearanceXML(appearanceEl));
                }
            } catch (IllegalArgumentException ex) {
                LOG.error("Bad base64 encoded appearance ignored", ex);
            }
        } catch (XPathExpressionException e) {
            LOG.error("Error while evaluating XPath expression for appearance: " + e);
        }
    }

    private COSDictionary parseStampAnnotationAppearanceXML(Element appearanceXML) throws IOException {
        COSDictionary dictionary = new COSDictionary();
        dictionary.setItem(COSName.N, (COSBase) new COSStream());
        LOG.debug("Build dictionary for Appearance based on the appearanceXML");
        NodeList nodeList = appearanceXML.getChildNodes();
        String parentAttrKey = appearanceXML.getAttribute("KEY");
        LOG.debug("Appearance Root - tag: " + appearanceXML.getTagName() + ", name: " + appearanceXML.getNodeName() + ", key: " + parentAttrKey + ", children: " + nodeList.getLength());
        if (!"AP".equals(appearanceXML.getAttribute("KEY"))) {
            LOG.warn(parentAttrKey + " => Not handling element: " + appearanceXML.getTagName() + " with key: " + appearanceXML.getAttribute("KEY"));
            return dictionary;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Element child = (Element) node;
                if ("STREAM".equalsIgnoreCase(child.getTagName())) {
                    LOG.debug(parentAttrKey + " => Process " + child.getAttribute("KEY") + " item in the dictionary after processing the " + child.getTagName());
                    dictionary.setItem(child.getAttribute("KEY"), (COSBase) parseStreamElement(child));
                    LOG.debug(parentAttrKey + " => Set " + child.getAttribute("KEY"));
                } else {
                    LOG.warn(parentAttrKey + " => Not handling element: " + child.getTagName());
                }
            }
        }
        return dictionary;
    }

    private COSStream parseStreamElement(Element streamEl) throws IOException {
        LOG.debug("Parse " + streamEl.getAttribute("KEY") + " Stream");
        COSStream stream = new COSStream();
        NodeList nodeList = streamEl.getChildNodes();
        String parentAttrKey = streamEl.getAttribute("KEY");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Element child = (Element) node;
                String childAttrKey = child.getAttribute("KEY");
                String childAttrVal = child.getAttribute("VAL");
                LOG.debug(parentAttrKey + " => reading child: " + child.getTagName() + " with key: " + childAttrKey);
                if ("INT".equalsIgnoreCase(child.getTagName())) {
                    if (!"Length".equals(childAttrKey)) {
                        stream.setInt(COSName.getPDFName(childAttrKey), Integer.parseInt(childAttrVal));
                        LOG.debug(parentAttrKey + " => Set " + childAttrKey + ": " + childAttrVal);
                    }
                } else if ("FIXED".equalsIgnoreCase(child.getTagName())) {
                    stream.setFloat(COSName.getPDFName(childAttrKey), Float.parseFloat(childAttrVal));
                    LOG.debug(parentAttrKey + " => Set " + childAttrKey + ": " + childAttrVal);
                } else if ("NAME".equalsIgnoreCase(child.getTagName())) {
                    stream.setName(COSName.getPDFName(childAttrKey), childAttrVal);
                    LOG.debug(parentAttrKey + " => Set " + childAttrKey + ": " + childAttrVal);
                } else if ("BOOL".equalsIgnoreCase(child.getTagName())) {
                    stream.setBoolean(COSName.getPDFName(childAttrKey), Boolean.parseBoolean(childAttrVal));
                    LOG.debug(parentAttrKey + " => Set " + childAttrVal);
                } else if ("ARRAY".equalsIgnoreCase(child.getTagName())) {
                    stream.setItem(COSName.getPDFName(childAttrKey), (COSBase) parseArrayElement(child));
                    LOG.debug(parentAttrKey + " => Set " + childAttrKey);
                } else if ("DICT".equalsIgnoreCase(child.getTagName())) {
                    stream.setItem(COSName.getPDFName(childAttrKey), (COSBase) parseDictElement(child));
                    LOG.debug(parentAttrKey + " => Set " + childAttrKey);
                } else if ("STREAM".equalsIgnoreCase(child.getTagName())) {
                    stream.setItem(COSName.getPDFName(childAttrKey), (COSBase) parseStreamElement(child));
                    LOG.debug(parentAttrKey + " => Set " + childAttrKey);
                } else if ("DATA".equalsIgnoreCase(child.getTagName())) {
                    LOG.debug(parentAttrKey + " => Handling DATA with encoding: " + child.getAttribute(Chunk.ENCODING));
                    if ("HEX".equals(child.getAttribute(Chunk.ENCODING))) {
                        OutputStream os = null;
                        try {
                            os = stream.createRawOutputStream();
                            os.write(Hex.decodeHex(child.getTextContent()));
                            LOG.debug(parentAttrKey + " => Data was streamed");
                            IOUtils.closeQuietly(os);
                        } catch (Throwable th) {
                            IOUtils.closeQuietly(os);
                            throw th;
                        }
                    } else if ("ASCII".equals(child.getAttribute(Chunk.ENCODING))) {
                        OutputStream os2 = null;
                        try {
                            os2 = stream.createOutputStream();
                            os2.write(child.getTextContent().getBytes());
                            LOG.debug(parentAttrKey + " => Data was streamed");
                            IOUtils.closeQuietly(os2);
                        } catch (Throwable th2) {
                            IOUtils.closeQuietly(os2);
                            throw th2;
                        }
                    } else {
                        LOG.warn(parentAttrKey + " => Not handling element DATA encoding: " + child.getAttribute(Chunk.ENCODING));
                    }
                } else {
                    LOG.warn(parentAttrKey + " => Not handling child element: " + child.getTagName());
                }
            }
        }
        return stream;
    }

    private COSArray parseArrayElement(Element arrayEl) throws IOException {
        LOG.debug("Parse " + arrayEl.getAttribute("KEY") + " Array");
        COSArray array = new COSArray();
        NodeList nodeList = arrayEl.getChildNodes();
        String parentAttrKey = arrayEl.getAttribute("KEY");
        if ("BBox".equals(parentAttrKey) && nodeList.getLength() < 4) {
            throw new IOException("BBox does not have enough coordinates, only has: " + nodeList.getLength());
        }
        if ("Matrix".equals(parentAttrKey) && nodeList.getLength() < 6) {
            throw new IOException("Matrix does not have enough coordinates, only has: " + nodeList.getLength());
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Element child = (Element) node;
                String childAttrKey = child.getAttribute("KEY");
                String childAttrVal = child.getAttribute("VAL");
                LOG.debug(parentAttrKey + " => reading child: " + child.getTagName() + " with key: " + childAttrKey);
                if ("INT".equalsIgnoreCase(child.getTagName()) || "FIXED".equalsIgnoreCase(child.getTagName())) {
                    LOG.debug(parentAttrKey + " value(" + i + "): " + childAttrVal);
                    array.add((COSBase) COSNumber.get(childAttrVal));
                } else if ("NAME".equalsIgnoreCase(child.getTagName())) {
                    LOG.debug(parentAttrKey + " value(" + i + "): " + childAttrVal);
                    array.add((COSBase) COSName.getPDFName(childAttrVal));
                } else if ("BOOL".equalsIgnoreCase(child.getTagName())) {
                    LOG.debug(parentAttrKey + " value(" + i + "): " + childAttrVal);
                    array.add((COSBase) COSBoolean.getBoolean(Boolean.parseBoolean(childAttrVal)));
                } else if ("DICT".equalsIgnoreCase(child.getTagName())) {
                    LOG.debug(parentAttrKey + " value(" + i + "): " + childAttrVal);
                    array.add((COSBase) parseDictElement(child));
                } else if ("STREAM".equalsIgnoreCase(child.getTagName())) {
                    LOG.debug(parentAttrKey + " value(" + i + "): " + childAttrVal);
                    array.add((COSBase) parseStreamElement(child));
                } else if ("ARRAY".equalsIgnoreCase(child.getTagName())) {
                    LOG.debug(parentAttrKey + " value(" + i + "): " + childAttrVal);
                    array.add((COSBase) parseArrayElement(child));
                } else {
                    LOG.warn(parentAttrKey + " => Not handling child element: " + child.getTagName());
                }
            }
        }
        return array;
    }

    private COSDictionary parseDictElement(Element dictEl) throws IOException {
        LOG.debug("Parse " + dictEl.getAttribute("KEY") + " Dictionary");
        COSDictionary dict = new COSDictionary();
        NodeList nodeList = dictEl.getChildNodes();
        String parentAttrKey = dictEl.getAttribute("KEY");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Element child = (Element) node;
                String childAttrKey = child.getAttribute("KEY");
                String childAttrVal = child.getAttribute("VAL");
                if ("DICT".equals(child.getTagName())) {
                    LOG.debug(parentAttrKey + " => Handling DICT element with key: " + childAttrKey);
                    dict.setItem(COSName.getPDFName(childAttrKey), (COSBase) parseDictElement(child));
                    LOG.debug(parentAttrKey + " => Set " + childAttrKey);
                } else if ("STREAM".equals(child.getTagName())) {
                    LOG.debug(parentAttrKey + " => Handling STREAM element with key: " + childAttrKey);
                    dict.setItem(COSName.getPDFName(childAttrKey), (COSBase) parseStreamElement(child));
                } else if ("NAME".equals(child.getTagName())) {
                    LOG.debug(parentAttrKey + " => Handling NAME element with key: " + childAttrKey);
                    dict.setName(COSName.getPDFName(childAttrKey), childAttrVal);
                    LOG.debug(parentAttrKey + " => Set " + childAttrKey + ": " + childAttrVal);
                } else if ("INT".equalsIgnoreCase(child.getTagName())) {
                    dict.setInt(COSName.getPDFName(childAttrKey), Integer.parseInt(childAttrVal));
                    LOG.debug(parentAttrKey + " => Set " + childAttrKey + ": " + childAttrVal);
                } else if ("FIXED".equalsIgnoreCase(child.getTagName())) {
                    dict.setFloat(COSName.getPDFName(childAttrKey), Float.parseFloat(childAttrVal));
                    LOG.debug(parentAttrKey + " => Set " + childAttrKey + ": " + childAttrVal);
                } else if ("BOOL".equalsIgnoreCase(child.getTagName())) {
                    dict.setBoolean(COSName.getPDFName(childAttrKey), Boolean.parseBoolean(childAttrVal));
                    LOG.debug(parentAttrKey + " => Set " + childAttrVal);
                } else if ("ARRAY".equalsIgnoreCase(child.getTagName())) {
                    dict.setItem(COSName.getPDFName(childAttrKey), (COSBase) parseArrayElement(child));
                    LOG.debug(parentAttrKey + " => Set " + childAttrKey);
                } else {
                    LOG.warn(parentAttrKey + " => NOT handling child element: " + child.getTagName());
                }
            }
        }
        return dict;
    }
}
