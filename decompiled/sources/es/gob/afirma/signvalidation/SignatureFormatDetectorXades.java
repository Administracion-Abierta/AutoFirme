package es.gob.afirma.signvalidation;

import es.gob.afirma.core.misc.AOFileUtils;
import es.gob.afirma.signers.xades.XAdESConstants;
import es.gob.afirma.signers.xml.Utils;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/SignatureFormatDetectorXades.class */
public final class SignatureFormatDetectorXades implements ISignatureFormatDetector {
    private SignatureFormatDetectorXades() {
    }

    public static String getSignatureFormat(byte[] bArr) {
        String strResolveXAdESBaselineFormat = ISignatureFormatDetector.FORMAT_UNRECOGNIZED;
        if (AOFileUtils.isXML(bArr)) {
            try {
                List<Element> listSignatures = getListSignatures(Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr)));
                if (isXAdESEPES(listSignatures)) {
                    strResolveXAdESBaselineFormat = resolveXAdESBaselineFormat(ISignatureFormatDetector.FORMAT_XADES_EPES, listSignatures);
                    if (strResolveXAdESBaselineFormat.equals(ISignatureFormatDetector.FORMAT_XADES_EPES)) {
                        strResolveXAdESBaselineFormat = resolveXAdESNoBaselineFormat(ISignatureFormatDetector.FORMAT_XADES_EPES, listSignatures);
                    }
                } else if (isXAdESBES(listSignatures)) {
                    strResolveXAdESBaselineFormat = resolveXAdESBaselineFormat(ISignatureFormatDetector.FORMAT_XADES_BES, listSignatures);
                    if (strResolveXAdESBaselineFormat.equals(ISignatureFormatDetector.FORMAT_XADES_BES)) {
                        strResolveXAdESBaselineFormat = resolveXAdESNoBaselineFormat(ISignatureFormatDetector.FORMAT_XADES_BES, listSignatures);
                    }
                }
            } catch (Exception e) {
                strResolveXAdESBaselineFormat = ISignatureFormatDetector.FORMAT_UNRECOGNIZED;
            }
        }
        return strResolveXAdESBaselineFormat;
    }

    private static String resolveXAdESBaselineFormat(String str, List<Element> list) {
        String str2 = str;
        if (isXAdESBLevel(list)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_B_LEVEL;
            if (isXAdEST(list)) {
                str2 = ISignatureFormatDetector.FORMAT_XADES_T_LEVEL;
                if (isXAdESLTLevel(list)) {
                    str2 = ISignatureFormatDetector.FORMAT_XADES_LT_LEVEL;
                    if (isXAdESLTALevel(list)) {
                        str2 = ISignatureFormatDetector.FORMAT_XADES_LTA_LEVEL;
                    }
                } else {
                    resolveXAdESNoBaselineFormat(str2, list);
                }
            }
        }
        return str2;
    }

    private static String resolveXAdESNoBaselineFormat(String str, List<Element> list) {
        String str2 = str;
        if (isXAdESA(list)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_A;
        } else if (isXAdESXL1(list)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_XL1;
        } else if (isXAdESXL2(list)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_XL2;
        } else if (isXAdESX1(list)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_X1;
        } else if (isXAdESX2(list)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_X2;
        } else if (isXAdESC(list)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_C;
        } else if (isXAdEST(list) && !str2.equals(ISignatureFormatDetector.FORMAT_XADES_T_LEVEL)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_T;
        }
        return str2;
    }

    private static Element getXMLElement(Element element, String str, String str2) {
        Element element2 = (Element) element.getElementsByTagNameNS(str, str2).item(0);
        if (element2 == null) {
            element2 = (Element) element.getElementsByTagName(str2).item(0);
        }
        return element2;
    }

    public static boolean hasSignaturePolicyIdentifier(Element element) {
        Element xMLElement;
        Element xMLElement2 = getXMLElement(element, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNED_PROPERTIES);
        if (xMLElement2 != null && (xMLElement = getXMLElement(xMLElement2, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNED_SIGNATURE_PROPERTIES)) != null && getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNATURE_POLICY_IDENTIFIER) != null) {
            return true;
        }
        return false;
    }

    private static boolean isXAdESLTALevel(List<Element> list) {
        return isXAdESA(list);
    }

    private static boolean isXAdESLTALevel(Element element) {
        return isXAdESA(element);
    }

    private static boolean isXAdESLTLevel(List<Element> list) {
        if (!list.isEmpty()) {
            Iterator<Element> it = list.iterator();
            while (it.hasNext()) {
                if (isXAdESLTLevel(it.next())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESLTLevel(Element element) {
        Element xMLElement;
        Element xMLElement2 = getXMLElement(element, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_PROPERTIES);
        if (xMLElement2 != null && (xMLElement = getXMLElement(xMLElement2, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_SIGNATURE_PROPERTIES)) != null) {
            boolean z = getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_CERTIFICATE_VALUES) != null;
            if (checkLTLevel(getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_REVOCATION_VALUES) != null, false, getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.4.1#", XAdESConstants.TAG_TIME_STAMP_VALIDATION_DATA) != null, getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_COMPLETE_CERTIFICATE_REFS) != null, getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_COMPLETE_REVOCATION_REFS) != null, getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_ATTRIBUTE_CERTIFICATE_REFS) != null, getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_ATTRIBUTE_REVOCATION_REFS) != null, getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIG_AND_REFS_TIMESTAMP) != null, getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_REFS_ONLY_TIMESTAMP) != null)) {
                return true;
            }
            return false;
        }
        return false;
    }

    private static boolean checkLTLevel(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9) {
        if (((z && z2) || z3) && !z4 && !z5 && !z6 && !z7 && !z8 && !z9) {
            return true;
        }
        return false;
    }

    private static boolean isXAdESBLevel(List<Element> list) {
        if (!list.isEmpty()) {
            Iterator<Element> it = list.iterator();
            if (it.hasNext()) {
                return isXAdESBLevel(it.next());
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESBLevel(Element element) {
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        if (element.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_QUALIFYING_PROPERTIES_REFERENCE).getLength() == 0 && element.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNED_PROPERTIES).getLength() > 0) {
            Element element2 = (Element) element.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNED_PROPERTIES).item(0);
            if (element2.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNED_SIGNATURE_PROPERTIES).getLength() > 0) {
                Element element3 = (Element) element.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNED_SIGNATURE_PROPERTIES).item(0);
                z = element3.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNING_CERTIFICATE).getLength() > 0;
                z2 = element3.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNING_TIME).getLength() > 0;
            }
            if (element2.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNED_DATA_OBJECT_PROPERTIES).getLength() > 0) {
                Element xMLElement = getXMLElement((Element) element2.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNED_DATA_OBJECT_PROPERTIES).item(0), "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_DATA_OBJECT_FORMAT);
                z3 = xMLElement != null;
                if (xMLElement != null) {
                    z3 = true;
                    z4 = xMLElement.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", "MimeType").getLength() > 0;
                }
            }
            if (z2 && z && z3 && z4) {
                return true;
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESBBLevel(Element element) {
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        if (element.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_QUALIFYING_PROPERTIES_REFERENCE).getLength() == 0 && element.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNED_PROPERTIES).getLength() > 0) {
            Element element2 = (Element) element.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNED_PROPERTIES).item(0);
            if (element2.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNED_SIGNATURE_PROPERTIES).getLength() > 0) {
                Element element3 = (Element) element.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNED_SIGNATURE_PROPERTIES).item(0);
                z = element3.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNING_CERTIFICATE_V2).getLength() > 0;
                z2 = element3.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNING_TIME).getLength() > 0;
            }
            if (element2.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNED_DATA_OBJECT_PROPERTIES).getLength() > 0) {
                Element xMLElement = getXMLElement((Element) element2.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNED_DATA_OBJECT_PROPERTIES).item(0), "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_DATA_OBJECT_FORMAT);
                z3 = xMLElement != null;
                if (xMLElement != null) {
                    z3 = true;
                    z4 = xMLElement.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", "MimeType").getLength() > 0;
                }
            }
            if (z2 && z && z3 && z4) {
                return true;
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESA(List<Element> list) {
        if (!list.isEmpty()) {
            Iterator<Element> it = list.iterator();
            while (it.hasNext()) {
                if (isXAdESA(it.next())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESA(Element element) {
        Element xMLElement;
        Element xMLElement2 = getXMLElement(element, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_PROPERTIES);
        if (xMLElement2 != null && (xMLElement = getXMLElement(xMLElement2, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_SIGNATURE_PROPERTIES)) != null) {
            NodeList childNodes = xMLElement.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i).getNodeType() == 1 && childNodes.item(i).getLocalName().equals(XAdESConstants.TAG_ARCHIVE_TIMESTAMP)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESXL1(List<Element> list) {
        if (!list.isEmpty()) {
            Iterator<Element> it = list.iterator();
            while (it.hasNext()) {
                if (isXAdESXL1(it.next())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESXL1(Element element) {
        Element xMLElement;
        Element xMLElement2 = getXMLElement(element, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_PROPERTIES);
        if (xMLElement2 != null && (xMLElement = getXMLElement(xMLElement2, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_SIGNATURE_PROPERTIES)) != null) {
            Element xMLElement3 = getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIG_AND_REFS_TIMESTAMP);
            Element xMLElement4 = getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_CERTIFICATE_VALUES);
            Element xMLElement5 = getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_REVOCATION_VALUES);
            if (xMLElement3 != null && xMLElement4 != null && xMLElement5 != null) {
                return true;
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESXL2(List<Element> list) {
        if (!list.isEmpty()) {
            Iterator<Element> it = list.iterator();
            while (it.hasNext()) {
                if (isXAdESXL2(it.next())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESXL2(Element element) {
        Element xMLElement;
        Element xMLElement2 = getXMLElement(element, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_PROPERTIES);
        if (xMLElement2 != null && (xMLElement = getXMLElement(xMLElement2, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_SIGNATURE_PROPERTIES)) != null) {
            Element xMLElement3 = getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_REFS_ONLY_TIMESTAMP);
            Element xMLElement4 = getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_CERTIFICATE_VALUES);
            Element xMLElement5 = getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_REVOCATION_VALUES);
            if (xMLElement3 != null && xMLElement4 != null && xMLElement5 != null) {
                return true;
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESX1(List<Element> list) {
        if (!list.isEmpty()) {
            Iterator<Element> it = list.iterator();
            while (it.hasNext()) {
                if (isXAdESX1(it.next())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESX1(Element element) {
        Element xMLElement;
        Element xMLElement2 = getXMLElement(element, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_PROPERTIES);
        if (xMLElement2 != null && (xMLElement = getXMLElement(xMLElement2, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_SIGNATURE_PROPERTIES)) != null && getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIG_AND_REFS_TIMESTAMP) != null) {
            return true;
        }
        return false;
    }

    private static boolean isXAdESX2(List<Element> list) {
        if (!list.isEmpty()) {
            Iterator<Element> it = list.iterator();
            while (it.hasNext()) {
                if (isXAdESX2(it.next())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESX2(Element element) {
        Element xMLElement;
        Element xMLElement2 = getXMLElement(element, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_PROPERTIES);
        if (xMLElement2 != null && (xMLElement = getXMLElement(xMLElement2, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_SIGNATURE_PROPERTIES)) != null && getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_REFS_ONLY_TIMESTAMP) != null) {
            return true;
        }
        return false;
    }

    private static boolean isXAdESC(List<Element> list) {
        if (!list.isEmpty()) {
            Iterator<Element> it = list.iterator();
            while (it.hasNext()) {
                if (isXAdESC(it.next())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESC(Element element) {
        Element xMLElement;
        Element xMLElement2 = getXMLElement(element, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_PROPERTIES);
        if (xMLElement2 != null && (xMLElement = getXMLElement(xMLElement2, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_SIGNATURE_PROPERTIES)) != null) {
            Element xMLElement3 = getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_COMPLETE_CERTIFICATE_REFS);
            Element xMLElement4 = getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_COMPLETE_REVOCATION_REFS);
            if (xMLElement3 != null && xMLElement4 != null) {
                return true;
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESEPES(List<Element> list) {
        if (!list.isEmpty()) {
            Iterator<Element> it = list.iterator();
            while (it.hasNext()) {
                if (hasSignaturePolicyIdentifier(it.next())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESBES(List<Element> list) {
        if (!list.isEmpty()) {
            Iterator<Element> it = list.iterator();
            while (it.hasNext()) {
                if (getXMLElement(it.next(), "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_QUALIFYING_PROPERTIES) != null) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdESBES(Element element) {
        if (getXMLElement(element, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_QUALIFYING_PROPERTIES) != null) {
            return true;
        }
        return false;
    }

    private static boolean isXAdEST(List<Element> list) {
        if (!list.isEmpty()) {
            Iterator<Element> it = list.iterator();
            while (it.hasNext()) {
                if (isXAdEST(it.next())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isXAdEST(Element element) {
        Element xMLElement;
        Element xMLElement2 = getXMLElement(element, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_PROPERTIES);
        if (xMLElement2 != null && (xMLElement = getXMLElement(xMLElement2, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_UNSIGNED_SIGNATURE_PROPERTIES)) != null && getXMLElement(xMLElement, "http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNATURE_TIMESTAMP) != null) {
            return true;
        }
        return false;
    }

    public static String resolveSignerXAdESFormat(Element element) {
        String strResolveXAdESBaselineFormat = ISignatureFormatDetector.FORMAT_UNRECOGNIZED;
        if (element != null) {
            if (isXAdESEPES(element)) {
                strResolveXAdESBaselineFormat = resolveXAdESBaselineFormat(ISignatureFormatDetector.FORMAT_XADES_EPES, element);
                if (strResolveXAdESBaselineFormat.equals(ISignatureFormatDetector.FORMAT_XADES_EPES)) {
                    strResolveXAdESBaselineFormat = resolveXAdESNoBaselineFormat(ISignatureFormatDetector.FORMAT_XADES_EPES, element);
                }
            } else if (isXAdESBES(element)) {
                strResolveXAdESBaselineFormat = resolveXAdESBaselineFormat(ISignatureFormatDetector.FORMAT_XADES_BES, element);
                if (strResolveXAdESBaselineFormat.equals(ISignatureFormatDetector.FORMAT_XADES_BES)) {
                    strResolveXAdESBaselineFormat = resolveXAdESNoBaselineFormat(ISignatureFormatDetector.FORMAT_XADES_BES, element);
                }
            }
        }
        return strResolveXAdESBaselineFormat;
    }

    private static boolean isXAdESEPES(Element element) {
        if (hasSignaturePolicyIdentifier(element)) {
            return true;
        }
        return false;
    }

    private static String resolveXAdESBaselineFormat(String str, Element element) {
        String str2 = str;
        if (isXAdESBLevel(element)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_B_LEVEL;
            if (isXAdEST(element)) {
                str2 = ISignatureFormatDetector.FORMAT_XADES_T_LEVEL;
                if (isXAdESLTLevel(element)) {
                    str2 = ISignatureFormatDetector.FORMAT_XADES_LT_LEVEL;
                    if (isXAdESLTALevel(element)) {
                        str2 = ISignatureFormatDetector.FORMAT_XADES_LTA_LEVEL;
                    }
                } else {
                    resolveXAdESNoBaselineFormat(str2, element);
                }
            }
        } else if (isXAdESBBLevel(element)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_B_B_LEVEL;
        }
        return str2;
    }

    private static String resolveXAdESNoBaselineFormat(String str, Element element) {
        String str2 = str;
        if (isXAdESA(element)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_A;
        } else if (isXAdESXL1(element)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_XL1;
        } else if (isXAdESXL2(element)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_XL2;
        } else if (isXAdESX1(element)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_X1;
        } else if (isXAdESX2(element)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_X2;
        } else if (isXAdESC(element)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_C;
        } else if (isXAdEST(element) && !str2.equals(ISignatureFormatDetector.FORMAT_XADES_T_LEVEL)) {
            str2 = ISignatureFormatDetector.FORMAT_XADES_T;
        }
        return str2;
    }

    public static boolean isXAdESBaseline(String str) {
        return str.equals(ISignatureFormatDetector.FORMAT_XADES_B_LEVEL) || str.equals(ISignatureFormatDetector.FORMAT_XADES_T_LEVEL) || str.equals(ISignatureFormatDetector.FORMAT_XADES_LT_LEVEL) || str.equals(ISignatureFormatDetector.FORMAT_XADES_LTA_LEVEL);
    }

    public static List<Element> getListSignatures(Document document) {
        ArrayList arrayList = new ArrayList();
        NodeList elementsByTagNameNS = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        if (elementsByTagNameNS != null) {
            for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
                Element element = (Element) elementsByTagNameNS.item(i);
                if (element.getParentNode() == null || (!"XMLTimeStamp".equals(element.getParentNode().getLocalName()) && !"XMLTimeStamp".equals(element.getParentNode().getLocalName()))) {
                    arrayList.add(element);
                }
            }
        }
        return arrayList;
    }
}
