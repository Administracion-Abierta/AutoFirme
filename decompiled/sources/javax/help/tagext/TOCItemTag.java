package javax.help.tagext;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.Merge;
import javax.help.MergeHelpUtilities;
import javax.help.NavigatorView;
import javax.help.TOCItem;
import javax.help.TOCView;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/* JADX INFO: loaded from: autofirma.jar:javax/help/tagext/TOCItemTag.class */
public class TOCItemTag extends BodyTagSupport {
    private Enumeration treeEnum;
    private DefaultMutableTreeNode topNode;
    private HelpBroker hb;
    private String baseID = "root";
    private TOCView view = null;

    public void setTocView(TOCView tOCView) {
        this.view = tOCView;
    }

    public void setBaseID(String str) {
        this.baseID = str;
    }

    public void setHelpBroker(HelpBroker helpBroker) {
        this.hb = helpBroker;
    }

    private void initialize() {
        if (this.view == null) {
            return;
        }
        this.topNode = this.view.getDataAsTree();
        String mergeType = this.view.getMergeType();
        HelpSet helpSet = this.view.getHelpSet();
        helpSet.getLocale();
        MergeHelpUtilities.mergeNodeChildren(mergeType, this.topNode);
        addSubHelpSets(helpSet);
        this.treeEnum = this.topNode.preorderEnumeration();
    }

    private void addSubHelpSets(HelpSet helpSet) {
        Merge merge;
        Enumeration helpSets = helpSet.getHelpSets();
        while (helpSets.hasMoreElements()) {
            HelpSet helpSet2 = (HelpSet) helpSets.nextElement();
            if (helpSet2 != null) {
                NavigatorView[] navigatorViews = helpSet2.getNavigatorViews();
                for (int i = 0; i < navigatorViews.length; i++) {
                    if ((navigatorViews[i] instanceof TOCView) && (merge = Merge.DefaultMergeFactory.getMerge(this.view, navigatorViews[i])) != null) {
                        merge.processMerge(this.topNode);
                    }
                }
                addSubHelpSets(helpSet2);
            }
        }
    }

    public int doStartTag() {
        initialize();
        if (this.treeEnum.hasMoreElements()) {
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) this.treeEnum.nextElement();
            if (defaultMutableTreeNode == this.topNode) {
                try {
                    defaultMutableTreeNode = (DefaultMutableTreeNode) this.treeEnum.nextElement();
                } catch (NoSuchElementException e) {
                    return 0;
                }
            }
            setNodeAttributes(defaultMutableTreeNode);
            return 2;
        }
        return 0;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.servlet.jsp.JspTagException */
    public int doAfterBody() throws JspException, JspTagException {
        BodyContent bodyContent = getBodyContent();
        try {
            bodyContent.writeOut(getPreviousOut());
            bodyContent.clearBody();
            if (this.treeEnum.hasMoreElements()) {
                setNodeAttributes((DefaultMutableTreeNode) this.treeEnum.nextElement());
                return 2;
            }
            return 0;
        } catch (IOException e) {
            throw new JspTagException(new StringBuffer().append("TOCItemTag: ").append(e.getMessage()).toString());
        }
    }

    private void setNodeAttributes(DefaultMutableTreeNode defaultMutableTreeNode) {
        TOCItem tOCItem = (TOCItem) defaultMutableTreeNode.getUserObject();
        this.pageContext.setAttribute("name", tOCItem.getName());
        String str = "";
        if (tOCItem.getID() != null) {
            str = tOCItem.getID().id;
        }
        this.pageContext.setAttribute("helpID", str);
        this.pageContext.setAttribute("parent", Integer.toHexString(defaultMutableTreeNode.getParent().hashCode()));
        this.pageContext.setAttribute("parentID", getID(defaultMutableTreeNode.getParent()));
        this.pageContext.setAttribute("node", Integer.toHexString(defaultMutableTreeNode.hashCode()));
        this.pageContext.setAttribute("nodeID", getID(defaultMutableTreeNode));
        this.pageContext.setAttribute("contentURL", getContentURL(tOCItem));
        this.pageContext.setAttribute("iconURL", getIconURL(defaultMutableTreeNode, tOCItem, true));
        this.pageContext.setAttribute("iconOpenURL", getIconURL(defaultMutableTreeNode, tOCItem, false));
        this.pageContext.setAttribute("expansionType", Integer.toString(tOCItem.getExpansionType()));
    }

    private String getID(TreeNode treeNode) {
        if (treeNode == this.topNode) {
            return this.baseID;
        }
        TreeNode parent = treeNode.getParent();
        if (parent == null) {
            return "";
        }
        return getID(parent).concat(new StringBuffer().append("_").append(Integer.toString(parent.getIndex(treeNode))).toString());
    }

    private String getContentURL(TOCItem tOCItem) {
        URL uRLFromID = null;
        Map.ID id = tOCItem.getID();
        if (id != null) {
            try {
                uRLFromID = id.hs.getLocalMap().getURLFromID(id);
            } catch (MalformedURLException e) {
            }
        }
        if (uRLFromID == null) {
            return "";
        }
        return uRLFromID.toExternalForm();
    }

    private String getIconURL(DefaultMutableTreeNode defaultMutableTreeNode, TOCItem tOCItem, boolean z) {
        URL uRLFromID = null;
        Map.ID imageID = tOCItem.getImageID();
        if (imageID == null) {
            if (defaultMutableTreeNode.isLeaf()) {
                imageID = this.view.getTopicImageID();
            } else if (z) {
                imageID = this.view.getCategoryClosedImageID();
            } else {
                imageID = this.view.getCategoryOpenImageID();
            }
        }
        if (imageID != null) {
            try {
                uRLFromID = imageID.hs.getLocalMap().getURLFromID(imageID);
            } catch (MalformedURLException e) {
            }
        }
        if (uRLFromID == null) {
            return "";
        }
        return uRLFromID.toExternalForm();
    }
}
