package es.gob.jmulticard.card.icao.vdsned;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;
import es.gob.jmulticard.card.icao.CountryCodes;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/vdsned/Vdsned.class */
public final class Vdsned {
    private static final byte MAGIC = -36;
    private final byte[] encoded;
    private final int version;
    private final String issuingCountry;
    private final String caCr;
    private final Date documentIssueDate;
    private final Date signatureCreationDate;
    private final int documentFeatureDefinitionReference;
    private final int documentTypeCategory;
    private String mrzB;
    private int nEntries;
    private int durationOfStay;
    private String passportNumber;
    private byte[] signature;
    private byte[] dataTbs;
    private static final String DEFAULT_SIGNATURE_ALGORITHM = "SHA256withECDSA";

    public Vdsned(byte[] enc) throws TlvException, IOException {
        this.mrzB = null;
        this.nEntries = 0;
        this.durationOfStay = 0;
        this.passportNumber = null;
        this.signature = null;
        this.dataTbs = null;
        if (enc == null || enc.length < 1) {
            throw new IllegalArgumentException("La codificacion binaria del VDSNED no puede ser nula ni vacia");
        }
        this.encoded = (byte[]) enc.clone();
        int offset = 0 + 1;
        if (this.encoded[0] != MAGIC) {
            throw new IllegalArgumentException("La codificacion binaria proporcionada no corresponde con un VDSNED");
        }
        int offset2 = offset + 1;
        this.version = this.encoded[offset] + 1;
        if (this.version != 3 && this.version != 4) {
            throw new IllegalArgumentException("Solo se soportan VDSNED v3 o v4, y se ha proporcionado un v" + this.version);
        }
        int offset3 = offset2 + 1;
        int offset4 = offset3 + 1;
        this.issuingCountry = C40Decoder.decode(new byte[]{this.encoded[offset2], this.encoded[offset3]});
        int offset5 = offset4 + 1;
        int offset6 = offset5 + 1;
        int offset7 = offset6 + 1;
        int offset8 = offset7 + 1;
        int offset9 = offset8 + 1;
        int offset10 = offset9 + 1;
        this.caCr = C40Decoder.decode(new byte[]{this.encoded[offset4], this.encoded[offset5], this.encoded[offset6], this.encoded[offset7], this.encoded[offset8], this.encoded[offset9]});
        int offset11 = offset10 + 1;
        int offset12 = offset11 + 1;
        int offset13 = offset12 + 1;
        byte[] tmpDateBytes = {0, this.encoded[offset10], this.encoded[offset11], this.encoded[offset12]};
        String tmpDate = Integer.toString(ByteBuffer.wrap(tmpDateBytes).getInt());
        try {
            this.documentIssueDate = new SimpleDateFormat(tmpDate.length() == 7 ? "Mddyyyy" : "MMddyyyy").parse(tmpDate);
            int offset14 = offset13 + 1;
            int offset15 = offset14 + 1;
            int offset16 = offset15 + 1;
            byte[] tmpDateBytes2 = {0, this.encoded[offset13], this.encoded[offset14], this.encoded[offset15]};
            String tmpDate2 = Integer.toString(ByteBuffer.wrap(tmpDateBytes2).getInt());
            try {
                this.signatureCreationDate = new SimpleDateFormat(tmpDate2.length() == 7 ? "Mddyyyy" : "MMddyyyy").parse(tmpDate2);
                int offset17 = offset16 + 1;
                this.documentFeatureDefinitionReference = this.encoded[offset16];
                int offset18 = offset17 + 1;
                this.documentTypeCategory = this.encoded[offset17];
                if ((this.documentTypeCategory & 1) == 0) {
                    JmcLogger.warning("La categoria deberia ser un numero impar, pero se ha encontrado " + this.documentTypeCategory);
                }
                while (offset18 < this.encoded.length) {
                    byte[] data = new byte[this.encoded.length - offset18];
                    System.arraycopy(this.encoded, offset18, data, 0, data.length);
                    Tlv tlv = new Tlv(data);
                    switch (tlv.getTag()) {
                        case -1:
                            this.dataTbs = new byte[offset18];
                            System.arraycopy(this.encoded, 0, this.dataTbs, 0, offset18);
                            byte[] sig = tlv.getValue();
                            byte[] r = new byte[tlv.getLength() / 2];
                            System.arraycopy(sig, 0, r, 0, tlv.getLength() / 2);
                            byte[] s = new byte[tlv.getLength() / 2];
                            System.arraycopy(sig, tlv.getLength() / 2, s, 0, tlv.getLength() / 2);
                            this.signature = encodeEcdsaSignature(r, s);
                            break;
                        case 0:
                        case 1:
                        default:
                            JmcLogger.warning("Encontrado campo de datos desconocido: " + tlv);
                            break;
                        case 2:
                            this.mrzB = C40Decoder.decode(tlv.getValue());
                            break;
                        case 3:
                            this.nEntries = HexUtils.getUnsignedInt(tlv.getValue(), 0);
                            break;
                        case 4:
                            byte[] durBytes = tlv.getValue();
                            if (durBytes.length < 3) {
                                this.durationOfStay = HexUtils.getUnsignedInt(durBytes, 0);
                            } else if (durBytes.length == 3) {
                                this.durationOfStay = ByteBuffer.wrap(new byte[]{0, durBytes[2], durBytes[1], durBytes[0]}).getInt();
                            } else {
                                this.durationOfStay = ByteBuffer.wrap(tmpDateBytes2).getInt();
                            }
                            break;
                        case 5:
                            this.passportNumber = C40Decoder.decode(tlv.getValue());
                            break;
                    }
                    offset18 += tlv.getBytes().length;
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException("La fecha de creacion de la firma es invalida (" + HexUtils.hexify(tmpDateBytes2, false) + ", " + tmpDate2 + ")", e);
            }
        } catch (ParseException e2) {
            throw new IllegalArgumentException("La fecha de emision del documento es invalida (" + HexUtils.hexify(tmpDateBytes, false) + ", " + tmpDate + ")", e2);
        }
    }

    private static byte[] encodeEcdsaSignature(byte[] r, byte[] s) {
        Tlv rTlv = new Tlv((byte) 2, r);
        Tlv sTlv = new Tlv((byte) 2, s);
        Tlv sequenceTlv = new Tlv((byte) 48, HexUtils.concatenateByteArrays(rTlv.getBytes(), sTlv.getBytes()));
        return sequenceTlv.getBytes();
    }

    public void verifyEcDsaSignature(PublicKey publicKey) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(publicKey);
        sig.update(this.dataTbs);
        if (!sig.verify(this.signature)) {
            throw new SignatureException("La firma no es valida");
        }
    }

    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "Visible Digital Seal for Non-Electronic Documents\n Version: " + this.version + "\n Pais emisor: " + CountryCodes.getCountryName(this.issuingCountry) + "\n Autoridad de certificacion y referencia: " + this.caCr + "\n Fecha de emision del documento: " + sdf.format(this.documentIssueDate) + "\n Fecha de creacion de la firma: " + sdf.format(this.signatureCreationDate) + "\n Referencia: " + this.documentFeatureDefinitionReference + "\n Categoria: " + this.documentTypeCategory + "\n MRZ-B: " + this.mrzB + "\n Numero de entradas: " + this.nEntries + "\n Duracion de la estancia: " + this.durationOfStay + "\n Numero de pasaporte: " + this.passportNumber + '\n';
    }

    public String getIssuingCountry() {
        return this.issuingCountry;
    }

    public String getCaCr() {
        return this.caCr;
    }

    public Date getDocumentIssueDate() {
        return this.documentIssueDate;
    }

    public Date getSignatureCreationDate() {
        return this.signatureCreationDate;
    }

    public int getDocumentFeatureDefinitionReference() {
        return this.documentFeatureDefinitionReference;
    }

    public int getDocumentTypeCategory() {
        return this.documentTypeCategory;
    }

    public int getVersion() {
        return this.version;
    }
}
