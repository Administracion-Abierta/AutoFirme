package es.gob.afirma.signers.ooxml;

import es.gob.afirma.core.misc.AOUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/OOXMLUtil.class */
final class OOXMLUtil {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String OOXML_SIGNATURE_RELATIONSHIP_TYPE = "http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/signature";
    private static final String OOXML_SIGNATURE_ORIGIN_RELATIONSHIP_TYPE = "http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/origin";

    private OOXMLUtil() {
    }

    static int countOOXMLSignatures(byte[] bArr) throws ParserConfigurationException, SAXException, IOException {
        Relationship[] oOXMLSignaturesRelationships = getOOXMLSignaturesRelationships(bArr);
        if (oOXMLSignaturesRelationships == null) {
            return 0;
        }
        return oOXMLSignaturesRelationships.length;
    }

    private static Relationship[] getOOXMLSignaturesRelationships(byte[] bArr) throws ParserConfigurationException, SAXException, IOException {
        ArrayList arrayList = new ArrayList();
        try {
            byte[] signaturesRels = getSignaturesRels(bArr, getEntryList(bArr));
            if (signaturesRels == null) {
                throw new IOException("No se ha encontrado el listado de relaciones");
            }
            for (Relationship relationship : new RelationshipsParser(new ByteArrayInputStream(signaturesRels)).getRelationships()) {
                if (OOXML_SIGNATURE_RELATIONSHIP_TYPE.equals(relationship.getType())) {
                    arrayList.add(relationship);
                }
            }
            return (Relationship[]) arrayList.toArray(new Relationship[0]);
        } catch (Exception e) {
            LOGGER.severe("No se pudieron leer las firmas del documento OOXML: " + e);
        }
    }

    static byte[][] getOOXMLSignatures(byte[] bArr) throws ParserConfigurationException, SAXException, IOException {
        byte[] entry;
        ArrayList arrayList = new ArrayList();
        boolean z = false;
        try {
            List<ZipEntry> entryList = getEntryList(bArr);
            byte[] signaturesRels = getSignaturesRels(bArr, entryList);
            if (signaturesRels == null) {
                throw new IOException("No se han contrado relaciones en el documento");
            }
            for (Relationship relationship : new RelationshipsParser(new ByteArrayInputStream(signaturesRels)).getRelationships()) {
                if (OOXML_SIGNATURE_RELATIONSHIP_TYPE.equals(relationship.getType())) {
                    String target = relationship.getTarget();
                    if (hasEntry(entryList, "_xmlsignatures/" + target)) {
                        entry = readEntry(bArr, "_xmlsignatures/" + target);
                    } else if (hasEntry(entryList, "_xmlsignatures\\" + target)) {
                        entry = readEntry(bArr, "_xmlsignatures\\" + target);
                    } else {
                        LOGGER.severe("El documento OOXML no contiene las firmas declaradas");
                        throw new IOException("El documento OOXML no contiene las firmas declaradas");
                    }
                    arrayList.add(entry);
                }
            }
            if (z) {
                return new byte[0][];
            }
            return (byte[][]) arrayList.toArray(new byte[0][]);
        } catch (Exception e) {
            LOGGER.severe("No se pudieron leer las firmas del documento OOXML: " + e);
            z = true;
        }
    }

