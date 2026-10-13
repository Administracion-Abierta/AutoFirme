package com.twelvemonkeys.imageio;

import com.twelvemonkeys.image.BufferedImageIcon;
import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import com.twelvemonkeys.imageio.util.IIOUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.Border;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/ImageReaderBase.class */
public abstract class ImageReaderBase extends ImageReader {
    private static final Point ORIGIN = new Point(0, 0);
    protected ImageInputStream imageInput;

    protected abstract void resetMembers();

    protected ImageReaderBase(ImageReaderSpi imageReaderSpi) {
        super(imageReaderSpi);
    }

    public void setInput(Object obj, boolean z, boolean z2) {
        resetMembers();
        super.setInput(obj, z, z2);
        if (obj instanceof ImageInputStream) {
            this.imageInput = (ImageInputStream) obj;
        } else {
            this.imageInput = null;
        }
    }

    public void dispose() {
        resetMembers();
        super.dispose();
    }

    public void reset() {
        resetMembers();
        super.reset();
    }

    public IIOMetadata getImageMetadata(int i) throws IOException {
        return null;
    }

    public IIOMetadata getStreamMetadata() throws IOException {
        return null;
    }

    public int getNumImages(boolean z) throws IOException {
        assertInput();
        return 1;
    }

    protected void checkBounds(int i) throws IOException {
        assertInput();
        if (i < getMinIndex()) {
            throw new IndexOutOfBoundsException("index < minIndex");
        }
        int numImages = getNumImages(false);
        if (numImages != -1 && i >= numImages) {
            throw new IndexOutOfBoundsException("index >= numImages (" + i + " >= " + numImages + ")");
        }
    }

    protected void assertInput() {
        if (getInput() == null) {
            throw new IllegalStateException("getInput() == null");
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public static BufferedImage getDestination(ImageReadParam imageReadParam, Iterator<ImageTypeSpecifier> it, int i, int i2) throws IIOException {
        if (it == null || !it.hasNext()) {
            throw new IllegalArgumentException("imageTypes null or empty!");
        }
        ImageTypeSpecifier next = null;
        if (imageReadParam != null) {
            BufferedImage destination = imageReadParam.getDestination();
            if (destination != null) {
                boolean z = false;
                while (it.hasNext()) {
                    ImageTypeSpecifier next2 = it.next();
                    int bufferedImageType = next2.getBufferedImageType();
                    if (bufferedImageType != 0 && bufferedImageType == destination.getType()) {
                        z = true;
                        break;
                    }
                    if (next2.getSampleModel().getTransferType() == destination.getSampleModel().getTransferType() && Arrays.equals(next2.getSampleModel().getSampleSize(), destination.getSampleModel().getSampleSize()) && next2.getNumBands() <= destination.getSampleModel().getNumBands()) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    throw new IIOException(String.format("Destination image from ImageReadParam does not match legal imageTypes from reader: %s", destination));
                }
                return destination;
            }
            next = imageReadParam.getDestinationType();
        }
        if (next == null) {
            next = it.next();
        } else {
            boolean z2 = false;
            while (it.hasNext()) {
                if (it.next().equals(next)) {
                    z2 = true;
                    break;
                }
            }
            if (!z2) {
                throw new IIOException(String.format("Destination type from ImageReadParam does not match legal imageTypes from reader: %s", next));
            }
        }
        Rectangle rectangle = new Rectangle(0, 0, 0, 0);
        Rectangle rectangle2 = new Rectangle(0, 0, 0, 0);
        computeRegions(imageReadParam, i, i2, null, rectangle, rectangle2);
        int i3 = rectangle2.x + rectangle2.width;
        int i4 = rectangle2.y + rectangle2.height;
        long j = ((long) i3) * ((long) i4);
        if (j > 2147483647L) {
            throw new IIOException(String.format("destination width * height > Integer.MAX_VALUE: %d", Long.valueOf(j)));
        }
        long numDataElements = j * ((long) next.getSampleModel().getNumDataElements());
        if (numDataElements > 2147483647L) {
            throw new IIOException(String.format("destination width * height * samplesPerPixel > Integer.MAX_VALUE: %d", Long.valueOf(numDataElements)));
        }
        return next.createBufferedImage(i3, i4);
    }

    protected static BufferedImage fakeAOI(BufferedImage bufferedImage, ImageReadParam imageReadParam) {
        return IIOUtil.fakeAOI(bufferedImage, getSourceRegion(imageReadParam, bufferedImage.getWidth(), bufferedImage.getHeight()));
    }

    protected static Image fakeSubsampling(Image image, ImageReadParam imageReadParam) {
        return IIOUtil.fakeSubsampling(image, imageReadParam);
    }

    protected static boolean hasExplicitDestination(ImageReadParam imageReadParam) {
        return (imageReadParam == null || (imageReadParam.getDestination() == null && imageReadParam.getDestinationType() == null && ORIGIN.equals(imageReadParam.getDestinationOffset()))) ? false : true;
    }

    public static void main(String[] strArr) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(strArr[0]));
        if (bufferedImage == null) {
            System.err.println("Supported formats: " + Arrays.toString(IIOUtil.getNormalizedReaderFormatNames()));
            System.exit(1);
        }
        showIt(bufferedImage, strArr[0]);
    }

