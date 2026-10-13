package com.aowagie.text.pdf;

import com.aowagie.text.xml.XMLConstants;
import com.aowagie.text.xml.XmlDomWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/XfaForm.class */
public class XfaForm {
    private Xml2SomTemplate templateSom;
    private Node templateNode;
    private Xml2SomDatasets datasetsSom;
    private Node datasetsNode;
    private AcroFieldsSearch acroFieldsSom;
    private PdfReader reader;
    private boolean xfaPresent;
    private Document domDocument;
    private boolean changed;
    public static final String XFA_DATA_SCHEMA = "http://www.xfa.org/schema/xfa-data/1.0/";
    private static DocumentBuilderFactory SECURE_FACTORY = null;
    private static final Logger LOGGER = Logger.getLogger(XfaForm.class.getName());

    public XfaForm() {
    }

    public static PdfObject getXfaObject(PdfReader reader) {
        PdfDictionary af = (PdfDictionary) PdfReader.getPdfObjectRelease(reader.getCatalog().get(PdfName.ACROFORM));
        if (af == null) {
            return null;
        }
        return PdfReader.getPdfObjectRelease(af.get(PdfName.XFA));
    }

    public XfaForm(PdfReader reader) throws ParserConfigurationException, SAXException, IOException {
        this.reader = reader;
        PdfObject xfa = getXfaObject(reader);
        if (xfa == null) {
            this.xfaPresent = false;
            return;
        }
        this.xfaPresent = true;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        if (xfa.isArray()) {
            PdfArray ar = (PdfArray) xfa;
            for (int k = 1; k < ar.size(); k += 2) {
                PdfObject ob = ar.getDirectObject(k);
                if (ob instanceof PRStream) {
                    byte[] b = PdfReader.getStreamBytes((PRStream) ob);
                    bout.write(b);
                }
            }
        } else if (xfa instanceof PRStream) {
            byte[] b2 = PdfReader.getStreamBytes((PRStream) xfa);
            bout.write(b2);
        }
        bout.close();
        DocumentBuilderFactory fact = getSecureDocumentFactory();
        fact.setNamespaceAware(true);
        DocumentBuilder db = fact.newDocumentBuilder();
        db.setEntityResolver(new SafeEmptyEntityResolver());
        this.domDocument = db.parse(new ByteArrayInputStream(bout.toByteArray()));
        extractNodes();
    }

    private void extractNodes() {
        Node n;
        Node firstChild = this.domDocument.getFirstChild();
        while (true) {
            n = firstChild;
            if (n.getChildNodes().getLength() != 0) {
                break;
            } else {
                firstChild = n.getNextSibling();
            }
        }
        Node firstChild2 = n.getFirstChild();
        while (true) {
            Node n2 = firstChild2;
            if (n2 != null) {
                if (n2.getNodeType() == 1) {
                    String s = n2.getLocalName();
                    if (s.equals("template")) {
                        this.templateNode = n2;
                        this.templateSom = new Xml2SomTemplate(n2);
                    } else if (s.equals("datasets")) {
                        this.datasetsNode = n2;
                        this.datasetsSom = new Xml2SomDatasets(n2.getFirstChild());
                    }
                }
                firstChild2 = n2.getNextSibling();
            } else {
                return;
            }
        }
    }

    public static void setXfa(XfaForm form, PdfReader reader, PdfWriter writer) throws IOException {
        PdfDictionary af = (PdfDictionary) PdfReader.getPdfObjectRelease(reader.getCatalog().get(PdfName.ACROFORM));
        if (af == null) {
            return;
        }
        PdfObject xfa = getXfaObject(reader);
        if (xfa.isArray()) {
            PdfArray ar = (PdfArray) xfa;
            int t = -1;
            int d = -1;
            for (int k = 0; k < ar.size(); k += 2) {
                PdfString s = ar.getAsString(k);
                if ("template".equals(s.toString())) {
                    t = k + 1;
                }
                if ("datasets".equals(s.toString())) {
                    d = k + 1;
                }
            }
            if (t > -1 && d > -1) {
                reader.killXref(ar.getAsIndirectObject(t));
                reader.killXref(ar.getAsIndirectObject(d));
                PdfStream tStream = new PdfStream(serializeDoc(form.templateNode));
                tStream.flateCompress(writer.getCompressionLevel());
                ar.set(t, writer.addToBody(tStream).getIndirectReference());
                PdfStream dStream = new PdfStream(serializeDoc(form.datasetsNode));
                dStream.flateCompress(writer.getCompressionLevel());
                ar.set(d, writer.addToBody(dStream).getIndirectReference());
                af.put(PdfName.XFA, new PdfArray(ar));
                return;
            }
        }
        reader.killXref(af.get(PdfName.XFA));
        PdfStream str = new PdfStream(serializeDoc(form.domDocument));
        str.flateCompress(writer.getCompressionLevel());
        PdfIndirectReference ref = writer.addToBody(str).getIndirectReference();
        af.put(PdfName.XFA, ref);
    }

