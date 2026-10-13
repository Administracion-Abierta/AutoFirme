package nu.xom.xinclude;

import java.util.ArrayList;
import java.util.List;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.IllegalNameException;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.XMLException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/xinclude/XPointer.class */
class XPointer {
    private XPointer() {
    }

    /* JADX WARN: Code duplicated, block: B:42:0x0113  */
    /* JADX WARN: Code duplicated, block: B:45:0x0127 A[LOOP:1: B:40:0x010b->B:45:0x0127, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:75:0x012d A[EDGE_INSN: B:75:0x012d->B:46:0x012d BREAK  A[LOOP:1: B:40:0x010b->B:45:0x0127], SYNTHETIC] */
    static Nodes query(Document document, String str) throws XPointerSyntaxException, XPointerResourceException {
        int i;
        Nodes nodes = new Nodes();
        boolean z = false;
        try {
            new Element(str, "http://www.example.com");
            Element elementFindByID = findByID(document.getRootElement(), str);
            if (elementFindByID != null) {
                nodes.append(elementFindByID);
                return nodes;
            }
        } catch (IllegalNameException e) {
            List listFindElementSchemeData = findElementSchemeData(str);
            if (listFindElementSchemeData.isEmpty()) {
                throw new XPointerSyntaxException("No supported XPointer schemes found");
            }
            for (int i2 = 0; i2 < listFindElementSchemeData.size(); i2++) {
                String str2 = (String) listFindElementSchemeData.get(i2);
                int[] iArrSplit = new int[0];
                ParentNode parentNodeFindNthChildElement = document;
                if (str2.indexOf(47) == -1) {
                    try {
                        new Element(str2);
                        Element elementFindByID2 = findByID(document.getRootElement(), str2);
                        if (elementFindByID2 != null) {
                            if (!z) {
                                nodes.append(elementFindByID2);
                            }
                            z = true;
                        }
                        for (int i3 : iArrSplit) {
                            parentNodeFindNthChildElement = findNthChildElement(parentNodeFindNthChildElement, i3);
                            if (parentNodeFindNthChildElement == null) {
                                break;
                            }
                        }
                        if (parentNodeFindNthChildElement == document && parentNodeFindNthChildElement != null) {
                            if (!z) {
                                nodes.append(parentNodeFindNthChildElement);
                            }
                            z = true;
                        }
                    } catch (IllegalNameException e2) {
                    }
                } else {
                    if (str2.startsWith("/")) {
                        iArrSplit = split(str2);
                    } else {
                        String strSubstring = str2.substring(0, str2.indexOf(47));
                        try {
                            new Element(strSubstring);
                            parentNodeFindNthChildElement = findByID(document.getRootElement(), strSubstring);
                            iArrSplit = split(str2.substring(str2.indexOf(47)));
                            if (parentNodeFindNthChildElement == null) {
                            }
                        } catch (XMLException e3) {
                        }
                    }
                    while (i < iArrSplit.length) {
                        parentNodeFindNthChildElement = findNthChildElement(parentNodeFindNthChildElement, i3);
                        if (parentNodeFindNthChildElement == null) {
                            break;
                            break;
                        }
                    }
                    if (parentNodeFindNthChildElement == document) {
                    }
                }
            }
        }
        if (z) {
            return nodes;
        }
        throw new XPointerResourceException(new StringBuffer().append("XPointer ").append(str).append(" did not locate any nodes in the document ").append(document.getBaseURI()).toString());
    }

    private static Element findNthChildElement(ParentNode parentNode, int i) {
        int i2 = 1;
        for (int i3 = 0; i3 < parentNode.getChildCount(); i3++) {
            Node child = parentNode.getChild(i3);
            if (child instanceof Element) {
                if (i2 == i) {
                    return (Element) child;
                }
                i2++;
            }
        }
        return null;
    }

