package jakarta.xml.bind.annotation.adapters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/annotation/adapters/CollapsedStringAdapter.class */
public class CollapsedStringAdapter extends XmlAdapter<String, String> {
    @Override // jakarta.xml.bind.annotation.adapters.XmlAdapter
    public String unmarshal(String text) {
        if (text == null) {
            return null;
        }
        int len = text.length();
        int s = 0;
        while (s < len && !isWhiteSpace(text.charAt(s))) {
            s++;
        }
        if (s == len) {
            return text;
        }
        StringBuilder result = new StringBuilder(len);
        if (s != 0) {
            for (int i = 0; i < s; i++) {
                result.append(text.charAt(i));
            }
            result.append(' ');
        }
        boolean inStripMode = true;
        for (int i2 = s + 1; i2 < len; i2++) {
            char ch = text.charAt(i2);
            boolean b = isWhiteSpace(ch);
            if (!inStripMode || !b) {
                inStripMode = b;
                if (inStripMode) {
                    result.append(' ');
                } else {
                    result.append(ch);
                }
            }
        }
        int len2 = result.length();
        if (len2 > 0 && result.charAt(len2 - 1) == ' ') {
            result.setLength(len2 - 1);
        }
        return result.toString();
    }

    @Override // jakarta.xml.bind.annotation.adapters.XmlAdapter
    public String marshal(String s) {
        return s;
    }

    protected static boolean isWhiteSpace(char ch) {
        if (ch > ' ') {
            return false;
        }
        return ch == '\t' || ch == '\n' || ch == '\r' || ch == ' ';
    }
}