    private static byte[] getSignaturesRels(byte[] bArr, List<ZipEntry> list) throws IOException {
        byte[] entry;
        if (hasEntry(list, "_rels/.rels")) {
            entry = readEntry(bArr, "_rels/.rels");
        } else if (hasEntry(list, "_rels\\.rels")) {
            entry = readEntry(bArr, "_rels\\.rels");
        } else {
            return null;
        }
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(entry);
            Throwable th = null;
            try {
                try {
                    RelationshipsParser relationshipsParser = new RelationshipsParser(byteArrayInputStream);
                    if (byteArrayInputStream != null) {
                        if (0 != 0) {
                            try {
                                byteArrayInputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            byteArrayInputStream.close();
                        }
                    }
                    byte[] entry2 = null;
                    for (Relationship relationship : relationshipsParser.getRelationships()) {
                        if (OOXML_SIGNATURE_ORIGIN_RELATIONSHIP_TYPE.equals(relationship.getType())) {
                            String strSubstring = relationship.getTarget().substring(0, "_xmlsignatures".length() + 1);
                            String strSubstring2 = relationship.getTarget().substring("_xmlsignatures".length() + 1);
                            if (hasEntry(list, strSubstring + "_rels/" + strSubstring2 + ".rels")) {
                                entry2 = readEntry(bArr, strSubstring + "_rels/" + strSubstring2 + ".rels");
                                break;
                            }
                            if (!hasEntry(list, strSubstring + "_rels\\" + strSubstring2 + ".rels")) {
                                break;
                            }
                            entry2 = readEntry(bArr, strSubstring + "_rels\\" + strSubstring2 + ".rels");
                            break;
                        }
                    }
                    return entry2;
                } catch (Throwable th3) {
                    if (byteArrayInputStream != null) {
                        if (th != null) {
                            try {
                                byteArrayInputStream.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            byteArrayInputStream.close();
                        }
                    }
                    throw th3;
                }
            } catch (Throwable th5) {
                th = th5;
                throw th5;
            }
        } catch (Exception e) {
            LOGGER.severe("Error en la lectura del OOXML: " + e);
            return null;
        }
    }

    /* JADX WARN: Code duplicated, block: B:20:0x0063  */
    /* JADX WARN: Code duplicated, block: B:25:0x007b  */
    /* JADX WARN: Code duplicated, block: B:30:0x0068 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    static List<ZipEntry> getEntryList(byte[] bArr) throws IOException {
        ArrayList arrayList = new ArrayList();
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bArr));
        Throwable th = null;
        while (true) {
            try {
                try {
                    ZipEntry nextEntry = zipInputStream.getNextEntry();
                    if (nextEntry == null) {
                        break;
                    }
                    arrayList.add(nextEntry);
                } catch (Throwable th2) {
                    th = th2;
                    throw th2;
                }
            } catch (Throwable th3) {
                if (zipInputStream != null) {
                    if (th != null) {
                        try {
                            zipInputStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        zipInputStream.close();
                    }
                }
                throw th3;
            }
            if (zipInputStream != null) {
                if (th != null) {
                    zipInputStream.close();
                } else {
                    zipInputStream.close();
                }
            }
            throw th3;
        }
        if (zipInputStream != null) {
            if (0 != 0) {
                try {
                    zipInputStream.close();
                } catch (Throwable th5) {
                    th.addSuppressed(th5);
                }
            } else {
                zipInputStream.close();
            }
        }
        return arrayList;
    }

    static boolean hasEntry(List<ZipEntry> list, String str) {
        boolean z = false;
        for (int i = 0; !z && i < list.size(); i++) {
            if (str.equals(list.get(i).getName())) {
                z = true;
            }
        }
        return z;
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0075  */
    /* JADX WARN: Code duplicated, block: B:29:0x008e  */
    /* JADX WARN: Code duplicated, block: B:36:0x007a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    static byte[] readEntry(byte[] bArr, String str) throws IOException {
        byte[] dataFromInputStream = null;
        boolean z = false;
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bArr));
        Throwable th = null;
        while (!z) {
            try {
                try {
                    ZipEntry nextEntry = zipInputStream.getNextEntry();
                    if (nextEntry == null) {
                        break;
                    }
                    if (str.equals(nextEntry.getName())) {
                        dataFromInputStream = AOUtil.getDataFromInputStream(zipInputStream);
                        z = true;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    throw th2;
                }
            } catch (Throwable th3) {
                if (zipInputStream != null) {
                    if (th != null) {
                        try {
                            zipInputStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        zipInputStream.close();
                    }
                }
                throw th3;
            }
            if (zipInputStream != null) {
                if (th != null) {
                    zipInputStream.close();
                } else {
                    zipInputStream.close();
                }
            }
            throw th3;
        }
        if (zipInputStream != null) {
            if (0 != 0) {
                try {
                    zipInputStream.close();
                } catch (Throwable th5) {
                    th.addSuppressed(th5);
                }
            } else {
                zipInputStream.close();
            }
        }
        return dataFromInputStream;
    }
}
