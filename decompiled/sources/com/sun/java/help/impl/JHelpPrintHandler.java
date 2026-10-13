package com.sun.java.help.impl;

import com.aowagie.text.pdf.ColumnText;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.JHelp;
import javax.help.TreeItem;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.EditorKit;
import javax.swing.text.Position;
import javax.swing.text.View;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/JHelpPrintHandler.class */
public class JHelpPrintHandler implements ActionListener {
    public static final String PRINT_BUTTON_NAME = "PrintButton";
    public static final String PAGE_SETUP_BUTTON_NAME = "PageSetupButton";
    public static final String PRINT_LATER_BUTTON_NAME = "PrintLaterButton";
    private boolean enabled = true;
    private PrinterJob printerJob;
    private PageFormat pageFormat;
    private JHelp help;
    private HelpModel helpModel;
    private URL documentURL;
    private String documentTitle;
    private static WeakHashMap handlers;
    protected SwingPropertyChangeSupport changeSupport;
    private static final boolean debug = false;

    public JHelpPrintHandler(JHelp jHelp) {
        this.help = jHelp;
    }

    public static JHelpPrintHandler getJHelpPrintHandler(JHelp jHelp) {
        JHelpPrintHandler jHelpPrintHandler = null;
        if (handlers == null) {
            handlers = new WeakHashMap();
        } else {
            jHelpPrintHandler = (JHelpPrintHandler) handlers.get(jHelp);
        }
        if (jHelpPrintHandler == null) {
            jHelpPrintHandler = new JHelpPrintHandler(jHelp);
            handlers.put(jHelp, jHelpPrintHandler);
        }
        return jHelpPrintHandler;
    }

    protected void firePropertyChange(String str, Object obj, Object obj2) {
        if (this.changeSupport == null) {
            return;
        }
        this.changeSupport.firePropertyChange(str, obj, obj2);
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (this.changeSupport == null) {
            this.changeSupport = new SwingPropertyChangeSupport(this);
        }
        this.changeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (this.changeSupport == null) {
            return;
        }
        this.changeSupport.removePropertyChangeListener(propertyChangeListener);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean z) {
        boolean z2 = this.enabled;
        this.enabled = z;
        firePropertyChange("enabled", new Boolean(z2), new Boolean(z));
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String name = null;
        Object source = actionEvent.getSource();
        if (source instanceof MenuComponent) {
            name = ((MenuComponent) source).getName();
        }
        if (source instanceof Component) {
            name = ((Component) source).getName();
        }
        if (isEnabled()) {
            if (name != null && name.equals(PAGE_SETUP_BUTTON_NAME)) {
                printSetup();
            } else if (getHelp() != null) {
                print(getURLs());
            } else if (getHelpModel() != null) {
                print(getHelpModel().getCurrentURL());
            }
        }
    }

    public void printSetup() {
        if (isEnabled()) {
            new PageSetupThread(this).start();
        }
    }

    public void print(URL url) {
        if (isEnabled()) {
            new PrintThread(this, url).start();
        }
    }