    private static int[] split(String str) throws XPointerSyntaxException {
        int i = 0;
        for (int i2 = 0; i2 < str.length(); i2++) {
            if (str.charAt(i2) == '/') {
                i++;
            }
        }
        int[] iArr = new int[i];
        int i3 = 0;
        StringBuffer stringBuffer = new StringBuffer(3);
        for (int i4 = 1; i4 < str.length(); i4++) {
            try {
                if (str.charAt(i4) == '/') {
                    iArr[i3] = Integer.parseInt(stringBuffer.toString());
                    i3++;
                    stringBuffer = new StringBuffer(3);
                } else {
                    stringBuffer.append(str.charAt(i4));
                }
            } catch (NumberFormatException e) {
                throw new XPointerSyntaxException(new StringBuffer().append(str).append(" is not syntactically correct").toString(), e);
            }
        }
        iArr[iArr.length - 1] = Integer.parseInt(stringBuffer.toString());
        return iArr;
    }

    private static List findElementSchemeData(String str) throws XPointerSyntaxException {
        char cCharAt;
        ArrayList arrayList = new ArrayList(1);
        StringBuffer stringBuffer = new StringBuffer(str.trim());
        StringBuffer stringBuffer2 = new StringBuffer();
        int i = 0;
        while (i < stringBuffer.length() && (cCharAt = stringBuffer.charAt(i)) != '(') {
            stringBuffer2.append(cCharAt);
            i++;
        }
        try {
            new Element(stringBuffer2.toString(), "http://www.example.com/");
            int i2 = 1;
            int i3 = i + 1;
            StringBuffer stringBuffer3 = new StringBuffer();
            while (i2 > 0) {
                try {
                    char cCharAt2 = stringBuffer.charAt(i3);
                    if (cCharAt2 == '^') {
                        char cCharAt3 = stringBuffer.charAt(i3 + 1);
                        stringBuffer3.append(cCharAt3);
                        if (cCharAt3 != '^' && cCharAt3 != '(' && cCharAt3 != ')') {
                            throw new XPointerSyntaxException("Illegal XPointer escape sequence");
                        }
                        i3++;
                    } else if (cCharAt2 == '(') {
                        stringBuffer3.append(cCharAt2);
                        i2++;
                    } else if (cCharAt2 == ')') {
                        i2--;
                        if (i2 > 0) {
                            stringBuffer3.append(cCharAt2);
                        }
                    } else {
                        stringBuffer3.append(cCharAt2);
                    }
                    i3++;
                } catch (StringIndexOutOfBoundsException e) {
                    throw new XPointerSyntaxException("Unbalanced parentheses");
                }
            }
            if (stringBuffer2.toString().equals("element")) {
                arrayList.add(stringBuffer3.toString());
            }
            if (i3 + 1 < stringBuffer.length()) {
                arrayList.addAll(findElementSchemeData(stringBuffer.substring(i3)));
            }
            return arrayList;
        } catch (IllegalNameException e2) {
            throw new XPointerSyntaxException(e2.getMessage());
        }
    }

    static Element findByID(Element element, String str) {
        Node child = element;
        boolean z = false;
        int iIndexOf = -1;
        while (true) {
            if (child instanceof Element) {
                Element element2 = (Element) child;
                for (int i = 0; i < element2.getAttributeCount(); i++) {
                    Attribute attribute = element2.getAttribute(i);
                    if (attribute.getType() == Attribute.Type.ID && attribute.getValue().trim().equals(str)) {
                        return element2;
                    }
                }
            }
            if (!z && child.getChildCount() > 0) {
                child = child.getChild(0);
                iIndexOf = 0;
            } else {
                if (z && child == element) {
                    return null;
                }
                z = false;
                ParentNode parent = child.getParent();
                if (parent.getChildCount() - 1 == iIndexOf) {
                    child = parent;
                    if (child != element) {
                        iIndexOf = child.getParent().indexOf(child);
                    }
                    z = true;
                } else {
                    iIndexOf++;
                    child = parent.getChild(iIndexOf);
                }
            }
        }
    }
}
