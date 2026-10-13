package nu.xom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/CDATASection.class */
class CDATASection extends Text {
    CDATASection(Text text) {
        super(text);
    }

    CDATASection(String str) {
        super(str);
    }

    @Override // nu.xom.Text
    boolean isCDATASection() {
        return true;
    }

    static Text build(String str) {
        return new CDATASection(str);
    }

    @Override // nu.xom.Text
    String escapeText() {
        String value = getValue();
        return value.indexOf("]]>") != -1 ? super.escapeText() : new StringBuffer().append("<![CDATA[").append(value).append("]]>").toString();
    }
}
