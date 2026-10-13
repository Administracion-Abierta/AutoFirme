package es.gob.afirma.signers.ooxml;

import es.gob.afirma.core.misc.Platform;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.LinkedList;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.SignatureProperty;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/OOXMLOfficeObjectHelper.class */
final class OOXMLOfficeObjectHelper {
    private static final String MS_DIGITAL_SIGNATURE_SCHEMA = "http://schemas.microsoft.com/office/2006/digsig";
    private static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";

    private OOXMLOfficeObjectHelper() {
    }

    static XMLObject getOfficeObject(String str, XMLSignatureFactory xMLSignatureFactory, Document document, String str2, String str3, String str4, String str5, String str6) {
        LinkedList linkedList = new LinkedList();
        Element elementCreateElementNS = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "SignatureInfoV1");
        elementCreateElementNS.setAttributeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, MS_DIGITAL_SIGNATURE_SCHEMA);
        elementCreateElementNS.appendChild(document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "SetupID"));
        elementCreateElementNS.appendChild(document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "SignatureText"));
        elementCreateElementNS.appendChild(document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "SignatureImage"));
        if (str3 != null) {
            Element elementCreateElementNS2 = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "SignatureComments");
            elementCreateElementNS2.setTextContent(str3);
            elementCreateElementNS.appendChild(elementCreateElementNS2);
        }
        if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
            Element elementCreateElementNS3 = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "WindowsVersion");
            elementCreateElementNS3.setTextContent(System.getProperty("os.version"));
            elementCreateElementNS.appendChild(elementCreateElementNS3);
        }
        Element elementCreateElementNS4 = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "OfficeVersion");
        elementCreateElementNS4.setTextContent("16.0");
        elementCreateElementNS.appendChild(elementCreateElementNS4);
        Element elementCreateElementNS5 = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "ApplicationVersion");
        elementCreateElementNS5.setTextContent("16.0");
        elementCreateElementNS.appendChild(elementCreateElementNS5);
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Element elementCreateElementNS6 = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "Monitors");
        elementCreateElementNS6.setTextContent(Integer.toString(localGraphicsEnvironment.getScreenDevices().length));
        elementCreateElementNS.appendChild(elementCreateElementNS6);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Element elementCreateElementNS7 = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "HorizontalResolutionElement");
        elementCreateElementNS7.setTextContent(Integer.toString(screenSize.width));
        elementCreateElementNS.appendChild(elementCreateElementNS7);
        Element elementCreateElementNS8 = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "VerticalResolutionElement");
        elementCreateElementNS8.setTextContent(Integer.toString(screenSize.height));
        elementCreateElementNS.appendChild(elementCreateElementNS8);
        Element elementCreateElementNS9 = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "ColorDepth");
        elementCreateElementNS9.setTextContent(Integer.toString(localGraphicsEnvironment.getScreenDevices()[0].getDisplayMode().getBitDepth()));
        elementCreateElementNS.appendChild(elementCreateElementNS9);
        Element elementCreateElementNS10 = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "SignatureProviderId");
        elementCreateElementNS10.setTextContent("{00000000-0000-0000-0000-000000000000}");
        elementCreateElementNS.appendChild(elementCreateElementNS10);
        elementCreateElementNS.appendChild(document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "SignatureProviderUrl"));
        Element elementCreateElementNS11 = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "SignatureProviderDetails");
        elementCreateElementNS11.setTextContent("9");
        elementCreateElementNS.appendChild(elementCreateElementNS11);
        if (str6 != null && !str6.isEmpty()) {
            Element elementCreateElementNS12 = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "SignatureType");
            elementCreateElementNS12.setTextContent(str6);
            elementCreateElementNS.appendChild(elementCreateElementNS12);
        }
        Element elementCreateElementNS13 = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "SignatureInfoV2");
        elementCreateElementNS13.setAttributeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, MS_DIGITAL_SIGNATURE_SCHEMA);
        if (str4 != null) {
            Element elementCreateElementNS14 = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "Address1");
            elementCreateElementNS14.setTextContent(str4);
            elementCreateElementNS13.appendChild(elementCreateElementNS14);
        }
        if (str5 != null) {
            Element elementCreateElementNS15 = document.createElementNS(MS_DIGITAL_SIGNATURE_SCHEMA, "Address2");
            elementCreateElementNS15.setTextContent(str5);
            elementCreateElementNS13.appendChild(elementCreateElementNS15);
        }
        LinkedList linkedList2 = new LinkedList();
        linkedList2.add(new DOMStructure(elementCreateElementNS));
        linkedList2.add(new DOMStructure(elementCreateElementNS13));
        SignatureProperty signaturePropertyNewSignatureProperty = xMLSignatureFactory.newSignatureProperty(linkedList2, "#" + str2, "idOfficeV1Details");
        LinkedList linkedList3 = new LinkedList();
        linkedList3.add(signaturePropertyNewSignatureProperty);
        linkedList.add(xMLSignatureFactory.newSignatureProperties(linkedList3, (String) null));
        return xMLSignatureFactory.newXMLObject(linkedList, str, (String) null, (String) null);
    }
}
