package nu.xom;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/NodeFactory.class */
public class NodeFactory {
    public Element makeRootElement(String str, String str2) {
        return startMakingElement(str, str2);
    }

    public Element startMakingElement(String str, String str2) {
        return new Element(str, str2);
    }

    public Nodes finishMakingElement(Element element) {
        return new Nodes(element);
    }

    public Document startMakingDocument() {
        return new Document(Element.build("root", "http://www.xom.nu/fakeRoot", "root"));
    }

    public void finishMakingDocument(Document document) {
    }

    public Nodes makeAttribute(String str, String str2, String str3, Attribute.Type type) {
        return new Nodes(new Attribute(str, str2, str3, type));
    }

    public Nodes makeComment(String str) {
        return new Nodes(new Comment(str));
    }

    public Nodes makeDocType(String str, String str2, String str3) {
        return new Nodes(new DocType(str, str2, str3));
    }

    public Nodes makeText(String str) {
        return new Nodes(new Text(str));
    }

    Nodes makeCDATASection(String str) {
        return makeText(str);
    }

    public Nodes makeProcessingInstruction(String str, String str2) {
        return new Nodes(new ProcessingInstruction(str, str2));
    }

    void addAttribute(Element element, Attribute attribute) {
        element.addAttribute(attribute);
    }

    void insertChild(Element element, Node node, int i) {
        element.insertChild(node, i);
    }
}
