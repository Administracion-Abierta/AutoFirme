package nu.xom;

import java.io.CharConversionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UTFDataFormatException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.xerces.impl.Version;
import org.apache.xerces.parsers.SAXParser;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.AlertDescription;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.crypto.tls.ExtensionType;
import org.spongycastle.pqc.math.linearalgebra.Matrix;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Builder.class */
public class Builder {
    private XMLReader parser;
    private NodeFactory factory;
    private static double xercesVersion;
    private static String[] parsers;
    private static String fileURLPrefix;

    /* JADX INFO: renamed from: nu.xom.Builder$1, reason: invalid class name */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Builder$1.class */
    static class AnonymousClass1 {
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Builder$NamespaceWellformednessRequired.class */
    private static class NamespaceWellformednessRequired implements ErrorHandler {
        private NamespaceWellformednessRequired() {
        }

        @Override // org.xml.sax.ErrorHandler
        public void warning(SAXParseException sAXParseException) {
        }

        @Override // org.xml.sax.ErrorHandler
        public void error(SAXParseException sAXParseException) throws SAXParseException {
            if (!sAXParseException.getMessage().equals("Illegal Namespace prefix: \"xml\".")) {
                throw sAXParseException;
            }
        }

        @Override // org.xml.sax.ErrorHandler
        public void fatalError(SAXParseException sAXParseException) throws SAXParseException {
            throw sAXParseException;
        }

