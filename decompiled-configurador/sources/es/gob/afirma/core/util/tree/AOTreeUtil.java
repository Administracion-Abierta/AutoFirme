package es.gob.afirma.core.util.tree;

import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/util/tree/AOTreeUtil.class */
public final class AOTreeUtil {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private AOTreeUtil() {
    }

    public static String showTreeAsString(AOTreeModel aOTreeModel, String str, String str2) {
        if (aOTreeModel == null || aOTreeModel.getRoot() == null) {
            LOGGER.severe("Se ha proporcionado un arbol de firmas vacio");
            return null;
        }
        if (!(aOTreeModel.getRoot() instanceof AOTreeNode)) {
            LOGGER.severe("La raiz del arbol de firmas no es de tipo DafaultMutableTreeNode");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        AOTreeNode aOTreeNode = (AOTreeNode) aOTreeModel.getRoot();
        for (int i = 0; i < aOTreeNode.getChildCount(); i++) {
            archiveTreeNode(aOTreeNode.getChildAt(i), 0, str != null ? str : "", str2 != null ? str2 : "\t", sb);
        }
        return sb.toString();
    }

    private static void archiveTreeNode(AOTreeNode aOTreeNode, int i, String str, String str2, StringBuilder sb) {
        sb.append('\n').append(str);
        for (int i2 = 0; i2 < i; i2++) {
            sb.append(str2);
        }
        sb.append(aOTreeNode.getUserObject());
        for (int i3 = 0; i3 < aOTreeNode.getChildCount(); i3++) {
            archiveTreeNode(aOTreeNode.getChildAt(i3), i + 1, str, str2, sb);
        }
    }
}
