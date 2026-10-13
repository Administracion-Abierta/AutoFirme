package javax.help.tagext;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.IndexItem;
import javax.help.IndexView;
import javax.help.Map;
import javax.help.Merge;
import javax.help.MergeHelpUtilities;
import javax.help.NavigatorView;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/* JADX INFO: loaded from: autofirma.jar:javax/help/tagext/IndexItemTag.class */
public class IndexItemTag extends BodyTagSupport {
    private Enumeration treeEnum;
    private DefaultMutableTreeNode topNode;
    private HelpBroker hb;
    private String baseID = "root";
    private IndexView view = null;

    public void setIndexView(IndexView indexView) {
        this.view = indexView;
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
        this.treeEnum = this.topNode.preorderEnumeration();
        String mergeType = this.view.getMergeType();
        HelpSet helpSet = this.view.getHelpSet();
        helpSet.getLocale();
        MergeHelpUtilities.mergeNodeChildren(mergeType, this.topNode);
        addSubHelpSets(helpSet);
    }

    private void addSubHelpSets(HelpSet helpSet) {
        Merge merge;
        Enumeration helpSets = helpSet.getHelpSets();
        while (helpSets.hasMoreElements()) {
            HelpSet helpSet2 = (HelpSet) helpSets.nextElement();
            if (helpSet2 != null) {
                NavigatorView[] navigatorViews = helpSet2.getNavigatorViews();
                for (int i = 0; i < navigatorViews.length; i++) {
                    if ((navigatorViews[i] instanceof IndexView) && (merge = Merge.DefaultMergeFactory.getMerge(this.view, navigatorViews[i])) != null) {
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
            throw new JspTagException(new StringBuffer().append("IndexItemTag: ").append(e.getMessage()).toString());
        }
    }

    private void setNodeAttributes(DefaultMutableTreeNode defaultMutableTreeNode) {
        IndexItem indexItem = (IndexItem) defaultMutableTreeNode.getUserObject();
        this.pageContext.setAttribute("name", indexItem.getName());
        String str = "";
        if (indexItem.getID() != null) {
            str = indexItem.getID().id;
        }
        this.pageContext.setAttribute("helpID", str);
        this.pageContext.setAttribute("parent", Integer.toHexString(defaultMutableTreeNode.getParent().hashCode()));
        this.pageContext.setAttribute("parentID", getID(defaultMutableTreeNode.getParent()));
        this.pageContext.setAttribute("node", Integer.toHexString(defaultMutableTreeNode.hashCode()));
        this.pageContext.setAttribute("nodeID", getID(defaultMutableTreeNode));
        this.pageContext.setAttribute("contentURL", getContentURL(indexItem));
        this.pageContext.setAttribute("expansionType", Integer.toString(indexItem.getExpansionType()));
    }

    private String getID(TreeNode treeNode) {
        if (treeNode == this.topNode) {
            return this.baseID;
        }
        TreeNode parent = treeNode.getParent();
        if (parent == null) {
            return "";
        }
        return getID(parent).concat(new StringBuffer().append(".").append(Integer.toString(parent.getIndex(treeNode))).toString());
    }

    private String getContentURL(IndexItem indexItem) {
        URL uRLFromID = null;
        Map.ID id = indexItem.getID();
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
}
