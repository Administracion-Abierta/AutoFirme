package javax.help;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:javax/help/FavoritesNode.class */
public class FavoritesNode extends DefaultMutableTreeNode {
    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE favorites\n PUBLIC \"-//Sun Microsystems Inc.//DTD JavaHelp Favorites Version 2.0//EN\"\n        \"http://java.sun.com/products/javahelp/favorites_2_0.dtd\">\n\n<favorites version=\"2.0\">\n";
    public static final String ELEMENT = "favoriteitem";
    public static final String FOOTER = "</favorites>";
    private FavoritesItem item;
    private static final boolean debug = false;

    public FavoritesNode(FavoritesItem favoritesItem) {
        super(favoritesItem);
        this.item = favoritesItem;
    }

    public boolean getAllowsChildren() {
        return ((FavoritesItem) getUserObject()).isFolder();
    }

    public void add(DefaultMutableTreeNode defaultMutableTreeNode) {
        super.add(defaultMutableTreeNode);
        FavoritesItem favoritesItem = (FavoritesItem) defaultMutableTreeNode.getUserObject();
        favoritesItem.getParent();
        ((FavoritesItem) getUserObject()).add(favoritesItem);
    }

    public void remove(DefaultMutableTreeNode defaultMutableTreeNode) {
        super.remove(defaultMutableTreeNode);
        FavoritesItem favoritesItem = (FavoritesItem) ((FavoritesNode) defaultMutableTreeNode).getUserObject();
        FavoritesItem favoritesItem2 = (FavoritesItem) getUserObject();
        if (this.parent != null) {
            favoritesItem2.remove(favoritesItem);
        }
    }

    public int getVisibleChildCount() {
        int i = 0;
        if (this.item == null) {
            return 0;
        }
        Enumeration enumerationElements = this.item.getChildren().elements();
        while (enumerationElements.hasMoreElements()) {
            if (((FavoritesItem) enumerationElements.nextElement()).isVisible()) {
                i++;
            }
        }
        return i;
    }

    public String getOffset() {
        String string;
        FavoritesNode parent = getParent();
        if (parent != null) {
            string = new StringBuffer().append(parent.getOffset()).append("  ").toString();
        } else {
            string = "  ";
        }
        return string;
    }

    public void export(OutputStream outputStream) throws IOException {
        new OutputStreamWriter(outputStream);
        OutputStreamWriter outputStreamWriterExportHeader = exportHeader(outputStream);
        Enumeration enumerationChildren = children();
        if (!enumerationChildren.equals(DefaultMutableTreeNode.EMPTY_ENUMERATION)) {
            while (enumerationChildren.hasMoreElements()) {
                ((FavoritesNode) enumerationChildren.nextElement()).exportNode(outputStreamWriterExportHeader);
            }
        }
        outputStreamWriterExportHeader.write(FOOTER);
        outputStreamWriterExportHeader.close();
    }

    public void exportNode(OutputStreamWriter outputStreamWriter) throws IOException {
        getParent();
        FavoritesItem favoritesItem = (FavoritesItem) getUserObject();
        outputStreamWriter.write(new StringBuffer().append(getOffset()).append("<").append(getXMLElement()).append(" text=\"").append(favoritesItem.getName()).append("\" ").toString());
        String target = favoritesItem.getTarget();
        if (target != null) {
            outputStreamWriter.write(new StringBuffer().append("target=\"").append(target).append("\" ").toString());
        }
        String uRLSpec = favoritesItem.getURLSpec();
        if (uRLSpec != null) {
            outputStreamWriter.write(new StringBuffer().append("url=\"").append(uRLSpec).append(OperatorName.SHOW_TEXT_LINE_AND_SPACE).toString());
        }
        String helpSetTitle = favoritesItem.getHelpSetTitle();
        if (helpSetTitle != null) {
            outputStreamWriter.write(new StringBuffer().append(" hstitle=\"").append(helpSetTitle).append(OperatorName.SHOW_TEXT_LINE_AND_SPACE).toString());
        }
        if (children().equals(DefaultMutableTreeNode.EMPTY_ENUMERATION)) {
            outputStreamWriter.write("/>\n");
            return;
        }
        outputStreamWriter.write(">\n");
        Enumeration enumerationElements = this.children.elements();
        while (enumerationElements.hasMoreElements()) {
            FavoritesNode favoritesNode = (FavoritesNode) enumerationElements.nextElement();
            debug(new StringBuffer().append("offspring: ").append(favoritesNode).toString());
            favoritesNode.exportNode(outputStreamWriter);
        }
        outputStreamWriter.write(new StringBuffer().append(getOffset()).append("</").append(ELEMENT).append(">\n").toString());
    }

    public OutputStreamWriter exportHeader(OutputStream outputStream) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
        outputStreamWriter.write(HEADER);
        return outputStreamWriter;
    }

    public String getXMLHeader() {
        return HEADER;
    }

    public String getXMLElement() {
        return ELEMENT;
    }

    public FavoritesNode getDeepCopy() {
        return new FavoritesNode((FavoritesItem) this.item.clone());
    }

    public boolean isVisible() {
        return this.item.isVisible();
    }

    public void setVisible(boolean z) {
        this.item.setVisible(z);
    }

    private static void debug(String str) {
    }
}
