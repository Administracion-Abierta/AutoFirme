package jakarta.xml.bind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/DatatypeConverterInterface.class */
public interface DatatypeConverterInterface {
    String parseString(String str);

    BigInteger parseInteger(String str);

    int parseInt(String str);

    long parseLong(String str);

    short parseShort(String str);

    BigDecimal parseDecimal(String str);

    float parseFloat(String str);

    double parseDouble(String str);

    boolean parseBoolean(String str);

    byte parseByte(String str);

    QName parseQName(String str, NamespaceContext namespaceContext);

    Calendar parseDateTime(String str);

    byte[] parseBase64Binary(String str);

    byte[] parseHexBinary(String str);

    long parseUnsignedInt(String str);

    int parseUnsignedShort(String str);

    Calendar parseTime(String str);

    Calendar parseDate(String str);

    String parseAnySimpleType(String str);

    String printString(String str);

    String printInteger(BigInteger bigInteger);

    String printInt(int i);

    String printLong(long j);

    String printShort(short s);

    String printDecimal(BigDecimal bigDecimal);

    String printFloat(float f);

    String printDouble(double d);

    String printBoolean(boolean z);

    String printByte(byte b);

    String printQName(QName qName, NamespaceContext namespaceContext);

    String printDateTime(Calendar calendar);

    String printBase64Binary(byte[] bArr);

    String printHexBinary(byte[] bArr);

    String printUnsignedInt(long j);

    String printUnsignedShort(int i);

    String printTime(Calendar calendar);

    String printDate(Calendar calendar);

    String printAnySimpleType(String str);
}
