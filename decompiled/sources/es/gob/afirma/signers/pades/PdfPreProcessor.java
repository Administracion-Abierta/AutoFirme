package es.gob.afirma.signers.pades;

import com.aowagie.text.Annotation;
import com.aowagie.text.DocumentException;
import com.aowagie.text.Image;
import com.aowagie.text.Jpeg;
import com.aowagie.text.Rectangle;
import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.PdfContentByte;
import com.aowagie.text.pdf.PdfReader;
import com.aowagie.text.pdf.PdfStamper;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/PdfPreProcessor.class */
public final class PdfPreProcessor {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final int MAX_PATH_SIZE = 500;
    private static final int LAST_PAGE = -1;
    private static final int ALL_PAGES = 0;
    private static final int FIRST_PAGE = 1;

    private PdfPreProcessor() {
    }

    public static void addMoreInfo(Map<String, String> map, PdfStamper pdfStamper) {
        if (map == null || map.isEmpty()) {
            return;
        }
        pdfStamper.setMoreInfo(map);
    }

    static void attachFile(Properties properties, PdfStamper pdfStamper, boolean z) throws IOException {
        if (properties == null) {
            return;
        }
        if (pdfStamper == null) {
            throw new IllegalArgumentException("No se puede adjuntar un fichero a un PdfStamper nulo");
        }
        String property = properties.getProperty(PdfExtraParams.ATTACH);
        String property2 = properties.getProperty(PdfExtraParams.ATTACH_FILENAME);
        String property3 = properties.getProperty(PdfExtraParams.ATTACH_DESCRIPTION);
        if (property != null && property2 != null) {
            byte[] bArrDecode = null;
            if (!z && property.length() < 500) {
                try {
                    InputStream inputStreamLoadFile = AOUtil.loadFile(AOUtil.createURI(property));
                    Throwable th = null;
                    try {
                        try {
                            bArrDecode = AOUtil.getDataFromInputStream(inputStreamLoadFile);
                            if (inputStreamLoadFile != null) {
                                if (0 != 0) {
                                    try {
                                        inputStreamLoadFile.close();
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                    }
                                } else {
                                    inputStreamLoadFile.close();
                                }
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            throw th3;
                        }
                    } catch (Throwable th4) {
                        if (inputStreamLoadFile != null) {
                            if (th != null) {
                                try {
                                    inputStreamLoadFile.close();
                                } catch (Throwable th5) {
                                    th.addSuppressed(th5);
                                }
                            } else {
                                inputStreamLoadFile.close();
                            }
                        }
                        throw th4;
                    }
                } catch (Exception e) {
                    LOGGER.info("El parametro de adjunto no contiene una ruta valida a un recurso: " + e);
                    bArrDecode = null;
                }
            }
            if (bArrDecode == null) {
                try {
                    bArrDecode = Base64.decode(property);
                } catch (IOException e2) {
                    LOGGER.warning("Se ha indicado un adjunto, pero no estaba en formato Base64, se ignorara : " + e2);
                    return;
                }
            }
            pdfStamper.getWriter().addFileAttachment(property3, bArrDecode, null, property2);
        }
    }

    public static void addImage(byte[] bArr, int i, int i2, int i3, int i4, int i5, String str, PdfStamper pdfStamper) throws IOException {
        try {
            addImage(new Jpeg(bArr), i, i2, i3, i4, i5, str, pdfStamper);
        } catch (DocumentException e) {
            throw new IOException("Error durante la carga de la imagen JPG", e);
        }
    }

    private static void addImage(Image image, int i, int i2, int i3, int i4, int i5, String str, PdfStamper pdfStamper) throws IOException {
        PdfContentByte overContent = pdfStamper.getOverContent(i5);
        if (str != null) {
            try {
                image.setAnnotation(new Annotation(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, str));
            } catch (DocumentException e) {
                throw new IOException("Error durante la insercion de la imagen en el PDF: " + e, e);
            }
        }
        overContent.addImage(image, i, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, i2, i3, i4, false);
    }

    static void addImage(Properties properties, PdfStamper pdfStamper, PdfReader pdfReader, boolean z) throws IOException {
        String property;
        String property2;
        int numberOfPages;
        if (properties == null || pdfStamper == null || (property = properties.getProperty("image")) == null || property.length() < 1) {
            return;
        }
        Image image = getImage(property, z);
        if (image == null) {
            throw new IOException("No se pudo cargar la imagen para agregarla al PDF");
        }
        Rectangle positionOnPage = PdfUtil.getPositionOnPage(properties, "image");
        if (positionOnPage == null || (property2 = properties.getProperty(PdfExtraParams.IMAGE_PAGE)) == null) {
            return;
        }
        try {
            int numberOfPages2 = Integer.parseInt(property2.trim());
            if (numberOfPages2 == -1) {
                numberOfPages2 = pdfReader.getNumberOfPages();
            }
            if (numberOfPages2 == 0) {
                numberOfPages2 = 1;
                numberOfPages = pdfReader.getNumberOfPages();
            } else {
                numberOfPages = numberOfPages2;
            }
            for (int i = numberOfPages2; i <= numberOfPages; i++) {
                addImage(image, (int) positionOnPage.getWidth(), (int) positionOnPage.getHeight(), (int) positionOnPage.getLeft(), (int) positionOnPage.getBottom(), i, (String) null, pdfStamper);
            }
            LOGGER.info("Anadida imagen al PDF antes de la firma");
        } catch (NumberFormatException e) {
            throw new IOException("Se ha indicado un numero de pagina con formato invalido para insertar la imagen (" + property2 + "): " + e, e);
        }
    }

    static Image getImage(String str, boolean z) throws IOException {
        byte[] bArr;
        if (str == null || str.isEmpty()) {
            return null;
        }
        byte[] bArrDecode = null;
        if (!z && str.length() < 500) {
            try {
                InputStream inputStreamLoadFile = AOUtil.loadFile(AOUtil.createURI(str));
                Throwable th = null;
                try {
                    bArrDecode = AOUtil.getDataFromInputStream(inputStreamLoadFile);
                    if (inputStreamLoadFile != null) {
                        if (0 != 0) {
                            try {
                                inputStreamLoadFile.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            inputStreamLoadFile.close();
                        }
                    }
                } catch (Throwable th3) {
                    if (inputStreamLoadFile != null) {
                        if (0 != 0) {
                            try {
                                inputStreamLoadFile.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            inputStreamLoadFile.close();
                        }
                    }
                    throw th3;
                }
            } catch (Exception e) {
                throw new IOException("El parametro de imagen no contiene una ruta valida a un recurso", e);
            }
        }
        if (bArrDecode == null) {
            try {
                bArrDecode = Base64.decode(str);
            } catch (Exception e2) {
                throw new IOException("Se ha proporcionado una imagen de rubrica que no esta codificada en Base64", e2);
            }
        }
        try {
            bArr = (byte[]) Class.forName("es.gob.afirma.ui.utils.ImageUtils").getMethod("normalizeImageToPdf", byte[].class).invoke(null, bArrDecode);
        } catch (Throwable th5) {
            LOGGER.log(Level.WARNING, "No se pudo normalizar la imagen de rubrica. Se agregara tal cual: " + th5);
            bArr = bArrDecode;
        }
        try {
            return new Jpeg(bArr);
        } catch (Exception e3) {
            throw new IOException("Se ha proporcionado una imagen de rubrica que no esta codificada en JPEG", e3);
        }
    }
}
