package es.gob.afirma.standalone.configurator.common.xmlwise;

import com.ctc.wstx.cfg.XmlConsts;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/configurator/common/xmlwise/XmlElementAttributes.class */
public class XmlElementAttributes extends HashMap<String, String> {
    XmlElementAttributes() {
    }

    public XmlElementAttributes(Element element) {
        super(element.getAttributes().getLength());
        NamedNodeMap attributes = element.getAttributes();
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            put(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
        }
    }

    public int getInt(String str) throws XmlParseException {
        String str2 = get(str);
        if (str2 == null) {
            throw new XmlParseException("Could not find attribute " + str);
        }
        try {
            return Integer.parseInt(str2);
        } catch (NumberFormatException e) {
            throw new XmlParseException("Failed to parse int attribute " + str, e);
        }
    }

    public double getDouble(String str) throws XmlParseException {
        String str2 = get(str);
        if (str2 == null) {
            throw new XmlParseException("Could not find attribute " + str);
        }
        try {
            return Double.parseDouble(str2);
        } catch (NumberFormatException e) {
            throw new XmlParseException("Failed to parse double attribute " + str, e);
        }
    }

    public boolean getBoolean(String str) throws XmlParseException {
        String str2 = get(str);
        if (str2 == null) {
            throw new XmlParseException("Could not find attribute " + str);
        }
        String lowerCase = str2.toLowerCase();
        if ("true".equals(lowerCase) || XmlConsts.XML_SA_YES.equals(lowerCase) || OperatorName.CURVE_TO_REPLICATE_FINAL_POINT.equals(lowerCase)) {
            return true;
        }
        if ("false".equals(lowerCase) || XmlConsts.XML_SA_NO.equals(lowerCase) || OperatorName.ENDPATH.equals(lowerCase)) {
            return false;
        }
        throw new XmlParseException("Attribute " + str + " did not have boolean value (was: " + lowerCase + ')');
    }

    public String toXml() {
        StringBuilder sb = new StringBuilder(10 * size());
        for (Map.Entry<String, String> entry : entrySet()) {
            sb.append(' ').append(entry.getKey()).append("=").append(OperatorName.SHOW_TEXT_LINE);
            sb.append(Xmlwise.escapeXML(entry.getValue())).append(OperatorName.SHOW_TEXT_LINE);
        }
        return sb.toString();
    }
}
