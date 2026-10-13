package jakarta.xml.bind.annotation.adapters;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/adapters/NormalizedStringAdapter.class */
public final class NormalizedStringAdapter extends XmlAdapter<String, String> {
    @Override // jakarta.xml.bind.annotation.adapters.XmlAdapter
    public String unmarshal(String text) {
        if (text == null) {
            return null;
        }
        int i = text.length() - 1;
        while (i >= 0 && !isWhiteSpaceExceptSpace(text.charAt(i))) {
            i--;
        }
        if (i < 0) {
            return text;
        }
        char[] buf = text.toCharArray();
        int i2 = i;
        buf[i2] = ' ';
        for (int i3 = i - 1; i3 >= 0; i3--) {
            if (isWhiteSpaceExceptSpace(buf[i3])) {
                buf[i3] = ' ';
            }
        }
        return new String(buf);
    }

    @Override // jakarta.xml.bind.annotation.adapters.XmlAdapter
    public String marshal(String s) {
        return s;
    }

    protected static boolean isWhiteSpaceExceptSpace(char ch) {
        if (ch >= ' ') {
            return false;
        }
        return ch == '\t' || ch == '\n' || ch == '\r';
    }
}
