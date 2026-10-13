package com.aowagie.text.pdf;

import com.aowagie.text.DocWriter;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.PageSize;
import com.aowagie.text.Rectangle;
import com.aowagie.text.exceptions.BadPasswordException;
import com.aowagie.text.exceptions.InvalidPdfException;
import com.aowagie.text.exceptions.UnsupportedPdfException;
import com.aowagie.text.pdf.interfaces.PdfViewerPreferences;
import com.aowagie.text.pdf.internal.PdfViewerPreferencesImp;
import es.gob.afirma.signvalidation.ISignatureFormatDetector;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.zip.InflaterInputStream;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.spongycastle.cms.CMSEnvelopedData;
import org.spongycastle.cms.RecipientInformation;
import org.spongycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfReader.class */
public class PdfReader implements PdfViewerPreferences {
    private static final PdfName[] pageInhCandidates = {PdfName.MEDIABOX, PdfName.ROTATE, PdfName.RESOURCES, PdfName.CROPBOX};
    private static final byte[] endstream = PdfEncodings.convertToBytes("endstream", (String) null);
    private static final byte[] endobj = PdfEncodings.convertToBytes("endobj", (String) null);
    protected PRTokeniser tokens;
    private int[] xref;
    private HashMap objStmMark;
    private IntHashtable objStmToOffset;
    private boolean newXrefType;
    private ArrayList xrefObj;
    private PdfDictionary rootPages;
    protected PdfDictionary trailer;
    protected PdfDictionary catalog;
    PageRefs pageRefs;
    private PRAcroForm acroForm;
    private boolean acroFormParsed;
    private boolean encrypted;
    private boolean rebuilt;
    private int freeXref;
    private boolean tampered;
    private int lastXref;
    private int eofPos;
    private char pdfVersion;
    private PdfEncryption decrypt;
    private byte[] password;
    private Key certificateKey;
    private Certificate certificate;
    private String certificateKeyProvider;
    private boolean ownerPasswordUsed;
    private final ArrayList strings;
    private boolean sharedStreams;
    private boolean consolidateNamedDestinations;
    private int rValue;
    private int pValue;
    private int objNum;
    private int objGen;
    private int fileLength;
    private boolean hybridXref;
    private int lastXrefPartial;
    private boolean partial;
    private PRIndirectReference cryptoRef;
    private final PdfViewerPreferencesImp viewerPreferences;
    private boolean encryptionError;
    private boolean appendable;
    private int readDepth;

    protected PdfReader() {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.strings = new ArrayList();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
    }

    public PdfReader(String filename) throws IOException {
        this(filename, (byte[]) null);
    }

    private PdfReader(String filename, byte[] ownerPassword) throws IOException {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.strings = new ArrayList();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
        this.password = ownerPassword;
        this.tokens = new PRTokeniser(filename);
        readPdf();
    }

    public PdfReader(byte[] pdfIn) throws IOException {
        this(pdfIn, (byte[]) null);
    }

    public PdfReader(byte[] pdfIn, byte[] ownerPassword) throws IOException {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.strings = new ArrayList();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
        this.password = ownerPassword;
        this.tokens = new PRTokeniser(pdfIn);
        readPdf();
    }

    public PdfReader(String filename, Certificate certificate, Key certificateKey, String certificateKeyProvider) throws IOException {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.strings = new ArrayList();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
        this.certificate = certificate;
        this.certificateKey = certificateKey;
        this.certificateKeyProvider = certificateKeyProvider;
        this.tokens = new PRTokeniser(filename);
        readPdf();
    }

    private PdfReader(URL url, byte[] ownerPassword) throws IOException {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.strings = new ArrayList();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
        this.password = ownerPassword;
        this.tokens = new PRTokeniser(new RandomAccessFileOrArray(url));
        readPdf();
    }

    private PdfReader(InputStream is, byte[] ownerPassword) throws IOException {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.strings = new ArrayList();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
        this.password = ownerPassword;
        this.tokens = new PRTokeniser(new RandomAccessFileOrArray(is));
        readPdf();
    }

    public PdfReader(InputStream is) throws IOException {
        this(is, (byte[]) null);
    }

    PdfReader(PdfReader reader) {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.strings = new ArrayList();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
        this.appendable = reader.appendable;
        this.consolidateNamedDestinations = reader.consolidateNamedDestinations;
        this.encrypted = reader.encrypted;
        this.rebuilt = reader.rebuilt;
        this.sharedStreams = reader.sharedStreams;
        this.tampered = reader.tampered;
        this.password = reader.password;
        this.pdfVersion = reader.pdfVersion;
        this.eofPos = reader.eofPos;
        this.freeXref = reader.freeXref;
        this.lastXref = reader.lastXref;
        this.tokens = new PRTokeniser(reader.tokens.getSafeFile());
        if (reader.decrypt != null) {
            this.decrypt = new PdfEncryption(reader.decrypt);
        }
        this.pValue = reader.pValue;
        this.rValue = reader.rValue;
        this.xrefObj = new ArrayList(reader.xrefObj);
        for (int k = 0; k < reader.xrefObj.size(); k++) {
            this.xrefObj.set(k, duplicatePdfObject((PdfObject) reader.xrefObj.get(k), this));
        }
        this.pageRefs = new PageRefs(reader.pageRefs, this);
        this.trailer = (PdfDictionary) duplicatePdfObject(reader.trailer, this);
        this.catalog = this.trailer.getAsDict(PdfName.ROOT);
        this.rootPages = this.catalog.getAsDict(PdfName.PAGES);
        this.fileLength = reader.fileLength;
        this.partial = reader.partial;
        this.hybridXref = reader.hybridXref;
        this.objStmToOffset = reader.objStmToOffset;
        this.xref = reader.xref;
        this.cryptoRef = (PRIndirectReference) duplicatePdfObject(reader.cryptoRef, this);
        this.ownerPasswordUsed = reader.ownerPasswordUsed;
    }

    public RandomAccessFileOrArray getSafeFile() {
        return this.tokens.getSafeFile();
    }

    protected PdfReaderInstance getPdfReaderInstance(PdfWriter writer) {
        return new PdfReaderInstance(this, writer);
    }

    public int getNumberOfPages() {
        return this.pageRefs.size();
    }

    public PdfDictionary getCatalog() {
        return this.catalog;
    }

    public PRAcroForm getAcroForm() {
        if (!this.acroFormParsed) {
            this.acroFormParsed = true;
            PdfObject form = this.catalog.get(PdfName.ACROFORM);
            if (form != null) {
                try {
                    this.acroForm = new PRAcroForm(this);
                    this.acroForm.readAcroForm((PdfDictionary) getPdfObject(form));
                } catch (Exception e) {
                    this.acroForm = null;
                }
            }
        }
        return this.acroForm;
    }

    public int getPageRotation(int index) {
        return getPageRotation(this.pageRefs.getPageNRelease(index));
    }

    public int getPageRotation(PdfDictionary page) {
        PdfNumber rotate = page.getAsNumber(PdfName.ROTATE);
        if (rotate == null) {
            return 0;
        }
        int n = rotate.intValue() % 360;
        return n < 0 ? n + 360 : n;
    }

    public Rectangle getPageSizeWithRotation(int index) {
        return getPageSizeWithRotation(this.pageRefs.getPageNRelease(index));
    }

    Rectangle getPageSizeWithRotation(PdfDictionary page) {
        Rectangle rect = getPageSize(page);
        for (int rotation = getPageRotation(page); rotation > 0; rotation -= 90) {
            rect = rect.rotate();
        }
        return rect;
    }

    public Rectangle getPageSize(int index) {
        return getPageSize(this.pageRefs.getPageNRelease(index));
    }

    private Rectangle getPageSize(PdfDictionary page) {
        PdfArray mediaBox = page.getAsArray(PdfName.MEDIABOX);
        return getNormalizedRectangle(mediaBox);
    }

    Rectangle getBoxSize(int index, String boxName) {
        PdfDictionary page = this.pageRefs.getPageNRelease(index);
        PdfArray box = null;
        if (boxName.equals("trim")) {
            box = (PdfArray) getPdfObjectRelease(page.get(PdfName.TRIMBOX));
        } else if (boxName.equals("art")) {
            box = (PdfArray) getPdfObjectRelease(page.get(PdfName.ARTBOX));
        } else if (boxName.equals("bleed")) {
            box = (PdfArray) getPdfObjectRelease(page.get(PdfName.BLEEDBOX));
        } else if (boxName.equals("crop")) {
            box = (PdfArray) getPdfObjectRelease(page.get(PdfName.CROPBOX));
        } else if (boxName.equals("media")) {
            box = (PdfArray) getPdfObjectRelease(page.get(PdfName.MEDIABOX));
        }
        if (box == null) {
            return null;
        }
        return getNormalizedRectangle(box);
    }

    public HashMap getInfo() {
        HashMap map = new LinkedHashMap();
        PdfDictionary info = this.trailer.getAsDict(PdfName.INFO);
        if (info == null) {
            return map;
        }
        for (Object element : info.getKeys()) {
            PdfName key = (PdfName) element;
            PdfObject obj = getPdfObject(info.get(key));
            if (obj != null) {
                String value = obj.toString();
                switch (obj.type()) {
                    case 3:
                        value = ((PdfString) obj).toUnicodeString();
                        break;
                    case 4:
                        value = PdfName.decodeName(value);
                        break;
                }
                map.put(PdfName.decodeName(key.toString()), value);
            }
        }
        return map;
    }

    static Rectangle getNormalizedRectangle(PdfArray box) {
        float llx = ((PdfNumber) getPdfObjectRelease(box.getPdfObject(0))).floatValue();
        float lly = ((PdfNumber) getPdfObjectRelease(box.getPdfObject(1))).floatValue();
        float urx = ((PdfNumber) getPdfObjectRelease(box.getPdfObject(2))).floatValue();
        float ury = ((PdfNumber) getPdfObjectRelease(box.getPdfObject(3))).floatValue();
        return new Rectangle(Math.min(llx, urx), Math.min(lly, ury), Math.max(llx, urx), Math.max(lly, ury));
    }

    protected void readPdf() throws IOException {
        try {
            this.fileLength = this.tokens.getFile().length();
            this.pdfVersion = this.tokens.checkPdfHeader();
            try {
                readXref();
            } catch (Exception e) {
                try {
                    this.rebuilt = true;
                    rebuildXref();
                    this.lastXref = -1;
                } catch (Exception ne) {
                    throw new InvalidPdfException("Rebuild failed: " + ne.getMessage() + "; Original message: " + e.getMessage());
                }
            }
            try {
                readDocObj();
            } catch (Exception e2) {
                if (e2 instanceof BadPasswordException) {
                    throw new BadPasswordException(e2.getMessage());
                }
                if (this.rebuilt || this.encryptionError) {
                    throw new InvalidPdfException(e2.getMessage());
                }
                this.rebuilt = true;
                this.encrypted = false;
                rebuildXref();
                this.lastXref = -1;
                readDocObj();
            }
            this.strings.clear();
            readPages();
            eliminateSharedStreams();
            removeUnusedObjects();
            try {
                this.tokens.close();
            } catch (Exception e3) {
            }
        } catch (Throwable th) {
            try {
                this.tokens.close();
            } catch (Exception e4) {
            }
            throw th;
        }
    }

    private void readPdfPartial() throws IOException {
        try {
            this.fileLength = this.tokens.getFile().length();
            this.pdfVersion = this.tokens.checkPdfHeader();
            try {
                readXref();
            } catch (Exception e) {
                try {
                    this.rebuilt = true;
                    rebuildXref();
                    this.lastXref = -1;
                } catch (Exception ne) {
                    throw new InvalidPdfException("Rebuild failed: " + ne.getMessage() + "; Original message: " + e.getMessage());
                }
            }
            readDocObjPartial();
            readPages();
        } catch (IOException e2) {
            try {
                this.tokens.close();
            } catch (Exception e3) {
            }
            throw e2;
        }
    }

    private boolean equalsArray(byte[] ar1, byte[] ar2, int size) {
        for (int k = 0; k < size; k++) {
            if (ar1[k] != ar2[k]) {
                return false;
            }
        }
        return true;
    }

