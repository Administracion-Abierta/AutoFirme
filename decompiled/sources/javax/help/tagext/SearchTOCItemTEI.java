package javax.help.tagext;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/* JADX INFO: loaded from: autofirma.jar:javax/help/tagext/SearchTOCItemTEI.class */
public class SearchTOCItemTEI extends TagExtraInfo {
    public VariableInfo[] getVariableInfo(TagData tagData) {
        return new VariableInfo[]{new VariableInfo("name", "java.lang.String", true, 0), new VariableInfo("helpID", "java.lang.String", true, 0), new VariableInfo("confidence", "java.lang.String", true, 0), new VariableInfo("hits", "java.lang.String", true, 0), new VariableInfo("contentURL", "java.lang.String", true, 0), new VariableInfo("hitBoundries", "java.lang.String", true, 0)};
    }
}