        /* synthetic */ NamespaceWellformednessRequired(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Builder$ValidityRequired.class */
    private static class ValidityRequired implements ErrorHandler {
        ValidityException vexception;

        private ValidityRequired() {
            this.vexception = null;
        }

        void reset() {
            this.vexception = null;
        }

        @Override // org.xml.sax.ErrorHandler
        public void warning(SAXParseException sAXParseException) {
        }

        @Override // org.xml.sax.ErrorHandler
        public void error(SAXParseException sAXParseException) {
            if (this.vexception == null) {
                this.vexception = new ValidityException(sAXParseException.getMessage(), sAXParseException.getSystemId(), sAXParseException.getLineNumber(), sAXParseException.getColumnNumber(), sAXParseException);
            }
            this.vexception.addError(sAXParseException);
        }

        @Override // org.xml.sax.ErrorHandler
        public void fatalError(SAXParseException sAXParseException) throws SAXParseException {
            throw sAXParseException;
        }

        boolean isValid() {
            return this.vexception == null;
        }

        /* synthetic */ ValidityRequired(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public Builder() {
        this(false);
    }

    public Builder(boolean z) {
        this(findParser(z), z, null);
    }

    public Builder(boolean z, NodeFactory nodeFactory) {
        this(findParser(z), z, nodeFactory);
    }

    static XMLReader findParser(boolean z) {
        try {
            SAXParser xML1_0Parser = new XML1_0Parser();
            setupParser(xML1_0Parser, z);
            return xML1_0Parser;
        } catch (NoClassDefFoundError | SAXException e) {
            try {
                XMLReader xMLReader = (XMLReader) Class.forName("nu.xom.JDK15XML1_0Parser").newInstance();
                setupParser(xMLReader, z);
                return xMLReader;
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoClassDefFoundError | SAXException e2) {
                for (int i = 2; i < parsers.length; i++) {
                    try {
                        XMLReader xMLReaderCreateXMLReader = XMLReaderFactory.createXMLReader(parsers[i]);
                        setupParser(xMLReaderCreateXMLReader, z);
                        return xMLReaderCreateXMLReader;
                    } catch (NoClassDefFoundError | SAXException e3) {
                    }
                }
                try {
                    XMLReader xMLReaderCreateXMLReader2 = XMLReaderFactory.createXMLReader();
                    setupParser(xMLReaderCreateXMLReader2, z);
                    return xMLReaderCreateXMLReader2;
                } catch (SAXException e4) {
                    throw new XMLException("Could not find a suitable SAX2 parser", e4);
                }
            }
        }
    }

    private static void setupParser(XMLReader xMLReader, boolean z) throws SAXNotRecognizedException, SAXNotSupportedException {
        XMLReader xMLReader2;
        xMLReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        xMLReader.setFeature("http://xml.org/sax/features/namespaces", true);
        XMLReader xMLReader3 = xMLReader;
        while (true) {
            xMLReader2 = xMLReader3;
            if (!(xMLReader2 instanceof XMLFilter)) {
                break;
            }
            XMLReader parent = ((XMLFilter) xMLReader2).getParent();
            if (parent == null) {
                break;
            } else {
                xMLReader3 = parent;
            }
        }
        String name = xMLReader2.getClass().getName();
        if (z) {
            xMLReader.setFeature("http://xml.org/sax/features/validation", true);
            xMLReader.setErrorHandler(new ValidityRequired(null));
        } else if (name.equals("org.apache.crimson.parser.XMLReaderImpl")) {
            xMLReader.setErrorHandler(new NamespaceWellformednessRequired(null));
        } else {
            xMLReader.setFeature("http://xml.org/sax/features/external-general-entities", true);
            xMLReader.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
        }
        try {
            xMLReader.setFeature("http://xml.org/sax/features/string-interning", true);
        } catch (SAXException e) {
        }
        if (name.equals("nu.xom.XML1_0Parser") || name.equals("nu.xom.JDK15XML1_0Parser") || name.equals("org.apache.xerces.parsers.SAXParser") || name.equals("com.sun.org.apache.xerces.internal.parsers.SAXParser") || name.equals("org.apache.xerces.jaxp.SAXParserImpl$JAXPSAXParser") || name.equals("com.sun.org.apache.xerces.internal.jaxp.SAXParserImpl$JAXPSAXParser")) {
            try {
                xMLReader.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
            } catch (SAXException e2) {
            }
            try {
                xMLReader.setFeature("http://apache.org/xml/features/standard-uri-conformant", true);
            } catch (SAXException e3) {
            }
        }
    }

    public Builder(XMLReader xMLReader) {
        this(xMLReader, false);
    }

    public Builder(NodeFactory nodeFactory) {
        this(findParser(false), false, nodeFactory);
    }

    public Builder(XMLReader xMLReader, boolean z) {
        this(xMLReader, z, null);
    }

    public Builder(XMLReader xMLReader, boolean z, NodeFactory nodeFactory) {
        try {
            setupParser(xMLReader, z);
            this.parser = xMLReader;
            this.factory = nodeFactory;
            setHandlers();
        } catch (SAXException e) {
            if (!z) {
                throw new XMLException(new StringBuffer().append(xMLReader.getClass().getName()).append(" does not support the entity resolution").append(" features XOM requires.").toString(), e);
            }
            throw new XMLException(new StringBuffer().append(xMLReader.getClass().getName()).append(" does not support validation.").toString(), e);
        }
    }

    private static boolean knownGoodParser(XMLReader xMLReader) {
        String name = xMLReader.getClass().getName();
        if (name.equals("org.apache.xml.resolver.tools.ResolvingXMLFilter")) {
            name = ((XMLFilter) xMLReader).getParent().getClass().getName();
        }
        if (name.equals("gnu.xml.aelfred2.XmlReader") || name.equals("net.sf.saxon.aelfred.SAXDriver") || name.equals("com.icl.saxon.aelfred.SAXDriver")) {
            return false;
        }
        if (name.equals("org.apache.xerces.parsers.SAXParser") && xercesVersion >= 2.4d) {
            return false;
        }
        for (int i = 0; i < parsers.length; i++) {
            if (name.equals(parsers[i])) {
                return true;
            }
        }
        return false;
    }

    private void setHandlers() {
        XOMHandler nonVerifyingHandler;
        if ((this.factory == null || this.factory.getClass().getName().equals("nu.xom.NodeFactory")) && knownGoodParser(this.parser)) {
            NodeFactory nodeFactory = this.factory;
            if (nodeFactory == null) {
                nodeFactory = new NodeFactory();
            }
            nonVerifyingHandler = new NonVerifyingHandler(nodeFactory);
        } else {
            if (this.factory == null) {
                this.factory = new NodeFactory();
            }
            nonVerifyingHandler = new XOMHandler(this.factory);
        }
        this.parser.setContentHandler(nonVerifyingHandler);
        this.parser.setDTDHandler(nonVerifyingHandler);
        try {
            this.parser.setProperty("http://xml.org/sax/properties/lexical-handler", nonVerifyingHandler);
        } catch (SAXException e) {
        }
        try {
            this.parser.setProperty("http://xml.org/sax/properties/declaration-handler", nonVerifyingHandler);
            if (this.parser.getClass().getName().equals("org.apache.crimson.parser.XMLReaderImpl")) {
                nonVerifyingHandler.usingCrimson = true;
            }
        } catch (SAXException e2) {
        }
    }

    public Document build(String str) throws IOException, ParsingException {
        return build(new InputSource(canonicalizeURL(str)));
    }

    public Document build(InputStream inputStream) throws IOException, ParsingException {
        if (inputStream == null) {
            throw new NullPointerException("Null InputStream");
        }
        return build(new InputSource(inputStream));
    }

    public Document build(InputStream inputStream, String str) throws IOException, ParsingException {
        InputSource inputSource = new InputSource(inputStream);
        if (str != null) {
            inputSource.setSystemId(canonicalizeURL(str));
        }
        return build(inputSource);
    }

    public Document build(File file) throws IOException, ParsingException {
        FileInputStream fileInputStream = new FileInputStream(file);
        String absolutePath = file.getAbsolutePath();
        StringBuffer stringBuffer = new StringBuffer(fileURLPrefix);
        int length = absolutePath.length();
        char c = File.separatorChar;
        int i = 0;
        while (i < length) {
            char cCharAt = absolutePath.charAt(i);
            if (cCharAt != c) {
                switch (cCharAt) {
                    case ' ':
                        stringBuffer.append("%20");
                        break;
                    case '!':
                        stringBuffer.append(cCharAt);
                        break;
                    case '\"':
                        stringBuffer.append("%22");
                        break;
                    case '#':
                        stringBuffer.append("%23");
                        break;
                    case EACTags.APPLICATION_EXPIRATION_DATE /* 36 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case EACTags.APPLICATION_EFFECTIVE_DATE /* 37 */:
                        stringBuffer.append("%25");
                        break;
                    case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                        stringBuffer.append('&');
                        break;
                    case EACTags.INTERCHANGE_CONTROL /* 39 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case '(':
                        stringBuffer.append(cCharAt);
                        break;
                    case ')':
                        stringBuffer.append(cCharAt);
                        break;
                    case '*':
                        stringBuffer.append(cCharAt);
                        break;
                    case '+':
                        stringBuffer.append("%2B");
                        break;
                    case ',':
                        stringBuffer.append(cCharAt);
                        break;
                    case '-':
                        stringBuffer.append(cCharAt);
                        break;
                    case '.':
                        stringBuffer.append(cCharAt);
                        break;
                    case '/':
                        stringBuffer.append("%2F");
                        break;
                    case '0':
                        stringBuffer.append(cCharAt);
                        break;
                    case '1':
                        stringBuffer.append(cCharAt);
                        break;
                    case '2':
                        stringBuffer.append(cCharAt);
                        break;
                    case '3':
                        stringBuffer.append(cCharAt);
                        break;
                    case '4':
                        stringBuffer.append(cCharAt);
                        break;
                    case '5':
                        stringBuffer.append(cCharAt);
                        break;
                    case '6':
                        stringBuffer.append(cCharAt);
                        break;
                    case '7':
                        stringBuffer.append(cCharAt);
                        break;
                    case '8':
                        stringBuffer.append(cCharAt);
                        break;
                    case '9':
                        stringBuffer.append(cCharAt);
                        break;
                    case ':':
                        stringBuffer.append(cCharAt);
                        break;
                    case ';':
                        stringBuffer.append(cCharAt);
                        break;
                    case '<':
                        stringBuffer.append("%3C");
                        break;
                    case '=':
                        stringBuffer.append(cCharAt);
                        break;
                    case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
                        stringBuffer.append("%3E");
                        break;
                    case '?':
                        stringBuffer.append("%3F");
                        break;
                    case '@':
                        stringBuffer.append("%40");
                        break;
                    case 'A':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'B':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'C':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'D':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'E':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'F':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'G':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'H':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'I':
                        stringBuffer.append(cCharAt);
                        break;
                    case EACTags.CERTIFICATION_AUTHORITY_PUBLIC_KEY /* 74 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case EACTags.DEPRECATED /* 75 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case 'L':
                        stringBuffer.append(cCharAt);
                        break;
                    case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case 'N':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'O':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'P':
                        stringBuffer.append(cCharAt);
                        break;
                    case EACTags.ANSWER_TO_RESET /* 81 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case 'R':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'S':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'T':
                        stringBuffer.append(cCharAt);
                        break;
                    case Matrix.MATRIX_TYPE_RANDOM_UT /* 85 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case AlertDescription.inappropriate_fallback /* 86 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case 'W':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'X':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'Y':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'Z':
                        stringBuffer.append(cCharAt);
                        break;
                    case '[':
                        stringBuffer.append("%5B");
                        break;
                    case '\\':
                        stringBuffer.append("%5C");
                        break;
                    case ']':
                        stringBuffer.append("%5D");
                        break;
                    case '^':
                        stringBuffer.append("%5E");
                        break;
                    case '_':
                        stringBuffer.append(cCharAt);
                        break;
                    case '`':
                        stringBuffer.append("%60");
                        break;
                    case 'a':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'b':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'c':
                        stringBuffer.append(cCharAt);
                        break;
                    case AlertDescription.no_renegotiation /* 100 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case ExtensionType.negotiated_ff_dhe_groups /* 101 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case 'f':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'g':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'h':
                        stringBuffer.append(cCharAt);
                        break;
                    case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256 /* 105 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256 /* 106 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 /* 107 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256 /* 108 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 /* 109 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case AlertDescription.unsupported_extension /* 110 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case AlertDescription.certificate_unobtainable /* 111 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case AlertDescription.unrecognized_name /* 112 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case AlertDescription.bad_certificate_status_response /* 113 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case AlertDescription.bad_certificate_hash_value /* 114 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case AlertDescription.unknown_psk_identity /* 115 */:
                        stringBuffer.append(cCharAt);
                        break;
                    case 't':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'u':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'v':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'w':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'x':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'y':
                        stringBuffer.append(cCharAt);
                        break;
                    case 'z':
                        stringBuffer.append(cCharAt);
                        break;
                    case '{':
                        stringBuffer.append("%7B");
                        break;
                    case '|':
                        stringBuffer.append("%7C");
                        break;
                    case '}':
                        stringBuffer.append("%7D");
                        break;
                    case '~':
                        stringBuffer.append(cCharAt);
                        break;
                    default:
                        if (cCharAt < 55296 || cCharAt > 57343) {
                            stringBuffer.append(URIUtil.percentEscape(cCharAt));
                        } else if (cCharAt <= 56319) {
                            i++;
                            try {
                                byte[] bytes = new StringBuffer().append(String.valueOf(cCharAt)).append(absolutePath.charAt(i)).toString().getBytes("UTF8");
                                for (int i2 = 0; i2 < 4; i2++) {
                                    stringBuffer.append('%');
                                    String upperCase = Integer.toHexString(bytes[i2]).toUpperCase();
                                    stringBuffer.append(upperCase.substring(upperCase.length() - 2));
                                }
                            } catch (IndexOutOfBoundsException e) {
                                stringBuffer = new StringBuffer(0);
                            }
                        } else {
                            stringBuffer = new StringBuffer(0);
                        }
                        break;
                }
            } else {
                stringBuffer.append('/');
            }
            i++;
        }
        try {
            return build(fileInputStream, stringBuffer.toString());
        } finally {
            fileInputStream.close();
        }
    }

    public Document build(Reader reader) throws IOException, ParsingException {
        if (reader == null) {
            throw new NullPointerException("Attempted to build from null reader");
        }
        return build(new InputSource(reader));
    }

    public Document build(Reader reader, String str) throws IOException, ParsingException {
        InputSource inputSource = new InputSource(reader);
        if (str != null) {
            inputSource.setSystemId(canonicalizeURL(str));
        }
        return build(inputSource);
    }

    public Document build(String str, String str2) throws IOException, ParsingException {
        return build(new StringReader(str), str2);
    }

    private String canonicalizeURL(String str) {
        try {
            URL url = new URL(str);
            String path = url.getPath();
            String protocol = url.getProtocol();
            String host = url.getHost();
            String query = url.getQuery();
            int port = url.getPort();
            if (path == null || path.length() == 0) {
                path = "/";
            }
            String strRemoveDotSegments = URIUtil.removeDotSegments(path);
            StringBuffer stringBuffer = new StringBuffer(str.length());
            stringBuffer.append(protocol);
            stringBuffer.append("://");
            if (host != null) {
                stringBuffer.append(host);
            }
            if (port >= 0) {
                stringBuffer.append(new StringBuffer().append(":").append(port).toString());
            }
            stringBuffer.append(strRemoveDotSegments);
            if (query != null) {
                stringBuffer.append(new StringBuffer().append("?").append(query).toString());
            }
            return stringBuffer.toString();
        } catch (MalformedURLException e) {
            return str;
        }
    }

    private Document build(InputSource inputSource) throws IOException, ParsingException {
        XOMHandler xOMHandler = (XOMHandler) this.parser.getContentHandler();
        try {
            try {
                try {
                    try {
                        try {
                            try {
                                try {
                                    this.parser.parse(inputSource);
                                    Document document = xOMHandler.getDocument();
                                    xOMHandler.freeMemory();
                                    if (document == null) {
                                        throw new ParsingException("Parser did not build document", inputSource.getSystemId(), -1, -1);
                                    }
                                    if ("".equals(document.getBaseURI())) {
                                        document.setBaseURI(inputSource.getSystemId());
                                    }
                                    ErrorHandler errorHandler = this.parser.getErrorHandler();
                                    if (errorHandler instanceof ValidityRequired) {
                                        ValidityRequired validityRequired = (ValidityRequired) errorHandler;
                                        if (!validityRequired.isValid()) {
                                            ValidityException validityException = validityRequired.vexception;
                                            validityException.setDocument(document);
                                            validityRequired.reset();
                                            throw validityException;
                                        }
                                    }
                                    return document;
                                } catch (SAXException e) {
                                    throw new ParsingException(e.getMessage(), inputSource.getSystemId(), e);
                                }
                            } catch (CharConversionException e2) {
                                throw new ParsingException(e2.getMessage(), inputSource.getSystemId(), e2);
                            }
                        } catch (IOException e3) {
                            if (e3.getClass().getName().equals("org.apache.xerces.util.URI$MalformedURIException")) {
                                throw new ParsingException(e3.getMessage(), inputSource.getSystemId(), e3);
                            }
                            throw e3;
                        }
                    } catch (UTFDataFormatException e4) {
                        throw new ParsingException(e4.getMessage(), inputSource.getSystemId(), e4);
                    }
                } catch (SAXParseException e5) {
                    throw new ParsingException(e5.getMessage(), e5.getSystemId(), e5.getLineNumber(), e5.getColumnNumber(), e5);
                }
            } catch (XMLException e6) {
                throw new ParsingException(e6.getMessage(), e6);
            } catch (RuntimeException e7) {
                throw new ParsingException(e7.getMessage(), inputSource.getSystemId(), e7);
            }
        } catch (Throwable th) {
            xOMHandler.freeMemory();
            throw th;
        }
    }

    public NodeFactory getNodeFactory() {
        return this.factory;
    }

    static {
        xercesVersion = 2.6d;
        try {
            String version = Version.getVersion();
            String strSubstring = version.substring(9);
            int iIndexOf = strSubstring.indexOf(".");
            int iLastIndexOf = strSubstring.lastIndexOf(".");
            String strSubstring2 = strSubstring.substring(0, iIndexOf);
            if (Integer.parseInt(strSubstring.substring(iIndexOf + 1, iLastIndexOf)) < 10 && Integer.parseInt(strSubstring2) < 3) {
                xercesVersion = Double.parseDouble(version.substring(9, 12));
            }
        } catch (Error e) {
        } catch (Exception e2) {
        }
        parsers = new String[]{"nu.xom.XML1_0Parser", "nu.xom.JDK15XML1_0Parser", "org.apache.xerces.parsers.SAXParser", "org.apache.xerces.jaxp.SAXParserImpl$JAXPSAXParser", "com.sun.org.apache.xerces.internal.jaxp.SAXParserImpl$JAXPSAXParser", "com.sun.org.apache.xerces.internal.parsers.SAXParser", "gnu.xml.aelfred2.XmlReader", "org.apache.crimson.parser.XMLReaderImpl", "com.bluecast.xml.Piccolo", "oracle.xml.parser.v2.SAXParser", "com.jclark.xml.sax.SAX2Driver", "net.sf.saxon.aelfred.SAXDriver", "com.icl.saxon.aelfred.SAXDriver", "org.dom4j.io.aelfred2.SAXDriver", "org.dom4j.io.aelfred.SAXDriver", "org.xmlpull.v1.sax2.Driver"};
        fileURLPrefix = "file://";
        if (System.getProperty("os.name", "Unix").indexOf("Windows") >= 0) {
            fileURLPrefix = "file:/";
        }
    }
}
