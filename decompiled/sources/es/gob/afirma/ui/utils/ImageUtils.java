package es.gob.afirma.ui.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/utils/ImageUtils.class */
public class ImageUtils {
    private static Boolean imageIOExist = null;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r8v0, types: [java.io.ByteArrayOutputStream, java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r8v1 */
    /* JADX WARN: Type inference failed for: r8v2 */
    public static byte[] normalizeImageToPdf(byte[] bArr) throws IOException {
        if (!canNormalize()) {
            throw new UnsupportedOperationException("El entorno actual no soporta esta operacion");
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        ?? r8 = 0;
        try {
            try {
                BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
                if (byteArrayInputStream != null) {
                    if (0 != 0) {
                        try {
                            byteArrayInputStream.close();
                        } catch (Throwable th) {
                            r8.addSuppressed(th);
                        }
                    } else {
                        byteArrayInputStream.close();
                    }
                }
                try {
                    try {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        Throwable th2 = null;
                        if (!ImageIO.write(removeAlphaChannel(bufferedImage), "jpg", byteArrayOutputStream)) {
                            throw new IOException("No se ha podido convertir la imagen a JPEG");
                        }
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        if (byteArrayOutputStream != null) {
                            if (0 != 0) {
                                try {
                                    byteArrayOutputStream.close();
                                } catch (Throwable th3) {
                                    th2.addSuppressed(th3);
                                }
                            } else {
                                byteArrayOutputStream.close();
                            }
                        }
                        return byteArray;
                    } catch (Throwable th4) {
                        if (0 != 0) {
                            if (th != null) {
                                try {
                                    r8.close();
                                } catch (Throwable th5) {
                                    th.addSuppressed(th5);
                                }
                            } else {
                                r8.close();
                            }
                        }
                        throw th4;
                    }
                } catch (Exception e) {
                    throw new IOException("No ha podido decodificar la imagen", e);
                }
            } catch (Throwable th6) {
                r8 = th6;
                throw th6;
            }
        } catch (Throwable th7) {
            if (byteArrayInputStream != null) {
                if (r8 != 0) {
                    try {
                        byteArrayInputStream.close();
                    } catch (Throwable th8) {
                        r8.addSuppressed(th8);
                    }
                } else {
                    byteArrayInputStream.close();
                }
            }
            throw th7;
        }
    }

    private static BufferedImage removeAlphaChannel(BufferedImage bufferedImage) {
        if (!bufferedImage.getColorModel().hasAlpha()) {
            return bufferedImage;
        }
        BufferedImage bufferedImage2 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 1);
        Graphics2D graphics2DCreateGraphics = bufferedImage2.createGraphics();
        graphics2DCreateGraphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics2DCreateGraphics.drawImage(bufferedImage, 0, 0, (ImageObserver) null);
        graphics2DCreateGraphics.dispose();
        return bufferedImage2;
    }

    private static boolean canNormalize() {
        if (imageIOExist == null) {
            try {
                Class.forName("javax.imageio.ImageIO", false, ImageUtils.class.getClassLoader());
                imageIOExist = Boolean.TRUE;
            } catch (Exception e) {
                imageIOExist = Boolean.FALSE;
            }
        }
        return imageIOExist.booleanValue();
    }
}
