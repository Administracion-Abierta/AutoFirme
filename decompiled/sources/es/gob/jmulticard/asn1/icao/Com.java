package es.gob.jmulticard.asn1.icao;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.TlvException;
import es.gob.jmulticard.asn1.bertlv.BerTlv;
import java.io.ByteArrayInputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icao/Com.class */
public final class Com extends DecoderObject {
    private static final byte TAG_COM = 96;
    private static final char DOT = '.';
    private static final AbstractMap<Byte, String> DGTAGS = new HashMap(16);
    private String ldsVersion = null;
    private String unicodeVersion = null;
    private final List<String> presentDgs = new ArrayList();

    static {
        DGTAGS.put((byte) 97, "DG1");
        DGTAGS.put((byte) 117, "DG2");
        DGTAGS.put((byte) 99, "DG3");
        DGTAGS.put((byte) 118, "DG4");
        DGTAGS.put((byte) 101, "DG5");
        DGTAGS.put((byte) 102, "DG6");
        DGTAGS.put((byte) 103, "DG7");
        DGTAGS.put((byte) 104, "DG8");
        DGTAGS.put((byte) 105, "DG9");
        DGTAGS.put((byte) 106, "DG10");
        DGTAGS.put((byte) 107, "DG11");
        DGTAGS.put((byte) 108, "DG12");
        DGTAGS.put((byte) 109, "DG13");
        DGTAGS.put((byte) 110, "DG14");
        DGTAGS.put((byte) 111, "DG15");
        DGTAGS.put((byte) 112, "DG16");
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException, Asn1Exception {
        BerTlv tlv = BerTlv.createInstance(getBytes());
        checkTag(tlv.getTag());
        ByteArrayInputStream tlvs = new ByteArrayInputStream(tlv.getValue());
        BerTlv tlv2 = BerTlv.createInstance(tlvs);
        if (tlv2.getLength() != 4) {
            throw new Asn1Exception("El valor del TLV de version LDS debe tener exactamente cuarto octetos, pero se han encontrado " + tlv2.getLength());
        }
        if (tlv2.getTag() != 1) {
            throw new Asn1Exception("El valor del TLV de version LDS debe tener etiqueta '01', pero se ha encontrado '" + HexUtils.hexify(new byte[]{tlv2.getTag()}, false) + OperatorName.SHOW_TEXT_LINE);
        }
        this.ldsVersion = new String(new byte[]{tlv2.getValue()[0], tlv2.getValue()[1]}) + '.' + new String(new byte[]{tlv2.getValue()[2], tlv2.getValue()[3]});
        BerTlv tlv3 = BerTlv.createInstance(tlvs);
        if (tlv3.getLength() != 6) {
            throw new Asn1Exception("El valor del TLV de version Unicode debe tener exactamente seis octetos, pero se han encontrado " + tlv3.getLength());
        }
        if (tlv3.getTag() != 54) {
            throw new Asn1Exception("El valor del TLV de version Unicode debe tener etiqueta '36', pero se ha encontrado '" + HexUtils.hexify(new byte[]{tlv3.getTag()}, false) + OperatorName.SHOW_TEXT_LINE);
        }
        this.unicodeVersion = new String(new byte[]{tlv3.getValue()[0], tlv3.getValue()[1]}) + '.' + new String(new byte[]{tlv3.getValue()[2], tlv3.getValue()[3]}) + '.' + new String(new byte[]{tlv3.getValue()[4], tlv3.getValue()[5]});
        BerTlv tlv4 = BerTlv.createInstance(tlvs);
        if (tlv4.getTag() != 92) {
            throw new Asn1Exception("El valor del TLV de lista de rotulos debe tener etiqueta '5C', pero se han encontrado '" + HexUtils.hexify(new byte[]{tlv4.getTag()}, false) + OperatorName.SHOW_TEXT_LINE);
        }
        byte[] dgList = tlv4.getValue();
        for (byte dgTag : dgList) {
            this.presentDgs.add(DGTAGS.get(Byte.valueOf(dgTag)));
        }
    }

    public String toString() {
        return "Common Data (COM): Version de LDS = " + this.ldsVersion + ", version de Unicode = " + this.unicodeVersion + ", Grupos de datos presentes = " + this.presentDgs;
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 96;
    }

    public String getLdsVersion() {
        return this.ldsVersion;
    }

    public String getUnicodeVersion() {
        return this.unicodeVersion;
    }

    public String[] getPresentDgs() {
        return (String[]) this.presentDgs.toArray(new String[0]);
    }

    public boolean isDg1Present() {
        return this.presentDgs.contains("DG1");
    }

    public boolean isDg2Present() {
        return this.presentDgs.contains("DG2");
    }

    public boolean isDg3Present() {
        return this.presentDgs.contains("DG3");
    }

    public boolean isDg4Present() {
        return this.presentDgs.contains("DG4");
    }

    public boolean isDg5Present() {
        return this.presentDgs.contains("DG5");
    }

    public boolean isDg6Present() {
        return this.presentDgs.contains("DG6");
    }

    public boolean isDg7Present() {
        return this.presentDgs.contains("DG7");
    }

    public boolean isDg8Present() {
        return this.presentDgs.contains("DG8");
    }

    public boolean isDg9Present() {
        return this.presentDgs.contains("DG9");
    }

    public boolean isDg10Present() {
        return this.presentDgs.contains("DG10");
    }

    public boolean isDg11Present() {
        return this.presentDgs.contains("DG11");
    }

    public boolean isDg12Present() {
        return this.presentDgs.contains("DG12");
    }

    public boolean isDg13Present() {
        return this.presentDgs.contains("DG13");
    }

    public boolean isDg14Present() {
        return this.presentDgs.contains("DG14");
    }
}
