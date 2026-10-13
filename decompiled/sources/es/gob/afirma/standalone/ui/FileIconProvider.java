package es.gob.afirma.standalone.ui;

import es.gob.afirma.standalone.CommandLineParameters;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/FileIconProvider.class */
public class FileIconProvider {
    private static final String DEFAULT_ICON = "default_icon.png";
    private static final String XML_ICON = "xml_icon.png";
    private static final String PDF_ICON = "pdf_icon.png";
    private static final String TXT_ICON = "txt_icon.png";
    private static final String IMAGE_ICON = "image_icon.png";
    private static final String ODF_ICON = "odf_icon.png";
    private static final String OOXML_ICON = "ooxml_icon.png";
    private static final Map<String, String> fileTypes = new HashMap();

    static {
        fileTypes.put("xml", XML_ICON);
        fileTypes.put("pdf", PDF_ICON);
        fileTypes.put(CommandLineParameters.FORMAT_HASH_DIR_PLAIN, TXT_ICON);
        fileTypes.put("docx", OOXML_ICON);
        fileTypes.put("xlsx", OOXML_ICON);
        fileTypes.put("pptx", OOXML_ICON);
        fileTypes.put("ppsx", OOXML_ICON);
        fileTypes.put("odt", ODF_ICON);
        fileTypes.put("odp", ODF_ICON);
        fileTypes.put("ods", ODF_ICON);
        fileTypes.put("odg", ODF_ICON);
        fileTypes.put("odc", ODF_ICON);
        fileTypes.put(CommandLineParameters.FORMAT_ODF, ODF_ICON);
        fileTypes.put("odb", ODF_ICON);
        fileTypes.put("odi", ODF_ICON);
        fileTypes.put("odm", ODF_ICON);
        fileTypes.put("jpg", IMAGE_ICON);
        fileTypes.put("jpeg", IMAGE_ICON);
        fileTypes.put("png", IMAGE_ICON);
        fileTypes.put("gif", IMAGE_ICON);
    }

    private FileIconProvider() {
    }

    public static BufferedImage getIcon(String str) throws IOException {
        if (str == null || !fileTypes.containsKey(str)) {
            return loadIconImage(DEFAULT_ICON);
        }
        return loadIconImage(fileTypes.get(str));
    }

    private static BufferedImage loadIconImage(String str) throws IOException {
        try {
            InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("resources/file_icons/" + str);
            Throwable th = null;
            try {
                try {
                    BufferedImage bufferedImage = ImageIO.read(systemResourceAsStream);
                    if (systemResourceAsStream != null) {
                        if (0 != 0) {
                            try {
                                systemResourceAsStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            systemResourceAsStream.close();
                        }
                    }
                    return bufferedImage;
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (systemResourceAsStream != null) {
                    if (th != null) {
                        try {
                            systemResourceAsStream.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        systemResourceAsStream.close();
                    }
                }
                throw th4;
            }
        } catch (Exception e) {
            throw new IOException("No se pudo cargar la imagen del icono " + str, e);
        }
    }
}
