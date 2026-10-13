package nu.xom.xinclude;

import com.ctc.wstx.cfg.XmlConsts;
import com.ctc.wstx.io.CharsetNames;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.MalformedURIException;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.ParsingException;
import nu.xom.Text;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.spongycastle.i18n.TextBundle;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/xinclude/XIncluder.class */
public class XIncluder {
    private static String version = System.getProperty("java.version");
    public static final String XINCLUDE_NS = "http://www.w3.org/2001/XInclude";

    private XIncluder() {
    }

    public static Document resolve(Document document) throws IOException, ParsingException, XIncludeException {
        return resolve(document, new Builder());
    }

    public static Document resolve(Document document, Builder builder) throws IOException, ParsingException, XIncludeException {
        Document document2 = new Document(document);
        resolveInPlace(document2, builder);
        return document2;
    }

    public static void resolveInPlace(Document document) throws IOException, ParsingException, XIncludeException {
        resolveInPlace(document, new Builder());
    }

    public static void resolveInPlace(Document document, Builder builder) throws IOException, ParsingException, XIncludeException {
        resolveInPlace(document, builder, new ArrayList());
    }

    private static void resolveInPlace(Document document, Builder builder, ArrayList arrayList) throws IOException, ParsingException, XIncludeException {
        String baseURI = document.getBaseURI();
        if (baseURI != null && baseURI.startsWith("file:///")) {
            baseURI = new StringBuffer().append("file:/").append(baseURI.substring(8)).toString();
        }
        arrayList.add(baseURI);
        resolve(document.getRootElement(), builder, arrayList);
        arrayList.remove(arrayList.size() - 1);
    }

    private static void resolve(Element element, Builder builder, ArrayList arrayList) throws IOException, ParsingException, XIncludeException {
        resolve(element, builder, arrayList, null);
    }