    public void print(URL[] urlArr) {
        if (isEnabled()) {
            new PrintThread(this, urlArr).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public JHelp getHelp() {
        return this.help;
    }

    private URL[] getURLs() {
        TreeItem[] selectedItems;
        URL[] urlArr = null;
        if (getHelp() != null && (selectedItems = getHelp().getSelectedItems()) != null) {
            debug(new StringBuffer().append("pages to print: ").append(selectedItems.length).toString());
            urlArr = new URL[selectedItems.length];
            for (int i = 0; i < selectedItems.length; i++) {
                debug(new StringBuffer().append("   ").append(selectedItems[i].getName()).append(": ").append(selectedItems[i].getURL()).toString());
                urlArr[i] = selectedItems[i].getURL();
            }
        }
        return urlArr;
    }

    public PageFormat getPageFormat() {
        if (this.pageFormat == null && getPrinterJob() != null) {
            this.pageFormat = getPrinterJob().defaultPage();
        }
        return this.pageFormat;
    }

    public PageFormat getPF() {
        return getPageFormat();
    }

    public void setPageFormat(PageFormat pageFormat) {
        if (this.pageFormat == pageFormat) {
            return;
        }
        PageFormat pageFormat2 = this.pageFormat;
        this.pageFormat = pageFormat;
        firePropertyChange("pageFormat", pageFormat2, pageFormat);
    }

    public void setPF(PageFormat pageFormat) {
        setPageFormat(pageFormat);
    }

    public PrinterJob getPrinterJob() {
        if (this.printerJob == null) {
            try {
                this.printerJob = PrinterJob.getPrinterJob();
            } catch (SecurityException e) {
                setEnabled(false);
                processException(e);
            }
        }
        return this.printerJob;
    }

    public void handlePageSetup(Component component) {
        component.setName(PAGE_SETUP_BUTTON_NAME);
    }

    public void setHelpModel(HelpModel helpModel) {
        HelpModel helpModel2 = this.helpModel;
        this.helpModel = helpModel;
        firePropertyChange("helpModel", helpModel2, helpModel);
    }

    public HelpModel getHelpModel() {
        return this.helpModel;
    }

    static Window getWindowForObject(Object obj) {
        if (obj == null || (obj instanceof Frame) || (obj instanceof Dialog)) {
            return (Window) obj;
        }
        MenuContainer parent = null;
        if (obj instanceof MenuComponent) {
            parent = ((MenuComponent) obj).getParent();
        } else if (obj instanceof Component) {
            parent = ((Component) obj).getParent();
        }
        return getWindowForObject(parent);
    }

    static Insets getInsetsForContainer(Container container) {
        Insets insets = container.getInsets();
        Container parent = container.getParent();
        while (true) {
            Container container2 = parent;
            if (container2 == null) {
                break;
            }
            Insets insets2 = container2.getInsets();
            insets.bottom += insets2.bottom;
            insets.left += insets2.left;
            insets.right += insets2.right;
            insets.top += insets2.top;
            if (container2 instanceof Window) {
                break;
            }
            parent = container2.getParent();
        }
        return insets;
    }

    EditorKit createEditorKitForContentType(String str) {
        HelpModel helpModel;
        HelpSet helpSet;
        String str2;
        EditorKit editorKit;
        Class<?> cls;
        JHelp help = getHelp();
        if (help != null) {
            helpModel = help.getModel();
        } else {
            helpModel = getHelpModel();
        }
        if (helpModel == null || (helpSet = helpModel.getHelpSet()) == null || (str2 = (String) helpSet.getKeyData(HelpSet.kitTypeRegistry, str)) == null) {
            return null;
        }
        ClassLoader loader = (ClassLoader) helpSet.getKeyData(HelpSet.kitLoaderRegistry, str);
        if (loader == null) {
            loader = helpSet.getLoader();
        }
        try {
            if (loader != null) {
                cls = loader.loadClass(str2);
            } else {
                cls = Class.forName(str2);
            }
            editorKit = (EditorKit) cls.newInstance();
        } catch (Throwable th) {
            System.err.println(th);
            editorKit = null;
        }
        if (editorKit != null) {
            return (EditorKit) editorKit.clone();
        }
        return editorKit;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/JHelpPrintHandler$PageSetupThread.class */
    class PageSetupThread extends Thread {
        PrinterJob job;
        private final JHelpPrintHandler this$0;

        PageSetupThread(JHelpPrintHandler jHelpPrintHandler) {
            this.this$0 = jHelpPrintHandler;
            setDaemon(true);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            PrinterJob printerJob = this.this$0.getPrinterJob();
            if (printerJob != null) {
                synchronized (printerJob) {
                    try {
                        this.this$0.setPageFormat(printerJob.pageDialog(this.this$0.getPageFormat()));
                    } catch (Exception e) {
                        JHelpPrintHandler.processException(e);
                    }
                }
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/JHelpPrintHandler$PrintThread.class */
    class PrintThread extends Thread {
        private URL[] urls;
        private int index;
        private final JHelpPrintHandler this$0;

        PrintThread(JHelpPrintHandler jHelpPrintHandler, URL url) {
            this(jHelpPrintHandler, new URL[]{url});
        }

        PrintThread(JHelpPrintHandler jHelpPrintHandler, URL[] urlArr) {
            this.this$0 = jHelpPrintHandler;
            this.index = 0;
            this.urls = urlArr;
            setDaemon(true);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            PrinterJob printerJob = this.this$0.getPrinterJob();
            if (printerJob != null) {
                synchronized (printerJob) {
                    JHEditorPane jHEditorPane = new JHEditorPane(this.this$0);
                    JHFrame jHFrame = new JHFrame(this.this$0);
                    jHFrame.setContentPane(jHEditorPane);
                    try {
                        printerJob.setPageable(new JHPageable(this.this$0, jHEditorPane, this.urls, (PageFormat) this.this$0.getPageFormat().clone()));
                    } catch (Exception e) {
                        JHelpPrintHandler.processException(e);
                    }
                    if (printerJob.printDialog()) {
                        try {
                            printerJob.print();
                        } catch (Exception e2) {
                            JHelpPrintHandler.processException(e2);
                        }
                        jHFrame.dispose();
                    } else {
                        jHFrame.dispose();
                    }
                    throw th;
                }
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/JHelpPrintHandler$PageTransform.class */
    class PageTransform extends AffineTransform {
        private double height;
        private final JHelpPrintHandler this$0;

        PageTransform(JHelpPrintHandler jHelpPrintHandler) {
            this.this$0 = jHelpPrintHandler;
        }

        public double getHeight() {
            return this.height;
        }

        public void setHeight(double d) {
            this.height = d;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/JHelpPrintHandler$JHPrintable.class */
    class JHPrintable implements Printable, PropertyChangeListener {
        JEditorPane editor;
        URL url;
        PageFormat pf;
        int firstPage;
        boolean scaleToFit;
        Vector transforms;
        private final JHelpPrintHandler this$0;

        public JHPrintable(JHelpPrintHandler jHelpPrintHandler, JEditorPane jEditorPane, URL url, PageFormat pageFormat, int i, boolean z) {
            this.this$0 = jHelpPrintHandler;
            this.editor = jEditorPane;
            this.url = url;
            this.pf = pageFormat;
            this.firstPage = i;
            this.scaleToFit = z;
        }

        private synchronized void loadPage() {
            JHelpPrintHandler.debug(new StringBuffer().append("JHPrintable.loadPage(): ").append(this.url).toString());
            URL page = this.editor.getPage();
            if (page != null && page.equals(this.url)) {
                return;
            }
            this.editor.addPropertyChangeListener("page", this);
            try {
                this.editor.setPage(this.url);
                wait();
            } catch (Exception e) {
                JHelpPrintHandler.processException(e);
            }
            this.editor.removePropertyChangeListener("page", this);
        }

        public int getNumberOfPages() {
            if (this.transforms == null) {
                loadPage();
                this.transforms = createTransforms();
            }
            return this.transforms.size();
        }

        @Override // java.beans.PropertyChangeListener
        public synchronized void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            notifyAll();
        }

        public Vector createTransforms() {
            int i = 0;
            Vector vector = new Vector();
            double d = 0.0d;
            double imageableWidth = 1.0d;
            JEditorPane jEditorPaneWindowForComponent = SwingUtilities.windowForComponent(this.editor);
            if (jEditorPaneWindowForComponent == null) {
                jEditorPaneWindowForComponent = this.editor;
            }
            jEditorPaneWindowForComponent.addNotify();
            View rootView = this.editor.getUI().getRootView(this.editor);
            Rectangle viewRec = JHelpPrintHandler.getViewRec(rootView, (float) this.pf.getImageableWidth(), (float) this.pf.getImageableHeight());
            JHelpPrintHandler.debug(new StringBuffer().append("viewRec=").append(viewRec).toString());
            Insets insetsForContainer = JHelpPrintHandler.getInsetsForContainer(this.editor);
            jEditorPaneWindowForComponent.setBounds(0, 0, viewRec.width + insetsForContainer.left + insetsForContainer.right, viewRec.height + insetsForContainer.top + insetsForContainer.bottom);
            jEditorPaneWindowForComponent.setVisible(true);
            if (this.scaleToFit) {
                if (viewRec.getWidth() > this.pf.getImageableWidth()) {
                    imageableWidth = this.pf.getImageableWidth() / viewRec.getWidth();
                }
                JHelpPrintHandler.debug(new StringBuffer().append("scale=").append(imageableWidth).append(" ImageableWidth=").append(this.pf.getImageableWidth()).append(" width=").append(viewRec.getWidth()).toString());
            }
            Rectangle2D.Double r0 = new Rectangle2D.Double(0.0d, 0.0d, this.pf.getImageableWidth(), this.pf.getImageableHeight() / imageableWidth);
            JHelpPrintHandler.debug(new StringBuffer().append("printRec=").append(r0).toString());
            Position.Bias[] biasArr = new Position.Bias[1];
            do {
                JHelpPrintHandler.debug(new StringBuffer().append("preparing page=").append(i).append(" curHeight=").append(d).toString());
                double height = r0.getHeight() + d;
                int iViewToModel = rootView.viewToModel(ColumnText.GLOBAL_SPACE_CHAR_RATIO, (float) height, viewRec, biasArr);
                JHelpPrintHandler.debug(new StringBuffer().append("point=").append(iViewToModel).toString());
                try {
                    Rectangle2D bounds2D = rootView.modelToView(iViewToModel, viewRec, biasArr[0]).getBounds2D();
                    JHelpPrintHandler.debug(new StringBuffer().append("pointRec=").append(bounds2D).toString());
                    double d2 = d;
                    double y = bounds2D.getY() - 1.0d;
                    JHelpPrintHandler.debug(new StringBuffer().append("Starting height=").append(y).toString());
                    if (height >= bounds2D.getY() + bounds2D.getHeight()) {
                        y = (bounds2D.getY() + bounds2D.getHeight()) - 1.0d;
                        JHelpPrintHandler.debug(new StringBuffer().append("Adjusted height=").append(y).toString());
                    }
                    double y2 = bounds2D.getY();
                    double y3 = (bounds2D.getY() + bounds2D.getHeight()) - 1.0d;
                    double x = bounds2D.getX() + 20.0d;
                    Rectangle2D rectangle2D = bounds2D;
                    while (x <= this.pf.getImageableWidth() * imageableWidth) {
                        Rectangle2D bounds2D2 = rootView.modelToView(rootView.viewToModel((float) x, (float) height, viewRec, biasArr), viewRec, biasArr[0]).getBounds2D();
                        if (!rectangle2D.equals(bounds2D2) && bounds2D2.getX() >= x) {
                            JHelpPrintHandler.debug(new StringBuffer().append("pointRec2=").append(bounds2D2).toString());
                            double y4 = bounds2D2.getY();
                            double y5 = (bounds2D2.getY() + bounds2D2.getHeight()) - 1.0d;
                            if (y5 > y2) {
                                if (y5 > y3) {
                                    if (y5 < height) {
                                        y = y5;
                                        y3 = y5;
                                        if (y4 < y2) {
                                            y2 = y4;
                                        }
                                        JHelpPrintHandler.debug(new StringBuffer().append("Adjust height to testheight ").append(y).toString());
                                    } else if (y4 > y3) {
                                        y = y4 - 1.0d;
                                        y3 = y5;
                                        y2 = y4;
                                        JHelpPrintHandler.debug(new StringBuffer().append("new base component ").append(y).toString());
                                    } else if (y4 < y2) {
                                        y = y4 - 1.0d;
                                        y2 = y4;
                                        y3 = y5;
                                        JHelpPrintHandler.debug(new StringBuffer().append("test height > maxheight. Adjust height testY - 1 ").append(y).toString());
                                    } else {
                                        y = y2 - 1.0d;
                                        y3 = y5;
                                        JHelpPrintHandler.debug(new StringBuffer().append("test height > maxheight. Adjust height baseY - 1 ").append(y).toString());
                                    }
                                } else if (y3 < height) {
                                    y = y3;
                                    if (y4 < y2) {
                                        y2 = y4;
                                    }
                                    JHelpPrintHandler.debug(new StringBuffer().append("baseHeight ok ").append(y).toString());
                                } else if (y2 <= y4) {
                                    y = y2 - 1.0d;
                                    JHelpPrintHandler.debug(new StringBuffer().append("baseHeight too long - height ok").append(y).toString());
                                } else {
                                    y = y4 - 1.0d;
                                    y2 = y4;
                                    JHelpPrintHandler.debug(new StringBuffer().append("baseHeight too long - use testY - 1 ").append(y).toString());
                                }
                            }
                            rectangle2D = bounds2D2;
                            x = rectangle2D.getX() + 20.0d;
                        } else {
                            x += 20.0d;
                        }
                    }
                    PageTransform pageTransform = new PageTransform(this.this$0);
                    pageTransform.translate(this.pf.getImageableX(), this.pf.getImageableY());
                    JHelpPrintHandler.debug(new StringBuffer().append("t.translate=").append(pageTransform).toString());
                    pageTransform.translate((-insetsForContainer.left) * imageableWidth, (-(((double) insetsForContainer.top) + d2)) * imageableWidth);
                    JHelpPrintHandler.debug(new StringBuffer().append("t.translate=").append(pageTransform).toString());
                    pageTransform.scale(imageableWidth, imageableWidth);
                    JHelpPrintHandler.debug(new StringBuffer().append("t.scale=").append(pageTransform).toString());
                    pageTransform.setHeight(y + ((double) insetsForContainer.top));
                    if (d == y + 1.0d) {
                        break;
                    }
                    vector.add(i, pageTransform);
                    d = y + 1.0d;
                    JHelpPrintHandler.debug(new StringBuffer().append("Setting curHeight=").append(d).toString());
                    i++;
                    JHelpPrintHandler.debug(new StringBuffer().append("curHeight=").append(d).append(" viewRec.getHeight()=").append(viewRec.getHeight()).toString());
                } catch (BadLocationException e) {
                }
            } while (d < viewRec.getHeight());
            return vector;
        }

        private void printHeader(Graphics2D graphics2D, int i) {
            graphics2D.setClip(new Rectangle2D.Double(0.0d, 0.0d, this.pf.getWidth(), this.pf.getHeight()));
            graphics2D.setFont(new Font("Serif", 2, 10));
            String string = new StringBuffer().append((String) this.editor.getDocument().getProperty("title")).append(" ").append(i + 1).append("/").append(this.transforms.size()).toString();
            Rectangle2D stringBounds = graphics2D.getFontMetrics().getStringBounds(string, graphics2D);
            graphics2D.drawString(string, (float) (((this.pf.getWidth() - stringBounds.getX()) - stringBounds.getWidth()) - 36.0d), 36.0f);
            graphics2D.setClip(new Rectangle2D.Double(this.pf.getImageableX(), this.pf.getImageableY(), this.pf.getImageableWidth(), this.pf.getImageableHeight()));
        }

        public int print(Graphics graphics, PageFormat pageFormat, int i) {
            JHelpPrintHandler.debug(new StringBuffer().append("Printing document page=").append(i).toString());
            int i2 = i - this.firstPage;
            JHelpPrintHandler.debug(new StringBuffer().append("Printing page=").append(i2).toString());
            if (i2 >= getNumberOfPages()) {
                return 1;
            }
            Graphics2D graphics2D = (Graphics2D) graphics;
            printHeader(graphics2D, i2);
            graphics2D.transform((AffineTransform) this.transforms.get(i2));
            JHelpPrintHandler.debug(new StringBuffer().append("Graphics tansform=").append(graphics2D.getTransform()).toString());
            JHelpPrintHandler.debug(new StringBuffer().append("Graphics clip=").append(graphics2D.getClip()).toString());
            Rectangle2D bounds2D = graphics2D.getClip().getBounds2D();
            double height = ((PageTransform) this.transforms.get(i2)).getHeight();
            double y = ((bounds2D.getY() + bounds2D.getHeight()) - 1.0d) - height;
            if (y > 0.0d) {
                JHelpPrintHandler.debug(new StringBuffer().append("Graphics adjusted height=").append(y).toString());
                graphics2D.clip(new Rectangle2D.Double(bounds2D.getX(), bounds2D.getY(), bounds2D.getWidth(), bounds2D.getHeight() - y));
                bounds2D = graphics2D.getClip().getBounds2D();
                JHelpPrintHandler.debug(new StringBuffer().append("Graphics tansform=").append(graphics2D.getTransform()).toString());
                JHelpPrintHandler.debug(new StringBuffer().append("Graphics clip=").append(graphics2D.getClip()).toString());
            }
            if (bounds2D.getY() < height) {
                this.editor.paint(graphics2D);
                return 0;
            }
            return 1;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/JHelpPrintHandler$EmptyPrintable.class */
    class EmptyPrintable implements Printable {
        private final JHelpPrintHandler this$0;

        EmptyPrintable(JHelpPrintHandler jHelpPrintHandler) {
            this.this$0 = jHelpPrintHandler;
        }

        public int print(Graphics graphics, PageFormat pageFormat, int i) throws PrinterException {
            return 1;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/JHelpPrintHandler$JHPageable.class */
    public class JHPageable implements Pageable {
        private JEditorPane editor;
        private URL[] urls;
        private PageFormat pf;
        private Printable printable;
        private final JHelpPrintHandler this$0;
        private int lastIndex = -1;
        private int lastPage = 0;
        private int numPages = 0;

        public JHPageable(JHelpPrintHandler jHelpPrintHandler, JEditorPane jEditorPane, URL[] urlArr, PageFormat pageFormat) {
            this.this$0 = jHelpPrintHandler;
            this.printable = new EmptyPrintable(this.this$0);
            this.editor = jEditorPane;
            this.urls = urlArr;
            this.pf = pageFormat;
        }

        public PageFormat getPageFormat(int i) throws IndexOutOfBoundsException {
            return this.pf;
        }

        public int getNumberOfPages() {
            if (this.numPages != 0) {
                return this.numPages;
            }
            if (this.urls != null) {
                for (int i = 0; i < this.urls.length; i++) {
                    this.numPages += new JHPrintable(this.this$0, this.editor, this.urls[i], this.pf, 0, true).getNumberOfPages();
                }
            }
            return this.numPages;
        }

        public Printable getPrintable(int i) throws IndexOutOfBoundsException {
            JHelpPrintHandler.debug(new StringBuffer().append("JHPageable.getPrintable(").append(i).append("): lastIndex=").append(this.lastIndex).append(", lastPage").append(this.lastPage).toString());
            if (i < 0) {
                throw new IndexOutOfBoundsException(new StringBuffer().append(i).append(" < 0").toString());
            }
            if (this.urls != null) {
                while (i >= this.lastPage && this.lastIndex + 1 < this.urls.length) {
                    URL[] urlArr = this.urls;
                    int i2 = this.lastIndex + 1;
                    this.lastIndex = i2;
                    if (urlArr[i2] != null) {
                        this.printable = new JHPrintable(this.this$0, this.editor, this.urls[this.lastIndex], this.pf, this.lastPage, true);
                        this.lastPage += ((JHPrintable) this.printable).getNumberOfPages();
                    } else {
                        this.printable = new EmptyPrintable(this.this$0);
                    }
                }
            }
            if (i > this.lastPage) {
                throw new IndexOutOfBoundsException(new StringBuffer().append(i).append(" > ").append(this.lastPage).toString());
            }
            return this.printable;
        }
    }

    protected static Rectangle getViewRec(View view, float f, float f2) {
        Rectangle rectangle = new Rectangle();
        view.setSize(f, f2);
        rectangle.width = (int) Math.max((long) Math.ceil(view.getMinimumSpan(0)), (long) f);
        rectangle.height = (int) Math.min((long) Math.ceil(view.getPreferredSpan(1)), 2147483647L);
        view.setSize(rectangle.width, rectangle.height);
        if (view.getView(0) instanceof BoxView) {
            BoxView view2 = view.getView(0);
            rectangle.width = view2.getWidth();
            rectangle.height = view2.getHeight();
        } else {
            rectangle.height = (int) Math.min((long) Math.ceil(view.getPreferredSpan(1)), 2147483647L);
        }
        return rectangle;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/JHelpPrintHandler$JHFrame.class */
    class JHFrame extends JFrame {
        private final JHelpPrintHandler this$0;

        JHFrame(JHelpPrintHandler jHelpPrintHandler) {
            this.this$0 = jHelpPrintHandler;
        }

        public void addNotify() {
            getRootPane().addNotify();
        }

        public void validate() {
            validateTree();
        }

        public Graphics getGraphics() {
            return this.this$0.getHelp().getGraphics();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/JHelpPrintHandler$JHEditorPane.class */
    class JHEditorPane extends JEditorPane {
        private final JHelpPrintHandler this$0;

        public JHEditorPane(JHelpPrintHandler jHelpPrintHandler) {
            this.this$0 = jHelpPrintHandler;
            setDoubleBuffered(false);
            setEditable(false);
            setDropTarget(null);
        }

        public EditorKit getEditorKitForContentType(String str) {
            EditorKit editorKitCreateEditorKitForContentType = this.this$0.createEditorKitForContentType(str);
            if (editorKitCreateEditorKitForContentType == null) {
                editorKitCreateEditorKitForContentType = super.getEditorKitForContentType(str);
            }
            return editorKitCreateEditorKitForContentType;
        }

        public void addMouseListener(MouseListener mouseListener) {
        }

        public void removeMouseListener(MouseListener mouseListener) {
        }

        public void addMouseMotionListener(MouseMotionListener mouseMotionListener) {
        }

        public void removeMouseMotionListener(MouseMotionListener mouseMotionListener) {
        }

        public void addFocusListener(FocusListener focusListener) {
        }

        public void removeFocusListener(FocusListener focusListener) {
        }

        public void addKeyListener(KeyListener keyListener) {
        }

        public void removeKeyListener(KeyListener keyListener) {
        }
    }

    protected static void processException(Exception exc) {
        System.err.println(exc);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void debug(String str) {
    }
}
