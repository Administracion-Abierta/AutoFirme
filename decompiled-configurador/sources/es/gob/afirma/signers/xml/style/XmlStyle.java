package es.gob.afirma.signers.xml.style;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.SecureXmlBuilder;
import es.gob.afirma.core.ui.AOUIFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xml/style/XmlStyle.class */
public final class XmlStyle {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String HTTP_PROTOCOL_PREFIX = "http://";
    private static final String HTTPS_PROTOCOL_PREFIX = "https://";
    private Element element;
    private final String type;
    private final String href;
    private final String encoding;

    public XmlStyle() {
        this.element = null;
        this.type = null;
        this.href = null;
        this.encoding = null;
    }

    public XmlStyle(byte[] bArr, boolean z) throws IsInnerlException, IOException, ReferenceIsNotXmlException, CannotDereferenceException {
        this(bArr, z, true);
    }

    public XmlStyle(byte[] bArr, boolean z, boolean z2) throws IsInnerlException, IOException, ReferenceIsNotXmlException, CannotDereferenceException {
        Properties styleSheetHeader = getStyleSheetHeader(new String(bArr));
        this.type = styleSheetHeader.getProperty("type");
        this.href = styleSheetHeader.getProperty("href");
        if (this.type != null && this.href != null) {
            LOGGER.info("Se ha encontrado una hoja de estilo asociada al XML a firmar: tipo=" + this.type + ", referencia=" + this.href);
            Document documentDereferenceStyleSheet = dereferenceStyleSheet(this.href, z, z2);
            if (!this.href.startsWith(HTTP_PROTOCOL_PREFIX) && !this.href.startsWith(HTTPS_PROTOCOL_PREFIX)) {
                this.element = documentDereferenceStyleSheet.getDocumentElement();
            } else {
                this.element = null;
            }
            this.encoding = documentDereferenceStyleSheet.getXmlEncoding();
            return;
        }
        this.encoding = null;
        this.element = null;
    }

    public void setStyleElement(Element element) {
        this.element = element;
    }

    public Element getStyleElement() {
        return this.element;
    }

    public String getStyleType() {
        return this.type;
    }

    public String getStyleHref() {
        return this.href;
    }

    public String getStyleEncoding() {
        return this.encoding;
    }

    private static Properties getStyleSheetHeader(String str) throws IOException {
        Properties properties = new Properties();
        if (str == null) {
            return properties;
        }
        int iIndexOf = str.indexOf("<?xml-stylesheet ");
        if (iIndexOf == -1) {
            return properties;
        }
        String strSubstring = str.substring(iIndexOf);
        properties.load(new ByteArrayInputStream(strSubstring.substring(0, strSubstring.indexOf(62) + 1).replace("<?xml-stylesheet ", "").replace("?>", "").replace(" ", "\n").replace("\"", "").replace("'", "").getBytes()));
        return properties;
    }

    private static Document dereferenceStyleSheet(String str, boolean z, boolean z2) throws IsInnerlException, ReferenceIsNotXmlException, CannotDereferenceException {
        if (str == null || str.isEmpty()) {
            throw new CannotDereferenceException("La hoja de estilo era nula o vacia");
        }
        byte[] dataFromInputStream = null;
        try {
            URI uriCreateURI = AOUtil.createURI(str);
            if (uriCreateURI.getScheme().equalsIgnoreCase("file")) {
                throw new UnsupportedOperationException("No se aceptan dereferenciaciones directas con file://");
            }
            if (!z2 && (uriCreateURI.getScheme().equalsIgnoreCase("http") || uriCreateURI.getScheme().equalsIgnoreCase("https") || uriCreateURI.getScheme().equalsIgnoreCase("ftp") || uriCreateURI.getScheme().equalsIgnoreCase("ftps"))) {
                throw new UnsupportedOperationException("No se acepta la dereferenciacion de direcciones remotas");
            }
            InputStream inputStreamLoadFile = AOUtil.loadFile(uriCreateURI);
            Throwable th = null;
            try {
                try {
                    dataFromInputStream = AOUtil.getDataFromInputStream(inputStreamLoadFile);
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
            String[] strArrSplit = str.replace(File.separator, "/").split("/");
            String str2 = strArrSplit[strArrSplit.length - 1];
            if (str2.startsWith("#")) {
                throw new IsInnerlException(e);
            }
            if (!z && str.startsWith("file://")) {
                if (AOUIFactory.showConfirmDialog(null, XmlStyleMessages.getString("XmlStyle.5"), XmlStyleMessages.getString("XmlStyle.6"), AOUIFactory.OK_CANCEL_OPTION, AOUIFactory.INFORMATION_MESSAGE) == AOUIFactory.OK_OPTION) {
                    try {
                        try {
                            FileInputStream fileInputStream = new FileInputStream(AOUIFactory.getLoadFiles(XmlStyleMessages.getString("XmlStyle.7"), null, str2, null, XmlStyleMessages.getString("XmlStyle.8", str2), false, false, null, null)[0]);
                            Throwable th6 = null;
                            try {
                                dataFromInputStream = AOUtil.getDataFromInputStream(fileInputStream);
                                if (fileInputStream != null) {
                                    if (0 != 0) {
                                        try {
                                            fileInputStream.close();
                                        } catch (Throwable th7) {
                                            th6.addSuppressed(th7);
                                        }
                                    } else {
                                        fileInputStream.close();
                                    }
                                }
                            } catch (Throwable th8) {
                                if (fileInputStream != null) {
                                    if (0 != 0) {
                                        try {
                                            fileInputStream.close();
                                        } catch (Throwable th9) {
                                            th6.addSuppressed(th9);
                                        }
                                    } else {
                                        fileInputStream.close();
                                    }
                                }
                                throw th8;
                            }
                        } catch (Exception e2) {
                            throw new CannotDereferenceException("No se ha podido dereferenciar la hoja de estilo: " + e, e2);
                        }
                    } catch (AOCancelledOperationException e3) {
                        LOGGER.warning("El usuario ha cancelado la seleccion de hoja de estilo: " + e3);
                        throw new CannotDereferenceException("No se ha podido dereferenciar la hoja de estilo: " + e3, e);
                    }
                }
            } else {
                throw new CannotDereferenceException("No se ha podido dereferenciar la hoja de estilo '" + str + "': " + e, e);
            }
        }
        try {
            if (dataFromInputStream != null) {
                return SecureXmlBuilder.getSecureDocumentBuilder().parse(new ByteArrayInputStream(dataFromInputStream));
            }
            throw new CannotDereferenceException("No se ha dereferenciado la hoja de estilo");
        } catch (Exception e4) {
            throw new ReferenceIsNotXmlException(e4);
        }
    }
}