    public void setXfa(PdfWriter writer) throws IOException {
        setXfa(this, this.reader, writer);
    }

    public static byte[] serializeDoc(Node n) throws IOException {
        XmlDomWriter xw = new XmlDomWriter();
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        xw.setOutput(fout, null);
        xw.setCanonical(false);
        xw.write(n);
        fout.close();
        return fout.toByteArray();
    }

    public boolean isXfaPresent() {
        return this.xfaPresent;
    }

    public Document getDomDocument() {
        return this.domDocument;
    }

    public String findFieldName(String name, AcroFields af) {
        HashMap<String, AcroFields.Item> fields = af.getFields();
        if (fields.containsKey(name)) {
            return name;
        }
        if (this.acroFieldsSom == null) {
            if (fields.isEmpty() && this.xfaPresent) {
                this.acroFieldsSom = new AcroFieldsSearch(this.datasetsSom.getName2Node().keySet());
            } else {
                this.acroFieldsSom = new AcroFieldsSearch(fields.keySet());
            }
        }
        if (this.acroFieldsSom.getAcroShort2LongName().containsKey(name)) {
            return (String) this.acroFieldsSom.getAcroShort2LongName().get(name);
        }
        return this.acroFieldsSom.inverseSearchGlobal(Xml2Som.splitParts(name));
    }

    public String findDatasetsName(String name) {
        if (this.datasetsSom.getName2Node().containsKey(name)) {
            return name;
        }
        return this.datasetsSom.inverseSearchGlobal(Xml2Som.splitParts(name));
    }

    public Node findDatasetsNode(String name) {
        String name2;
        if (name == null || (name2 = findDatasetsName(name)) == null) {
            return null;
        }
        return (Node) this.datasetsSom.getName2Node().get(name2);
    }

    public static String getNodeText(Node n) {
        if (n == null) {
            return "";
        }
        return getNodeText(n, "");
    }

    private static String getNodeText(Node n, String name) {
        Node firstChild = n.getFirstChild();
        while (true) {
            Node n2 = firstChild;
            if (n2 != null) {
                if (n2.getNodeType() == 1) {
                    name = getNodeText(n2, name);
                } else if (n2.getNodeType() == 3) {
                    name = name + n2.getNodeValue();
                }
                firstChild = n2.getNextSibling();
            } else {
                return name;
            }
        }
    }

    public void setNodeText(Node n, String text) {
        if (n == null) {
            return;
        }
        while (true) {
            Node nc = n.getFirstChild();
            if (nc == null) {
                break;
            } else {
                n.removeChild(nc);
            }
        }
        if (n.getAttributes().getNamedItemNS(XFA_DATA_SCHEMA, "dataNode") != null) {
            n.getAttributes().removeNamedItemNS(XFA_DATA_SCHEMA, "dataNode");
        }
        n.appendChild(this.domDocument.createTextNode(text));
        this.changed = true;
    }

    public void setXfaPresent(boolean xfaPresent) {
        this.xfaPresent = xfaPresent;
    }

    public void setDomDocument(Document domDocument) {
        this.domDocument = domDocument;
        extractNodes();
    }

    public PdfReader getReader() {
        return this.reader;
    }

    public void setReader(PdfReader reader) {
        this.reader = reader;
    }