    protected static void showIt(final BufferedImage bufferedImage, final String str) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() { // from class: com.twelvemonkeys.imageio.ImageReaderBase.1
                @Override // java.lang.Runnable
                public void run() {
                    JFrame jFrame = new JFrame(str);
                    jFrame.getRootPane().getActionMap().put("window-close", new AbstractAction() { // from class: com.twelvemonkeys.imageio.ImageReaderBase.1.1
                        public void actionPerformed(ActionEvent actionEvent) {
                            Window windowAncestor = SwingUtilities.getWindowAncestor((Component) actionEvent.getSource());
                            windowAncestor.setVisible(false);
                            windowAncestor.dispose();
                        }
                    });
                    jFrame.getRootPane().getInputMap().put(KeyStroke.getKeyStroke(87, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "window-close");
                    jFrame.addWindowListener(new ExitIfNoWindowPresentHandler());
                    jFrame.setDefaultCloseOperation(2);
                    jFrame.setLocationByPlatform(true);
                    JPanel jPanel = new JPanel(new BorderLayout());
                    JScrollPane jScrollPane = new JScrollPane(bufferedImage != null ? new ImageLabel(bufferedImage) : new JLabel("(no image data)", 0));
                    jScrollPane.setBorder((Border) null);
                    jPanel.add(jScrollPane);
                    jFrame.setContentPane(jPanel);
                    jFrame.pack();
                    jFrame.setVisible(true);
                }
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e2) {
            if (e2.getCause() instanceof RuntimeException) {
                throw ((RuntimeException) e2.getCause());
            }
            throw new RuntimeException(e2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/ImageReaderBase$ImageLabel.class */
    private static class ImageLabel extends JLabel {
        static final String ZOOM_IN = "zoom-in";
        static final String ZOOM_OUT = "zoom-out";
        static final String ZOOM_ACTUAL = "zoom-actual";
        static final String ZOOM_FIT = "zoom-fit";
        private BufferedImage image;
        Paint backgroundPaint;
        final Paint checkeredBG;
        final Color defaultBG;

        public ImageLabel(BufferedImage bufferedImage) {
            super(new BufferedImageIcon(bufferedImage));
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(1));
            this.image = bufferedImage;
            this.checkeredBG = createTexture();
            this.defaultBG = getDefaultBackground(bufferedImage);
            this.backgroundPaint = this.defaultBG != null ? this.defaultBG : this.checkeredBG;
            setupActions();
            setComponentPopupMenu(createPopupMenu());
            addMouseListener(new MouseAdapter() { // from class: com.twelvemonkeys.imageio.ImageReaderBase.ImageLabel.1
                public void mouseClicked(MouseEvent mouseEvent) {
                    if (mouseEvent.isPopupTrigger()) {
                        ImageLabel.this.getComponentPopupMenu().show(ImageLabel.this, mouseEvent.getX(), mouseEvent.getY());
                    }
                }
            });
            setTransferHandler(new TransferHandler() { // from class: com.twelvemonkeys.imageio.ImageReaderBase.ImageLabel.2
                public int getSourceActions(JComponent jComponent) {
                    return 1;
                }

                protected Transferable createTransferable(JComponent jComponent) {
                    return new ImageTransferable(ImageLabel.this.image);
                }

                public boolean importData(JComponent jComponent, Transferable transferable) {
                    if (canImport(jComponent, transferable.getTransferDataFlavors())) {
                        try {
                            Image image = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
                            ImageLabel.this.image = ImageUtil.toBuffered(image);
                            ImageLabel.this.setIcon(new BufferedImageIcon(ImageLabel.this.image));
                            return true;
                        } catch (UnsupportedFlavorException | IOException e) {
                            return false;
                        }
                    }
                    return false;
                }

                public boolean canImport(JComponent jComponent, DataFlavor[] dataFlavorArr) {
                    for (DataFlavor dataFlavor : dataFlavorArr) {
                        if (dataFlavor.equals(DataFlavor.imageFlavor)) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        private void setupActions() {
            bindAction(new ZoomAction("Zoom in", 2.0d), ZOOM_IN, KeyStroke.getKeyStroke('+'), KeyStroke.getKeyStroke(TIFF.TAG_OLD_JPEG_AC_TABLES, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), KeyStroke.getKeyStroke(107, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            bindAction(new ZoomAction("Zoom out", 0.5d), ZOOM_OUT, KeyStroke.getKeyStroke('-'), KeyStroke.getKeyStroke(45, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), KeyStroke.getKeyStroke(109, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            bindAction(new ZoomAction(this, "Zoom actual"), ZOOM_ACTUAL, KeyStroke.getKeyStroke('0'), KeyStroke.getKeyStroke(48, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            bindAction(new ZoomToFitAction("Zoom fit"), ZOOM_FIT, KeyStroke.getKeyStroke('9'), KeyStroke.getKeyStroke(57, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            bindAction(TransferHandler.getCopyAction(), (String) TransferHandler.getCopyAction().getValue("Name"), KeyStroke.getKeyStroke(67, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            bindAction(TransferHandler.getPasteAction(), (String) TransferHandler.getPasteAction().getValue("Name"), KeyStroke.getKeyStroke(86, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }

        private void bindAction(Action action, String str, KeyStroke... keyStrokeArr) {
            for (KeyStroke keyStroke : keyStrokeArr) {
                getInputMap(2).put(keyStroke, str);
            }
            getActionMap().put(str, action);
        }

        private JPopupMenu createPopupMenu() {
            JPopupMenu jPopupMenu = new JPopupMenu();
            jPopupMenu.add(getActionMap().get(ZOOM_FIT));
            jPopupMenu.add(getActionMap().get(ZOOM_ACTUAL));
            jPopupMenu.add(getActionMap().get(ZOOM_IN));
            jPopupMenu.add(getActionMap().get(ZOOM_OUT));
            jPopupMenu.addSeparator();
            ButtonGroup buttonGroup = new ButtonGroup();
            JMenu jMenu = new JMenu("Background");
            jPopupMenu.add(jMenu);
            ChangeBackgroundAction changeBackgroundAction = new ChangeBackgroundAction("Checkered", this.checkeredBG);
            changeBackgroundAction.putValue("SwingSelectedKey", Boolean.valueOf(this.backgroundPaint == this.checkeredBG));
            addCheckBoxItem(changeBackgroundAction, jMenu, buttonGroup);
            jMenu.addSeparator();
            addCheckBoxItem(new ChangeBackgroundAction("White", Color.WHITE), jMenu, buttonGroup);
            addCheckBoxItem(new ChangeBackgroundAction("Light", Color.LIGHT_GRAY), jMenu, buttonGroup);
            addCheckBoxItem(new ChangeBackgroundAction("Gray", Color.GRAY), jMenu, buttonGroup);
            addCheckBoxItem(new ChangeBackgroundAction("Dark", Color.DARK_GRAY), jMenu, buttonGroup);
            addCheckBoxItem(new ChangeBackgroundAction("Black", Color.BLACK), jMenu, buttonGroup);
            jMenu.addSeparator();
            ChooseBackgroundAction chooseBackgroundAction = new ChooseBackgroundAction("Choose...", this.defaultBG != null ? this.defaultBG : new Color(16737792));
            chooseBackgroundAction.putValue("SwingSelectedKey", Boolean.valueOf(this.backgroundPaint == this.defaultBG));
            addCheckBoxItem(chooseBackgroundAction, jMenu, buttonGroup);
            return jPopupMenu;
        }

        private void addCheckBoxItem(Action action, JMenu jMenu, ButtonGroup buttonGroup) {
            JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(action);
            buttonGroup.add(jCheckBoxMenuItem);
            jMenu.add(jCheckBoxMenuItem);
        }

        private static Color getDefaultBackground(BufferedImage bufferedImage) {
            IndexColorModel colorModel;
            int transparentPixel;
            if ((bufferedImage.getColorModel() instanceof IndexColorModel) && (transparentPixel = (colorModel = bufferedImage.getColorModel()).getTransparentPixel()) >= 0) {
                return new Color(colorModel.getRGB(transparentPixel), false);
            }
            return null;
        }

        private static Paint createTexture() {
            BufferedImage bufferedImageCreateCompatibleImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(20, 20);
            Graphics2D graphics2DCreateGraphics = bufferedImageCreateCompatibleImage.createGraphics();
            try {
                graphics2DCreateGraphics.setColor(Color.LIGHT_GRAY);
                graphics2DCreateGraphics.fillRect(0, 0, bufferedImageCreateCompatibleImage.getWidth(), bufferedImageCreateCompatibleImage.getHeight());
                graphics2DCreateGraphics.setColor(Color.GRAY);
                graphics2DCreateGraphics.fillRect(0, 0, bufferedImageCreateCompatibleImage.getWidth() / 2, bufferedImageCreateCompatibleImage.getHeight() / 2);
                graphics2DCreateGraphics.fillRect(bufferedImageCreateCompatibleImage.getWidth() / 2, bufferedImageCreateCompatibleImage.getHeight() / 2, bufferedImageCreateCompatibleImage.getWidth() / 2, bufferedImageCreateCompatibleImage.getHeight() / 2);
                return new TexturePaint(bufferedImageCreateCompatibleImage, new Rectangle(bufferedImageCreateCompatibleImage.getWidth(), bufferedImageCreateCompatibleImage.getHeight()));
            } finally {
                graphics2DCreateGraphics.dispose();
            }
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setPaint(this.backgroundPaint);
            graphics2D.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(graphics);
        }

        /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/ImageReaderBase$ImageLabel$ChangeBackgroundAction.class */
        private class ChangeBackgroundAction extends AbstractAction {
            protected Paint paint;

            public ChangeBackgroundAction(String str, Paint paint) {
                super(str);
                this.paint = paint;
            }

            public void actionPerformed(ActionEvent actionEvent) {
                ImageLabel.this.backgroundPaint = this.paint;
                ImageLabel.this.repaint();
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/ImageReaderBase$ImageLabel$ChooseBackgroundAction.class */
        private class ChooseBackgroundAction extends ChangeBackgroundAction {
            public ChooseBackgroundAction(String str, Color color) {
                super(str, color);
                putValue("SmallIcon", new Icon() { // from class: com.twelvemonkeys.imageio.ImageReaderBase.ImageLabel.ChooseBackgroundAction.1
                    public void paintIcon(Component component, Graphics graphics, int i, int i2) {
                        Graphics graphicsCreate = graphics.create();
                        graphicsCreate.setColor(ChooseBackgroundAction.this.paint);
                        graphicsCreate.fillRect(i, i2, 16, 16);
                        graphicsCreate.dispose();
                    }

                    public int getIconWidth() {
                        return 16;
                    }

                    public int getIconHeight() {
                        return 16;
                    }
                });
            }

            @Override // com.twelvemonkeys.imageio.ImageReaderBase.ImageLabel.ChangeBackgroundAction
            public void actionPerformed(ActionEvent actionEvent) {
                Color colorShowDialog = JColorChooser.showDialog(ImageLabel.this, "Choose background", this.paint);
                if (colorShowDialog != null) {
                    this.paint = colorShowDialog;
                    super.actionPerformed(actionEvent);
                }
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/ImageReaderBase$ImageLabel$ZoomAction.class */
        private class ZoomAction extends AbstractAction {
            private final double zoomFactor;

            public ZoomAction(String str, double d) {
                super(str);
                this.zoomFactor = d;
            }

            public ZoomAction(ImageLabel imageLabel, String str) {
                this(str, 0.0d);
            }

            public void actionPerformed(ActionEvent actionEvent) {
                if (this.zoomFactor <= 0.0d) {
                    ImageLabel.this.setIcon(new BufferedImageIcon(ImageLabel.this.image));
                    return;
                }
                Icon icon = ImageLabel.this.getIcon();
                int iMax = Math.max(Math.min((int) (((double) icon.getIconWidth()) * this.zoomFactor), ImageLabel.this.image.getWidth() * 16), ImageLabel.this.image.getWidth() / 16);
                int iMax2 = Math.max(Math.min((int) (((double) icon.getIconHeight()) * this.zoomFactor), ImageLabel.this.image.getHeight() * 16), ImageLabel.this.image.getHeight() / 16);
                ImageLabel.this.setIcon(new BufferedImageIcon(ImageLabel.this.image, Math.max(iMax, 2), Math.max(iMax2, 2), iMax > ImageLabel.this.image.getWidth() || iMax2 > ImageLabel.this.image.getHeight()));
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/ImageReaderBase$ImageLabel$ZoomToFitAction.class */
        private class ZoomToFitAction extends ZoomAction {
            public ZoomToFitAction(String str) {
                super(str, -1.0d);
            }

            @Override // com.twelvemonkeys.imageio.ImageReaderBase.ImageLabel.ZoomAction
            public void actionPerformed(ActionEvent actionEvent) {
                Component component = (JComponent) actionEvent.getSource();
                if (component instanceof JMenuItem) {
                    component = (JComponent) SwingUtilities.getAncestorOfClass(JPopupMenu.class, component).getInvoker();
                }
                Container ancestorOfClass = SwingUtilities.getAncestorOfClass(JViewport.class, component);
                double dMin = Math.min(((double) ancestorOfClass.getWidth()) / ((double) ImageLabel.this.image.getWidth()), ((double) ancestorOfClass.getHeight()) / ((double) ImageLabel.this.image.getHeight()));
                ImageLabel.this.setIcon(new BufferedImageIcon(ImageLabel.this.image, Math.max(Math.min((int) (((double) ImageLabel.this.image.getWidth()) * dMin), ImageLabel.this.image.getWidth() * 16), ImageLabel.this.image.getWidth() / 16), Math.max(Math.min((int) (((double) ImageLabel.this.image.getHeight()) * dMin), ImageLabel.this.image.getHeight() * 16), ImageLabel.this.image.getHeight() / 16), dMin > 1.0d));
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/ImageReaderBase$ImageLabel$ImageTransferable.class */
        private static class ImageTransferable implements Transferable {
            private final BufferedImage image;

            public ImageTransferable(BufferedImage bufferedImage) {
                this.image = bufferedImage;
            }

            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.imageFlavor};
            }

            public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
                return DataFlavor.imageFlavor.equals(dataFlavor);
            }

            /* JADX INFO: Thrown type has an unknown type hierarchy: java.awt.datatransfer.UnsupportedFlavorException */
            public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException {
                if (isDataFlavorSupported(dataFlavor)) {
                    return this.image;
                }
                throw new UnsupportedFlavorException(dataFlavor);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/ImageReaderBase$ExitIfNoWindowPresentHandler.class */
    private static class ExitIfNoWindowPresentHandler extends WindowAdapter {
        private ExitIfNoWindowPresentHandler() {
        }

        public void windowClosed(WindowEvent windowEvent) {
            Window[] windows = Window.getWindows();
            if (windows == null || windows.length == 0) {
                System.exit(0);
            }
        }
    }
}
