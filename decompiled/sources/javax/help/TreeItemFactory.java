package javax.help;

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import javax.swing.tree.DefaultMutableTreeNode;

/* JADX INFO: loaded from: autofirma.jar:javax/help/TreeItemFactory.class */
public interface TreeItemFactory {
    void parsingStarted(URL url);

    void processDOCTYPE(String str, String str2, String str3);

    void processPI(HelpSet helpSet, String str, String str2);

    TreeItem createItem(String str, Hashtable hashtable, HelpSet helpSet, Locale locale);

    TreeItem createItem();

    void reportMessage(String str, boolean z);

    Enumeration listMessages();

    DefaultMutableTreeNode parsingEnded(DefaultMutableTreeNode defaultMutableTreeNode);
}
