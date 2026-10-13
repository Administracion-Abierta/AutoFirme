package javax.help.tagext;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/* JADX INFO: loaded from: autofirma.jar:javax/help/tagext/IndexItemTEI.class */
public class IndexItemTEI extends TagExtraInfo {
    public VariableInfo[] getVariableInfo(TagData tagData) {
        return new VariableInfo[]{new VariableInfo("name", "java.lang.String", true, 0), new VariableInfo("helpID", "java.lang.String", true, 0), new VariableInfo("parent", "java.lang.String", true, 0), new VariableInfo("parentID", "java.lang.String", true, 0), new VariableInfo("node", "java.lang.String", true, 0), new VariableInfo("nodeID", "java.lang.String", true, 0), new VariableInfo("contentURL", "java.lang.String", true, 0), new VariableInfo("expansionType", "java.lang.String", true, 0)};
    }
}
