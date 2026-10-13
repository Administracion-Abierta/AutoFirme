package javax.help.tagext;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.NavigatorView;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/* JADX INFO: loaded from: autofirma.jar:javax/help/tagext/NavigatorsTag.class */
public class NavigatorsTag extends BodyTagSupport {
    private HelpBroker hb;
    private HelpSet hs;
    private String curNav = null;
    private NavigatorView[] views;
    private int i;

    public void setHelpBroker(HelpBroker helpBroker) {
        this.hb = helpBroker;
        this.hs = helpBroker.getHelpSet();
    }

    public void setCurrentNav(String str) {
        this.curNav = str;
    }

    public void initialize() {
        checkRequestParams();
        initCurNav();
        this.views = this.hs.getNavigatorViews();
    }

    public int doStartTag() {
        initialize();
        if (this.views.length > 0) {
            this.i = 0;
            NavigatorView[] navigatorViewArr = this.views;
            int i = this.i;
            this.i = i + 1;
            setNavigatorAttributes(navigatorViewArr[i]);
            return 2;
        }
        return 0;
    }

    private void checkRequestParams() {
        String parameter = this.pageContext.getRequest().getParameter("nav");
        if (parameter != null) {
            this.curNav = parameter;
        }
    }

    private void initCurNav() {
        if (this.curNav != null) {
            try {
                this.hb.setCurrentView(this.curNav);
            } catch (IllegalArgumentException e) {
            }
        } else {
            this.curNav = this.hb.getCurrentView();
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.servlet.jsp.JspTagException */
    public int doAfterBody() throws JspException, JspTagException {
        BodyContent bodyContent = getBodyContent();
        try {
            bodyContent.writeOut(getPreviousOut());
            bodyContent.clearBody();
            if (this.i < this.views.length) {
                NavigatorView[] navigatorViewArr = this.views;
                int i = this.i;
                this.i = i + 1;
                setNavigatorAttributes(navigatorViewArr[i]);
                return 2;
            }
            return 0;
        } catch (IOException e) {
            throw new JspTagException(new StringBuffer().append("NavigatorsTag: ").append(e.getMessage()).toString());
        }
    }

    private void setNavigatorAttributes(NavigatorView navigatorView) {
        this.pageContext.setAttribute("className", navigatorView.getClass().getName());
        this.pageContext.setAttribute("name", navigatorView.getName());
        this.pageContext.setAttribute("tip", navigatorView.getLabel());
        this.pageContext.setAttribute("iconURL", getIconURL(navigatorView));
        this.pageContext.setAttribute("isCurrentNav", new Boolean(this.curNav.compareTo(navigatorView.getName()) == 0));
    }

    private String getIconURL(NavigatorView navigatorView) {
        URL uRLFromID = null;
        Map.ID imageID = navigatorView.getImageID();
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
