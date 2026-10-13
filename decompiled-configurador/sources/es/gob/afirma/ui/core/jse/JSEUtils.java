package es.gob.afirma.ui.core.jse;

import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.core.util.tree.AOTreeNode;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/ui/core/jse/JSEUtils.class */
public final class JSEUtils {
    private JSEUtils() {
    }

    public static DefaultTreeModel convertToSwingModel(AOTreeModel aOTreeModel) {
        AOTreeNode aOTreeNode = (AOTreeNode) aOTreeModel.getRoot();
        DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(aOTreeNode.getUserObject());
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(defaultMutableTreeNode);
        ArrayList arrayList = new ArrayList();
        arrayList.add(aOTreeNode);
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(defaultMutableTreeNode);
        for (int i = 0; i < arrayList.size(); i++) {
            AOTreeNode aOTreeNode2 = (AOTreeNode) arrayList.get(i);
            int childCount = aOTreeNode2.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                AOTreeNode childAt = aOTreeNode2.getChildAt(i2);
                arrayList.add(childAt);
                DefaultMutableTreeNode defaultMutableTreeNode2 = new DefaultMutableTreeNode(childAt.getUserObject());
                arrayList2.add(defaultMutableTreeNode2);
                ((DefaultMutableTreeNode) arrayList2.get(i)).add(defaultMutableTreeNode2);
            }
        }
        return defaultTreeModel;
    }
}
