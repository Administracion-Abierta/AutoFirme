package javax.help.plaf.gtk;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import javax.swing.ImageIcon;

/* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/gtk/GTKCursorFactory.class */
public class GTKCursorFactory {
    private static Cursor onItemCursor;
    private static Cursor dndCursor;
    private static GTKCursorFactory theFactory;
    private static final boolean debug = false;

    public static Cursor getOnItemCursor() {
        debug("getOnItemCursor");
        if (theFactory == null) {
            theFactory = new GTKCursorFactory();
        }
        if (onItemCursor == null) {
            onItemCursor = theFactory.createCursor("OnItemCursor");
        }
        return onItemCursor;
    }

    public static Cursor getDnDCursor() {
        debug("getDnDCursor");
        if (theFactory == null) {
            theFactory = new GTKCursorFactory();
        }
        if (dndCursor == null) {
            dndCursor = theFactory.createCursor("DnDCursor");
        }
        return dndCursor;
    }

    private Cursor createCursor(String str) {
        debug(new StringBuffer().append("CreateCursor for ").append(str).toString());
        InputStream resourceAsStream = getClass().getResourceAsStream(new StringBuffer().append("images/").append(str).append(".properties").toString());
        if (resourceAsStream == null) {
            debug(new StringBuffer().append(getClass().getName()).append("/").append("images/").append(str).append(".properties").append(" not found.").toString());
            return null;
        }
        try {
            PropertyResourceBundle propertyResourceBundle = new PropertyResourceBundle(resourceAsStream);
            String string = propertyResourceBundle.getString("Cursor.File");
            String string2 = propertyResourceBundle.getString("Cursor.HotSpot");
            try {
                InputStream resourceAsStream2 = getClass().getResourceAsStream(string);
                if (resourceAsStream2 == null) {
                    debug(new StringBuffer().append(getClass().getName()).append("/").append(string).append(" not found.").toString());
                    return null;
                }
                BufferedInputStream bufferedInputStream = new BufferedInputStream(resourceAsStream2);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] bArr = new byte[1024];
                while (true) {
                    int i = bufferedInputStream.read(bArr);
                    if (i <= 0) {
                        break;
                    }
                    byteArrayOutputStream.write(bArr, 0, i);
                }
                bufferedInputStream.close();
                byteArrayOutputStream.flush();
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                if (byteArray.length == 0) {
                    debug(new StringBuffer().append("warning: ").append(string).append(" is zero-length").toString());
                    return null;
                }
                ImageIcon imageIcon = new ImageIcon(byteArray);
                int iIndexOf = string2.indexOf(44);
                Point point = new Point(Integer.parseInt(string2.substring(0, iIndexOf)), Integer.parseInt(string2.substring(iIndexOf + 1)));
                debug("Toolkit fetching cursor");
                try {
                    Image image = imageIcon.getImage();
                    int iconWidth = imageIcon.getIconWidth();
                    int iconHeight = imageIcon.getIconHeight();
                    Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                    Dimension bestCursorSize = defaultToolkit.getBestCursorSize(iconWidth, iconHeight);
                    if (bestCursorSize.width > iconWidth || bestCursorSize.height > iconHeight) {
                        try {
                            Image bufferedImage = new BufferedImage(bestCursorSize.width, bestCursorSize.height, 2);
                            bufferedImage.getGraphics().drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
                            image = bufferedImage;
                        } catch (Exception e) {
                        }
                    }
                    return defaultToolkit.createCustomCursor(image, point, str);
                } catch (NoSuchMethodError e2) {
                    return null;
                }
            } catch (IOException e3) {
                debug(e3.toString());
                return null;
            }
        } catch (IOException e4) {
            debug(new StringBuffer().append(getClass().getName()).append("/").append("images/").append(str).append(".properties").append(" invalid.").toString());
            return null;
        } catch (MissingResourceException e5) {
            debug(new StringBuffer().append(getClass().getName()).append("/").append("images/").append(str).append(".properties").append(" invalid.").toString());
            return null;
        }
    }

    private static void debug(String str) {
    }
}
