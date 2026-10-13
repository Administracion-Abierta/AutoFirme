package net.sf.jmimemagic;

import com.aowagie.text.ElementTags;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParserFactory;
import org.mozilla.javascript.ES6Iterator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: loaded from: autofirma.jar:net/sf/jmimemagic/MagicParser.class */
public final class MagicParser extends DefaultHandler {
    private static final String MAGIC_FILE = "/magic.xml";
    private boolean initialized = false;
    private XMLReader parser = null;
    private final ArrayList<MagicMatcher> stack = new ArrayList<>();
    private final Collection<MagicMatcher> matchers = new ArrayList();
    private MagicMatcher matcher = null;
    private MagicMatch match = null;
    private HashMap<String, String> properties = null;
    private String finalValue = "";
    private boolean isMimeType = false;
    private boolean isExtension = false;
    private boolean isDescription = false;
    private boolean isTest = false;

    public synchronized void initialize() throws MagicParseException {
        if (!this.initialized) {
            try {
                this.parser = (XMLReader) MagicMatcher.classForName("com.sun.org.apache.xerces.internal.parsers.SAXParser").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Exception e) {
                try {
                    this.parser = (XMLReader) MagicMatcher.classForName("org.apache.xerces.parsers.SAXParser").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                } catch (Exception e2) {
                    Logger.getLogger("es.gob.afirma").info("No se ha podido obtener el analizador SAX de Apache Xerces, se usara el por defecto: " + e2);
                    try {
                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        this.parser = factory.newSAXParser().getXMLReader();
                    } catch (Exception e3) {
                        throw new MagicParseException("unable to instantiate parser", e3);
                    }
                }
            }
            this.parser.setErrorHandler(this);
            this.parser.setContentHandler(this);
            URL magicUrl = MagicParser.class.getResource(MAGIC_FILE);
            if (magicUrl == null) {
                throw new MagicParseException("No se ha podido cargar '/magic.xml'");
            }
            try {
                String magic = magicUrl.toString();
                this.parser.parse(magic);
            } catch (SAXParseException e4) {
            } catch (Exception e5) {
                throw new MagicParseException("Parse error occurred: " + e5);
            }
            this.initialized = true;
        }
    }