    private void readDecryptedDocObj() throws IOException {
        PdfObject encDic;
        PdfArray recipients;
        if (this.encrypted || (encDic = this.trailer.get(PdfName.ENCRYPT)) == null || encDic.toString().equals("null")) {
            return;
        }
        this.encryptionError = true;
        byte[] encryptionKey = null;
        this.encrypted = true;
        PdfDictionary enc = (PdfDictionary) getPdfObject(encDic);
        PdfArray documentIDs = this.trailer.getAsArray(PdfName.ID);
        byte[] documentID = null;
        if (documentIDs != null) {
            PdfObject o = documentIDs.getPdfObject(0);
            this.strings.remove(o);
            String s = o.toString();
            documentID = DocWriter.getISOBytes(s);
            if (documentIDs.size() > 1) {
                this.strings.remove(documentIDs.getPdfObject(1));
            }
        }
        if (documentID == null) {
            documentID = new byte[0];
        }
        byte[] uValue = null;
        byte[] oValue = null;
        int cryptoMode = 0;
        int lengthValue = 0;
        PdfObject filter = getPdfObjectRelease(enc.get(PdfName.FILTER));
        if (filter.equals(PdfName.STANDARD)) {
            String s2 = enc.get(PdfName.U).toString();
            this.strings.remove(enc.get(PdfName.U));
            uValue = DocWriter.getISOBytes(s2);
            String s3 = enc.get(PdfName.O).toString();
            this.strings.remove(enc.get(PdfName.O));
            oValue = DocWriter.getISOBytes(s3);
            PdfObject o2 = enc.get(PdfName.P);
            if (!o2.isNumber()) {
                throw new InvalidPdfException("Illegal P value.");
            }
            this.pValue = ((PdfNumber) o2).intValue();
            PdfObject o3 = enc.get(PdfName.R);
            if (!o3.isNumber()) {
                throw new InvalidPdfException("Illegal R value.");
            }
            this.rValue = ((PdfNumber) o3).intValue();
            switch (this.rValue) {
                case 2:
                    cryptoMode = 0;
                    break;
                case 3:
                    PdfObject o4 = enc.get(PdfName.LENGTH);
                    if (!o4.isNumber()) {
                        throw new InvalidPdfException("Illegal Length value.");
                    }
                    lengthValue = ((PdfNumber) o4).intValue();
                    if (lengthValue > 128 || lengthValue < 40 || lengthValue % 8 != 0) {
                        throw new InvalidPdfException("Illegal Length value.");
                    }
                    cryptoMode = 1;
                    break;
                    break;
                case 4:
                    PdfDictionary dic = (PdfDictionary) enc.get(PdfName.CF);
                    if (dic == null) {
                        throw new InvalidPdfException("/CF not found (encryption)");
                    }
                    PdfDictionary dic2 = (PdfDictionary) dic.get(PdfName.STDCF);
                    if (dic2 == null) {
                        throw new InvalidPdfException("/StdCF not found (encryption)");
                    }
                    if (PdfName.V2.equals(dic2.get(PdfName.CFM))) {
                        cryptoMode = 1;
                    } else if (PdfName.AESV2.equals(dic2.get(PdfName.CFM))) {
                        cryptoMode = 2;
                    } else {
                        throw new UnsupportedPdfException("No compatible encryption found");
                    }
                    PdfObject em = enc.get(PdfName.ENCRYPTMETADATA);
                    if (em != null && em.toString().equals("false")) {
                        cryptoMode |= 8;
                    }
                    break;
                    break;
                case 5:
                default:
                    throw new UnsupportedPdfException("Unknown encryption type R = " + this.rValue);
                case 6:
                    cryptoMode = 4;
                    PdfObject em2 = enc.get(PdfName.ENCRYPTMETADATA);
                    if (em2 != null && em2.toString().equals("false")) {
                        cryptoMode = 4 | 8;
                    }
                    break;
            }
        } else if (filter.equals(PdfName.PUBSEC)) {
            boolean foundRecipient = false;
            byte[] envelopedData = null;
            PdfObject o5 = enc.get(PdfName.V);
            if (!o5.isNumber()) {
                throw new InvalidPdfException("Illegal V value.");
            }
            int vValue = ((PdfNumber) o5).intValue();
            switch (vValue) {
                case 1:
                    cryptoMode = 0;
                    lengthValue = 40;
                    recipients = (PdfArray) enc.get(PdfName.RECIPIENTS);
                    break;
                case 2:
                    PdfObject o6 = enc.get(PdfName.LENGTH);
                    if (!o6.isNumber()) {
                        throw new InvalidPdfException("Illegal Length value.");
                    }
                    lengthValue = ((PdfNumber) o6).intValue();
                    if (lengthValue > 128 || lengthValue < 40 || lengthValue % 8 != 0) {
                        throw new InvalidPdfException("Illegal Length value.");
                    }
                    cryptoMode = 1;
                    recipients = (PdfArray) enc.get(PdfName.RECIPIENTS);
                    break;
                    break;
                case 3:
                default:
                    throw new UnsupportedPdfException("Unknown encryption type V = " + this.rValue);
                case 4:
                    PdfDictionary dic3 = (PdfDictionary) enc.get(PdfName.CF);
                    if (dic3 == null) {
                        throw new InvalidPdfException("/CF not found (encryption)");
                    }
                    PdfDictionary dic4 = (PdfDictionary) dic3.get(PdfName.DEFAULTCRYPTFILTER);
                    if (dic4 == null) {
                        throw new InvalidPdfException("/DefaultCryptFilter not found (encryption)");
                    }
                    if (PdfName.V2.equals(dic4.get(PdfName.CFM))) {
                        cryptoMode = 1;
                    } else if (PdfName.AESV2.equals(dic4.get(PdfName.CFM))) {
                        cryptoMode = 2;
                    } else {
                        throw new UnsupportedPdfException("No compatible encryption found");
                    }
                    lengthValue = 128;
                    PdfObject em3 = dic4.get(PdfName.ENCRYPTMETADATA);
                    if (em3 != null && em3.toString().equals("false")) {
                        cryptoMode |= 8;
                    }
                    recipients = (PdfArray) dic4.get(PdfName.RECIPIENTS);
                    break;
                    break;
            }
            for (int i = 0; i < recipients.size(); i++) {
                PdfObject recipient = recipients.getPdfObject(i);
                this.strings.remove(recipient);
                try {
                    CMSEnvelopedData data = new CMSEnvelopedData(recipient.getBytes());
                    for (RecipientInformation recipientInfo : data.getRecipientInfos().getRecipients()) {
                        if (recipientInfo.getRID().match(this.certificate) && !foundRecipient) {
                            envelopedData = recipientInfo.getContent(new JceKeyTransEnvelopedRecipient((PrivateKey) this.certificateKey));
                            foundRecipient = true;
                        }
                    }
                } catch (Exception f) {
                    throw new ExceptionConverter(f);
                }
            }
            if (!foundRecipient || envelopedData == null) {
                throw new UnsupportedPdfException("Bad certificate and key.");
            }
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(envelopedData, 0, 20);
                for (int i2 = 0; i2 < recipients.size(); i2++) {
                    byte[] encodedRecipient = recipients.getPdfObject(i2).getBytes();
                    md.update(encodedRecipient);
                }
                if ((cryptoMode & 8) != 0) {
                    md.update(new byte[]{-1, -1, -1, -1});
                }
                encryptionKey = md.digest();
            } catch (Exception f2) {
                throw new ExceptionConverter(f2);
            }
        }
        this.decrypt = new PdfEncryption();
        this.decrypt.setCryptoMode(cryptoMode, lengthValue);
        if (filter.equals(PdfName.STANDARD)) {
            if (this.rValue < 6) {
                this.decrypt.setupByOwnerPassword(documentID, this.password, uValue, oValue, this.pValue);
                if (!equalsArray(uValue, this.decrypt.userKey, (this.rValue == 3 || this.rValue == 4) ? 16 : 32)) {
                    this.decrypt.setupByUserPassword(documentID, this.password, oValue, this.pValue);
                    if (!equalsArray(uValue, this.decrypt.userKey, (this.rValue == 3 || this.rValue == 4) ? 16 : 32)) {
                        throw new BadPasswordException("bad.user.password");
                    }
                } else {
                    this.ownerPasswordUsed = true;
                }
            } else {
                String s4 = enc.get(PdfName.UE).toString();
                this.strings.remove(enc.get(PdfName.UE));
                byte[] ueValue = DocWriter.getISOBytes(s4);
                String s5 = enc.get(PdfName.OE).toString();
                this.strings.remove(enc.get(PdfName.OE));
                byte[] oeValue = DocWriter.getISOBytes(s5);
                String s6 = enc.get(PdfName.PERMS).toString();
                this.strings.remove(enc.get(PdfName.PERMS));
                byte[] permsValue = DocWriter.getISOBytes(s6);
                byte[] password = this.password;
                if (password == null) {
                    password = new byte[0];
                } else if (password.length > 127) {
                    password = Arrays.copyOf(password, 127);
                }
                if (uValue != null && uValue.length > 48) {
                    uValue = Arrays.copyOf(uValue, 48);
                }
                try {
                    PdfEncryption pdfEncryption = this.decrypt;
                    byte[] hashAlg2B = PdfEncryption.hashAlg2B(password, Arrays.copyOfRange(oValue, 32, 40), uValue);
                    if (equalsArray(hashAlg2B, oValue, 32)) {
                        this.decrypt.setupByOwnerPassword(documentID, password, uValue, ueValue, oValue, oeValue, this.pValue);
                        if (this.decrypt.decryptAndCheckPerms(permsValue)) {
                            this.ownerPasswordUsed = true;
                        }
                    }
                    if (!this.ownerPasswordUsed) {
                        PdfEncryption pdfEncryption2 = this.decrypt;
                        byte[] hashAlg2B2 = PdfEncryption.hashAlg2B(password, Arrays.copyOfRange(uValue, 32, 40), null);
                        if (!equalsArray(hashAlg2B2, uValue, 32)) {
                            throw new BadPasswordException("bad.user.password");
                        }
                        this.decrypt.setupByUserPassword(documentID, password, uValue, ueValue, oValue, oeValue, this.pValue);
                        if (!this.decrypt.decryptAndCheckPerms(permsValue)) {
                            throw new BadPasswordException("bad.user.password");
                        }
                    }
                    this.pValue = this.decrypt.permissions;
                } catch (IOException e) {
                    throw e;
                } catch (Exception e2) {
                    throw new IOException(e2);
                }
            }
        } else if (filter.equals(PdfName.PUBSEC)) {
            this.decrypt.setupByEncryptionKey(encryptionKey, lengthValue);
            this.ownerPasswordUsed = true;
        }
        for (int k = 0; k < this.strings.size(); k++) {
            PdfString str = (PdfString) this.strings.get(k);
            str.decrypt(this);
        }
        if (encDic.isIndirect()) {
            this.cryptoRef = (PRIndirectReference) encDic;
            this.xrefObj.set(this.cryptoRef.getNumber(), null);
        }
        this.encryptionError = false;
    }

    public static PdfObject getPdfObjectRelease(PdfObject obj) {
        PdfObject obj2 = getPdfObject(obj);
        releaseLastXrefPartial(obj);
        return obj2;
    }

    public static PdfObject getPdfObject(PdfObject obj) {
        if (obj == null) {
            return null;
        }
        if (!obj.isIndirect()) {
            return obj;
        }
        try {
            PRIndirectReference ref = (PRIndirectReference) obj;
            int idx = ref.getNumber();
            boolean appendable = ref.getReader().appendable;
            PdfObject obj2 = ref.getReader().getPdfObject(idx);
            if (obj2 == null) {
                return null;
            }
            if (appendable) {
                switch (obj2.type()) {
                    case 1:
                        obj2 = new PdfBoolean(((PdfBoolean) obj2).booleanValue());
                        break;
                    case 4:
                        obj2 = new PdfName(obj2.getBytes());
                        break;
                    case 8:
                        obj2 = new PdfNull();
                        break;
                }
                obj2.setIndRef(ref);
            }
            return obj2;
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    static PdfObject getPdfObjectRelease(PdfObject obj, PdfObject parent) {
        PdfObject obj2 = getPdfObject(obj, parent);
        releaseLastXrefPartial(obj);
        return obj2;
    }

    static PdfObject getPdfObject(PdfObject obj, PdfObject parent) {
        PRIndirectReference ref;
        if (obj == null) {
            return null;
        }
        if (!obj.isIndirect()) {
            if (parent != null && (ref = parent.getIndRef()) != null && ref.getReader().isAppendable()) {
                switch (obj.type()) {
                    case 1:
                        obj = new PdfBoolean(((PdfBoolean) obj).booleanValue());
                        break;
                    case 4:
                        obj = new PdfName(obj.getBytes());
                        break;
                    case 8:
                        obj = new PdfNull();
                        break;
                }
                obj.setIndRef(ref);
            }
            return obj;
        }
        return getPdfObject(obj);
    }

    PdfObject getPdfObjectRelease(int idx) {
        PdfObject obj = getPdfObject(idx);
        releaseLastXrefPartial();
        return obj;
    }

    public PdfObject getPdfObject(int idx) {
        try {
            this.lastXrefPartial = -1;
            if (idx < 0 || idx >= this.xrefObj.size()) {
                return null;
            }
            PdfObject obj = (PdfObject) this.xrefObj.get(idx);
            if (!this.partial || obj != null) {
                return obj;
            }
            if (idx * 2 >= this.xref.length) {
                return null;
            }
            PdfObject obj2 = readSingleObject(idx);
            this.lastXrefPartial = -1;
            if (obj2 != null) {
                this.lastXrefPartial = idx;
            }
            return obj2;
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseLastXrefPartial() {
        if (this.partial && this.lastXrefPartial != -1) {
            this.xrefObj.set(this.lastXrefPartial, null);
            this.lastXrefPartial = -1;
        }
    }

    static void releaseLastXrefPartial(PdfObject obj) {
        if (obj == null || !obj.isIndirect() || !(obj instanceof PRIndirectReference)) {
            return;
        }
        PRIndirectReference ref = (PRIndirectReference) obj;
        PdfReader reader = ref.getReader();
        if (reader.partial && reader.lastXrefPartial != -1 && reader.lastXrefPartial == ref.getNumber()) {
            reader.xrefObj.set(reader.lastXrefPartial, null);
        }
        reader.lastXrefPartial = -1;
    }

    private void setXrefPartialObject(int idx, PdfObject obj) {
        if (!this.partial || idx < 0) {
            return;
        }
        this.xrefObj.set(idx, obj);
    }

    public PRIndirectReference addPdfObject(PdfObject obj) {
        this.xrefObj.add(obj);
        return new PRIndirectReference(this, this.xrefObj.size() - 1);
    }

    protected void readPages() throws IOException {
        this.catalog = this.trailer.getAsDict(PdfName.ROOT);
        this.rootPages = this.catalog.getAsDict(PdfName.PAGES);
        this.pageRefs = new PageRefs();
    }

    private void readDocObjPartial() throws IOException {
        this.xrefObj = new ArrayList(this.xref.length / 2);
        this.xrefObj.addAll(Collections.nCopies(this.xref.length / 2, null));
        readDecryptedDocObj();
        if (this.objStmToOffset != null) {
            int[] keys = this.objStmToOffset.getKeys();
            for (int key : keys) {
                this.objStmToOffset.put(key, this.xref[key * 2]);
                this.xref[key * 2] = -1;
            }
        }
    }

    private PdfObject readSingleObject(int k) throws IOException {
        PdfObject obj;
        this.strings.clear();
        int k2 = k * 2;
        int pos = this.xref[k2];
        if (pos < 0) {
            return null;
        }
        if (this.xref[k2 + 1] > 0) {
            pos = this.objStmToOffset.get(this.xref[k2 + 1]);
        }
        if (pos == 0) {
            return null;
        }
        this.tokens.seek(pos);
        this.tokens.nextValidToken();
        if (this.tokens.getTokenType() != 1) {
            this.tokens.throwError("Invalid object number.");
        }
        this.objNum = this.tokens.intValue();
        this.tokens.nextValidToken();
        if (this.tokens.getTokenType() != 1) {
            this.tokens.throwError("Invalid generation number.");
        }
        this.objGen = this.tokens.intValue();
        this.tokens.nextValidToken();
        if (!this.tokens.getStringValue().equals("obj")) {
            this.tokens.throwError("Token 'obj' expected.");
        }
        try {
            obj = readPRObject();
            for (int j = 0; j < this.strings.size(); j++) {
                PdfString str = (PdfString) this.strings.get(j);
                str.decrypt(this);
            }
            if (obj.isStream()) {
                checkPRStreamLength((PRStream) obj);
            }
        } catch (Exception e) {
            obj = null;
        }
        if (this.xref[k2 + 1] > 0) {
            obj = readOneObjStm((PRStream) obj, this.xref[k2]);
        }
        this.xrefObj.set(k, obj);
        return obj;
    }

    private PdfObject readOneObjStm(PRStream stream, int idx) throws IOException {
        PdfObject obj;
        int first = stream.getAsNumber(PdfName.FIRST).intValue();
        byte[] b = getStreamBytes(stream, this.tokens.getFile());
        PRTokeniser saveTokens = this.tokens;
        this.tokens = new PRTokeniser(b);
        try {
            int address = 0;
            boolean ok = true;
            int idx2 = idx + 1;
            for (int k = 0; k < idx2; k++) {
                ok = this.tokens.nextToken();
                if (!ok) {
                    break;
                }
                if (this.tokens.getTokenType() != 1) {
                    ok = false;
                    break;
                }
                ok = this.tokens.nextToken();
                if (!ok) {
                    break;
                }
                if (this.tokens.getTokenType() != 1) {
                    ok = false;
                    break;
                }
                address = this.tokens.intValue() + first;
            }
            if (!ok) {
                throw new InvalidPdfException("Error reading ObjStm");
            }
            this.tokens.seek(address);
            this.tokens.nextToken();
            if (this.tokens.getTokenType() == 1) {
                obj = new PdfNumber(this.tokens.getStringValue());
            } else {
                this.tokens.seek(address);
                obj = readPRObject();
            }
            PdfObject pdfObject = obj;
            this.tokens = saveTokens;
            return pdfObject;
        } catch (Throwable th) {
            this.tokens = saveTokens;
            throw th;
        }
    }

    protected void readDocObj() throws IOException {
        PdfObject obj;
        ArrayList streams = new ArrayList();
        this.xrefObj = new ArrayList(this.xref.length / 2);
        this.xrefObj.addAll(Collections.nCopies(this.xref.length / 2, null));
        for (int k = 2; k < this.xref.length; k += 2) {
            int pos = this.xref[k];
            if (pos > 0 && this.xref[k + 1] <= 0) {
                this.tokens.seek(pos);
                this.tokens.nextValidToken();
                if (this.tokens.getTokenType() != 1) {
                    this.tokens.throwError("Invalid object number.");
                }
                this.objNum = this.tokens.intValue();
                this.tokens.nextValidToken();
                if (this.tokens.getTokenType() != 1) {
                    this.tokens.throwError("Invalid generation number.");
                }
                this.objGen = this.tokens.intValue();
                this.tokens.nextValidToken();
                if (!this.tokens.getStringValue().equals("obj")) {
                    this.tokens.throwError("Token 'obj' expected.");
                }
                try {
                    obj = readPRObject();
                    if (obj.isStream()) {
                        streams.add(obj);
                    }
                } catch (Exception e) {
                    obj = null;
                }
                this.xrefObj.set(k / 2, obj);
            }
        }
        for (int k2 = 0; k2 < streams.size(); k2++) {
            checkPRStreamLength((PRStream) streams.get(k2));
        }
        readDecryptedDocObj();
        if (this.objStmMark != null) {
            for (Map.Entry entry : this.objStmMark.entrySet()) {
                int n = ((Integer) entry.getKey()).intValue();
                IntHashtable h = (IntHashtable) entry.getValue();
                readObjStm((PRStream) this.xrefObj.get(n), h);
                this.xrefObj.set(n, null);
            }
            this.objStmMark = null;
        }
        this.xref = null;
    }

    private void checkPRStreamLength(PRStream stream) throws IOException {
        int pos;
        int fileLength = this.tokens.length();
        int start = stream.getOffset();
        boolean calc = false;
        int streamLength = 0;
        PdfObject obj = getPdfObjectRelease(stream.get(PdfName.LENGTH));
        if (obj != null && obj.type() == 2) {
            streamLength = ((PdfNumber) obj).intValue();
            if (streamLength + start > fileLength - 20) {
                calc = true;
            } else {
                this.tokens.seek(start + streamLength);
                String line = this.tokens.readString(20);
                if (!line.startsWith("\nendstream") && !line.startsWith("\r\nendstream") && !line.startsWith("\rendstream") && !line.startsWith("endstream")) {
                    calc = true;
                }
            }
        } else {
            calc = true;
        }
        if (calc) {
            byte[] tline = new byte[16];
            this.tokens.seek(start);
            do {
                pos = this.tokens.getFilePointer();
                if (this.tokens.readLineSegment(tline)) {
                    if (equalsn(tline, endstream)) {
                        streamLength = pos - start;
                    }
                }
            } while (!equalsn(tline, endobj));
            this.tokens.seek(pos - 16);
            String s = this.tokens.readString(16);
            int index = s.indexOf("endstream");
            if (index >= 0) {
                pos = (pos - 16) + index;
            }
            streamLength = pos - start;
        }
        stream.setLength(streamLength);
    }

    private void readObjStm(PRStream stream, IntHashtable map) throws IOException {
        PdfObject obj;
        int first = stream.getAsNumber(PdfName.FIRST).intValue();
        int n = stream.getAsNumber(PdfName.N).intValue();
        byte[] b = getStreamBytes(stream, this.tokens.getFile());
        PRTokeniser saveTokens = this.tokens;
        this.tokens = new PRTokeniser(b);
        try {
            int[] address = new int[n];
            int[] objNumber = new int[n];
            boolean ok = true;
            for (int k = 0; k < n; k++) {
                ok = this.tokens.nextToken();
                if (!ok) {
                    break;
                }
                if (this.tokens.getTokenType() != 1) {
                    ok = false;
                    break;
                }
                objNumber[k] = this.tokens.intValue();
                ok = this.tokens.nextToken();
                if (!ok) {
                    break;
                }
                if (this.tokens.getTokenType() != 1) {
                    ok = false;
                    break;
                }
                address[k] = this.tokens.intValue() + first;
            }
            if (!ok) {
                throw new InvalidPdfException("Error reading ObjStm");
            }
            for (int k2 = 0; k2 < n; k2++) {
                if (map.containsKey(k2)) {
                    this.tokens.seek(address[k2]);
                    this.tokens.nextToken();
                    if (this.tokens.getTokenType() == 1) {
                        obj = new PdfNumber(this.tokens.getStringValue());
                    } else {
                        this.tokens.seek(address[k2]);
                        obj = readPRObject();
                    }
                    this.xrefObj.set(objNumber[k2], obj);
                }
            }
            this.tokens = saveTokens;
        } catch (Throwable th) {
            this.tokens = saveTokens;
            throw th;
        }
    }

    static PdfObject killIndirect(PdfObject obj) {
        if (obj == null || obj.isNull()) {
            return null;
        }
        PdfObject ret = getPdfObjectRelease(obj);
        if (obj.isIndirect()) {
            PRIndirectReference ref = (PRIndirectReference) obj;
            PdfReader reader = ref.getReader();
            int n = ref.getNumber();
            reader.xrefObj.set(n, null);
            if (reader.partial) {
                reader.xref[n * 2] = -1;
            }
        }
        return ret;
    }

    private void ensureXrefSize(int size) {
        if (size == 0) {
            return;
        }
        if (this.xref == null) {
            this.xref = new int[size];
        } else if (this.xref.length < size) {
            int[] xref2 = new int[size];
            System.arraycopy(this.xref, 0, xref2, 0, this.xref.length);
            this.xref = xref2;
        }
    }

    private void readXref() throws IOException {
        PdfDictionary xrefSection;
        this.hybridXref = false;
        this.newXrefType = false;
        this.tokens.seek(this.tokens.getStartxref());
        this.tokens.nextToken();
        if (!this.tokens.getStringValue().equals("startxref")) {
            throw new InvalidPdfException("startxref not found.");
        }
        this.tokens.nextToken();
        if (this.tokens.getTokenType() != 1) {
            throw new InvalidPdfException("startxref is not followed by a number.");
        }
        int startxref = this.tokens.intValue();
        this.lastXref = startxref;
        this.eofPos = this.tokens.getFilePointer();
        try {
            if (readXRefStream(startxref)) {
                this.newXrefType = true;
                return;
            }
            while (true) {
                PdfDictionary trailer2 = xrefSection;
                PdfNumber prev = (PdfNumber) trailer2.get(PdfName.PREV);
                if (prev != null) {
                    this.tokens.seek(prev.intValue());
                    xrefSection = readXrefSection();
                } else {
                    return;
                }
            }
        } catch (Exception e) {
        }
        this.xref = null;
        this.tokens.seek(startxref);
        this.trailer = readXrefSection();
        xrefSection = this.trailer;
    }

    private PdfDictionary readXrefSection() throws IOException {
        this.tokens.nextValidToken();
        if (!this.tokens.getStringValue().equals("xref")) {
            this.tokens.throwError("xref subsection not found");
        }
        while (true) {
            this.tokens.nextValidToken();
            if (this.tokens.getStringValue().equals("trailer")) {
                break;
            }
            if (this.tokens.getTokenType() != 1) {
                this.tokens.throwError("Object number of the first object in this xref subsection not found");
            }
            int start = this.tokens.intValue();
            this.tokens.nextValidToken();
            if (this.tokens.getTokenType() != 1) {
                this.tokens.throwError("Number of entries in this xref subsection not found");
            }
            int end = this.tokens.intValue() + start;
            if (start == 1) {
                int back = this.tokens.getFilePointer();
                this.tokens.nextValidToken();
                int pos = this.tokens.intValue();
                this.tokens.nextValidToken();
                int gen = this.tokens.intValue();
                if (pos == 0 && gen == 65535) {
                    start--;
                    end--;
                }
                this.tokens.seek(back);
            }
            ensureXrefSize(end * 2);
            for (int k = start; k < end; k++) {
                this.tokens.nextValidToken();
                int pos2 = this.tokens.intValue();
                this.tokens.nextValidToken();
                this.tokens.intValue();
                this.tokens.nextValidToken();
                int p = k * 2;
                if (this.tokens.getStringValue().equals(OperatorName.ENDPATH)) {
                    if (this.xref[p] == 0 && this.xref[p + 1] == 0) {
                        this.xref[p] = pos2;
                    }
                } else if (this.tokens.getStringValue().equals(OperatorName.FILL_NON_ZERO)) {
                    if (this.xref[p] == 0 && this.xref[p + 1] == 0) {
                        this.xref[p] = -1;
                    }
                } else {
                    this.tokens.throwError("Invalid cross-reference entry in this xref subsection");
                }
            }
        }
        PdfDictionary trailer = (PdfDictionary) readPRObject();
        PdfNumber xrefSize = (PdfNumber) trailer.get(PdfName.SIZE);
        ensureXrefSize(xrefSize.intValue() * 2);
        PdfObject xrs = trailer.get(PdfName.XREFSTM);
        if (xrs != null && xrs.isNumber()) {
            int loc = ((PdfNumber) xrs).intValue();
            try {
                readXRefStream(loc);
                this.newXrefType = true;
                this.hybridXref = true;
            } catch (IOException e) {
                this.xref = null;
                throw e;
            }
        }
        return trailer;
    }

    private boolean readXRefStream(int ptr) throws IOException {
        PdfArray index;
        this.tokens.seek(ptr);
        if (!this.tokens.nextToken() || this.tokens.getTokenType() != 1) {
            return false;
        }
        int thisStream = this.tokens.intValue();
        if (!this.tokens.nextToken() || this.tokens.getTokenType() != 1 || !this.tokens.nextToken() || !this.tokens.getStringValue().equals("obj")) {
            return false;
        }
        PdfObject object = readPRObject();
        if (object.isStream()) {
            PRStream stm = (PRStream) object;
            if (!PdfName.XREF.equals(stm.get(PdfName.TYPE))) {
                return false;
            }
            if (this.trailer == null) {
                this.trailer = new PdfDictionary();
                this.trailer.putAll(stm);
            }
            stm.setLength(((PdfNumber) stm.get(PdfName.LENGTH)).intValue());
            int size = ((PdfNumber) stm.get(PdfName.SIZE)).intValue();
            PdfObject obj = stm.get(PdfName.INDEX);
            if (obj == null) {
                index = new PdfArray();
                index.add(new int[]{0, size});
            } else {
                index = (PdfArray) obj;
            }
            PdfArray w = (PdfArray) stm.get(PdfName.W);
            int prev = -1;
            PdfObject obj2 = stm.get(PdfName.PREV);
            if (obj2 != null) {
                prev = ((PdfNumber) obj2).intValue();
            }
            ensureXrefSize(size * 2);
            if (this.objStmMark == null && !this.partial) {
                this.objStmMark = new LinkedHashMap();
            }
            if (this.objStmToOffset == null && this.partial) {
                this.objStmToOffset = new IntHashtable();
            }
            byte[] b = getStreamBytes(stm, this.tokens.getFile());
            int bptr = 0;
            int[] wc = new int[3];
            for (int k = 0; k < 3; k++) {
                wc[k] = w.getAsNumber(k).intValue();
            }
            for (int idx = 0; idx < index.size(); idx += 2) {
                int start = index.getAsNumber(idx).intValue();
                int length = index.getAsNumber(idx + 1).intValue();
                ensureXrefSize((start + length) * 2);
                while (true) {
                    int i = length;
                    length--;
                    if (i > 0) {
                        int type = 1;
                        if (wc[0] > 0) {
                            type = 0;
                            for (int k2 = 0; k2 < wc[0]; k2++) {
                                int i2 = bptr;
                                bptr++;
                                type = (type << 8) + (b[i2] & 255);
                            }
                        }
                        int field2 = 0;
                        for (int k3 = 0; k3 < wc[1]; k3++) {
                            int i3 = bptr;
                            bptr++;
                            field2 = (field2 << 8) + (b[i3] & 255);
                        }
                        int field3 = 0;
                        for (int k4 = 0; k4 < wc[2]; k4++) {
                            int i4 = bptr;
                            bptr++;
                            field3 = (field3 << 8) + (b[i4] & 255);
                        }
                        int base = start * 2;
                        if (this.xref[base] == 0 && this.xref[base + 1] == 0) {
                            switch (type) {
                                case 0:
                                    this.xref[base] = -1;
                                    break;
                                case 1:
                                    this.xref[base] = field2;
                                    break;
                                case 2:
                                    this.xref[base] = field3;
                                    this.xref[base + 1] = field2;
                                    if (this.partial) {
                                        this.objStmToOffset.put(field2, 0);
                                    } else {
                                        Integer on = Integer.valueOf(field2);
                                        IntHashtable seq = (IntHashtable) this.objStmMark.get(on);
                                        if (seq == null) {
                                            IntHashtable seq2 = new IntHashtable();
                                            seq2.put(field3, 1);
                                            this.objStmMark.put(on, seq2);
                                        } else {
                                            seq.put(field3, 1);
                                        }
                                    }
                                    break;
                            }
                        }
                        start++;
                    }
                }
            }
            int thisStream2 = thisStream * 2;
            if (thisStream2 + 1 < this.xref.length && this.xref[thisStream2] == 0 && this.xref[thisStream2 + 1] == 0) {
                this.xref[thisStream2] = -1;
            }
            if (prev == -1) {
                return true;
            }
            return readXRefStream(prev);
        }
        return false;
    }

    protected void rebuildXref() throws IOException {
        int[] obj;
        this.hybridXref = false;
        this.newXrefType = false;
        this.tokens.seek(0);
        int[][] xr = new int[1024][];
        int top = 0;
        this.trailer = null;
        byte[] line = new byte[64];
        while (true) {
            int pos = this.tokens.getFilePointer();
            if (!this.tokens.readLineSegment(line)) {
                break;
            }
            if (line[0] == 116) {
                if (PdfEncodings.convertToString(line, null).startsWith("trailer")) {
                    this.tokens.seek(pos);
                    this.tokens.nextToken();
                    int pos2 = this.tokens.getFilePointer();
                    try {
                        PdfDictionary dic = (PdfDictionary) readPRObject();
                        if (dic.get(PdfName.ROOT) != null) {
                            this.trailer = dic;
                        } else {
                            this.tokens.seek(pos2);
                        }
                    } catch (Exception e) {
                        this.tokens.seek(pos2);
                    }
                }
            } else if (line[0] >= 48 && line[0] <= 57 && (obj = PRTokeniser.checkObjectStart(line)) != null) {
                int num = obj[0];
                int gen = obj[1];
                if (num >= xr.length) {
                    int newLength = num * 2;
                    int[][] xr2 = new int[newLength][];
                    System.arraycopy(xr, 0, xr2, 0, top);
                    xr = xr2;
                }
                if (num >= top) {
                    top = num + 1;
                }
                if (xr[num] == null || gen >= xr[num][1]) {
                    obj[0] = pos;
                    xr[num] = obj;
                }
            }
        }
        if (this.trailer == null) {
            throw new InvalidPdfException("trailer not found.");
        }
        this.xref = new int[top * 2];
        for (int k = 0; k < top; k++) {
            int[] obj2 = xr[k];
            if (obj2 != null) {
                this.xref[k * 2] = obj2[0];
            }
        }
    }

    private PdfDictionary readDictionary() throws IOException {
        PdfDictionary dic = new PdfDictionary();
        while (true) {
            this.tokens.nextValidToken();
            if (this.tokens.getTokenType() != 8) {
                if (this.tokens.getTokenType() != 3) {
                    this.tokens.throwError("Dictionary key is not a name.");
                }
                PdfName name = new PdfName(this.tokens.getStringValue(), false);
                PdfObject obj = readPRObject();
                int type = obj.type();
                if ((-type) == 8) {
                    this.tokens.throwError("Unexpected '>>'");
                }
                if ((-type) == 6) {
                    this.tokens.throwError("Unexpected ']'");
                }
                dic.put(name, obj);
            } else {
                return dic;
            }
        }
    }

    private PdfArray readArray() throws IOException {
        PdfArray array = new PdfArray();
        while (true) {
            PdfObject obj = readPRObject();
            int type = obj.type();
            if ((-type) != 6) {
                if ((-type) == 8) {
                    this.tokens.throwError("Unexpected '>>'");
                }
                array.add(obj);
            } else {
                return array;
            }
        }
    }

    private PdfObject readPRObject() throws IOException {
        boolean hasNext;
        this.tokens.nextValidToken();
        int type = this.tokens.getTokenType();
        switch (type) {
            case 1:
                return new PdfNumber(this.tokens.getStringValue());
            case 2:
                PdfString str = new PdfString(this.tokens.getStringValue(), null).setHexWriting(this.tokens.isHexString());
                str.setObjNum(this.objNum, this.objGen);
                if (this.strings != null) {
                    this.strings.add(str);
                }
                return str;
            case 3:
                PdfName cachedName = (PdfName) PdfName.staticNames.get(this.tokens.getStringValue());
                if (this.readDepth > 0 && cachedName != null) {
                    return cachedName;
                }
                return new PdfName(this.tokens.getStringValue(), false);
            case 4:
            case 6:
            case 8:
            default:
                String sv = this.tokens.getStringValue();
                if ("null".equals(sv)) {
                    if (this.readDepth == 0) {
                        return new PdfNull();
                    }
                    return PdfNull.PDFNULL;
                }
                if ("true".equals(sv)) {
                    if (this.readDepth == 0) {
                        return new PdfBoolean(true);
                    }
                    return PdfBoolean.PDFTRUE;
                }
                if ("false".equals(sv)) {
                    if (this.readDepth == 0) {
                        return new PdfBoolean(false);
                    }
                    return PdfBoolean.PDFFALSE;
                }
                return new PdfLiteral(-type, this.tokens.getStringValue());
            case 5:
                this.readDepth++;
                PdfArray arr = readArray();
                this.readDepth--;
                return arr;
            case 7:
                this.readDepth++;
                PdfDictionary dic = readDictionary();
                this.readDepth--;
                int pos = this.tokens.getFilePointer();
                do {
                    hasNext = this.tokens.nextToken();
                    if (hasNext) {
                    }
                    if (hasNext || !this.tokens.getStringValue().equals("stream")) {
                        this.tokens.seek(pos);
                        return dic;
                    }
                    while (true) {
                        int ch = this.tokens.read();
                        if (ch != 32 && ch != 9 && ch != 0 && ch != 12) {
                            if (ch != 10) {
                                ch = this.tokens.read();
                            }
                            if (ch != 10) {
                                this.tokens.backOnePosition(ch);
                            }
                            PRStream stream = new PRStream(this, this.tokens.getFilePointer());
                            stream.putAll(dic);
                            stream.setObjNum(this.objNum, this.objGen);
                            return stream;
                        }
                    }
                } while (this.tokens.getTokenType() == 4);
                if (hasNext) {
                }
                this.tokens.seek(pos);
                return dic;
            case 9:
                int num = this.tokens.getReference();
                PRIndirectReference ref = new PRIndirectReference(this, num, this.tokens.getGeneration());
                return ref;
        }
    }

    private static byte[] FlateDecode(byte[] in) {
        byte[] b = FlateDecode(in, true);
        if (b == null) {
            return FlateDecode(in, false);
        }
        return b;
    }

    private static byte[] decodePredictor(byte[] in, PdfObject dicPar) {
        int i;
        if (dicPar == null || !dicPar.isDictionary()) {
            return in;
        }
        PdfDictionary dic = (PdfDictionary) dicPar;
        PdfObject obj = getPdfObject(dic.get(PdfName.PREDICTOR));
        if (obj == null || !obj.isNumber()) {
            return in;
        }
        int predictor = ((PdfNumber) obj).intValue();
        if (predictor < 10) {
            return in;
        }
        int width = 1;
        PdfObject obj2 = getPdfObject(dic.get(PdfName.COLUMNS));
        if (obj2 != null && obj2.isNumber()) {
            width = ((PdfNumber) obj2).intValue();
        }
        int colors = 1;
        PdfObject obj3 = getPdfObject(dic.get(PdfName.COLORS));
        if (obj3 != null && obj3.isNumber()) {
            colors = ((PdfNumber) obj3).intValue();
        }
        int bpc = 8;
        PdfObject obj4 = getPdfObject(dic.get(PdfName.BITSPERCOMPONENT));
        if (obj4 != null && obj4.isNumber()) {
            bpc = ((PdfNumber) obj4).intValue();
        }
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(in));
        ByteArrayOutputStream fout = new ByteArrayOutputStream(in.length);
        int bytesPerPixel = (colors * bpc) / 8;
        int bytesPerRow = (((colors * width) * bpc) + 7) / 8;
        byte[] curr = new byte[bytesPerRow];
        byte[] prior = new byte[bytesPerRow];
        while (true) {
            try {
                int filter = dataStream.read();
                if (filter < 0) {
                    return fout.toByteArray();
                }
                dataStream.readFully(curr, 0, bytesPerRow);
                switch (filter) {
                    case 0:
                        break;
                    case 1:
                        for (int i2 = bytesPerPixel; i2 < bytesPerRow; i2++) {
                            byte[] bArr = curr;
                            int i3 = i2;
                            bArr[i3] = (byte) (bArr[i3] + curr[i2 - bytesPerPixel]);
                        }
                        break;
                    case 2:
                        for (int i4 = 0; i4 < bytesPerRow; i4++) {
                            byte[] bArr2 = curr;
                            int i5 = i4;
                            bArr2[i5] = (byte) (bArr2[i5] + prior[i4]);
                        }
                        break;
                    case 3:
                        for (int i6 = 0; i6 < bytesPerPixel; i6++) {
                            byte[] bArr3 = curr;
                            int i7 = i6;
                            bArr3[i7] = (byte) (bArr3[i7] + (prior[i6] / 2));
                        }
                        for (int i8 = bytesPerPixel; i8 < bytesPerRow; i8++) {
                            byte[] bArr4 = curr;
                            int i9 = i8;
                            bArr4[i9] = (byte) (bArr4[i9] + (((curr[i8 - bytesPerPixel] & 255) + (prior[i8] & 255)) / 2));
                        }
                        break;
                    case 4:
                        for (int i10 = 0; i10 < bytesPerPixel; i10++) {
                            byte[] bArr5 = curr;
                            int i11 = i10;
                            bArr5[i11] = (byte) (bArr5[i11] + prior[i10]);
                        }
                        for (int i12 = bytesPerPixel; i12 < bytesPerRow; i12++) {
                            int a = curr[i12 - bytesPerPixel] & 255;
                            int b = prior[i12] & 255;
                            int c = prior[i12 - bytesPerPixel] & 255;
                            int p = (a + b) - c;
                            int pa = Math.abs(p - a);
                            int pb = Math.abs(p - b);
                            int pc = Math.abs(p - c);
                            if (pa <= pb && pa <= pc) {
                                i = a;
                            } else if (pb <= pc) {
                                i = b;
                            } else {
                                i = c;
                            }
                            int ret = i;
                            byte[] bArr6 = curr;
                            int i13 = i12;
                            bArr6[i13] = (byte) (bArr6[i13] + ((byte) ret));
                        }
                        break;
                    default:
                        throw new RuntimeException("PNG filter unknown.");
                }
                try {
                    fout.write(curr);
                } catch (IOException e) {
                }
                byte[] tmp = prior;
                prior = curr;
                curr = tmp;
            } catch (Exception e2) {
                return fout.toByteArray();
            }
        }
    }

    public static byte[] FlateDecode(byte[] in, boolean strict) {
        ByteArrayInputStream stream = new ByteArrayInputStream(in);
        InflaterInputStream zip = new InflaterInputStream(stream);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[strict ? 4092 : 1];
        while (true) {
            try {
                int n = zip.read(b);
                if (n >= 0) {
                    out.write(b, 0, n);
                } else {
                    zip.close();
                    out.close();
                    return out.toByteArray();
                }
            } catch (Exception e) {
                if (strict) {
                    return null;
                }
                return out.toByteArray();
            }
        }
    }

    private static byte[] ASCIIHexDecode(byte[] in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean first = true;
        int n1 = 0;
        for (byte element : in) {
            int ch = element & 255;
            if (ch == 62) {
                break;
            }
            if (!PRTokeniser.isWhitespace(ch)) {
                int n = PRTokeniser.getHex(ch);
                if (n == -1) {
                    throw new RuntimeException("Illegal character in ASCIIHexDecode.");
                }
                if (first) {
                    n1 = n;
                } else {
                    out.write((byte) ((n1 << 4) + n));
                }
                first = !first;
            }
        }
        if (!first) {
            out.write((byte) (n1 << 4));
        }
        return out.toByteArray();
    }

    private static byte[] ASCII85Decode(byte[] in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int state = 0;
        int[] chn = new int[5];
        for (byte element : in) {
            int ch = element & 255;
            if (ch == 126) {
                break;
            }
            if (!PRTokeniser.isWhitespace(ch)) {
                if (ch == 122 && state == 0) {
                    out.write(0);
                    out.write(0);
                    out.write(0);
                    out.write(0);
                } else {
                    if (ch < 33 || ch > 117) {
                        throw new RuntimeException("Illegal character in ASCII85Decode.");
                    }
                    chn[state] = ch - 33;
                    state++;
                    if (state == 5) {
                        state = 0;
                        int r = 0;
                        for (int j = 0; j < 5; j++) {
                            r = (r * 85) + chn[j];
                        }
                        out.write((byte) (r >> 24));
                        out.write((byte) (r >> 16));
                        out.write((byte) (r >> 8));
                        out.write((byte) r);
                    }
                }
            }
        }
        if (state == 2) {
            out.write((byte) (((((((((chn[0] * 85) * 85) * 85) * 85) + (((chn[1] * 85) * 85) * 85)) + 614125) + 7225) + 85) >> 24));
        } else if (state == 3) {
            int r2 = (chn[0] * 85 * 85 * 85 * 85) + (chn[1] * 85 * 85 * 85) + (chn[2] * 85 * 85) + 7225 + 85;
            out.write((byte) (r2 >> 24));
            out.write((byte) (r2 >> 16));
        } else if (state == 4) {
            int r3 = (chn[0] * 85 * 85 * 85 * 85) + (chn[1] * 85 * 85 * 85) + (chn[2] * 85 * 85) + (chn[3] * 85) + 85;
            out.write((byte) (r3 >> 24));
            out.write((byte) (r3 >> 16));
            out.write((byte) (r3 >> 8));
        }
        return out.toByteArray();
    }

    private static byte[] LZWDecode(byte[] in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        LZWDecoder lzw = new LZWDecoder();
        lzw.decode(in, out);
        return out.toByteArray();
    }

    public boolean isRebuilt() {
        return this.rebuilt;
    }

    public PdfDictionary getPageN(int pageNum) {
        PdfDictionary dic = this.pageRefs.getPageN(pageNum);
        if (dic == null) {
            return null;
        }
        if (this.appendable) {
            dic.setIndRef(this.pageRefs.getPageOrigRef(pageNum));
        }
        return dic;
    }

    public PdfDictionary getPageNRelease(int pageNum) {
        PdfDictionary dic = getPageN(pageNum);
        this.pageRefs.releasePage(pageNum);
        return dic;
    }

    public void releasePage(int pageNum) {
        this.pageRefs.releasePage(pageNum);
    }

    public void resetReleasePage() {
        this.pageRefs.resetReleasePage();
    }

    public PRIndirectReference getPageOrigRef(int pageNum) {
        return this.pageRefs.getPageOrigRef(pageNum);
    }

    public byte[] getPageContent(int pageNum, RandomAccessFileOrArray file) throws IOException {
        PdfDictionary page = getPageNRelease(pageNum);
        if (page == null) {
            return null;
        }
        PdfObject contents = getPdfObjectRelease(page.get(PdfName.CONTENTS));
        if (contents == null) {
            return new byte[0];
        }
        if (contents.isStream()) {
            return getStreamBytes((PRStream) contents, file);
        }
        if (contents.isArray()) {
            PdfArray array = (PdfArray) contents;
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            for (int k = 0; k < array.size(); k++) {
                PdfObject item = getPdfObjectRelease(array.getPdfObject(k));
                if (item != null && item.isStream()) {
                    byte[] b = getStreamBytes((PRStream) item, file);
                    bout.write(b);
                    if (k != array.size() - 1) {
                        bout.write(10);
                    }
                }
            }
            return bout.toByteArray();
        }
        return new byte[0];
    }

    protected void killXref(PdfObject obj) {
        if (obj == null) {
        }
        if ((obj instanceof PdfIndirectReference) && !obj.isIndirect()) {
            return;
        }
        switch (obj.type()) {
            case 5:
                PdfArray t = (PdfArray) obj;
                for (int i = 0; i < t.size(); i++) {
                    killXref(t.getPdfObject(i));
                }
                break;
            case 6:
            case 7:
                PdfDictionary dic = (PdfDictionary) obj;
                for (Object element : dic.getKeys()) {
                    killXref(dic.get((PdfName) element));
                }
                break;
            case 10:
                int xr = ((PRIndirectReference) obj).getNumber();
                PdfObject obj2 = (PdfObject) this.xrefObj.get(xr);
                this.xrefObj.set(xr, null);
                this.freeXref = xr;
                killXref(obj2);
                break;
        }
    }

    private void setPageContent(int pageNum, byte[] content, int compressionLevel) {
        PdfDictionary page = getPageN(pageNum);
        if (page == null) {
            return;
        }
        PdfObject contents = page.get(PdfName.CONTENTS);
        this.freeXref = -1;
        killXref(contents);
        if (this.freeXref == -1) {
            this.xrefObj.add(null);
            this.freeXref = this.xrefObj.size() - 1;
        }
        page.put(PdfName.CONTENTS, new PRIndirectReference(this, this.freeXref));
        this.xrefObj.set(this.freeXref, new PRStream(this, content, compressionLevel));
    }

    private static byte[] getStreamBytes(PRStream stream, RandomAccessFileOrArray file) throws IOException {
        PdfObject filter = getPdfObjectRelease(stream.get(PdfName.FILTER));
        byte[] b = getStreamBytesRaw(stream, file);
        ArrayList filters = new ArrayList();
        if (filter != null) {
            if (filter.isName()) {
                filters.add(filter);
            } else if (filter.isArray()) {
                filters = ((PdfArray) filter).getArrayList();
            }
        }
        ArrayList dp = new ArrayList();
        PdfObject dpo = getPdfObjectRelease(stream.get(PdfName.DECODEPARMS));
        if (dpo == null || (!dpo.isDictionary() && !dpo.isArray())) {
            dpo = getPdfObjectRelease(stream.get(PdfName.DP));
        }
        if (dpo != null) {
            if (dpo.isDictionary()) {
                dp.add(dpo);
            } else if (dpo.isArray()) {
                dp = ((PdfArray) dpo).getArrayList();
            }
        }
        for (int j = 0; j < filters.size(); j++) {
            String name = ((PdfName) getPdfObjectRelease((PdfObject) filters.get(j))).toString();
            if (name.equals("/FlateDecode") || name.equals("/Fl")) {
                b = FlateDecode(b);
                if (j < dp.size()) {
                    PdfObject dicParam = (PdfObject) dp.get(j);
                    b = decodePredictor(b, dicParam);
                }
            } else if (name.equals("/ASCIIHexDecode") || name.equals("/AHx")) {
                b = ASCIIHexDecode(b);
            } else if (name.equals("/ASCII85Decode") || name.equals("/A85")) {
                b = ASCII85Decode(b);
            } else if (name.equals("/LZWDecode")) {
                b = LZWDecode(b);
                if (j < dp.size()) {
                    PdfObject dicParam2 = (PdfObject) dp.get(j);
                    b = decodePredictor(b, dicParam2);
                }
            } else if (!name.equals("/Crypt")) {
                throw new UnsupportedPdfException("The filter " + name + " is not supported.");
            }
        }
        return b;
    }

    public static byte[] getStreamBytes(PRStream stream) throws IOException {
        RandomAccessFileOrArray rf = stream.getReader().getSafeFile();
        try {
            rf.reOpen();
            return getStreamBytes(stream, rf);
        } finally {
            try {
                rf.close();
            } catch (Exception e) {
            }
        }
    }

    private static byte[] getStreamBytesRaw(PRStream stream, RandomAccessFileOrArray file) throws IOException {
        byte[] b;
        PdfReader reader = stream.getReader();
        if (stream.getOffset() < 0) {
            b = stream.getBytes();
        } else {
            b = new byte[stream.getLength()];
            file.seek(stream.getOffset());
            file.readFully(b);
            PdfEncryption decrypt = reader.getDecrypt();
            if (decrypt != null) {
                PdfObject filter = getPdfObjectRelease(stream.get(PdfName.FILTER));
                ArrayList filters = new ArrayList();
                if (filter != null) {
                    if (filter.isName()) {
                        filters.add(filter);
                    } else if (filter.isArray()) {
                        filters = ((PdfArray) filter).getArrayList();
                    }
                }
                boolean skip = false;
                for (int k = 0; k < filters.size(); k++) {
                    PdfObject obj = getPdfObjectRelease((PdfObject) filters.get(k));
                    if (obj != null && obj.toString().equals("/Crypt")) {
                        skip = true;
                        break;
                    }
                }
                if (!skip) {
                    decrypt.setHashKey(stream.getObjNum(), stream.getObjGen());
                    b = decrypt.decryptByteArray(b);
                }
            }
        }
        return b;
    }

    static byte[] getStreamBytesRaw(PRStream stream) throws IOException {
        RandomAccessFileOrArray rf = stream.getReader().getSafeFile();
        try {
            rf.reOpen();
            return getStreamBytesRaw(stream, rf);
        } finally {
            try {
                rf.close();
            } catch (Exception e) {
            }
        }
    }

    private void eliminateSharedStreams() {
        PdfObject contents;
        if (!this.sharedStreams) {
            return;
        }
        this.sharedStreams = false;
        if (this.pageRefs.size() == 1) {
            return;
        }
        ArrayList newRefs = new ArrayList();
        ArrayList newStreams = new ArrayList();
        IntHashtable visited = new IntHashtable();
        for (int k = 1; k <= this.pageRefs.size(); k++) {
            PdfDictionary page = this.pageRefs.getPageN(k);
            if (page != null && (contents = getPdfObject(page.get(PdfName.CONTENTS))) != null) {
                if (contents.isStream()) {
                    PRIndirectReference ref = (PRIndirectReference) page.get(PdfName.CONTENTS);
                    if (visited.containsKey(ref.getNumber())) {
                        newRefs.add(ref);
                        newStreams.add(new PRStream((PRStream) contents, (PdfDictionary) null));
                    } else {
                        visited.put(ref.getNumber(), 1);
                    }
                } else if (contents.isArray()) {
                    PdfArray array = (PdfArray) contents;
                    for (int j = 0; j < array.size(); j++) {
                        PRIndirectReference ref2 = (PRIndirectReference) array.getPdfObject(j);
                        if (visited.containsKey(ref2.getNumber())) {
                            newRefs.add(ref2);
                            newStreams.add(new PRStream((PRStream) getPdfObject(ref2), (PdfDictionary) null));
                        } else {
                            visited.put(ref2.getNumber(), 1);
                        }
                    }
                }
            }
        }
        if (newStreams.isEmpty()) {
            return;
        }
        for (int k2 = 0; k2 < newStreams.size(); k2++) {
            this.xrefObj.add(newStreams.get(k2));
            ((PRIndirectReference) newRefs.get(k2)).setNumber(this.xrefObj.size() - 1, 0);
        }
    }

    public boolean isTampered() {
        return this.tampered;
    }

    public void setTampered(boolean tampered) {
        this.tampered = tampered;
        this.pageRefs.keepPages();
    }

    public byte[] getMetadata() throws IOException {
        PdfObject obj = getPdfObject(this.catalog.get(PdfName.METADATA));
        if (!(obj instanceof PRStream)) {
            return null;
        }
        RandomAccessFileOrArray rf = getSafeFile();
        try {
            rf.reOpen();
            byte[] b = getStreamBytes((PRStream) obj, rf);
            return b;
        } finally {
            try {
                rf.close();
            } catch (Exception e) {
            }
        }
    }

    public int getLastXref() {
        return this.lastXref;
    }

    public int getXrefSize() {
        return this.xrefObj.size();
    }

    public int getEofPos() {
        return this.eofPos;
    }

    public char getPdfVersion() {
        return this.pdfVersion;
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

    public int getPermissions() {
        return this.pValue;
    }

    public PdfDictionary getTrailer() {
        return this.trailer;
    }

    PdfEncryption getDecrypt() {
        return this.decrypt;
    }

    private static boolean equalsn(byte[] a1, byte[] a2) {
        int length = a2.length;
        for (int k = 0; k < length; k++) {
            if (a1[k] != a2[k]) {
                return false;
            }
        }
        return true;
    }

    private static boolean existsName(PdfDictionary dic, PdfName key, PdfName value) {
        PdfObject type = getPdfObjectRelease(dic.get(key));
        if (type == null || !type.isName()) {
            return false;
        }
        PdfName name = (PdfName) type;
        return name.equals(value);
    }

    private static String getFontName(PdfDictionary dic) {
        PdfObject type;
        if (dic == null || (type = getPdfObjectRelease(dic.get(PdfName.BASEFONT))) == null || !type.isName()) {
            return null;
        }
        return PdfName.decodeName(type.toString());
    }

    private static String getSubsetPrefix(PdfDictionary dic) {
        String s;
        if (dic == null || (s = getFontName(dic)) == null || s.length() < 8 || s.charAt(6) != '+') {
            return null;
        }
        for (int k = 0; k < 6; k++) {
            char c = s.charAt(k);
            if (c < 'A' || c > 'Z') {
                return null;
            }
        }
        return s;
    }

    int shuffleSubsetNames() {
        PdfDictionary desc;
        String sde;
        int total = 0;
        for (int k = 1; k < this.xrefObj.size(); k++) {
            PdfObject obj = getPdfObjectRelease(k);
            if (obj != null && obj.isDictionary()) {
                PdfDictionary dic = (PdfDictionary) obj;
                if (existsName(dic, PdfName.TYPE, PdfName.FONT)) {
                    if (existsName(dic, PdfName.SUBTYPE, PdfName.TYPE1) || existsName(dic, PdfName.SUBTYPE, PdfName.MMTYPE1) || existsName(dic, PdfName.SUBTYPE, PdfName.TRUETYPE)) {
                        String s = getSubsetPrefix(dic);
                        if (s != null) {
                            PdfName newName = new PdfName(BaseFont.createSubsetPrefix() + s.substring(7));
                            dic.put(PdfName.BASEFONT, newName);
                            setXrefPartialObject(k, dic);
                            total++;
                            PdfDictionary fd = dic.getAsDict(PdfName.FONTDESCRIPTOR);
                            if (fd != null) {
                                fd.put(PdfName.FONTNAME, newName);
                            }
                        }
                    } else if (existsName(dic, PdfName.SUBTYPE, PdfName.TYPE0)) {
                        String s2 = getSubsetPrefix(dic);
                        PdfArray arr = dic.getAsArray(PdfName.DESCENDANTFONTS);
                        if (arr != null && !arr.isEmpty() && (sde = getSubsetPrefix((desc = arr.getAsDict(0)))) != null) {
                            String ns = BaseFont.createSubsetPrefix();
                            if (s2 != null) {
                                dic.put(PdfName.BASEFONT, new PdfName(ns + s2.substring(7)));
                            }
                            setXrefPartialObject(k, dic);
                            PdfName newName2 = new PdfName(ns + sde.substring(7));
                            desc.put(PdfName.BASEFONT, newName2);
                            total++;
                            PdfDictionary fd2 = desc.getAsDict(PdfName.FONTDESCRIPTOR);
                            if (fd2 != null) {
                                fd2.put(PdfName.FONTNAME, newName2);
                            }
                        }
                    }
                }
            }
        }
        return total;
    }

    private static PdfArray getNameArray(PdfObject obj) {
        PdfObject obj2;
        PdfObject arr2;
        if (obj == null || (obj2 = getPdfObjectRelease(obj)) == null) {
            return null;
        }
        if (obj2.isArray()) {
            return (PdfArray) obj2;
        }
        if (obj2.isDictionary() && (arr2 = getPdfObjectRelease(((PdfDictionary) obj2).get(PdfName.D))) != null && arr2.isArray()) {
            return (PdfArray) arr2;
        }
        return null;
    }

    public HashMap getNamedDestination() {
        return getNamedDestination(false);
    }

    private HashMap getNamedDestination(boolean keepNames) {
        HashMap names = getNamedDestinationFromNames(keepNames);
        names.putAll(getNamedDestinationFromStrings());
        return names;
    }

    public HashMap getNamedDestinationFromNames() {
        return getNamedDestinationFromNames(false);
    }

    private HashMap getNamedDestinationFromNames(boolean keepNames) {
        HashMap names = new LinkedHashMap();
        if (this.catalog.get(PdfName.DESTS) != null) {
            PdfDictionary dic = (PdfDictionary) getPdfObjectRelease(this.catalog.get(PdfName.DESTS));
            if (dic == null) {
                return names;
            }
            for (PdfName key : dic.getKeys()) {
                PdfArray arr = getNameArray(dic.get(key));
                if (arr != null) {
                    if (keepNames) {
                        names.put(key, arr);
                    } else {
                        String name = PdfName.decodeName(key.toString());
                        names.put(name, arr);
                    }
                }
            }
        }
        return names;
    }

    public HashMap getNamedDestinationFromStrings() {
        PdfDictionary dic;
        PdfDictionary dic2;
        if (this.catalog.get(PdfName.NAMES) != null && (dic = (PdfDictionary) getPdfObjectRelease(this.catalog.get(PdfName.NAMES))) != null && (dic2 = (PdfDictionary) getPdfObjectRelease(dic.get(PdfName.DESTS))) != null) {
            HashMap names = PdfNameTree.readTree(dic2);
            Iterator it = names.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                PdfArray arr = getNameArray((PdfObject) entry.getValue());
                if (arr != null) {
                    entry.setValue(arr);
                } else {
                    it.remove();
                }
            }
            return names;
        }
        return new LinkedHashMap();
    }

    private boolean replaceNamedDestination(PdfObject obj, HashMap names) {
        PdfObject obj2 = getPdfObject(obj);
        int objIdx = this.lastXrefPartial;
        releaseLastXrefPartial();
        if (obj2 != null && obj2.isDictionary()) {
            PdfObject ob2 = getPdfObjectRelease(((PdfDictionary) obj2).get(PdfName.DEST));
            Object name = null;
            if (ob2 != null) {
                if (ob2.isName()) {
                    name = ob2;
                } else if (ob2.isString()) {
                    name = ob2.toString();
                }
                PdfArray dest = (PdfArray) names.get(name);
                if (dest != null) {
                    ((PdfDictionary) obj2).put(PdfName.DEST, dest);
                    setXrefPartialObject(objIdx, obj2);
                    return true;
                }
                return false;
            }
            PdfObject ob3 = getPdfObject(((PdfDictionary) obj2).get(PdfName.A));
            if (ob3 != null) {
                int obj2Idx = this.lastXrefPartial;
                releaseLastXrefPartial();
                PdfDictionary dic = (PdfDictionary) ob3;
                PdfName type = (PdfName) getPdfObjectRelease(dic.get(PdfName.S));
                if (PdfName.GOTO.equals(type)) {
                    PdfObject ob4 = getPdfObjectRelease(dic.get(PdfName.D));
                    if (ob4 != null) {
                        if (ob4.isName()) {
                            name = ob4;
                        } else if (ob4.isString()) {
                            name = ob4.toString();
                        }
                    }
                    PdfArray dest2 = (PdfArray) names.get(name);
                    if (dest2 != null) {
                        dic.put(PdfName.D, dest2);
                        setXrefPartialObject(obj2Idx, ob3);
                        setXrefPartialObject(objIdx, obj2);
                        return true;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    void removeFields() {
        this.pageRefs.resetReleasePage();
        for (int k = 1; k <= this.pageRefs.size(); k++) {
            PdfDictionary page = this.pageRefs.getPageN(k);
            PdfArray annots = page.getAsArray(PdfName.ANNOTS);
            if (annots == null) {
                this.pageRefs.releasePage(k);
            } else {
                int j = 0;
                while (j < annots.size()) {
                    PdfObject obj = getPdfObjectRelease(annots.getPdfObject(j));
                    if (obj != null && obj.isDictionary()) {
                        PdfDictionary annot = (PdfDictionary) obj;
                        if (PdfName.WIDGET.equals(annot.get(PdfName.SUBTYPE))) {
                            int i = j;
                            j--;
                            annots.remove(i);
                        }
                    }
                    j++;
                }
                if (annots.isEmpty()) {
                    page.remove(PdfName.ANNOTS);
                } else {
                    this.pageRefs.releasePage(k);
                }
            }
        }
        this.catalog.remove(PdfName.ACROFORM);
        this.pageRefs.resetReleasePage();
    }

    private void iterateBookmarks(PdfObject outlineRef, HashMap names) {
        while (outlineRef != null) {
            replaceNamedDestination(outlineRef, names);
            PdfDictionary outline = (PdfDictionary) getPdfObjectRelease(outlineRef);
            PdfObject first = outline.get(PdfName.FIRST);
            if (first != null) {
                iterateBookmarks(first, names);
            }
            outlineRef = outline.get(PdfName.NEXT);
        }
    }

    void consolidateNamedDestinations() {
        if (this.consolidateNamedDestinations) {
            return;
        }
        this.consolidateNamedDestinations = true;
        HashMap names = getNamedDestination(true);
        if (names.isEmpty()) {
            return;
        }
        for (int k = 1; k <= this.pageRefs.size(); k++) {
            PdfDictionary page = this.pageRefs.getPageN(k);
            PdfObject annotsRef = page.get(PdfName.ANNOTS);
            PdfArray annots = (PdfArray) getPdfObject(annotsRef);
            int annotIdx = this.lastXrefPartial;
            releaseLastXrefPartial();
            if (annots == null) {
                this.pageRefs.releasePage(k);
            } else {
                boolean commitAnnots = false;
                for (int an = 0; an < annots.size(); an++) {
                    PdfObject objRef = annots.getPdfObject(an);
                    if (replaceNamedDestination(objRef, names) && !objRef.isIndirect()) {
                        commitAnnots = true;
                    }
                }
                if (commitAnnots) {
                    setXrefPartialObject(annotIdx, annots);
                }
                if (!commitAnnots || annotsRef.isIndirect()) {
                    this.pageRefs.releasePage(k);
                }
            }
        }
        PdfDictionary outlines = (PdfDictionary) getPdfObjectRelease(this.catalog.get(PdfName.OUTLINES));
        if (outlines == null) {
            return;
        }
        iterateBookmarks(outlines.get(PdfName.FIRST), names);
    }

    private static PdfDictionary duplicatePdfDictionary(PdfDictionary original, PdfDictionary copy, PdfReader newReader) {
        if (copy == null) {
            copy = new PdfDictionary();
        }
        for (Object element : original.getKeys()) {
            PdfName key = (PdfName) element;
            copy.put(key, duplicatePdfObject(original.get(key), newReader));
        }
        return copy;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PdfObject duplicatePdfObject(PdfObject original, PdfReader newReader) {
        if (original == null) {
            return null;
        }
        switch (original.type()) {
            case 5:
                PdfArray arr = new PdfArray();
                Iterator it = ((PdfArray) original).listIterator();
                while (it.hasNext()) {
                    arr.add(duplicatePdfObject(it.next(), newReader));
                }
                return arr;
            case 6:
                return duplicatePdfDictionary((PdfDictionary) original, null, newReader);
            case 7:
                PRStream org2 = (PRStream) original;
                PRStream stream = new PRStream(org2, (PdfDictionary) null, newReader);
                duplicatePdfDictionary(org2, stream, newReader);
                return stream;
            case 8:
            case 9:
            default:
                return original;
            case 10:
                PRIndirectReference org3 = (PRIndirectReference) original;
                return new PRIndirectReference(newReader, org3.getNumber(), org3.getGeneration());
        }
    }

    public void close() {
        if (!this.partial) {
            return;
        }
        try {
            this.tokens.close();
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    private void removeUnusedNode(PdfObject obj, boolean[] hits) {
        int num;
        int num2;
        Stack state = new Stack();
        state.push(obj);
        while (!state.empty()) {
            Object current = state.pop();
            if (current != null) {
                ArrayList ar = null;
                PdfDictionary dic = null;
                PdfName[] keys = null;
                Object[] objs = null;
                int idx = 0;
                if (current instanceof PdfObject) {
                    PdfObject obj2 = (PdfObject) current;
                    switch (obj2.type()) {
                        case 5:
                            ar = ((PdfArray) obj2).getArrayList();
                            break;
                        case 6:
                        case 7:
                            dic = (PdfDictionary) obj2;
                            keys = new PdfName[dic.size()];
                            dic.getKeys().toArray(keys);
                            break;
                        case 8:
                        case 9:
                        default:
                            continue;
                        case 10:
                            PRIndirectReference ref = (PRIndirectReference) obj2;
                            int num3 = ref.getNumber();
                            if (hits[num3]) {
                                continue;
                            } else {
                                hits[num3] = true;
                                state.push(getPdfObjectRelease(ref));
                            }
                            break;
                    }
                } else {
                    objs = (Object[]) current;
                    if (objs[0] instanceof ArrayList) {
                        ar = (ArrayList) objs[0];
                        idx = ((Integer) objs[1]).intValue();
                    } else {
                        keys = (PdfName[]) objs[0];
                        dic = (PdfDictionary) objs[1];
                        idx = ((Integer) objs[2]).intValue();
                    }
                }
                if (ar != null) {
                    for (int k = idx; k < ar.size(); k++) {
                        PdfObject v = (PdfObject) ar.get(k);
                        if (v.isIndirect() && ((num = ((PRIndirectReference) v).getNumber()) >= this.xrefObj.size() || (!this.partial && this.xrefObj.get(num) == null))) {
                            ar.set(k, PdfNull.PDFNULL);
                        } else {
                            if (objs == null) {
                                state.push(new Object[]{ar, Integer.valueOf(k + 1)});
                            } else {
                                objs[1] = Integer.valueOf(k + 1);
                                state.push(objs);
                            }
                            state.push(v);
                            break;
                        }
                    }
                } else {
                    for (int k2 = idx; k2 < keys.length; k2++) {
                        PdfName key = keys[k2];
                        PdfObject v2 = dic.get(key);
                        if (v2.isIndirect() && ((num2 = ((PRIndirectReference) v2).getNumber()) >= this.xrefObj.size() || (!this.partial && this.xrefObj.get(num2) == null))) {
                            dic.put(key, PdfNull.PDFNULL);
                        } else {
                            if (objs == null) {
                                state.push(new Object[]{keys, dic, Integer.valueOf(k2 + 1)});
                            } else {
                                objs[2] = Integer.valueOf(k2 + 1);
                                state.push(objs);
                            }
                            state.push(v2);
                            break;
                        }
                    }
                }
            }
        }
    }

    private int removeUnusedObjects() {
        boolean[] hits = new boolean[this.xrefObj.size()];
        removeUnusedNode(this.trailer, hits);
        int total = 0;
        if (this.partial) {
            for (int k = 1; k < hits.length; k++) {
                if (!hits[k]) {
                    this.xref[k * 2] = -1;
                    this.xref[(k * 2) + 1] = 0;
                    this.xrefObj.set(k, null);
                    total++;
                }
            }
        } else {
            for (int k2 = 1; k2 < hits.length; k2++) {
                if (!hits[k2]) {
                    this.xrefObj.set(k2, null);
                    total++;
                }
            }
        }
        return total;
    }

    public AcroFields getAcroFields() {
        return new AcroFields(this, null);
    }

    private String getJavaScript(RandomAccessFileOrArray file) throws IOException {
        PdfDictionary js;
        PdfObject obj;
        PdfDictionary names = (PdfDictionary) getPdfObjectRelease(this.catalog.get(PdfName.NAMES));
        if (names == null || (js = (PdfDictionary) getPdfObjectRelease(names.get(PdfName.JAVASCRIPT))) == null) {
            return null;
        }
        HashMap jscript = PdfNameTree.readTree(js);
        String[] sortedNames = (String[]) jscript.keySet().toArray(new String[jscript.size()]);
        Arrays.sort(sortedNames);
        StringBuffer buf = new StringBuffer();
        for (String sortedName : sortedNames) {
            PdfDictionary j = (PdfDictionary) getPdfObjectRelease((PdfIndirectReference) jscript.get(sortedName));
            if (j != null && (obj = getPdfObjectRelease(j.get(PdfName.JS))) != null) {
                if (obj.isString()) {
                    buf.append(((PdfString) obj).toUnicodeString()).append('\n');
                } else if (obj.isStream()) {
                    byte[] bytes = getStreamBytes((PRStream) obj, file);
                    if (bytes.length >= 2 && bytes[0] == -2 && bytes[1] == -1) {
                        buf.append(PdfEncodings.convertToString(bytes, PdfObject.TEXT_UNICODE));
                    } else {
                        buf.append(PdfEncodings.convertToString(bytes, ISignatureFormatDetector.FORMAT_PDF));
                    }
                    buf.append('\n');
                }
            }
        }
        return buf.toString();
    }

    public String getJavaScript() throws IOException {
        RandomAccessFileOrArray rf = getSafeFile();
        try {
            rf.reOpen();
            return getJavaScript(rf);
        } finally {
            try {
                rf.close();
            } catch (Exception e) {
            }
        }
    }

    void selectPages(List pagesToKeep) {
        this.pageRefs.selectPages(pagesToKeep);
        removeUnusedObjects();
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfViewerPreferences
    public void setViewerPreferences(int preferences) {
        this.viewerPreferences.setViewerPreferences(preferences);
        setViewerPreferences(this.viewerPreferences);
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfViewerPreferences
    public void addViewerPreference(PdfName key, PdfObject value) {
        this.viewerPreferences.addViewerPreference(key, value);
        setViewerPreferences(this.viewerPreferences);
    }

    void setViewerPreferences(PdfViewerPreferencesImp vp) {
        vp.addToCatalog(this.catalog);
    }

    public int getSimpleViewerPreferences() {
        return PdfViewerPreferencesImp.getViewerPreferences(this.catalog).getPageLayoutAndMode();
    }

    public boolean isAppendable() {
        return this.appendable;
    }

    public void setAppendable(boolean appendable) {
        this.appendable = appendable;
        if (appendable) {
            getPdfObject(this.trailer.get(PdfName.ROOT));
        }
    }

    public boolean isNewXrefType() {
        return this.newXrefType;
    }

    public int getFileLength() {
        return this.fileLength;
    }

    public boolean isHybridXref() {
        return this.hybridXref;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfReader$PageRefs.class */
    static class PageRefs {
        private final PdfReader reader;
        private IntHashtable refsp;
        private ArrayList refsn;
        private ArrayList pageInh;
        private int lastPageRead;
        private int sizep;
        private boolean keepPages;

        private PageRefs(PdfReader reader) throws IOException {
            this.lastPageRead = -1;
            this.reader = reader;
            if (reader.partial) {
                this.refsp = new IntHashtable();
                PdfNumber npages = (PdfNumber) PdfReader.getPdfObjectRelease(reader.rootPages.get(PdfName.COUNT));
                this.sizep = npages.intValue();
                return;
            }
            readPages();
        }

        private PageRefs(PageRefs other, PdfReader reader) {
            this.lastPageRead = -1;
            this.reader = reader;
            this.sizep = other.sizep;
            if (other.refsn != null) {
                this.refsn = new ArrayList(other.refsn);
                for (int k = 0; k < this.refsn.size(); k++) {
                    this.refsn.set(k, PdfReader.duplicatePdfObject((PdfObject) this.refsn.get(k), reader));
                }
                return;
            }
            this.refsp = (IntHashtable) other.refsp.clone();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int size() {
            if (this.refsn != null) {
                return this.refsn.size();
            }
            return this.sizep;
        }

        void readPages() throws IOException {
            if (this.refsn != null) {
                return;
            }
            this.refsp = null;
            this.refsn = new ArrayList();
            this.pageInh = new ArrayList();
            iteratePages((PRIndirectReference) this.reader.catalog.get(PdfName.PAGES));
            this.pageInh = null;
            this.reader.rootPages.put(PdfName.COUNT, new PdfNumber(this.refsn.size()));
        }

        public PdfDictionary getPageN(int pageNum) {
            PRIndirectReference ref = getPageOrigRef(pageNum);
            return (PdfDictionary) PdfReader.getPdfObject(ref);
        }

        public PdfDictionary getPageNRelease(int pageNum) {
            PdfDictionary page = getPageN(pageNum);
            releasePage(pageNum);
            return page;
        }

        public PRIndirectReference getPageOrigRef(int pageNum) {
            try {
                int pageNum2 = pageNum - 1;
                if (pageNum2 < 0 || pageNum2 >= size()) {
                    return null;
                }
                if (this.refsn != null) {
                    return (PRIndirectReference) this.refsn.get(pageNum2);
                }
                int n = this.refsp.get(pageNum2);
                if (n == 0) {
                    PRIndirectReference ref = getSinglePage(pageNum2);
                    if (this.reader.lastXrefPartial == -1) {
                        this.lastPageRead = -1;
                    } else {
                        this.lastPageRead = pageNum2;
                    }
                    this.reader.lastXrefPartial = -1;
                    this.refsp.put(pageNum2, ref.getNumber());
                    if (this.keepPages) {
                        this.lastPageRead = -1;
                    }
                    return ref;
                }
                if (this.lastPageRead != pageNum2) {
                    this.lastPageRead = -1;
                }
                if (this.keepPages) {
                    this.lastPageRead = -1;
                }
                return new PRIndirectReference(this.reader, n);
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void keepPages() {
            if (this.refsp == null || this.keepPages) {
                return;
            }
            this.keepPages = true;
            this.refsp.clear();
        }

        public void releasePage(int pageNum) {
            int pageNum2;
            if (this.refsp == null || (pageNum2 = pageNum - 1) < 0 || pageNum2 >= size() || pageNum2 != this.lastPageRead) {
                return;
            }
            this.lastPageRead = -1;
            this.reader.lastXrefPartial = this.refsp.get(pageNum2);
            this.reader.releaseLastXrefPartial();
            this.refsp.remove(pageNum2);
        }

        public void resetReleasePage() {
            if (this.refsp == null) {
                return;
            }
            this.lastPageRead = -1;
        }

        void insertPage(int pageNum, PRIndirectReference ref) {
            int pageNum2 = pageNum - 1;
            if (this.refsn != null) {
                if (pageNum2 >= this.refsn.size()) {
                    this.refsn.add(ref);
                    return;
                } else {
                    this.refsn.add(pageNum2, ref);
                    return;
                }
            }
            this.sizep++;
            this.lastPageRead = -1;
            if (pageNum2 >= size()) {
                this.refsp.put(size(), ref.getNumber());
                return;
            }
            IntHashtable refs2 = new IntHashtable((this.refsp.size() + 1) * 2);
            Iterator it = this.refsp.getEntryIterator();
            while (it.hasNext()) {
                IntHashtable.Entry entry = (IntHashtable.Entry) it.next();
                int p = entry.getKey();
                refs2.put(p >= pageNum2 ? p + 1 : p, entry.getValue());
            }
            refs2.put(pageNum2, ref.getNumber());
            this.refsp = refs2;
        }

        private void pushPageAttributes(PdfDictionary nodePages) {
            PdfDictionary dic = new PdfDictionary();
            if (!this.pageInh.isEmpty()) {
                dic.putAll((PdfDictionary) this.pageInh.get(this.pageInh.size() - 1));
            }
            for (PdfName pageInhCandidate : PdfReader.pageInhCandidates) {
                PdfObject obj = nodePages.get(pageInhCandidate);
                if (obj != null) {
                    dic.put(pageInhCandidate, obj);
                }
            }
            this.pageInh.add(dic);
        }

        private void popPageAttributes() {
            this.pageInh.remove(this.pageInh.size() - 1);
        }

        private void iteratePages(PRIndirectReference rpage) throws IOException {
            PdfDictionary page = (PdfDictionary) PdfReader.getPdfObject(rpage);
            PdfArray kidsPR = page.getAsArray(PdfName.KIDS);
            if (kidsPR == null) {
                page.put(PdfName.TYPE, PdfName.PAGE);
                PdfDictionary dic = (PdfDictionary) this.pageInh.get(this.pageInh.size() - 1);
                for (Object element : dic.getKeys()) {
                    PdfName key = (PdfName) element;
                    if (page.get(key) == null) {
                        page.put(key, dic.get(key));
                    }
                }
                if (page.get(PdfName.MEDIABOX) == null) {
                    PdfArray arr = new PdfArray(new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, PageSize.LETTER.getRight(), PageSize.LETTER.getTop()});
                    page.put(PdfName.MEDIABOX, arr);
                }
                this.refsn.add(rpage);
                return;
            }
            page.put(PdfName.TYPE, PdfName.PAGES);
            pushPageAttributes(page);
            for (int k = 0; k < kidsPR.size(); k++) {
                PdfObject obj = kidsPR.getPdfObject(k);
                if (!obj.isIndirect()) {
                    while (k < kidsPR.size()) {
                        kidsPR.remove(k);
                    }
                    break;
                }
                iteratePages((PRIndirectReference) obj);
            }
            popPageAttributes();
        }

        private PRIndirectReference getSinglePage(int n) {
            PdfDictionary acc = new PdfDictionary();
            PdfDictionary top = this.reader.rootPages;
            int base = 0;
            while (true) {
                for (PdfName pageInhCandidate : PdfReader.pageInhCandidates) {
                    PdfObject obj = top.get(pageInhCandidate);
                    if (obj != null) {
                        acc.put(pageInhCandidate, obj);
                    }
                }
                PdfArray kids = (PdfArray) PdfReader.getPdfObjectRelease(top.get(PdfName.KIDS));
                Iterator it = kids.listIterator();
                while (it.hasNext()) {
                    PRIndirectReference ref = (PRIndirectReference) it.next();
                    PdfDictionary dic = (PdfDictionary) PdfReader.getPdfObject(ref);
                    int last = this.reader.lastXrefPartial;
                    PdfObject count = PdfReader.getPdfObjectRelease(dic.get(PdfName.COUNT));
                    this.reader.lastXrefPartial = last;
                    int acn = 1;
                    if (count != null && count.type() == 2) {
                        acn = ((PdfNumber) count).intValue();
                    }
                    if (n >= base + acn) {
                        this.reader.releaseLastXrefPartial();
                        base += acn;
                    } else {
                        if (count != null) {
                            this.reader.releaseLastXrefPartial();
                            top = dic;
                            break;
                        }
                        dic.mergeDifferent(acc);
                        return ref;
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void selectPages(List pagesToKeep) {
            IntHashtable pg = new IntHashtable();
            ArrayList finalPages = new ArrayList();
            int psize = size();
            Iterator it = pagesToKeep.iterator();
            while (it.hasNext()) {
                Integer pi = (Integer) it.next();
                int p = pi.intValue();
                if (p >= 1 && p <= psize && pg.put(p, 1) == 0) {
                    finalPages.add(pi);
                }
            }
            if (this.reader.partial) {
                for (int k = 1; k <= psize; k++) {
                    getPageOrigRef(k);
                    resetReleasePage();
                }
            }
            PRIndirectReference parent = (PRIndirectReference) this.reader.catalog.get(PdfName.PAGES);
            PdfDictionary topPages = (PdfDictionary) PdfReader.getPdfObject(parent);
            ArrayList newPageRefs = new ArrayList(finalPages.size());
            PdfArray kids = new PdfArray();
            for (int k2 = 0; k2 < finalPages.size(); k2++) {
                int p2 = ((Integer) finalPages.get(k2)).intValue();
                PRIndirectReference pref = getPageOrigRef(p2);
                resetReleasePage();
                kids.add(pref);
                newPageRefs.add(pref);
                getPageN(p2).put(PdfName.PARENT, parent);
            }
            AcroFields af = this.reader.getAcroFields();
            boolean removeFields = af.getFields().size() > 0;
            for (int k3 = 1; k3 <= psize; k3++) {
                if (!pg.containsKey(k3)) {
                    if (removeFields) {
                        af.removeFieldsFromPage(k3);
                    }
                    int nref = getPageOrigRef(k3).getNumber();
                    this.reader.xrefObj.set(nref, null);
                    if (this.reader.partial) {
                        this.reader.xref[nref * 2] = -1;
                        this.reader.xref[(nref * 2) + 1] = 0;
                    }
                }
            }
            topPages.put(PdfName.COUNT, new PdfNumber(finalPages.size()));
            topPages.put(PdfName.KIDS, kids);
            this.refsp = null;
            this.refsn = newPageRefs;
        }
    }

    PdfIndirectReference getCryptoRef() {
        if (this.cryptoRef == null) {
            return null;
        }
        return new PdfIndirectReference(0, this.cryptoRef.getNumber(), this.cryptoRef.getGeneration());
    }

    public void removeUsageRights() {
        PdfDictionary perms = this.catalog.getAsDict(PdfName.PERMS);
        if (perms == null) {
            return;
        }
        perms.remove(PdfName.UR);
        perms.remove(PdfName.UR3);
        if (perms.size() == 0) {
            this.catalog.remove(PdfName.PERMS);
        }
    }

    public int getCertificationLevel() {
        PdfDictionary dic;
        PdfArray arr;
        PdfDictionary dic2;
        PdfDictionary dic3;
        PdfNumber p;
        PdfDictionary dic4 = this.catalog.getAsDict(PdfName.PERMS);
        if (dic4 == null || (dic = dic4.getAsDict(PdfName.DOCMDP)) == null || (arr = dic.getAsArray(PdfName.REFERENCE)) == null || arr.size() == 0 || (dic2 = arr.getAsDict(0)) == null || (dic3 = dic2.getAsDict(PdfName.TRANSFORMPARAMS)) == null || (p = dic3.getAsNumber(PdfName.P)) == null) {
            return 0;
        }
        return p.intValue();
    }

    public final boolean isOpenedWithFullPermissions() {
        return !this.encrypted || this.ownerPasswordUsed;
    }

    public int getCryptoMode() {
        if (this.decrypt == null) {
            return -1;
        }
        return this.decrypt.getCryptoMode();
    }

    public boolean isMetadataEncrypted() {
        if (this.decrypt == null) {
            return false;
        }
        return this.decrypt.isMetadataEncrypted();
    }
}
