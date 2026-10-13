package javax.help.tagext;

import com.aowagie.text.ElementTags;
import java.net.MalformedURLException;
import java.net.URL;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.InvalidHelpSetContextException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

/* JADX INFO: loaded from: autofirma.jar:javax/help/tagext/ValidateTag.class */
public class ValidateTag extends TagSupport {
    private HelpBroker helpBroker;
    private String invalidURLPath = "invalidhelp.html";
    private String hsName = null;
    private String id = null;
    private boolean merge = false;

    public void setHelpBroker(HelpBroker helpBroker) {
        this.helpBroker = helpBroker;
    }

    public void setInvalidURL(String str) {
        this.invalidURLPath = str;
    }

    public void setHelpSetName(String str) {
        this.hsName = str;
    }

    public void setCurrentID(String str) {
        this.id = str;
    }

    public void setMerge(boolean z) {
        this.merge = z;
    }

    public int doStartTag() {
        checkRequestParams();
        validateHelpSet();
        validateID();
        return 0;
    }

    private void checkRequestParams() {
        ServletRequest request = this.pageContext.getRequest();
        if (this.hsName == null) {
            this.hsName = request.getParameter("helpset");
        }
        if (this.id == null) {
            this.id = request.getParameter(ElementTags.ID);
        }
    }

    private void validateHelpSet() {
        HelpSet helpSet = this.helpBroker.getHelpSet();
        if (helpSet != null && this.hsName == null) {
            return;
        }
        if (helpSet == null && this.hsName == null) {
            try {
                this.pageContext.forward(this.invalidURLPath);
            } catch (Exception e) {
                return;
            }
        }
        if (helpSet == null && this.hsName != null) {
            this.helpBroker.setHelpSet(createHelpSet());
        } else if (helpSet != null && this.hsName != null && this.merge) {
            helpSet.add(createHelpSet());
        }
    }

    private HelpSet createHelpSet() {
        URL url;
        HelpSet helpSet = null;
        ServletRequest request = this.pageContext.getRequest();
        if (!this.hsName.startsWith("/")) {
            this.hsName = new StringBuffer().append("/").append(this.hsName).toString();
        }
        try {
            if (this.hsName.startsWith("http")) {
                url = new URL(this.hsName);
            } else {
                url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), this.hsName);
            }
            helpSet = new HelpSet(null, url);
        } catch (MalformedURLException e) {
        } catch (HelpSetException e2) {
            throw new RuntimeException(e2.getMessage());
        }
        return helpSet;
    }

    private void validateID() {
        if (this.id != null) {
            this.helpBroker.setCurrentID(this.id);
        } else if (this.helpBroker.getCurrentID() == null && this.helpBroker.getCurrentURL() == null) {
            try {
                this.helpBroker.setCurrentID(this.helpBroker.getHelpSet().getHomeID());
            } catch (InvalidHelpSetContextException e) {
            }
        }
    }
}