    public Collection<MagicMatcher> getMatchers() {
        return this.matchers;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startDocument() {
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endDocument() {
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void processingInstruction(String target, String data) {
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void characters(char[] ch, int offset, int length) {
        String value = new String(ch, offset, length);
        this.finalValue += value;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] ch, int offset, int length) {
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String uri, String localName, String qname, Attributes attributes) {
        if (localName.equals("match")) {
            this.match = new MagicMatch();
            this.matcher = new MagicMatcher();
            this.matcher.setMatch(this.match);
        }
        if (this.matcher != null) {
            if (localName.equals("mimetype")) {
                this.isMimeType = true;
                return;
            }
            if (localName.equals("extension")) {
                this.isExtension = true;
                return;
            }
            if (localName.equals("description")) {
                this.isDescription = true;
                return;
            }
            if (localName.equals("test")) {
                this.isTest = true;
                int length = attributes.getLength();
                for (int i = 0; i < length; i++) {
                    String attrLocalName = attributes.getLocalName(i);
                    String attrValue = attributes.getValue(i);
                    if (attrLocalName.equals(ElementTags.OFFSET)) {
                        if (!attrValue.equals("")) {
                            this.match.setOffset(Integer.parseInt(attrValue));
                        }
                    } else if (attrLocalName.equals(XSDatatype.FACET_LENGTH)) {
                        if (!attrValue.equals("")) {
                            this.match.setLength(Integer.parseInt(attrValue));
                        }
                    } else if (attrLocalName.equals("type")) {
                        this.match.setType(attrValue);
                    } else if (attrLocalName.equals("bitmask")) {
                        if (!attrValue.equals("")) {
                            this.match.setBitmask(attrValue);
                        }
                    } else if (attrLocalName.equals("comparator")) {
                        this.match.setComparator(attrValue);
                    }
                }
                return;
            }
            if (localName.equals("property")) {
                int length2 = attributes.getLength();
                String name = null;
                String value = null;
                for (int i2 = 0; i2 < length2; i2++) {
                    String attrLocalName2 = attributes.getLocalName(i2);
                    String attrValue2 = attributes.getValue(i2);
                    if (attrLocalName2.equals("name")) {
                        if (!attrValue2.equals("")) {
                            name = attrValue2;
                        }
                    } else if (attrLocalName2.equals(ES6Iterator.VALUE_PROPERTY) && !attrValue2.equals("")) {
                        value = attrValue2;
                    }
                }
                if (name != null && value != null) {
                    if (this.properties == null) {
                        this.properties = new HashMap<>();
                    }
                    if (!this.properties.containsKey(name)) {
                        this.properties.put(name, value);
                        return;
                    }
                    return;
                }
                return;
            }
            if (localName.equals("match-list")) {
                this.stack.add(this.matcher);
            }
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String uri, String localName, String qname) {
        if (this.isMimeType) {
            this.isMimeType = false;
            this.match.setMimeType(this.finalValue);
        } else if (this.isExtension) {
            this.isExtension = false;
            this.match.setExtension(this.finalValue);
        } else if (this.isDescription) {
            this.isDescription = false;
            this.match.setDescription(this.finalValue);
        } else if (this.isTest) {
            this.isTest = false;
            this.match.setTest(convertOctals(this.finalValue));
        }
        this.finalValue = "";
        if (localName.equals("match")) {
            if (this.matcher.isValid()) {
                this.match.setProperties(this.properties);
                if (this.stack.size() == 0) {
                    this.matchers.add(this.matcher);
                } else {
                    MagicMatcher m = this.stack.get(this.stack.size() - 1);
                    m.addSubMatcher(this.matcher);
                }
            }
            this.matcher = null;
            this.properties = null;
            return;
        }
        if (localName.equals("match-list")) {
            if (this.stack.size() > 0) {
                this.matcher = this.stack.get(this.stack.size() - 1);
                this.stack.remove(this.matcher);
                return;
            }
            return;
        }
        if (localName.equals("mimetype")) {
            this.isMimeType = false;
            return;
        }
        if (localName.equals("extension")) {
            this.isExtension = false;
        } else if (localName.equals("description")) {
            this.isDescription = false;
        } else if (localName.equals("test")) {
            this.isTest = false;
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void warning(SAXParseException ex) {
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void error(SAXParseException ex) throws SAXException {
        throw ex;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException ex) throws SAXException {
        throw ex;
    }

    private static ByteBuffer convertOctals(String s) {
        int end;
        int beg = 0;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        while (true) {
            int iIndexOf = s.indexOf(92, beg);
            end = iIndexOf;
            if (iIndexOf == -1) {
                break;
            }
            if (s.charAt(end + 1) != '\\') {
                for (int z = beg; z < end; z++) {
                    buf.write(s.charAt(z));
                }
                if (end + 4 <= s.length()) {
                    try {
                        int chr = Integer.parseInt(s.substring(end + 1, end + 4), 8);
                        buf.write(chr);
                        beg = end + 4;
                        end = beg;
                    } catch (NumberFormatException e) {
                        buf.write(92);
                        beg = end + 1;
                    }
                } else {
                    buf.write(92);
                    beg = end + 1;
                }
            } else {
                buf.write(92);
                beg = end + 1;
            }
        }
        if (end < s.length()) {
            for (int z2 = beg; z2 < s.length(); z2++) {
                buf.write(s.charAt(z2));
            }
        }
        try {
            ByteBuffer b = ByteBuffer.allocate(buf.size());
            return b.put(buf.toByteArray());
        } catch (Exception e2) {
            return ByteBuffer.allocate(0);
        }
    }
}