    private static void resolve(Element element, Builder builder, ArrayList arrayList, Document document) throws IOException, ParsingException, XIncludeException {
        Nodes nodesResolveXPointerSelection;
        Node node;
        if (!isIncludeElement(element)) {
            if (isFallbackElement(element)) {
                throw new MisplacedFallbackException("Fallback element outside include element", element.getDocument().getBaseURI());
            }
            Elements childElements = element.getChildElements();
            for (int i = 0; i < childElements.size(); i++) {
                resolve(childElements.get(i), builder, arrayList);
            }
            return;
        }
        verifyIncludeElement(element);
        String attributeValue = element.getAttributeValue("parse");
        if (attributeValue == null) {
            attributeValue = CanonicalizerBase.XML;
        }
        String attributeValue2 = element.getAttributeValue("xpointer");
        String attributeValue3 = element.getAttributeValue("encoding");
        String attributeValue4 = element.getAttributeValue("href");
        if ("".equals(attributeValue4)) {
            attributeValue4 = null;
        }
        ParentNode parent = element.getParent();
        URL url = null;
        try {
            url = new URL(element.getBaseURI());
        } catch (MalformedURLException e) {
        }
        URL url2 = null;
        try {
            if (url != null && attributeValue4 != null) {
                url2 = absolutize(url, attributeValue4);
            } else if (attributeValue4 != null) {
                try {
                    testURISyntax(attributeValue4);
                    url2 = new URL(attributeValue4);
                } catch (MalformedURIException e2) {
                    if (url != null) {
                        throw new BadHrefAttributeException("Illegal IRI in href attribute", attributeValue4);
                    }
                    throw new BadHrefAttributeException(new StringBuffer().append("Could not resolve relative URI ").append(attributeValue4).append(" because the xi:include element does").append(" not have a base URI.").toString(), attributeValue4);
                }
            }
            String attributeValue5 = element.getAttributeValue("accept");
            checkHeader(attributeValue5);
            String attributeValue6 = element.getAttributeValue("accept-language");
            checkHeader(attributeValue6);
            if (attributeValue.equals(CanonicalizerBase.XML)) {
                String xMLLangValue = parent instanceof Element ? getXMLLangValue((Element) parent) : "";
                if (url2 != null) {
                    nodesResolveXPointerSelection = downloadXMLDocument(url2, attributeValue2, builder, arrayList, attributeValue5, attributeValue6, xMLLangValue);
                    for (int i2 = 0; i2 < nodesResolveXPointerSelection.size(); i2++) {
                        Node node2 = nodesResolveXPointerSelection.get(i2);
                        if (node2 instanceof Element) {
                            String baseURI = node2.getBaseURI();
                            if (baseURI.indexOf(35) >= 0) {
                                baseURI = baseURI.substring(0, baseURI.indexOf(35));
                            }
                            Element element2 = (Element) node2;
                            String baseURI2 = parent.getBaseURI();
                            if (baseURI2 != null && !"".equals(baseURI2)) {
                                baseURI2 = getDirectoryBase(baseURI2);
                            }
                            if (baseURI.startsWith(baseURI2)) {
                                baseURI = baseURI.substring(baseURI2.length());
                            }
                            element2.addAttribute(new Attribute("xml:base", "http://www.w3.org/XML/1998/namespace", baseURI));
                        }
                    }
                } else {
                    Document document2 = element.getDocument();
                    if (document2 == null) {
                        document2 = document;
                    }
                    Nodes nodesQuery = XPointer.query(document2, attributeValue2);
                    Nodes nodes = new Nodes();
                    for (int i3 = 0; i3 < nodesQuery.size(); i3++) {
                        Node node3 = nodesQuery.get(i3);
                        if (contains((Element) node3, element)) {
                            throw new InclusionLoopException("Element tried to include itself");
                        }
                        nodes.append(node3.copy());
                    }
                    nodesResolveXPointerSelection = resolveXPointerSelection(nodes, builder, arrayList, document2);
                }
                if (parent instanceof Element) {
                    int iIndexOf = parent.indexOf(element);
                    for (int i4 = 0; i4 < nodesResolveXPointerSelection.size(); i4++) {
                        parent.insertChild(nodesResolveXPointerSelection.get(i4), iIndexOf + i4);
                    }
                    element.detach();
                } else {
                    Document document3 = (Document) parent;
                    int i5 = 0;
                    while (true) {
                        node = nodesResolveXPointerSelection.get(i5);
                        i5++;
                        if (node instanceof Element) {
                            break;
                        } else {
                            document3.insertChild(node, document3.indexOf(element));
                        }
                    }
                    document3.setRootElement((Element) node);
                    int iIndexOf2 = document3.indexOf(document3.getRootElement());
                    for (int i6 = i5; i6 < nodesResolveXPointerSelection.size(); i6++) {
                        document3.insertChild(nodesResolveXPointerSelection.get(i6), ((iIndexOf2 + 1) + i6) - i5);
                    }
                }
            } else {
                if (!attributeValue.equals(TextBundle.TEXT_ENTRY)) {
                    throw new BadParseAttributeException(new StringBuffer().append("Bad value for parse attribute: ").append(attributeValue).toString(), element.getDocument().getBaseURI());
                }
                Nodes nodesDownloadTextDocument = downloadTextDocument(url2, attributeValue3, builder, attributeValue5, attributeValue6);
                for (int i7 = 0; i7 < nodesDownloadTextDocument.size(); i7++) {
                    Node node4 = nodesDownloadTextDocument.get(i7);
                    if (node4 instanceof Attribute) {
                        ((Element) parent).addAttribute((Attribute) node4);
                    } else {
                        parent.insertChild(node4, parent.indexOf(element));
                    }
                }
                parent.removeChild(element);
            }
        } catch (IOException e3) {
            processFallback(element, builder, arrayList, parent, e3);
        } catch (XPointerResourceException e4) {
            processFallback(element, builder, arrayList, parent, e4);
        } catch (XPointerSyntaxException e5) {
            processFallback(element, builder, arrayList, parent, e5);
        }
    }

