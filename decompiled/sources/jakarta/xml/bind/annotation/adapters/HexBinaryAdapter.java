package jakarta.xml.bind.annotation.adapters;

import jakarta.xml.bind.DatatypeConverter;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/annotation/adapters/HexBinaryAdapter.class */
public final class HexBinaryAdapter extends XmlAdapter<String, byte[]> {
    @Override // jakarta.xml.bind.annotation.adapters.XmlAdapter
    public byte[] unmarshal(String s) {
        if (s == null) {
            return null;
        }
        return DatatypeConverter.parseHexBinary(s);
    }

    @Override // jakarta.xml.bind.annotation.adapters.XmlAdapter
    public String marshal(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return DatatypeConverter.printHexBinary(bytes);
    }
}