    public boolean isChanged() {
        return this.changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    private static synchronized DocumentBuilderFactory getSecureDocumentFactory() {
        if (SECURE_FACTORY != null) {
            return SECURE_FACTORY;
        }
        SECURE_FACTORY = DocumentBuilderFactory.newInstance();
        try {
            SECURE_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo configurar el procesador seguro: " + e);
        }
        String[] securityProperties = {XMLConstants.ACCESS_EXTERNAL_DTD, XMLConstants.ACCESS_EXTERNAL_SCHEMA, XMLConstants.ACCESS_EXTERNAL_STYLESHEET};
        for (String securityProperty : securityProperties) {
            try {
                SECURE_FACTORY.setAttribute(securityProperty, "");
            } catch (Exception e2) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "No se ha podido establecer una propiedad de seguridad '" + securityProperty + "' en la factoria XML: " + e2);
                }
            }
        }
        SECURE_FACTORY.setValidating(false);
        return SECURE_FACTORY;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/XfaForm$InverseStore.class */
    public static class InverseStore {
        protected ArrayList part = new ArrayList();
        protected ArrayList follow = new ArrayList();

        public String getDefaultName() {
            InverseStore inverseStore = this;
            while (true) {
                InverseStore store = inverseStore;
                Object obj = store.follow.get(0);
                if (obj instanceof String) {
                    return (String) obj;
                }
                inverseStore = (InverseStore) obj;
            }
        }

        public boolean isSimilar(String name) {
            int idx = name.indexOf(91);
            String name2 = name.substring(0, idx + 1);
            for (int k = 0; k < this.part.size(); k++) {
                if (((String) this.part.get(k)).startsWith(name2)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/XfaForm$Stack2.class */
    public static class Stack2 extends ArrayList {
        private static final long serialVersionUID = -7451476576174095212L;

        public Object peek() {
            if (size() == 0) {
                throw new EmptyStackException();
            }
            return get(size() - 1);
        }

        public Object pop() {
            if (size() == 0) {
                throw new EmptyStackException();
            }
            Object ret = get(size() - 1);
            remove(size() - 1);
            return ret;
        }

        public Object push(Object item) {
            add(item);
            return item;
        }

        public boolean empty() {
            return size() == 0;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/XfaForm$Xml2Som.class */
    public static class Xml2Som {
        protected ArrayList order;
        protected HashMap name2Node;
        protected HashMap inverseSearch;
        protected Stack2 stack;
        protected int anform;

        public static String escapeSom(String s) {
            int idx = s.indexOf(46);
            if (idx < 0) {
                return s;
            }
            StringBuffer sb = new StringBuffer();
            int last = 0;
            while (idx >= 0) {
                sb.append(s.substring(last, idx));
                sb.append('\\');
                last = idx;
                idx = s.indexOf(46, idx + 1);
            }
            sb.append(s.substring(last));
            return sb.toString();
        }

        public static String unescapeSom(String s) {
            int idx = s.indexOf(92);
            if (idx < 0) {
                return s;
            }
            StringBuffer sb = new StringBuffer();
            int last = 0;
            while (idx >= 0) {
                sb.append(s.substring(last, idx));
                last = idx + 1;
                idx = s.indexOf(92, idx + 1);
            }
            sb.append(s.substring(last));
            return sb.toString();
        }

        protected String printStack() {
            if (this.stack.empty()) {
                return "";
            }
            StringBuffer s = new StringBuffer();
            for (int k = 0; k < this.stack.size(); k++) {
                s.append('.').append((String) this.stack.get(k));
            }
            return s.substring(1);
        }

        public static String getShortName(String s) {
            int idx = s.indexOf(".#subform[");
            if (idx < 0) {
                return s;
            }
            int last = 0;
            StringBuffer sb = new StringBuffer();
            while (idx >= 0) {
                sb.append(s.substring(last, idx));
                int idx2 = s.indexOf("]", idx + 10);
                if (idx2 < 0) {
                    return sb.toString();
                }
                last = idx2 + 1;
                idx = s.indexOf(".#subform[", last);
            }
            sb.append(s.substring(last));
            return sb.toString();
        }

        public void inverseSearchAdd(String unstack) {
            inverseSearchAdd(this.inverseSearch, this.stack, unstack);
        }

        public static void inverseSearchAdd(HashMap inverseSearch, Stack2 stack, String unstack) {
            InverseStore store2;
            String last = (String) stack.peek();
            InverseStore store = (InverseStore) inverseSearch.get(last);
            if (store == null) {
                store = new InverseStore();
                inverseSearch.put(last, store);
            }
            for (int k = stack.size() - 2; k >= 0; k--) {
                String last2 = (String) stack.get(k);
                int idx = store.part.indexOf(last2);
                if (idx < 0) {
                    store.part.add(last2);
                    store2 = new InverseStore();
                    store.follow.add(store2);
                } else {
                    store2 = (InverseStore) store.follow.get(idx);
                }
                store = store2;
            }
            store.part.add("");
            store.follow.add(unstack);
        }

        public String inverseSearchGlobal(ArrayList parts) {
            if (parts.isEmpty()) {
                return null;
            }
            InverseStore store = (InverseStore) this.inverseSearch.get(parts.get(parts.size() - 1));
            if (store == null) {
                return null;
            }
            for (int k = parts.size() - 2; k >= 0; k--) {
                String part = (String) parts.get(k);
                int idx = store.part.indexOf(part);
                if (idx < 0) {
                    if (store.isSimilar(part)) {
                        return null;
                    }
                    return store.getDefaultName();
                }
                store = (InverseStore) store.follow.get(idx);
            }
            return store.getDefaultName();
        }

        public static Stack2 splitParts(String name) {
            int pos;
            while (name.startsWith(".")) {
                name = name.substring(1);
            }
            Stack2 parts = new Stack2();
            int last = 0;
            while (true) {
                int pos2 = last;
                while (true) {
                    pos = name.indexOf(46, pos2);
                    if (pos < 0 || name.charAt(pos - 1) != '\\') {
                        break;
                    }
                    pos2 = pos + 1;
                }
                if (pos < 0) {
                    break;
                }
                String part = name.substring(last, pos);
                if (!part.endsWith("]")) {
                    part = part + "[0]";
                }
                parts.add(part);
                last = pos + 1;
            }
            String part2 = name.substring(last);
            if (!part2.endsWith("]")) {
                part2 = part2 + "[0]";
            }
            parts.add(part2);
            return parts;
        }

        public ArrayList getOrder() {
            return this.order;
        }

        public void setOrder(ArrayList order) {
            this.order = order;
        }

        public HashMap getName2Node() {
            return this.name2Node;
        }

        public void setName2Node(HashMap name2Node) {
            this.name2Node = name2Node;
        }

        public HashMap getInverseSearch() {
            return this.inverseSearch;
        }

        public void setInverseSearch(HashMap inverseSearch) {
            this.inverseSearch = inverseSearch;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/XfaForm$Xml2SomDatasets.class */
    public static class Xml2SomDatasets extends Xml2Som {
        public Xml2SomDatasets(Node n) {
            this.order = new ArrayList();
            this.name2Node = new HashMap();
            this.stack = new Stack2();
            this.anform = 0;
            this.inverseSearch = new HashMap();
            processDatasetsInternal(n);
        }

        public Node insertNode(Node n, String shortName) {
            Stack2 stack = splitParts(shortName);
            Document doc = n.getOwnerDocument();
            Node n2 = null;
            Node n3 = n.getFirstChild();
            for (int k = 0; k < stack.size(); k++) {
                String part = (String) stack.get(k);
                int idx = part.lastIndexOf(91);
                String name = part.substring(0, idx);
                int idx2 = Integer.parseInt(part.substring(idx + 1, part.length() - 1));
                int found = -1;
                Node firstChild = n3.getFirstChild();
                while (true) {
                    n2 = firstChild;
                    if (n2 == null) {
                        break;
                    }
                    if (n2.getNodeType() == 1) {
                        String s = escapeSom(n2.getLocalName());
                        if (s.equals(name)) {
                            found++;
                            if (found == idx2) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    }
                    firstChild = n2.getNextSibling();
                }
                while (found < idx2) {
                    Node n4 = doc.createElementNS(null, name);
                    n2 = n3.appendChild(n4);
                    Node attr = doc.createAttributeNS(XfaForm.XFA_DATA_SCHEMA, "dataNode");
                    attr.setNodeValue("dataGroup");
                    n2.getAttributes().setNamedItemNS(attr);
                    found++;
                }
                n3 = n2;
            }
            inverseSearchAdd(this.inverseSearch, stack, shortName);
            this.name2Node.put(shortName, n2);
            this.order.add(shortName);
            return n2;
        }

        private static boolean hasChildren(Node n) {
            Node dataNodeN = n.getAttributes().getNamedItemNS(XfaForm.XFA_DATA_SCHEMA, "dataNode");
            if (dataNodeN != null) {
                String dataNode = dataNodeN.getNodeValue();
                if ("dataGroup".equals(dataNode)) {
                    return true;
                }
                if ("dataValue".equals(dataNode)) {
                    return false;
                }
            }
            if (!n.hasChildNodes()) {
                return false;
            }
            Node firstChild = n.getFirstChild();
            while (true) {
                Node n2 = firstChild;
                if (n2 != null) {
                    if (n2.getNodeType() == 1) {
                        return true;
                    }
                    firstChild = n2.getNextSibling();
                } else {
                    return false;
                }
            }
        }

        private void processDatasetsInternal(Node n) {
            Integer i;
            HashMap ss = new HashMap();
            Node firstChild = n.getFirstChild();
            while (true) {
                Node n2 = firstChild;
                if (n2 != null) {
                    if (n2.getNodeType() == 1) {
                        String s = escapeSom(n2.getLocalName());
                        Integer i2 = (Integer) ss.get(s);
                        if (i2 == null) {
                            i = new Integer(0);
                        } else {
                            i = new Integer(i2.intValue() + 1);
                        }
                        ss.put(s, i);
                        if (hasChildren(n2)) {
                            this.stack.push(s + "[" + i.toString() + "]");
                            processDatasetsInternal(n2);
                            this.stack.pop();
                        } else {
                            this.stack.push(s + "[" + i.toString() + "]");
                            String unstack = printStack();
                            this.order.add(unstack);
                            inverseSearchAdd(unstack);
                            this.name2Node.put(unstack, n2);
                            this.stack.pop();
                        }
                    }
                    firstChild = n2.getNextSibling();
                } else {
                    return;
                }
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/XfaForm$AcroFieldsSearch.class */
    public static class AcroFieldsSearch extends Xml2Som {
        private HashMap acroShort2LongName;

        public AcroFieldsSearch(Collection items) {
            this.inverseSearch = new HashMap();
            this.acroShort2LongName = new HashMap();
            Iterator it = items.iterator();
            while (it.hasNext()) {
                String itemName = (String) it.next();
                String itemShort = getShortName(itemName);
                this.acroShort2LongName.put(itemShort, itemName);
                inverseSearchAdd(this.inverseSearch, splitParts(itemShort), itemName);
            }
        }

        public HashMap getAcroShort2LongName() {
            return this.acroShort2LongName;
        }

        public void setAcroShort2LongName(HashMap acroShort2LongName) {
            this.acroShort2LongName = acroShort2LongName;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/XfaForm$Xml2SomTemplate.class */
    public static class Xml2SomTemplate extends Xml2Som {
        private boolean dynamicForm;
        private int templateLevel;

        public Xml2SomTemplate(Node n) {
            this.order = new ArrayList();
            this.name2Node = new HashMap();
            this.stack = new Stack2();
            this.anform = 0;
            this.templateLevel = 0;
            this.inverseSearch = new HashMap();
            processTemplate(n, null);
        }

        public String getFieldType(String s) {
            Node ui;
            Node n = (Node) this.name2Node.get(s);
            if (n == null) {
                return null;
            }
            if (n.getLocalName().equals("exclGroup")) {
                return "exclGroup";
            }
            Node firstChild = n.getFirstChild();
            while (true) {
                ui = firstChild;
                if (ui == null || (ui.getNodeType() == 1 && ui.getLocalName().equals("ui"))) {
                    break;
                }
                firstChild = ui.getNextSibling();
            }
            if (ui == null) {
                return null;
            }
            Node firstChild2 = ui.getFirstChild();
            while (true) {
                Node type = firstChild2;
                if (type != null) {
                    if (type.getNodeType() == 1 && (!type.getLocalName().equals("extras") || !type.getLocalName().equals("picture"))) {
                        return type.getLocalName();
                    }
                    firstChild2 = type.getNextSibling();
                } else {
                    return null;
                }
            }
        }

        private void processTemplate(Node n, HashMap ff) {
            Integer i;
            Integer i2;
            if (ff == null) {
                ff = new HashMap();
            }
            HashMap ss = new HashMap();
            Node firstChild = n.getFirstChild();
            while (true) {
                Node n2 = firstChild;
                if (n2 != null) {
                    if (n2.getNodeType() == 1) {
                        String s = n2.getLocalName();
                        if (s.equals("subform")) {
                            Node name = n2.getAttributes().getNamedItem("name");
                            String nn = "#subform";
                            boolean annon = true;
                            if (name != null) {
                                nn = escapeSom(name.getNodeValue());
                                annon = false;
                            }
                            if (annon) {
                                i2 = new Integer(this.anform);
                                this.anform++;
                            } else {
                                Integer i3 = (Integer) ss.get(nn);
                                if (i3 == null) {
                                    i2 = new Integer(0);
                                } else {
                                    i2 = new Integer(i3.intValue() + 1);
                                }
                                ss.put(nn, i2);
                            }
                            this.stack.push(nn + "[" + i2.toString() + "]");
                            this.templateLevel++;
                            if (annon) {
                                processTemplate(n2, ff);
                            } else {
                                processTemplate(n2, null);
                            }
                            this.templateLevel--;
                            this.stack.pop();
                        } else if (s.equals("field") || s.equals("exclGroup")) {
                            Node name2 = n2.getAttributes().getNamedItem("name");
                            if (name2 != null) {
                                String nn2 = escapeSom(name2.getNodeValue());
                                Integer i4 = (Integer) ff.get(nn2);
                                if (i4 == null) {
                                    i = new Integer(0);
                                } else {
                                    i = new Integer(i4.intValue() + 1);
                                }
                                ff.put(nn2, i);
                                this.stack.push(nn2 + "[" + i.toString() + "]");
                                String unstack = printStack();
                                this.order.add(unstack);
                                inverseSearchAdd(unstack);
                                this.name2Node.put(unstack, n2);
                                this.stack.pop();
                            }
                        } else if (!this.dynamicForm && this.templateLevel > 0 && s.equals("occur")) {
                            int initial = 1;
                            int min = 1;
                            int max = 1;
                            Node a = n2.getAttributes().getNamedItem("initial");
                            if (a != null) {
                                try {
                                    initial = Integer.parseInt(a.getNodeValue().trim());
                                } catch (Exception e) {
                                }
                            }
                            Node a2 = n2.getAttributes().getNamedItem("min");
                            if (a2 != null) {
                                try {
                                    min = Integer.parseInt(a2.getNodeValue().trim());
                                } catch (Exception e2) {
                                }
                            }
                            Node a3 = n2.getAttributes().getNamedItem("max");
                            if (a3 != null) {
                                try {
                                    max = Integer.parseInt(a3.getNodeValue().trim());
                                } catch (Exception e3) {
                                }
                            }
                            if (initial != min || min != max) {
                                this.dynamicForm = true;
                            }
                        }
                    }
                    firstChild = n2.getNextSibling();
                } else {
                    return;
                }
            }
        }

        public boolean isDynamicForm() {
            return this.dynamicForm;
        }

        public void setDynamicForm(boolean dynamicForm) {
            this.dynamicForm = dynamicForm;
        }
    }

    public Xml2SomTemplate getTemplateSom() {
        return this.templateSom;
    }

    public void setTemplateSom(Xml2SomTemplate templateSom) {
        this.templateSom = templateSom;
    }

    public Xml2SomDatasets getDatasetsSom() {
        return this.datasetsSom;
    }

    public void setDatasetsSom(Xml2SomDatasets datasetsSom) {
        this.datasetsSom = datasetsSom;
    }

    public AcroFieldsSearch getAcroFieldsSom() {
        return this.acroFieldsSom;
    }

    public void setAcroFieldsSom(AcroFieldsSearch acroFieldsSom) {
        this.acroFieldsSom = acroFieldsSom;
    }

    public Node getDatasetsNode() {
        return this.datasetsNode;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/XfaForm$SafeEmptyEntityResolver.class */
    private static class SafeEmptyEntityResolver implements EntityResolver {
        private SafeEmptyEntityResolver() {
        }

        @Override // org.xml.sax.EntityResolver
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return new InputSource(new StringReader(""));
        }
    }
}
