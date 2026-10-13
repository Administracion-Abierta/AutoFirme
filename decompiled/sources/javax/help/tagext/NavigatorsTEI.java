package javax.help.tagext;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/* JADX INFO: loaded from: autofirma.jar:javax/help/tagext/NavigatorsTEI.class */
public class NavigatorsTEI extends TagExtraInfo {
    public VariableInfo[] getVariableInfo(TagData tagData) {
        return new VariableInfo[]{new VariableInfo("className", "java.lang.String", true, 0), new VariableInfo("name", "java.lang.String", true, 0), new VariableInfo("tip", "java.lang.String", true, 0), new VariableInfo("iconURL", "java.lang.String", true, 0), new VariableInfo("isCurrentNav", "java.lang.Boolean", true, 0)};
    }
}