    private static String getDirectoryBase(String str) {
        return str.endsWith("/") ? str : str.substring(0, str.lastIndexOf(47) + 1);
    }

    private static void verifyIncludeElement(Element element) throws XIncludeException {
        testHref(element);
        testForFragmentIdentifier(element);
        verifyEncoding(element);
        testForForbiddenChildElements(element);
    }

    private static void testHref(Element element) throws NoIncludeLocationException {
        String attributeValue = element.getAttributeValue("href");
        String attributeValue2 = element.getAttributeValue("xpointer");
        if (attributeValue == null && attributeValue2 == null) {
            throw new NoIncludeLocationException("Missing href attribute", element.getDocument().getBaseURI());
        }
    }

    private static void testForFragmentIdentifier(Element element) throws BadHrefAttributeException {
        String attributeValue = element.getAttributeValue("href");
        if (attributeValue != null && attributeValue.indexOf(35) > -1) {
            throw new BadHrefAttributeException(new StringBuffer().append("fragment identifier in URI ").append(attributeValue).toString(), element.getBaseURI());
        }
    }

    private static void verifyEncoding(Element element) throws BadEncodingAttributeException {
        String attributeValue = element.getAttributeValue("encoding");
        if (attributeValue == null) {
            return;
        }
        char[] charArray = attributeValue.toCharArray();
        if (charArray.length == 0) {
            throw new BadEncodingAttributeException("Empty encoding attribute", element.getBaseURI());
        }
        char c = charArray[0];
        if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z')) {
            throw new BadEncodingAttributeException(new StringBuffer().append("Illegal value for encoding attribute: ").append(attributeValue).toString(), element.getBaseURI());
        }
        for (int i = 1; i < charArray.length; i++) {
            char c2 = charArray[i];
            if ((c2 < 'A' || c2 > 'Z') && ((c2 < 'a' || c2 > 'z') && !((c2 >= '0' && c2 <= '9') || c2 == '-' || c2 == '_' || c2 == '.'))) {
                throw new BadEncodingAttributeException(new StringBuffer().append("Illegal value for encoding attribute: ").append(attributeValue).toString(), element.getBaseURI());
            }
        }
    }

    private static URL absolutize(URL url, String str) throws BadHrefAttributeException, MalformedURLException {
        Element element = new Element("c");
        String externalForm = url.toExternalForm();
        element.setBaseURI(externalForm);
        Element element2 = new Element("c");
        element.appendChild(element2);
        element2.addAttribute(new Attribute("xml:base", "http://www.w3.org/XML/1998/namespace", str));
        URL url2 = new URL(element2.getBaseURI());
        if ("".equals(str) || !url2.toExternalForm().equals(externalForm) || url.toExternalForm().endsWith(str)) {
            return url2;
        }
        throw new BadHrefAttributeException(new StringBuffer().append(str).append(" is not a syntactically correct IRI").toString());
    }

    private static void testURISyntax(String str) {
        new Element("e").setNamespaceURI(str);
    }

    private static String getXMLLangValue(Element element) {
        while (true) {
            Attribute attribute = element.getAttribute("lang", "http://www.w3.org/XML/1998/namespace");
            if (attribute != null) {
                return attribute.getValue();
            }
            ParentNode parent = element.getParent();
            if (parent == null || (parent instanceof Document)) {
                return "";
            }
            element = (Element) parent;
        }
    }

    private static Nodes resolveXPointerSelection(Nodes nodes, Builder builder, ArrayList arrayList, Document document) throws IOException, ParsingException, XIncludeException {
        return resolveSilently((Element) nodes.get(0), builder, arrayList, document);
    }

    private static boolean contains(ParentNode parentNode, Node node) {
        Node parent = node;
        while (true) {
            Node node2 = parent;
            if (node2 == null) {
                return false;
            }
            if (node2 == parentNode) {
                return true;
            }
            parent = node2.getParent();
        }
    }

    private static Nodes resolveSilently(Element element, Builder builder, ArrayList arrayList, Document document) throws IOException, ParsingException, XIncludeException {
        if (isIncludeElement(element) || isFallbackElement(element)) {
            throw new RuntimeException("XOM BUG: include or fallback element passed to resolveSilently; please report with a test case");
        }
        Elements childElements = element.getChildElements();
        for (int i = 0; i < childElements.size(); i++) {
            resolve(childElements.get(i), builder, arrayList, document);
        }
        return new Nodes(element);
    }

    private static void testForForbiddenChildElements(Element element) throws XIncludeException {
        int i = 0;
        Elements childElements = element.getChildElements();
        int size = childElements.size();
        for (int i2 = 0; i2 < size; i2++) {
            Element element2 = childElements.get(i2);
            if (XINCLUDE_NS.equals(element2.getNamespaceURI())) {
                if (!"fallback".equals(element2.getLocalName())) {
                    throw new XIncludeException("Include element contains an include child", element.getDocument().getBaseURI());
                }
                i++;
                if (i > 1) {
                    throw new XIncludeException("Multiple fallback elements", element.getDocument().getBaseURI());
                }
            }
        }
    }

    private static void processFallback(Element element, Builder builder, ArrayList arrayList, ParentNode parentNode, Exception exc) throws IOException, ParsingException, XIncludeException {
        Element firstChildElement = element.getFirstChildElement("fallback", XINCLUDE_NS);
        if (firstChildElement == null) {
            if (exc instanceof IOException) {
                throw ((IOException) exc);
            }
            XIncludeException xIncludeException = new XIncludeException(exc.getMessage(), element.getDocument().getBaseURI());
            xIncludeException.initCause(exc);
            throw xIncludeException;
        }
        while (firstChildElement.getChildCount() > 0) {
            Node child = firstChildElement.getChild(0);
            if (child instanceof Element) {
                resolve((Element) child, builder, arrayList);
            }
            Node child2 = firstChildElement.getChild(0);
            child2.detach();
            parentNode.insertChild(child2, parentNode.indexOf(element));
        }
        element.detach();
    }

    private static Nodes downloadXMLDocument(URL url, String str, Builder builder, ArrayList arrayList, String str2, String str3, String str4) throws XPointerSyntaxException, IOException, ParsingException, XIncludeException, XPointerResourceException {
        Nodes nodes;
        String externalForm = url.toExternalForm();
        if (str == null && arrayList.indexOf(externalForm) != -1) {
            throw new InclusionLoopException(new StringBuffer().append("Tried to include the already included document ").append(externalForm).append(" from ").append(arrayList.get(arrayList.size() - 1)).toString(), (String) arrayList.get(arrayList.size() - 1));
        }
        URLConnection uRLConnectionOpenConnection = url.openConnection();
        setHeaders(uRLConnectionOpenConnection, str2, str3);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(uRLConnectionOpenConnection.getInputStream());
        try {
            Document documentBuild = builder.build(bufferedInputStream, url.toExternalForm());
            bufferedInputStream.close();
            resolveInPlace(documentBuild, builder, arrayList);
            if (str == null || str.length() == 0) {
                nodes = new Nodes();
                for (int i = 0; i < documentBuild.getChildCount(); i++) {
                    Node child = documentBuild.getChild(i);
                    if (!(child instanceof DocType)) {
                        nodes.append(child);
                    }
                }
            } else {
                nodes = XPointer.query(documentBuild, str);
                for (int i2 = 0; i2 < nodes.size(); i2++) {
                    Element element = (Element) nodes.get(i2);
                    if (element.getAttribute("lang", "http://www.w3.org/XML/1998/namespace") == null) {
                        String xMLLangValue = getXMLLangValue(element);
                        if (!str4.equals(xMLLangValue)) {
                            element.addAttribute(new Attribute("xml:lang", "http://www.w3.org/XML/1998/namespace", xMLLangValue));
                        }
                    }
                }
            }
            documentBuild.setRootElement(new Element("f"));
            for (int i3 = 0; i3 < nodes.size(); i3++) {
                Node node = nodes.get(i3);
                String baseURI = node.getBaseURI();
                if (baseURI.indexOf(35) >= 0) {
                    baseURI = baseURI.substring(0, baseURI.indexOf(35));
                }
                node.detach();
                if (node instanceof Element) {
                    ((Element) node).setBaseURI(baseURI);
                }
            }
            return nodes;
        } catch (Throwable th) {
            bufferedInputStream.close();
            throw th;
        }
    }

    private static Nodes downloadTextDocument(URL url, String str, Builder builder, String str2, String str3) throws IOException, XIncludeException {
        if (str == null || str.length() == 0) {
            str = "UTF-8";
        }
        URLConnection uRLConnectionOpenConnection = url.openConnection();
        setHeaders(uRLConnectionOpenConnection, str2, str3);
        String contentEncoding = uRLConnectionOpenConnection.getContentEncoding();
        String contentType = uRLConnectionOpenConnection.getContentType();
        int contentLength = uRLConnectionOpenConnection.getContentLength();
        if (contentLength < 0) {
            contentLength = 1024;
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(uRLConnectionOpenConnection.getInputStream());
        if (contentEncoding != null) {
            str = contentEncoding;
        } else if (contentType != null) {
            try {
                String lowerCase = contentType.toLowerCase(Locale.ENGLISH);
                if (lowerCase.equals("text/xml") || lowerCase.equals("application/xml") || ((lowerCase.startsWith("text/") && lowerCase.endsWith("+xml")) || (lowerCase.startsWith("application/") && lowerCase.endsWith("+xml")))) {
                    str = EncodingHeuristics.readEncodingFromStream(bufferedInputStream);
                }
            } finally {
                bufferedInputStream.close();
            }
        }
        if (version.startsWith("1.2") || version.startsWith(XmlConsts.XML_V_11_STR)) {
            if (str.equalsIgnoreCase(CharsetNames.CS_UTF16)) {
                bufferedInputStream.mark(2);
                str = bufferedInputStream.read() == 255 ? "UnicodeLittle" : "UnicodeBig";
                bufferedInputStream.reset();
            } else if (str.equalsIgnoreCase("UnicodeBigUnmarked")) {
                str = "UnicodeBig";
            } else if (str.equalsIgnoreCase("UnicodeLittleUnmarked")) {
                str = "UnicodeLittle";
            }
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream, str));
        StringBuffer stringBuffer = new StringBuffer(contentLength);
        for (int i = bufferedReader.read(); i != -1; i = bufferedReader.read()) {
            stringBuffer.append((char) i);
        }
        NodeFactory nodeFactory = builder.getNodeFactory();
        return nodeFactory != null ? nodeFactory.makeText(stringBuffer.toString()) : new Nodes(new Text(stringBuffer.toString()));
    }

    private static void setHeaders(URLConnection uRLConnection, String str, String str2) throws BadHTTPHeaderException {
        if (str != null) {
            checkHeader(str);
            uRLConnection.setRequestProperty("accept", str);
        }
        if (str2 != null) {
            checkHeader(str2);
            uRLConnection.setRequestProperty("accept-language", str2);
        }
    }

    private static void checkHeader(String str) throws BadHTTPHeaderException {
        if (str == null) {
            return;
        }
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt < ' ' || cCharAt > '~') {
                throw new BadHTTPHeaderException(new StringBuffer().append("Header contains illegal character 0x").append(Integer.toHexString(cCharAt).toUpperCase()).toString());
            }
        }
    }

    private static boolean isIncludeElement(Element element) {
        return element.getLocalName().equals("include") && element.getNamespaceURI().equals(XINCLUDE_NS);
    }

    private static boolean isFallbackElement(Element element) {
        return element.getLocalName().equals("fallback") && element.getNamespaceURI().equals(XINCLUDE_NS);
    }
}
